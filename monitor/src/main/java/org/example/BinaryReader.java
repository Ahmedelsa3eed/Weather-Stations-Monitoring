package org.example;

import java.io.*;

public class BinaryReader {
    public String readValue(RandomAccessFile activeFile, MapValue mapValue) {
        String value = null;
        try {
            long oldPosition = activeFile.getFilePointer();
            long valuePosition = mapValue.valuePosition;

            activeFile.seek(valuePosition);

            byte[] buffer = new byte[(int) mapValue.valueSize];
            activeFile.read(buffer);
            // reset the file pointer after reading
            activeFile.seek(oldPosition);
            value = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
