package com.example;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import org.apache.kafka.common.serialization.ByteArraySerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // Set up the producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
        Schema avroSchema = new Schema.Parser().parse(avroSchemaString);

        // Create Kafka producer
        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(properties);
        
        // Generate and send messages every 2 seconds
        App app = new App();
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
            byte[] array;
            try {
                array = app.genericRecordToByteArray(weatherData,avroSchema);
                ProducerRecord<String, byte[]> record = new ProducerRecord<>("my_first",null, array);
                Future<RecordMetadata> f  =producer.send(record);
                Object o = f.get();
                System.out.println("Success");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            messageCount++;
            try {
                Thread.sleep(15000); // Wait for 2 seconds before sending the next message
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Close the Kafka producer
        
     }
     private static byte[] genericRecordToByteArray(GenericData.Record record, Schema schema) throws IOException {

        // Create an AvroDatumWriter with the schema
        DatumWriter<GenericData.Record> datumWriter = new SpecificDatumWriter<>(schema);

        // Create a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create an AvroBinaryEncoder using EncoderFactory
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);

        // Serialize the Avro object into the ByteArrayOutputStream
        datumWriter.write(record, encoder);
        encoder.flush();
        byte[] byteArray = baos.toByteArray();

        // Use the byte array as needed
        // ...

        // Close the ByteArrayOutputStream
        baos.close();
        return byteArray;
    }
}
