package org.example.model;

import java.io.File;

public class MapValue {
    private File fileID;
    private long valueSize;
    private long valuePosition;
    private long timestamp;

    public MapValue(File fileID, long valueSize, long valuePosition, long timestamp) {
        this.fileID = fileID;
        this.valueSize = valueSize;
        this.valuePosition = valuePosition;
    }

    public File getFileID() {
        return fileID;
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

    @Override
    public String toString() {
        return "MapValue{" +
                "fileID=" + fileID +
                ", valueSize=" + valueSize +
                ", valuePosition=" + valuePosition +
                ", timestamp=" + timestamp +
                '}';
    }
}
