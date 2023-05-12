package org.example;

import java.io.File;
import java.util.HashMap;

public class Bitcask {
    private final String BITCASK_DIRECTORY = "bitcask";
    private final long FILE_THRESHOLD = (long) 1e9;  // 1GB
    private File activeFile;
    private HashMap<Integer, Value> keyDir;

    public Bitcask() {
        createNewFile();
        keyDir = new HashMap<>();
    }

    private void createNewFile() {
        this.activeFile = new File(BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".bin");
    }
    public void append(Entry entry) {
        checkFileSize();
        appendEntry(entry);
    }

    private void checkFileSize() {
        if (activeFile.length() >= FILE_THRESHOLD) {
            createNewFile();
        }
    }

    private void appendEntry(Entry entry) {
        BinaryWriter binaryWriter = new BinaryWriter();
        binaryWriter.writeEntry(entry, activeFile.getAbsolutePath());
    }

    public void readData() {
        BinaryReader binaryReader = new BinaryReader();
        binaryReader.readEntry(activeFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        Bitcask bitcask = new Bitcask();
        Entry entry = new Entry(4, 4, 1, 0);
        bitcask.append(entry);
        bitcask.readData();
    }

}