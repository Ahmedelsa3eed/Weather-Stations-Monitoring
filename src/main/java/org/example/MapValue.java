package org.example;

import java.io.File;

public class MapValue {
    File fileID;
    long valueSize;
    long valuePosition;

    public MapValue(File fileID, long valueSize, long valuePosition) {
        this.fileID = fileID;
        this.valueSize = valueSize;
        this.valuePosition = valuePosition;
    }
}
