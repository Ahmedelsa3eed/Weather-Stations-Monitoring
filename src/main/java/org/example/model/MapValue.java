package org.example.model;

import java.io.File;

public class MapValue {
    private File fileID;
    private long valueSize;
    private long valuePosition;

    public MapValue(File fileID, long valueSize, long valuePosition) {
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
}
