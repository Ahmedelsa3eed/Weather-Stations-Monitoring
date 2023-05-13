package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BinaryWriter {
    public long writeEntry(Entry entry, RandomAccessFile activeFile) {
        long pos = 0;
        try {
            // write the Entry in Pascal-style format
            writePascalString(activeFile, entry.key);
            pos = writePascalString(activeFile, entry.value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pos;
    }

    private long writePascalString(RandomAccessFile activeFile, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        activeFile.writeByte(bytes.length);
        activeFile.write(bytes);
        return activeFile.getFilePointer() - bytes.length;
    }

    /**
     *  writes the int value in little-endian format with the appropriate length prefix
     * */
    private void writePascalInteger(RandomAccessFile activeFile, int value) throws IOException {
        if (value >= -127 && value <= 127) {
            activeFile.writeByte(1);
            activeFile.writeByte(value);
        } else if (value >= -32767 && value <= 32767) {
            activeFile.writeByte(2);
            activeFile.writeShort(Short.reverseBytes((short)value));
        } else {
            activeFile.writeByte(4);
            activeFile.writeInt(Integer.reverseBytes(value));
        }
    }
}
