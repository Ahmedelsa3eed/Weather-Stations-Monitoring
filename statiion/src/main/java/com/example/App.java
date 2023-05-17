package com.example;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class App {

    public static void main(String[] args) {
        // Configure Kafka producer properties
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put("schema.registry.url", "http://localhost:8081"); // URL of the Schema Registry
        properties.put(ProducerConfig.ACKS_CONFIG, "all"); // Optional: Set the desired acks configuration
        // Add any additional producer properties you require

        // Create Avro schema
        String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
        Schema avroSchema = new Schema.Parser().parse(avroSchemaString);

        // Create Kafka producer
        Producer<String, GenericData.Record> producer = new KafkaProducer<>(properties);

        // Generate and send messages every 2 seconds
        int messageCount = 1;
        while (true) {
            GenericData.Record weatherData = new GenericData.Record(avroSchema);
            weatherData.put("station_id", 1L);
            weatherData.put("s_no", messageCount);
            weatherData.put("battery_status", "low");
            weatherData.put("status_timestamp", System.currentTimeMillis() / 1000L);

            GenericData.Record weather = new GenericData.Record(avroSchema.getField("weather").schema());
            weather.put("humidity", 35);
            weather.put("temperature", 100);
            weather.put("wind_speed", 13);

            weatherData.put("weather", weather);

            ProducerRecord<String, GenericData.Record> record = new ProducerRecord<>("your_topic_name", weatherData);
            producer.send(record);

            messageCount++;

            try {
                Thread.sleep(2000); // Wait for 2 seconds before sending the next message
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the Kafka producer
        producer.close();
    }
}
