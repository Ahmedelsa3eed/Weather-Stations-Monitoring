package org.example.model;

/**
 * With each write, a new entry is appended to the active file.
 * */
public class Entry {
    private Long stationId;
    private byte[] weatherMessage;

    public Entry(Long key, byte[] weatherMessage) {
        this.stationId = key;
        this.weatherMessage = weatherMessage;
    }

    public Long getKey() {
        return stationId;
    }

    public byte[] getValue() {
        return weatherMessage;
    }
}
