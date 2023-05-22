package org.example.model;

import org.example.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HintWeatherEntry implements HintEntry {
    private final byte initialSize = 14;
    private long timestamp;
    private byte valueSize;
    private int valuePosition;
    private long key;

    public HintWeatherEntry(long timestamp, byte valueSize, int valuePosition, long key) {
        this.timestamp = timestamp;
        this.valueSize = valueSize;
        this.valuePosition = valuePosition;
        this.key = key;
    }

    public HintWeatherEntry(byte[] bytes){
        fromByteArray(bytes);
    }

    private void fromByteArray(byte[] bytes) {
        timestamp = ByteUtils.bytesToLong(Arrays.copyOfRange(bytes, 0, 8));
        byte keySize = bytes[8];
        valueSize = bytes[9];
        valuePosition = ByteUtils.bytesToInt(Arrays.copyOfRange(bytes, 10, 14));
        key = ByteUtils.longFromCompressedBytes(Arrays.copyOfRange(bytes, 14, 14+keySize));
    }

    public HintWeatherEntry() {
    }

    public long getValueSize() {
        return valueSize;
    }

    public long getValuePosition() {
        return valuePosition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getKey() {
        return key;
    }

    @Override
    public byte[] toByteArray() {
        byte[] keyBytes = ByteUtils.compressLong(this.key);
        int finalSize = initialSize + keyBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(finalSize);
        buffer.putLong(timestamp);
        buffer.put((byte) keyBytes.length);
        buffer.put(valueSize);
        buffer.putInt(valuePosition);
        buffer.put(keyBytes);
        return buffer.array();
    }

    @Override
    public String toString() {
        return "HintWeatherEntry{" +
                "initialSize=" + initialSize +
                ", timestamp=" + timestamp +
                ", valueSize=" + valueSize +
                ", valuePosition=" + valuePosition +
                ", key=" + key +
                '}';
    }
}
