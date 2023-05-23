package org.example;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
public class BaseStation {
    private Bitcask bitcask;

    public BaseStation() {
        bitcask = new Bitcask();
    }

    public void consumeMessages() {
        String bootstrapServers = "localhost:9092";
        String groupId = "my-consumer-group";
        String topic = "try";
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singletonList(topic));
    
        try {
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));
                records.forEach(record -> {
                    byte[] value = record.value();
                    System.out.println("Received message: " + new String(value));
                    // Add your custom logic here to process the received message
                });
            }
        } finally {
            consumer.close();
        }
    }

    private void processMessage(GenericRecord message) {
        bitcask.put(message);
        // TODO archive records in batches
    }

    public static void main(String[] args) {
        BaseStation baseStation = new BaseStation();
        baseStation.consumeMessages();
    }
}
