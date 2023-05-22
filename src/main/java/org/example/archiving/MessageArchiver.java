package org.example.archiving;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.example.io.AvroIO;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
public class MessageArchiver {
    String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
    Schema avroSchema = new Schema.Parser().parse(avroSchemaString);
    private WeatherParquetWriter writer;
    private Path archivePath = new Path("Archive/weather.parquet");
    public static void main(String[] args) {
        SparkConf conf= new SparkConf().setAppName("Java Spark").setMaster("local[*]");
        // Create a SparkSession
        SparkSession spark = SparkSession.builder()
        
        .appName("Java Spark SQL basic example").config("spark.master", "local")
        .getOrCreate();

        // Sample Avro record schema
        String avroSchemaString = "{\"type\":\"record\",\"name\":\"User\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"age\",\"type\":\"int\"}]}";
        Schema avroSchema = new Schema.Parser().parse(avroSchemaString);

        // Create a new Avro record
        GenericRecord avroRecord = createMessage(avroSchema, 0, 0);

        // Convert Avro record to DataFrame
        Dataset<Row> avroRecordDF = spark.createDataset(Collections.singletonList(avroRecord), Encoders.bean(GenericRecord.class)).toDF();
        // Define the Parquet file path
        String parquetFilePath = "/output.parquet";

        // Write the DataFrame to Parquet file
        avroRecordDF.write().mode(SaveMode.Append).parquet(parquetFilePath);

        // Stop the SparkSession
        spark.stop();
    }
//     public MessageArchiver() throws IOException {
//         AvroIO avroIO = new AvroIO();
//         schema = avroIO.getSchema();
//         writer = new WeatherParquetWriter(archivePath, schema);
//     }

//     public void archiveMessage(GenericRecord record) throws IOException {
//         writer.write(record);
//         writer.closeWriter(); // TODO: Close after exceeding a certain size
//     }

//     /**
//      * For testing purposes
//      * */
//     public List<GenericRecord> readMessages() throws IOException {
//         WeatherParquetReader reader = new WeatherParquetReader(archivePath);
//         return reader.read();
//     }

//     private void partition() {
//         // TODO Partition the data by year, month, and day
//         // Write the data to a partitioned parquet file
// //        URI partition = URI.create("2020-01-01T00");
// //        String path = partition.getPath();
// //        File parentFolder = new File(path);
// //        parentFolder.mkdirs();
// //        File partitionFile = new File(parentFolder, "parquet0000");
// //        Path filePath = new Path(partitionFile.toURI());
//     }

//     public static void main(String[] args) throws IOException {
// //        MessageArchiver archiver = new MessageArchiver();
// //        AvroIO avroIO = new AvroIO();
// //        GenericRecord record = avroIO.readAvroRecord();
// //
// //        archiver.archiveMessage(record);

// //        List<GenericRecord> records = archiver.readMessages();
// //
// //        if (record == records.get(0)) {
// //            System.out.println("Success!");
// //        } else {
// //            System.out.println("Failure!");
// //        }
//     
static GenericData.Record createMessage(Schema schema,int messageCount,int station_id){
    GenericData.Record weatherData = new GenericData.Record(schema); 
    Random random = new Random();
    weatherData.put("station_id", station_id);
    weatherData.put("s_no", messageCount);
    
    weatherData.put("battery_status", "battery");
    weatherData.put("status_timestamp", System.currentTimeMillis());

    GenericData.Record weather = new GenericData.Record(schema.getField("weather").schema());
    weather.put("humidity", random.nextInt(101));
    // weather.put("humidity", 90);
    weather.put("temperature", -20 + (40 + 20) * random.nextInt());
    weather.put("wind_speed", random.nextInt(120 - 0 + 1) + 0);

    weatherData.put("weather", weather);
    return weatherData;
    }
}
