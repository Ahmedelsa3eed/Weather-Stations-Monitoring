package org.example.Recovery;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;

import java.io.File;
import java.io.IOException;

public class RecoverBitcask {
    Schema schema;

    public RecoverBitcask() {
        try {
            this.schema = new Schema.Parser().parse(
                    RecoverBitcask.class.getResourceAsStream("/hint-schema.avsc"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAvroRecord() throws IOException {
        // Write avro data to a file
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);

        dataFileWriter.create(schema, new File("src/main/resources/hint-data.avro"));

        GenericRecord hintRecord = new GenericData.Record(schema);

        // Set values for the fields of the weather message
        hintRecord.put("statusTimestamp", 123456L);
        hintRecord.put("KeySize", 7890L);
        hintRecord.put("ValueSize", 12L);
        hintRecord.put("valuePosition", 0L);
        hintRecord.put("Key", 123L);

        dataFileWriter.append(hintRecord);
        dataFileWriter.close();
    }

    public GenericRecord readAvroRecord() throws IOException {
        GenericRecord record = null;
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(
                new File("src/main/resources/hint-data.avro"), datumReader);

        while (dataFileReader.hasNext()) {
            record = dataFileReader.next();
            System.out.println(record.get("statusTimestamp"));
            System.out.println(record.get("KeySize"));
            System.out.println(record.get("ValueSize"));
            System.out.println(record.get("valuePosition"));
            System.out.println(record.get("Key"));
        }

        dataFileReader.close();
        return record;
    }

    public static void main(String[] args) {
        RecoverBitcask recoverBitcask = new RecoverBitcask();
        try {
            recoverBitcask.writeAvroRecord();
            GenericRecord record = recoverBitcask.readAvroRecord();
            System.out.println(record.get("statusTimestamp"));
            System.out.println(record.get("KeySize"));
            System.out.println(record.get("ValueSize"));
            System.out.println(record.get("valuePosition"));
            System.out.println(record.get("Key"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
