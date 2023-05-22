// package org.example.archiving;

// import org.apache.avro.Schema;
// import org.example.archiving.Modules.DTO.WeatherDataDTO;
// import org.example.archiving.Modules.entity.Weather;
// import org.example.archiving.Modules.entity.WeatherData;
// import org.example.archiving.ParquetWriter.SparkParquetWriter;
// import java.util.ArrayList;
// import java.util.List;
// public class MessageArchiver {
//     static String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
//     static Schema avroSchema = new Schema.Parser().parse(avroSchemaString);
//     public static void main(String[] args) {
//         WeatherDataDTO weatherDataDTO = new WeatherDataDTO(avroSchema);
//         SparkParquetWriter sparkParquetWriter = SparkParquetWriter.getInstance(avroSchema);
        
//         Weather weather = Weather.builder()
//         .humidity(50)
//         .temperature(90)
//         .wind_speed(100)
//         .build();
//         WeatherData weatherData = WeatherData.builder()
//         .weather(weather)
//         .battery_status("low")
//         .s_no(1)
//         .station_id(1)
//         .status_timestamp(1)
//         .build();
//         List<WeatherData> weatherDatas = new ArrayList<>();
//         for(int i =0 ;i<100000;i++){ 
//             weatherDatas.add(weatherData);
//         }
//         sparkParquetWriter.write(weatherDatas, 30);
//     }

//     // public static void main(String[] args) {
//     //     SparkConf conf = new SparkConf().setAppName("Java Spark").setMaster("local[*]");
//     //     JavaSparkContext sparkContext = new JavaSparkContext(conf);
//     //     SparkSession spark = SparkSession.builder()
//     //             .appName("Java Spark SQL basic example")
//     //             .config(conf)
//     //             .getOrCreate();

//     //     // Convert Avro schema to Spark schema
//     //     List<WeatherData> weatherDataList = new ArrayList<>();
       
//     //     for(int i =10000 ;i<20000;i++){ 
//     //         GenericData.Record createMessageA = createAMessage(avroSchema, i, 0);

//     //         WeatherData weatherData = avroMapper(createMessageA, i, 0);

//     //         weatherDataList.add(weatherData); 
//     //     }
  
//     //     // Create RDD of WeatherData objects
//     //     JavaRDD<WeatherData> weatherDataRDD = sparkContext.parallelize(weatherDataList);

//     //     // Create DataFrame using the converted Spark schema
//     //     Dataset<Row> avroRecordDF = spark.createDataFrame(weatherDataRDD, WeatherData.class);
//     //     System.out.println();
//     //     System.out.println(avroRecordDF);
//     //     String parquetFilePath = "/home/elsaber/Downloads/Project/Weather-Stations-Monitoring/src/main/java/org/example/archiving/output.parquet";

//     //     avroRecordDF.write().mode(SaveMode.Append).parquet(parquetFilePath);

//     //     spark.stop();
//     //     sparkContext.close();
//     //     sparkContext.stop();

//     // }
//     // static GenericData.Record createAMessage(Schema schema,int messageCount,int station_id){
//     //     GenericData.Record weatherData = new GenericData.Record(schema); 
//     //     Random random = new Random();
//     //     weatherData.put("station_id", station_id);
//     //     weatherData.put("s_no", messageCount);
//     //     // String battery = randomBattery();
//     //     weatherData.put("battery_status", "batter111111111y");
//     //     weatherData.put("status_timestamp", System.currentTimeMillis());

//     //     GenericData.Record weather = new GenericData.Record(schema.getField("weather").schema());
//     //     weather.put("humidity", random.nextInt(101));
//     //     // weather.put("humidity", 90);
//     //     weather.put("temperature", -20 + (40 + 20) * random.nextInt());
//     //     weather.put("wind_speed", random.nextInt(120 - 0 + 1) + 0);

//     //     weatherData.put("weather", weather);
//     //     return weatherData;
//     // }
//     // static WeatherData avroMapper(GenericRecord avRecord, int messageCount, int station_id) {
//     //     WeatherData weatherData = new WeatherData();
//     //     weatherData.setStation_id((Integer) avRecord.get("station_id"));
//     //     weatherData.setS_no((int)avRecord.get("s_no"));
//     //     weatherData.setBattery_status((String) avRecord.get("battery_status"));
//     //     weatherData.setStatus_timestamp((long) avRecord.get("status_timestamp"));

//     //     Weather weather = new Weather();
//     //     GenericRecord avroWeather = ((GenericRecord) avRecord.get("weather"));
//     //     weather.setHumidity((int) avroWeather.get("humidity"));
//     //     weather.setTemperature((int) avroWeather.get("temperature"));
//     //     weather.setWind_speed((int)avroWeather.get("wind_speed"));

//     //     weatherData.setWeather(weather);
//     //     return weatherData;
//     // }
// }
