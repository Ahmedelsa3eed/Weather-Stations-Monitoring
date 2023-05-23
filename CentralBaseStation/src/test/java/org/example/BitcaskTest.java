package org.example;

import org.apache.avro.generic.GenericRecord;
import org.example.io.AvroIO;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BitcaskTest {
    Bitcask bitcask = new Bitcask();
    @Test
    void testCorrectWriting() throws IOException {
        Long key = 12345L;
        AvroIO avroIO = new AvroIO();
        GenericRecord record = avroIO.writeAvroRecord("src/main/resources/data1.avro", key);
        byte[] inputValue = avroIO.serialize(avroIO.readAvroRecord("src/main/resources/data1.avro"));

        bitcask.put(avroIO.serialize(record));
        byte[] outputValue = bitcask.get(key);
        assertArrayEquals(outputValue, inputValue);
    }

    @Test
    void testCorrectTwoConsecutiveWriting() throws IOException {
        Long key1 = 12345L, key2 = 9999L;
        AvroIO avroIO = new AvroIO();
        GenericRecord rec1 = avroIO.writeAvroRecord("src/main/resources/data.avro", key1);
        GenericRecord rec2 = avroIO.writeAvroRecord("src/main/resources/data2.avro", key2);
        byte[] inputValue1 = avroIO.serialize(avroIO.readAvroRecord("src/main/resources/data.avro"));
        byte[] inputValue2 = avroIO.serialize(avroIO.readAvroRecord("src/main/resources/data2.avro"));

        bitcask.put(avroIO.serialize(rec1));
        bitcask.put(avroIO.serialize(rec2));

        byte[] outputValue1 = bitcask.get(key1);
        byte[] outputValue2 = bitcask.get(key2);

        assertArrayEquals(outputValue1, inputValue1);
        assertArrayEquals(outputValue2, inputValue2);
    }
}