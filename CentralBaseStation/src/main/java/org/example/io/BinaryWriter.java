package org.example.io;

import org.example.model.Entry;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BinaryWriter {

    public long writeEntry(RandomAccessFile activeFile, Entry entry) {
        long pos = 0;
        try {
            pos = activeFile.getFilePointer();
            activeFile.write(entry.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pos;
    }

    private void writeLong(RandomAccessFile activeFile, Long value) throws IOException {
        activeFile.writeLong(value);
    }

    private long writeRecord(RandomAccessFile accessFile, byte[] weatherMessage) throws IOException {
        accessFile.writeByte(weatherMessage.length);
        accessFile.write(weatherMessage);
        return accessFile.getFilePointer() - weatherMessage.length;
    }

    private void writePascalString(RandomAccessFile activeFile, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        activeFile.writeByte(bytes.length);
        activeFile.write(bytes);
    }
}
