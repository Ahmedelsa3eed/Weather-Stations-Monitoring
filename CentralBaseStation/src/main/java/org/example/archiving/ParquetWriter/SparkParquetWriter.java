// package org.example.archiving.ParquetWriter;

// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import org.apache.spark.SparkConf;
// import org.apache.spark.api.java.JavaRDD;
// import org.apache.spark.api.java.JavaSparkContext;
// import org.apache.spark.sql.*;
// import org.example.archiving.Modules.entity.WeatherData;
// import org.example.archiving.Modules.time_stamp.TimeStampHandler;

// import lombok.Setter;
// @Setter
// public class SparkParquetWriter {
//     private static SparkParquetWriter obj;
//     private SparkSession spark;
//     private JavaSparkContext sparkContext;
//     private Map<Long, List<WeatherData>> batch;
//     // final WeatherDataDTO weatherDataDTO;
//     private LocalDateTime lastAdded;
//     private TimeStampHandler timeStamp;

//     // private SparkParquetWriter(WeatherDataDTO weatherDataDTO) {
//     private SparkParquetWriter(TimeStampHandler timeStamp) {
//         SparkConf conf = new SparkConf().setAppName("Java Spark").setMaster("local[*]");
//         sparkContext = new JavaSparkContext(conf);
//         spark = SparkSession.builder()
//         .appName("Java Spark SQL basic example")
//         .config(conf)
//         .getOrCreate();
//         batch = new HashMap<>();
//         // this.weatherDataDTO = weatherDataDTO;
//         lastAdded = LocalDateTime.now();  
//         this.timeStamp = timeStamp;
//     }
 
//     public static SparkParquetWriter getInstance(TimeStampHandler timeStamp)
//     {
//         if (obj==null)
//             obj = new SparkParquetWriter(timeStamp);
//         return obj;
//     }
//     public void addMessage(WeatherData weatherData ){
//         LocalDateTime now = LocalDateTime.now();
//         if(!timeStamp.isSameDay(now, lastAdded)){
//             flush(batch, lastAdded);
//             batch.clear();
//         } 
//         List<WeatherData> weatherDatas= batch.get(weatherData.getStation_id());
//         if(weatherDatas == null){
//             weatherDatas = new ArrayList<>();
//             batch.put(weatherData.getStation_id(), weatherDatas);
//         }
//         weatherDatas.add(weatherData);
//         if(weatherDatas.size() == 50){
//             write(weatherDatas, weatherData.getStation_id(), now);
//             weatherDatas.clear();
//         }
//         lastAdded = now;

//     }
//     public void write(List<WeatherData> weatherDatas, long station_id,LocalDateTime localDateTime){
//         JavaRDD<WeatherData> weatherDataRDD = sparkContext.parallelize(weatherDatas);
//         Dataset<Row> avroRecordDF = spark.createDataFrame(weatherDataRDD, WeatherData.class);
//         StringBuilder parquetFilePath = new StringBuilder();
//         String s = System.getProperty("user.dir");
//         String now = s + "/" + "Archive" +"/" + localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
//         try {
//             Files.createDirectories(Paths.get(now));
//             parquetFilePath.append(now);
//             parquetFilePath.append("/" + station_id);
//             System.out.println(parquetFilePath.toString());
//             avroRecordDF.write().mode(SaveMode.Append).parquet(parquetFilePath.toString());
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public void flush(Map<Long, List<WeatherData>> oldBatch,LocalDateTime oldDateTime){
//         for (Map.Entry<Long, List<WeatherData>> entry : batch.entrySet()) {
//             write(entry.getValue(), entry.getKey(), oldDateTime);
//         }
//     }
//     void stop(){
//         spark.stop();
//         sparkContext.close();
//         sparkContext.stop();
//     } 
// }
