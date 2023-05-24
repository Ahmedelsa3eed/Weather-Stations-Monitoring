package org.example.model;

import org.example.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * With each write, a new entry is appended to the active file.
 * */
public class Entry {
    private Long timestamp;
    private Long stationId;
    private byte[] weatherMessage;


    public Entry(Long key, byte[] weatherMessage, Long timestamp) {
        this.stationId = key;
        this.weatherMessage = weatherMessage;
        this.timestamp = timestamp;
    }

    public Entry(byte[] record){
        timestamp = ByteUtils.bytesToLong(Arrays.copyOfRange(record, 0, 8));
        byte keySize = record[8];
        int valueSize = ByteUtils.bytesToInt(Arrays.copyOfRange(record, 9, 13));
        stationId = ByteUtils.longFromCompressedBytes(Arrays.copyOfRange(record, 13, 13+keySize));
        weatherMessage = Arrays.copyOfRange(record, 13+keySize, 13+keySize+valueSize);
    }

    public byte[] toByteArray(){
        byte[] compressedKey = ByteUtils.compressLong(stationId);
        // 13 = 8 (timestamp) + 1 (key size) + 4 (value size)
        int finalSize = 13 + compressedKey.length + weatherMessage.length;
        ByteBuffer buffer = ByteBuffer.allocate(finalSize);
        buffer.putLong(timestamp);
        buffer.put((byte) compressedKey.length);
        buffer.putInt(weatherMessage.length);
        buffer.put(compressedKey);
        buffer.put(weatherMessage);
        return buffer.array();
    }

    public Long getKey() {
        return stationId;
    }

    public byte[] getValue() {
        return weatherMessage;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
