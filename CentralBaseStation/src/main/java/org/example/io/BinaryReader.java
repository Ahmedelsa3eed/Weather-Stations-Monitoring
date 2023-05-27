package org.example.io;

import org.example.model.Entry;
import org.example.model.HintEntry;
import org.example.model.HintWeatherEntry;
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
            System.out.println("File pointer = " + activeFile.getFilePointer() + ", File Size = " + activeFile.length());

            long timestamp = activeFile.readLong();
            byte keySize = activeFile.readByte();
            int valueSize = activeFile.readInt();
            byte[] keyBuffer = new byte[keySize];
            activeFile.read(keyBuffer);
            long key = ByteUtils.longFromCompressedBytes(keyBuffer);
            byte[] weatherMessage = new byte[valueSize];
            activeFile.read(weatherMessage);
            return new Entry(key, weatherMessage, timestamp);
        } catch (IOException | NegativeArraySizeException e) {
            System.out.println("Error while reading value, invalid entry");
        }
        return null;
    }

    public Entry readEntry(RandomAccessFile activeFile){
        try {
            if(activeFile.getFilePointer() >= activeFile.length()) return null;
            long timestamp = activeFile.readLong();
            byte keySize = activeFile.readByte();
            int valueSize = activeFile.readInt();
            if(keySize < 0) return null;
            byte[] keyBuffer = new byte[keySize];
            activeFile.read(keyBuffer);
            long key = ByteUtils.longFromCompressedBytes(keyBuffer);
            byte[] weatherMessage = new byte[valueSize];
            activeFile.read(weatherMessage);
            return new Entry(key, weatherMessage, timestamp);
        } catch (Exception e) {
            System.out.println("Reached end of file " + activeFile.toString());
        }
        return null;
    }

    public HintEntry readHintEntry(RandomAccessFile activeFile){
        try {
            if(activeFile.getFilePointer() >= activeFile.length()) return null;
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
