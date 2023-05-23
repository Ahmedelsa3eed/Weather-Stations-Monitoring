package org.example.io;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.example.avro.Weather;
import org.example.avro.WeatherMessage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class AvroIO {
    Schema schema;
    public AvroIO() {
        try {
            this.schema = new Schema.Parser().parse(
                    AvroIO.class.getResourceAsStream("/schema.avsc"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAvroRecord(String path) throws IOException {
        Random r = new Random();
        long stationId = r.nextLong(), serialNumber = r.nextLong(), timestamp = System.currentTimeMillis();
        int humidity = r.nextInt(), temperature = r.nextInt(), windSpeed = r.nextInt();

        System.out.println("Path = " + path + " : \n" +
                "\t stationId = " + stationId + ", serialNumber = " + serialNumber + ", timestamp = " + timestamp + "\n" +
                "\t humidity = " + humidity + ", temperature = " + temperature + ", windSpeed = " + windSpeed
        );

        // Write avro data to a file
//        DatumWriter<WeatherMessage> datumWriter = new SpecificDatumWriter<>(WeatherMessage.class);
//        DataFileWriter<WeatherMessage> dataFileWriter = new DataFileWriter<>(datumWriter);
//
//        dataFileWriter.create(WeatherMessage.getClassSchema(), new File(path));

        WeatherMessage weatherMessage = new WeatherMessage();
        weatherMessage.setSerialNumber(serialNumber);
        weatherMessage.setStationId(stationId);
        weatherMessage.setBatteryStatus("high");
        weatherMessage.setStatusTimestamp(timestamp);
        weatherMessage.setWeather(Weather.newBuilder()
                .setHumidity(humidity)
                .setTemperature(temperature)
                .setWindSpeed(windSpeed).build());
        byte[] bytes = weatherMessage.toByteBuffer().array();
//        // Set values for the fields of the weather message
//        weatherMessage.put("stationId", stationId);
//        weatherMessage.put("serialNumber", serialNumber);
//        weatherMessage.put("batteryStatus", "high");
//        weatherMessage.put("statusTimestamp", timestamp);
//
//        // Create a nested record for the weather field
//        GenericRecord weather = new GenericData.Record(schema.getField("weather").schema());
//        weather.put("humidity", humidity);
//        weather.put("temperature", temperature);
//        weather.put("windSpeed", windSpeed);
//
//        // Set the weather field in the weather message record
//        weatherMessage.put("weather", weather);

        try (FileOutputStream stream = new FileOutputStream(path)) {
            stream.write(bytes);
        }
    }

    public GenericRecord readAvroRecord(String path) throws IOException {
        GenericRecord record = null;
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(
                new File(path), datumReader);

        record = dataFileReader.next();

        dataFileReader.close();
        return record;
    }

    /**
     * Serialize the Avro record to binary format.
     * */
    public byte[] serialize(GenericRecord record) {
        byte[] binaryRecord = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

            // Serialize the record to binary format
            datumWriter.write(record, encoder);
            encoder.flush();

            // Get the serialized binary data
            binaryRecord = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binaryRecord;
    }

    // TODO: remove after debugging
    public static GenericRecord deserialize(byte[] avroData) throws IOException{
        Schema schema = new Schema.Parser().parse(
                AvroIO.class.getResourceAsStream("/schema.avsc"));
        Decoder decoder = DecoderFactory.get().binaryDecoder(avroData, null);
        DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(schema);
        GenericRecord record = datumReader.read(null, decoder);
        return record;
    }

    public byte[] genericRecordToByteArray(long stationId) throws IOException {
        Random r = new Random();
        long serialNumber = r.nextLong(), timestamp = System.currentTimeMillis();
        int humidity = r.nextInt(), temperature = r.nextInt(), windSpeed = r.nextInt();

        System.out.println(
        "\t stationId = " + stationId + ", serialNumber = " + serialNumber + ", timestamp = " + timestamp + "\n" +
        "\t humidity = " + humidity + ", temperature = " + temperature + ", windSpeed = " + windSpeed
        );

        GenericData.Record weatherMessage = new GenericData.Record(schema);

        // Set values for the fields of the weather message
        weatherMessage.put("stationId", stationId);
        weatherMessage.put("serialNumber", serialNumber);
        weatherMessage.put("batteryStatus", "low");
        weatherMessage.put("statusTimestamp", timestamp);

        // Create a nested record for the weather field
        GenericRecord weather = new GenericData.Record(schema.getField("weather").schema());
        weather.put("humidity", humidity);
        weather.put("temperature", temperature);
        weather.put("windSpeed", windSpeed);

        // Set the weather field in the weather message record
        weatherMessage.put("weather", weather);
        DatumWriter<GenericData.Record> datumWriter = new SpecificDatumWriter<>(schema);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
        datumWriter.write(weatherMessage, encoder);
        encoder.flush();
        byte[] byteArray = baos.toByteArray();
        baos.close();
        return byteArray;
    }

    public static void main(String[] args) throws IOException {
        AvroIO avroIO = new AvroIO();
        byte[] bytes = avroIO.genericRecordToByteArray(5);
        System.out.println(avroIO.deserialize(bytes).toString());
    }
}
