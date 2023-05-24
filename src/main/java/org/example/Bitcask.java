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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Bitcask implements BitcaskIF {
    private final File BITCASK_DIRECTORY;
    private final long FILE_THRESHOLD = (long) 100 * 1024;  // 1MB
    private final static String ACTIVE_FILE_DIRECTORY = "/active.cask";
    private RandomAccessFile activeFile;
    private File fileID;
    private final Compactor compactor;
    private final ThreadGroup bitcaskGroup;
    private int uncompactedFiles;
    // keyDir key: StationID, value: <fileID, valueSize, valuePosition>
    private final ConcurrentHashMap<Long, MapValue> keyDir;

    public Bitcask() {
        /// TODO recover from previous state
        BITCASK_DIRECTORY = new File("bitcask");
        if (!BITCASK_DIRECTORY.exists())
            BITCASK_DIRECTORY.mkdir();
        keyDir = new ConcurrentHashMap<>();
        compactor = new Compactor(keyDir);
        bitcaskGroup = new ThreadGroup("Bitcask");
        uncompactedFiles = 0;
        createNewFile();
    }

    @Override
    public byte[] get(Long key) {
        MapValue mapValue = keyDir.get(key);
        System.out.println("Reading from file " + mapValue.getFileID().getName());
        try {
            return readValue(mapValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void put(Long stationId, byte[] weatherMessage) {
        try {
            // TODO: change with saber class
            long newTimestamp = (long) AvroIO.deserialize(weatherMessage).get("statusTimestamp");
            long valuePosition = append(new Entry(stationId, weatherMessage, newTimestamp));

            MapValue oldValue = keyDir.get(stationId);
            if(oldValue != null && newTimestamp <= oldValue.getTimestamp()) return;
            MapValue mapValue = new MapValue(fileID, weatherMessage.length, valuePosition, newTimestamp);
            keyDir.put(stationId, mapValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void merge() {
        if(bitcaskGroup.activeCount() > 0) return;
        compactor.setActiveFileName(fileID.getName());
        Thread compactionThread = new Thread(bitcaskGroup, compactor, "compactor");
        compactionThread.start();
    }

    private void createNewFile() {
        try {
            fileID = new File(BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".cask");
            activeFile = new RandomAccessFile(fileID, "rw");
            uncompactedFiles++;
            System.out.println("bitcask-main: Created new file " + fileID.getName());
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
            activeFile.close();
            createNewFile();
            if(uncompactedFiles > 2) {
                System.out.println("bitcask-main: Compacting (uncompactedFiles  = " + uncompactedFiles + ")");
                merge();
                uncompactedFiles = 1;
            }
        }
    }

    private long appendEntry(Entry entry) {
        BinaryWriter binaryWriter = new BinaryWriter();
        return binaryWriter.writeEntry(activeFile, entry);
    }

    public byte[] readValue(MapValue mapValue) throws IOException {
        BinaryReader binaryReader = new BinaryReader();
        RandomAccessFile activeFile = new RandomAccessFile(mapValue.getFileID(), "rw");
        byte[] res = binaryReader.readEntry(activeFile, mapValue.getValuePosition()).getValue();
        activeFile.close();
        return res;
    }

    public static void main(String[] args){
        Bitcask bitcask = new Bitcask();
        System.out.println(bitcask.bitcaskGroup.activeCount());
        Random r  = new Random();
        AvroIO avroIO = new AvroIO();
        for(int i = 0; i < 10000; i++){
            long key = r.nextInt(10)+1;
//            while(bitcask.keyDir.get(key) != null && System.currentTimeMillis() == bitcask.keyDir.get(key).getTimestamp()){}
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            byte[] value1 = new byte[0];
            try {
                value1 = avroIO.genericRecordToByteArray(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitcask.put(key, value1);
            if(i%1000 == 0) System.out.println("Finished " + i);
        }
        try {
            bitcask.activeFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        while (!bitcask.fileID.renameTo(new File(bitcask.BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".cask"))){}
        System.out.println(bitcask.fileID.getName());
//        try {
//            bitcask.activeFile = new RandomAccessFile(bitcask.fileID, "rw");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        for(Map.Entry<Long, MapValue> entry: bitcask.keyDir.entrySet()){
            try {
                System.out.println("In file: " + entry.getValue().getFileID().getName());
                System.out.println("ID = " + entry.getKey() + "\n\t VAL = " + AvroIO.deserialize(bitcask.get(entry.getKey())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        bitcask.merge();
//        System.out.println(bitcask.bitcaskGroup.activeCount());
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