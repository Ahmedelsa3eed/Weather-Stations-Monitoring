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
import java.util.concurrent.ConcurrentHashMap;

public class Bitcask implements BitcaskIF {
    private File BITCASK_DIRECTORY;
    private final long FILE_THRESHOLD = (long) 1e9;  // 1GB
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
            // TODO make timestamps real
            long valuePosition = append(new Entry(stationId, weatherMessage, 0L));
            MapValue mapValue = new MapValue(fileID, weatherMessage.length, valuePosition, 0L);
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
            fileID = new File(BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".bin");
            this.activeFile = new RandomAccessFile(fileID, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        return binaryWriter.writeEntry(activeFile, entry);
    }

    public byte[] readValue(MapValue mapValue) {
        BinaryReader binaryReader = new BinaryReader();
        return binaryReader.readValue(activeFile, mapValue);
    }

    public static void main(String[] args) throws IOException {
        Bitcask bitcask = new Bitcask();
        Long key = 12345L;
        AvroIO avroIO = new AvroIO();
        byte[] value = avroIO.serialize(avroIO.readAvroRecord());

        bitcask.put(key, value);

        byte[] outputValue = bitcask.get(key);
        System.out.println("len: " + outputValue.length);
        String output = new String(outputValue);
        System.out.println(output);
    }

}