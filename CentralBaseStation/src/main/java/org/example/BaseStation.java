package org.example;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.archiving.Modules.DTO.MessageValidator;
import org.example.archiving.Modules.DTO.WeatherDataDTO;
import org.example.archiving.Modules.entity.WeatherData;
import org.example.archiving.Modules.time_stamp.TimeStampHandler;
import org.example.archiving.ParquetWriter.ParquetWriterHadoop;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import java.io.IOException;
import java.time.Duration;

import java.util.Collections;
import java.util.Properties;


public class BaseStation {
    private Bitcask bitcask;
    static MessageValidator msgValidator;
    static String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
    static Schema avroSchema = new Schema.Parser().parse(avroSchemaString);
    static WeatherDataDTO wDto;
    static KafkaProducer<String, byte[]> producer;
    ParquetWriterHadoop dWriterHadoop = null;
    TimeStampHandler timeStampHandler;
    ElasticSearchProducer elasticSearch;
    public BaseStation() {
        bitcask = new Bitcask();
        wDto = new WeatherDataDTO(avroSchema);
        timeStampHandler = new TimeStampHandler();
        dWriterHadoop = new ParquetWriterHadoop();
        msgValidator = new MessageValidator(timeStampHandler);
        elasticSearch = new ElasticSearchProducer();
    }
    public void consumeMessages() {
        String bootstrapServers = "localhost:9092";
        String groupId = "my-consumer-group";
        String topic = "try";
        // consumer config to consume message
        Properties propertiesConsumer = new Properties();
        propertiesConsumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        propertiesConsumer.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propertiesConsumer.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propertiesConsumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(propertiesConsumer);
        consumer.subscribe(Collections.singletonList(topic));

        // producer to invalidate weather Data 
        Properties propertiesProducer = new Properties();
        propertiesProducer.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propertiesProducer.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propertiesProducer.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        producer = new KafkaProducer<>(propertiesProducer);
            
        try {
            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));
                records.forEach(record -> processMessage(record.value()));
            }
        } finally {
            consumer.close();
        }
    }

    private void processMessage(byte[] SerializedMessage) {
        WeatherData weatherData;
        try {
            weatherData = wDto.map(SerializedMessage);
            GenericRecord r = wDto.getWeather(SerializedMessage);
            if(msgValidator.notValidate(weatherData)){
                ProducerRecord<String, byte[]> record = new ProducerRecord<>("invalide_channel",null, SerializedMessage);
                producer.send(record);
                System.out.println("invalid Message from " + weatherData.getStation_id());
                return;
            }
            bitcask.put(SerializedMessage);
            dWriterHadoop.addMessage(r);
            elasticSearch.processMessage(weatherData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BaseStation baseStation = new BaseStation();
        
        baseStation.consumeMessages();
    }
}
