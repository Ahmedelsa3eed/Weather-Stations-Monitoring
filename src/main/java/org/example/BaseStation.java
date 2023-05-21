package org.example;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.io.AvroIO;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class BaseStation {
    private Bitcask bitcask;

    public BaseStation() {
        bitcask = new Bitcask();
    }

    public void consumeMessage() {
        // Set up the consumer properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());

        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList("try"));

        try {
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofNanos(100));
                for (ConsumerRecord<String, byte[]> record : records) {
                    // Deserialize the message using the Avro schema
                    AvroIO avroIO = new AvroIO();
                    GenericRecord message = avroIO.deserialize(record.value());

                    processMessage(message);
                }
            }
        }
        finally {
            consumer.close();
        }
    }

    private void processMessage(GenericRecord message) {
        bitcask.put(message);
        // TODO archive records in batches
    }
}
