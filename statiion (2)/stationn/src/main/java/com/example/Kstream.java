package com.example;
import io.confluent.common.utils.TestUtils;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
public class Kstream {
    static final String inputTopic = "my_first";
    static final String outputTopic = "streams-wordcount-output";
    public static void main(String[] args) {
        final Properties streamsConfiguration = getStreamsConfiguration();
        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);
        streams.cleanUp();

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();
    
        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
    }
    /**
   * Configure the Streams application.
   *
   * Various Kafka Streams related settings are defined here such as the location of the target Kafka cluster to use.
   * Additionally, you could also define Kafka Producer and Kafka Consumer settings when needed.
   *
   * @param bootstrapServers Kafka cluster address
   * @return Properties getStreamsConfiguration
   */
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

  static void createWordCountStream(final StreamsBuilder builder) {
    // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
    // represent lines of text (for the sake of this example, we ignore whatever may be stored
    // in the message keys).  The default key and value serdes will be used.
    final KStream<String, byte[]> input = builder.stream(inputTopic);
    input.foreach(new ForeachAction<String, byte[]>() {
        @Override
        public void apply(String key, byte[] value) {
            // Perform some processing on each record
            int humidity;
            try {
                humidity = getHumidaty(value);
                System.out.println("Key: " + key + ", Value: " + humidity);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Assuming you have a byte array representing the Avro serialized data
            
    
            // Add your custom logic here
        }
    });
    // final KTable<String, Long> wordCounts = textLines
    //   // Split each text line, by whitespace, into words.  The text lines are the record
    //   // values, i.e. we can ignore whatever data is in the record keys and thus invoke
    //   // `flatMapValues()` instead of the more generic `flatMap()`.
    //   .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
    //   // Group the split data by word so that we can subsequently count the occurrences per word.
    //   // This step re-keys (re-partitions) the input data, with the new record key being the words.
    //   // Note: No need to specify explicit serdes because the resulting key and value types
    //   // (String and String) match the application's default serdes.
    //   .groupBy((keyIgnored, word) -> word)
    //   // Count the occurrences of each word (record key).
    //   .count();

    // Write the `KTable<String, Long>` to the output topic.
    // filteredStream.to(outputTopic);
  }
  static int getHumidaty(byte[] avroData) throws IOException{
   
        // Assuming you have the Avro schema for WeatherData
        String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherData\",\"fields\":[{\"name\":\"station_id\",\"type\":\"long\"},{\"name\":\"s_no\",\"type\":\"long\"},{\"name\":\"battery_status\",\"type\":\"string\"},{\"name\":\"status_timestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"wind_speed\",\"type\":\"int\"}]}}]}";
        Schema schema = new Schema.Parser().parse(avroSchemaString);

        // Assuming you have a byte array representing the Avro serialized data

        Decoder decoder = DecoderFactory.get().binaryDecoder(avroData, null);

        DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(schema);
        GenericRecord record = datumReader.read(null, decoder);

        // Extract the humidity field from the deserialized Avro object
        GenericRecord weather = (GenericRecord) record.get("weather");
        int humidity = (int) weather.get("humidity");

        System.out.println("Humidity: " + humidity); 
        return humidity;
    }
}
