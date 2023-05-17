package org.example.io;

import org.example.model.MapValue;

import java.io.*;

public class BinaryReader {
    public byte[] readValue(RandomAccessFile activeFile, MapValue mapValue) {
        byte[] value = null;
        try {
            long oldPosition = activeFile.getFilePointer();
            long valuePosition = mapValue.getValuePosition();

            activeFile.seek(valuePosition);

            byte[] buffer = new byte[(int) mapValue.getValueSize()];
            activeFile.read(buffer);
            // reset the file pointer after reading
            activeFile.seek(oldPosition);
            value = buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
