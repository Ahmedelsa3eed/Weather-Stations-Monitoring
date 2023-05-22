package org.example.archiving.ParquetWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.example.archiving.Modules.DTO.WeatherDataDTO;
import org.example.archiving.Modules.entity.WeatherData;

public class SparkParquetWriter {
    private static SparkParquetWriter obj;
    SparkSession spark;
    JavaSparkContext sparkContext;
    Map<Integer, List<WeatherData>> batch;
    final WeatherDataDTO weatherDataDTO;
    LocalDateTime lastAdded;
    private SparkParquetWriter(WeatherDataDTO weatherDataDTO) {
        SparkConf conf = new SparkConf().setAppName("Java Spark").setMaster("local[*]");
        sparkContext = new JavaSparkContext(conf);
        spark = SparkSession.builder()
        .appName("Java Spark SQL basic example")
        .config(conf)
        .getOrCreate();
        batch = new HashMap<>();
        this.weatherDataDTO = weatherDataDTO;
        lastAdded = LocalDateTime.now();  
    }
 
    public static SparkParquetWriter getInstance(Schema schema)
    {
        if (obj==null)
            obj = new SparkParquetWriter(new WeatherDataDTO(schema));
        return obj;
    }
    public void addMessage(byte[] data){
        LocalDateTime now = LocalDateTime.now();
        if(!isSameDay(now, lastAdded)){
            flush(batch, lastAdded);
            batch.clear();
        }
        try {
        
            WeatherData weatherData = weatherDataDTO.map(data);
            List<WeatherData> weatherDatas= batch.get(weatherData.getStation_id());
            if(weatherDatas == null){
                weatherDatas = new ArrayList<>();
                batch.put(weatherData.getStation_id(), weatherDatas);
            }
            weatherDatas.add(weatherData);
            if(weatherDatas.size() == 10000){
                write(weatherDatas, weatherData.getStation_id(), now);
                weatherDatas.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void write(List<WeatherData> weatherDatas, int station_id,LocalDateTime localDateTime){
        JavaRDD<WeatherData> weatherDataRDD = sparkContext.parallelize(weatherDatas);
        Dataset<Row> avroRecordDF = spark.createDataFrame(weatherDataRDD, WeatherData.class);
        StringBuilder parquetFilePath = new StringBuilder();
        String s = System.getProperty("user.dir");
        String now = s + "/" +localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        try {
            Files.createDirectories(Paths.get(now));
            parquetFilePath.append(now);
            parquetFilePath.append("/" + station_id);
            System.out.println(parquetFilePath.toString());
            avroRecordDF.write().mode(SaveMode.Append).parquet(parquetFilePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isSameDay(LocalDateTime localDateTime1, LocalDateTime localDateTime2){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String str1 = fmt.format(localDateTime1);
        String str2 = fmt.format(localDateTime2);
        return str1.equals(str2);
    }
    public void flush(Map<Integer, List<WeatherData>> oldBatch,LocalDateTime oldDateTime){
        for (Map.Entry<Integer, List<WeatherData>> entry : batch.entrySet()) {
            write(entry.getValue(), entry.getKey(), oldDateTime);
        }
    }
    void stop(){
        spark.stop();
        sparkContext.close();
        sparkContext.stop();
    } 
}
