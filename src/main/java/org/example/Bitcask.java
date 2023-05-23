package org.example;

import org.example.io.AvroIO;
import org.example.io.BinaryReader;
import org.example.io.BinaryWriter;
import org.example.model.Entry;
import org.example.model.MapValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bitcask implements BitcaskIF {
    private File BITCASK_DIRECTORY;
    private final long FILE_THRESHOLD = (long) 1e9;  // 1GB
    private final static String ACTIVE_FILE_DIRECTORY = "/active.cask";
    private RandomAccessFile activeFile;
    private File fileID;
    // keyDir key: StationID, value: <fileID, valueSize, valuePosition>
    private final ConcurrentHashMap<Long, MapValue> keyDir;

    public Bitcask() {
        BITCASK_DIRECTORY = new File("bitcask");
        if (!BITCASK_DIRECTORY.exists())
            BITCASK_DIRECTORY.mkdir();
        keyDir = new ConcurrentHashMap<>();
        createNewFile();
    }

    @Override
    public byte[] get(Long key) {
        MapValue mapValue = keyDir.get(key);
        return readValue(mapValue);
    }

    @Override
    public void put(Long stationId, byte[] weatherMessage) {
        try {
            // TODO: change with saber class
            long newTimestamp = (long) AvroIO.deserialize(weatherMessage).get("statusTimestamp");
            MapValue oldValue = keyDir.get(stationId);
            if(oldValue != null && newTimestamp <= oldValue.getTimestamp()) return;

            long valuePosition = append(new Entry(stationId, weatherMessage, newTimestamp));
            MapValue mapValue = new MapValue(fileID, weatherMessage.length, valuePosition, newTimestamp);
            keyDir.put(stationId, mapValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void merge() {
        /// TODO Compaction
    }
    private void createNewFile() {
        try {
            fileID = new File(BITCASK_DIRECTORY + ACTIVE_FILE_DIRECTORY);
            this.activeFile = new RandomAccessFile(fileID, "rw");
            activeFile.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public long append(Entry entry) throws IOException {
        checkFileSize();
        return appendEntry(entry);
    }

    private void checkFileSize() throws IOException {
        if (activeFile.length() >= FILE_THRESHOLD) {
            while (!fileID.renameTo(new File(BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".cask"))){}
            activeFile.close();
            createNewFile();
        }
    }

    private long appendEntry(Entry entry) {
        BinaryWriter binaryWriter = new BinaryWriter();
        return binaryWriter.writeEntry(activeFile, entry);
    }

    public byte[] readValue(MapValue mapValue) {
        BinaryReader binaryReader = new BinaryReader();
        return binaryReader.readEntry(activeFile, mapValue.getValuePosition()).getValue();
    }

    public static void main(String[] args) throws IOException {
        Bitcask bitcask = new Bitcask();
        Random r  = new Random();
        AvroIO avroIO = new AvroIO();
        for(int i = 0; i < 10000; i++){
            int key = r.nextInt(10-1)+1;
            byte[] value1 = avroIO.genericRecordToByteArray(key);
            bitcask.put((long)key, value1);
        }
//        Long key1 = 12345L, key2 = 9738L;
//        byte[] value1 = avroIO.genericRecordToByteArray(key1);
//        byte[] value2 = avroIO.genericRecordToByteArray(key2);
//
//
//        bitcask.put(key1, value1);
//        bitcask.put(key2, value2);

//        byte[] outputValue = bitcask.get(key1);
//        System.out.println("key: " + key1);
//        System.out.println(avroIO.deserialize(outputValue).toString());
//        outputValue = bitcask.get(key2);
//        System.out.println("key: " + key2);
//        System.out.println(avroIO.deserialize(outputValue).toString());
    }
}