package org.example.io;

import org.example.model.MapValue;

import java.io.*;

public class BinaryReader {
    public byte[] readValue(MapValue mapValue) {
        byte[] value = null;
        try {
            RandomAccessFile file = new RandomAccessFile(mapValue.getFileID(), "r");
            long valuePosition = mapValue.getValuePosition();

            file.seek(valuePosition);

            byte[] buffer = new byte[(int) mapValue.getValueSize()];
            file.read(buffer);
            value = buffer;
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
