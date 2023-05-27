package org.example.archiving.ParquetWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericData.Record;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.example.archiving.Modules.time_stamp.TimeStampHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

// @RequiredArgsConstructor
public class ParquetWriterHadoop {

  private String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
  private Schema avroSchema = new Schema.Parser().parse(avroSchemaString);
  private Map<Long, List<GenericRecord>> batch;
  // // final WeatherDataDTO weatherDataDTO;
  private LocalDateTime lastAdded;
  private TimeStampHandler timeStamp;

  public ParquetWriterHadoop() {
    batch = new HashMap<>();
    lastAdded = LocalDateTime.now();
    this.timeStamp = new TimeStampHandler();
  }

  public void addMessage(GenericRecord weatherData) {
    LocalDateTime now = LocalDateTime.now();
    if (!timeStamp.isSameDay(now, lastAdded)) {
      flush(batch, lastAdded);
      batch.clear();
    }
    System.out.println(weatherData.get("station_id"));
    List<GenericRecord> weatherDatas = batch.get((Long) weatherData.get("station_id"));
    if (weatherDatas == null) {
      weatherDatas = new ArrayList<>();
      batch.put(Long.valueOf((Long) weatherData.get("station_id")), weatherDatas);
    }
    weatherDatas.add(weatherData);
    if (weatherDatas.size() == 50) {
      writeToParquet(weatherDatas, (Long) weatherData.get("station_id"), now);
      weatherDatas.clear();
    }
    lastAdded = now;

  }

  private void flush(Map<Long, List<GenericRecord>> batch2, LocalDateTime oldDateTime) {
    for (Map.Entry<Long, List<GenericRecord>> entry : batch.entrySet()) {
      writeToParquet(entry.getValue(), entry.getKey(), oldDateTime);
    }
  }

  public void writeToParquet(List<GenericRecord> recordList, long station_id, LocalDateTime localDateTime) {
    // Path to Parquet file in HDFS

    String s = System.getProperty("user.dir");
    String now = s + "/" + "Archive" + "/" + localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)+"/" + station_id;
    ParquetWriter<GenericRecord> writer = null;
    // Creating ParquetWriter using builder
    try {
      Files.createDirectories(Paths.get(now));
      Configuration conf = new Configuration();
      conf.set("fs.defaultFS", "file:///");
      Path path = new Path("file:///"+now + "/" + System.currentTimeMillis() / 1000 + ".parquet");
      writer = AvroParquetWriter.<GenericRecord>builder(path)
          .withSchema(avroSchema)
          .withWriteMode(Mode.OVERWRITE)
          .withConf(conf)
          .build();

      for (GenericRecord record : recordList) {
        writer.write(record);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
