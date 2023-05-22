package com.example.station;
import java.util.concurrent.CompletableFuture;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.example.station.WeatherMessage;

import jakarta.annotation.PostConstruct;

@Component
class KafkaMessageProducer {

    private final KafkaTemplate<String, GenericRecord> kafkaTemplate;

    @Autowired
    public KafkaMessageProducer(KafkaTemplate<String, GenericRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void produceMessage() throws InterruptedException {
        // Prepare the message
        GenericRecord avroMessage = new GenericData.Record(WeatherMessage.getClassSchema());
        avroMessage.put("stationId", 1L);
        avroMessage.put("serialNumber", 1L);
        avroMessage.put("batteryStatus", "low");
        avroMessage.put("statusTimestamp", 1681521224L);

        GenericRecord avroWeather = new GenericData.Record(Weather.getClassSchema());
        avroWeather.put("humidity", 35);
        avroWeather.put("temperature", 100);
        avroWeather.put("windSpeed", 13);

        avroMessage.put("weather", avroWeather);

        String topic = "topic"; // Replace with your actual topic name

        // Send the message to Kafka topic every 2 seconds
        while (true) {
            ListenableFuture<SendResult<String, GenericRecord>> s = (ListenableFuture<SendResult<String, GenericRecord>>) kafkaTemplate.send(topic, avroMessage);
                    s.addCallback(
                            result -> System.out.println("Message sent successfully!"),
                            ex -> {
                                
                                
                                System.out.println("LLLLLLLLLLLL");
                                System.out.println("Failed to send message: " + ex.getMessage());}
                    );
            Thread.sleep(2000); // Delay for 2 seconds
        }
        }
    }