package org.example.io;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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

    public GenericRecord writeAvroRecord(String path, Long stationId) throws IOException {
        // Write avro data to a file
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        dataFileWriter.create(schema, new File(path));

        GenericRecord weatherMessage = new GenericData.Record(schema);

        // Set values for the fields of the weather message
        weatherMessage.put("stationId", stationId);
        weatherMessage.put("serialNumber", 7890L);
        weatherMessage.put("batteryStatus", "low");
        weatherMessage.put("statusTimestamp", System.currentTimeMillis());

        // Create a nested record for the weather field
        GenericRecord weather = new GenericData.Record(schema.getField("weather").schema());
        weather.put("humidity", 80);
        weather.put("temperature", 25);
        weather.put("windSpeed", 10);

        // Set the weather field in the weather message record
        weatherMessage.put("weather", weather);

        dataFileWriter.append(weatherMessage);
        dataFileWriter.close();

        return weatherMessage;
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

    public GenericRecord deserialize(byte[] messageBytes) {
        GenericRecord message = null;
        try {
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            Decoder decoder = DecoderFactory.get().binaryDecoder(messageBytes, null);
            message = datumReader.read(null, decoder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }
}