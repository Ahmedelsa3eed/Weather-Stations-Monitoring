package com.example.Adapter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import com.example.MessageHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Adapter { 
        final static String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
        final static Schema avroSchema = new Schema.Parser().parse(avroSchemaString);
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(properties);
        try {


            // Use Jackson ObjectMapper to convert the JSON response to a Map
            MessageHandler msgHandler = new MessageHandler(avroSchema);
            ObjectMapper objectMapper = new ObjectMapper();
            // LinkedHashMap<String, Integer> linkedHashMap = (LinkedHashMap<String, Integer>) jsonResponse.get("current_weather");
            // System.out.println(linkedHashMap.get("time"));
            APICaller apiCaller = new APICaller("https://api.open-meteo.com/v1/forecast?latitude=31.20&longitude=29.92&hourly=relativehumidity_2m&current_weather=true&timeformat=unixtime&forecast_days=1");
            int messageCount = 1;
            boolean work = true;
            long lastTimeStamp = 0;
            Map<String, Object> jsonResponse = null;
            while (work) {
                long cur = System.currentTimeMillis();
                if(!sameDay(lastTimeStamp, cur)){
                    String responseString = apiCaller.sendRequest();
                    jsonResponse = objectMapper.readValue(responseString, Map.class);
                    lastTimeStamp = cur;
                }
                GenericData.Record weatherData = msgHandler.createMessage(avroSchema, 500,messageCount, jsonResponse );
                byte[] array;
                try {
                    array = msgHandler.genericRecordToByteArray(weatherData,avroSchema);
                    ProducerRecord<String, byte[]> record = new ProducerRecord<>("try",null, array);
                    producer.send(record);
                    System.out.println("Success");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                messageCount++;
                try {
                    Thread.sleep(15000); // Wait for 2 seconds before sending the next message
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        producer.close();
    }
    static boolean sameDay(long timestamp1, long timestamp2){
        Instant instant1 = Instant.ofEpochSecond(timestamp1);
        Instant instant2 = Instant.ofEpochSecond(timestamp2);
        // Convert the Instants to LocalDate objects
        LocalDate date1 = LocalDateTime.ofInstant(instant1, ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = LocalDateTime.ofInstant(instant2, ZoneId.systemDefault()).toLocalDate();
        return date1.getDayOfMonth() == date2.getDayOfMonth()
        && date1.getYear() == date2.getYear();
    }
}
