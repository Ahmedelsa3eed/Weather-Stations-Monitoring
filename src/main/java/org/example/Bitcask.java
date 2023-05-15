package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class Bitcask implements BitcaskIF {
    private File BITCASK_DIRECTORY;
    private final long FILE_THRESHOLD = (long) 1e9;  // 1GB
    private RandomAccessFile activeFile;
    private File fileID;
    private final HashMap<String, MapValue> keyDir;

    public Bitcask() {
        BITCASK_DIRECTORY = new File("bitcask");
        if (!BITCASK_DIRECTORY.exists())
            BITCASK_DIRECTORY.mkdir();
        keyDir = new HashMap<>();
        createNewFile();
    }

    @Override
    public String get(String key) {
        MapValue mapValue = keyDir.get(key);
        return readValue(mapValue);
    }

    @Override
    public void put(String key, String value) {
        try {
            long valuePosition = append(new Entry(key, value));
            MapValue mapValue = new MapValue(fileID, value.getBytes().length, valuePosition);
            keyDir.put(key, mapValue);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void merge() {
        /// TODO Compaction
    }
    private void createNewFile() {
        try {
            fileID = new File(BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".bin");
            this.activeFile = new RandomAccessFile(fileID, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public long append(Entry entry) throws IOException {
        checkFileSize();
        return appendEntry(entry);
    }

    private void checkFileSize() throws IOException {
        if (activeFile.length() >= FILE_THRESHOLD) {
            activeFile.close();
            createNewFile();
        }
    }

    private long appendEntry(Entry entry) {
        BinaryWriter binaryWriter = new BinaryWriter();
        return binaryWriter.writeEntry(entry, activeFile);
    }

    public String readValue(MapValue mapValue) {
        BinaryReader binaryReader = new BinaryReader();
        return binaryReader.readValue(activeFile, mapValue);
    }

    public static void main(String[] args) {
        Bitcask bitcask = new Bitcask();
        String key = "weather_station_1";
        String value = "station_id: 1, s_no: 1";

        bitcask.put(key, value);
        String outputValue = bitcask.get(key);

        System.out.println(outputValue);
    }

}