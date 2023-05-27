package com.example;


import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
public class App 
{
    public static void main( String[] args )
    {
        Random r = new Random();
        int station_id = (int) (Math.random() * 500);
        System.out.println("Starting with station_id = " + station_id);
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-service:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
        Schema avroSchema = new Schema.Parser().parse(avroSchemaString);
        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(properties);
        MessageHandler msqHandler = new MessageHandler(avroSchema);
        int messageCount = 1;
        boolean work = true;
        while (work) {
            GenericData.Record weatherData = msqHandler.createMessage(avroSchema, messageCount, station_id);
            byte[] array;
            try {
                float dropProb = (float) Math.random();
                if(dropProb > 0.1){
                    array = msqHandler.genericRecordToByteArray(weatherData,avroSchema);
                    ProducerRecord<String, byte[]> record = new ProducerRecord<>("try",null, array);
                    producer.send(record);
                    System.out.println("Success " + messageCount);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageCount++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        producer.close();
     }
}
