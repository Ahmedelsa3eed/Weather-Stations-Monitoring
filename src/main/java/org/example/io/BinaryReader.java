package org.example.io;

import org.example.model.Entry;
import org.example.model.HintEntry;
import org.example.model.HintWeatherEntry;
import org.example.model.MapValue;
import org.example.utils.ByteUtils;

import java.io.*;
import java.util.Arrays;

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

    public Entry readEntry(RandomAccessFile activeFile){
        try {
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
            System.out.println("Reached end of file " + activeFile.toString());
        }
        return null;
    }

    public HintEntry readHintEntry(RandomAccessFile activeFile){
        try {
            long timestamp = activeFile.readLong();
            byte keySize = activeFile.readByte();
            byte valueSize = activeFile.readByte();
            int valPosition = activeFile.readInt();
            byte[] keyBuffer = new byte[keySize];
            activeFile.read(keyBuffer);
            long key = ByteUtils.longFromCompressedBytes(keyBuffer);
            return new HintWeatherEntry(timestamp, valueSize, valPosition, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
