package org.example.io;

import org.example.model.Entry;
import org.example.model.MapValue;
import org.example.utils.ByteUtils;

import java.io.*;
import java.util.Arrays;

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

    public Entry readEntry(RandomAccessFile activeFile, long position){
        try {
            // Position = -1 means don't move position
            if(position != -1)
                activeFile.seek(position);
            long timestamp = activeFile.readLong();
            byte keySize = activeFile.readByte();
            int valueSize = activeFile.readInt();
            byte[] keyBuffer = new byte[keySize];
            activeFile.read(keyBuffer);
            long key = ByteUtils.longFromCompressedBytes(keyBuffer);
            byte[] weatherMessage = new byte[valueSize];
            activeFile.read(weatherMessage);
            return new Entry(key, weatherMessage, timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
