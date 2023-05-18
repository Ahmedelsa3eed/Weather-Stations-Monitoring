package org.example;

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
        avroIO.writeAvroRecord("src/main/resources/data1.avro");
        byte[] inputValue = avroIO.serialize(avroIO.readAvroRecord("src/main/resources/data1.avro"));

        bitcask.put(key, inputValue);
        byte[] outputValue = bitcask.get(key);
        assertArrayEquals(outputValue, inputValue);
    }

    @Test
    void testCorrectTwoConsecutiveWriting() throws IOException {
        Long key1 = 12345L, key2 = 12346L;
        AvroIO avroIO = new AvroIO();
        avroIO.writeAvroRecord("src/main/resources/data.avro");
        avroIO.writeAvroRecord("src/main/resources/data2.avro");
        byte[] inputValue1 = avroIO.serialize(avroIO.readAvroRecord("src/main/resources/data.avro"));
        byte[] inputValue2 = avroIO.serialize(avroIO.readAvroRecord("src/main/resources/data2.avro"));

        bitcask.put(key1, inputValue1);
        bitcask.put(key2, inputValue2);

        byte[] outputValue1 = bitcask.get(key1);
        byte[] outputValue2 = bitcask.get(key2);

        assertArrayEquals(outputValue1, inputValue1);
        assertArrayEquals(outputValue2, inputValue2);
    }
}