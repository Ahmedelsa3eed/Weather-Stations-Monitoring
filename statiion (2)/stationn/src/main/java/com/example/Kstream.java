package com.example;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
public class Kstream {
    static final String inputTopic = "try";
    static final String outputTopic = "streams-wordcount-output";
    static final String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
    static final Schema schema= new Schema.Parser().parse(avroSchemaString);;
    static final MessageHandler messageHandler = new MessageHandler(schema);
    public static void main(String[] args) {
        final Properties streamsConfiguration = getStreamsConfiguration();
        final StreamsBuilder builder = new StreamsBuilder(); 
        final KStream<String, byte[]> input = builder.stream(inputTopic);
        KStream<String, String> processedStream = input.flatMap(new MyValueMapper());
        processedStream.to(outputTopic, Produced.with(Serdes.String(), Serdes.String())); 
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.cleanUp();

        streams.start();
    }
  static Properties getStreamsConfiguration() {
    final Properties streamsConfiguration = new Properties();
    // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
    // against which the application is run.
    streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "Raining_Triggers");
    streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "Raining_Triggers-client");
    // Where to find Kafka broker(s).
    streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    // Specify default (de)serializers for record keys and for record values.
    streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass());
    return streamsConfiguration;
  }

  private static class MyValueMapper implements org.apache.kafka.streams.kstream.KeyValueMapper<String, byte[], Iterable<KeyValue<String, String>>> {
    @Override
    public Iterable<KeyValue<String, String>> apply(String key, byte[] value) {
        List<KeyValue<String, String>> results = new ArrayList<>();
        try {
            GenericRecord wRecord = messageHandler.getWeather(value);
            GenericRecord weather = (GenericRecord) wRecord.get("weather");
            int humidity = (int)weather.get("humidity");
            if (humidity > 70) {
                System.out.println("High humidity detected");
                String message = "High humidity detected: " + humidity + " from station " + wRecord.get("station_id") + " at time " + wRecord.get("status_timestamp");
                results.add(new KeyValue<>(key, message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
}
