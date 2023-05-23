package org.example;

import org.example.io.AvroIO;
import org.example.io.BinaryReader;
import org.example.io.BinaryWriter;
import org.example.model.Entry;
import org.example.model.HintEntry;
import org.example.model.HintWeatherEntry;
import org.example.model.MapValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Compactor implements Runnable{
    private final ConcurrentHashMap<Long, MapValue> keyDir;
    private final long FILE_THRESHOLD = (long) 1e9;  // 1GB
    private final static String BITCASK_DIRECTORY = "bitcask";
   private final static String activeFileName = "active.cask";
    private final BinaryReader binaryReader = new BinaryReader();
    private final BinaryWriter binaryWriter = new BinaryWriter();
    private final File folder = new File(BITCASK_DIRECTORY);
    Map<Long, Entry> latest;

    public Compactor(ConcurrentHashMap<Long, MapValue> keyDir) {
        this.keyDir = keyDir;
    }

    @Override
    public void run() {
        latest = new HashMap<>();
        List<File> caskFiles = new LinkedList<>(), hintFiles = new LinkedList<>();


        // Get finished files to be operated on
        // split files by extension name
        for(File file: Objects.requireNonNull(folder.listFiles())){
            if(!file.isFile()) continue;
            String fileName = file.getName();
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            String extension = fileName.substring(fileName.lastIndexOf('.')+1);
            if(extension.equals("hint")){
                hintFiles.add(file);
                caskFiles.remove(hintToRegular(fileName));
                System.out.println("File " + fileName);
            }

            if(
                extension.equals("cask") && 
                !fileName.substring(fileName.lastIndexOf('/')+1).equals(activeFileName) && 
                !hintFiles.contains(new File(folder, nameWithoutExtension + ".hint"))
            ){
                caskFiles.add(file);
                System.out.println("File " + fileName);
            }
        }

        if(caskFiles.isEmpty()) return;

        readHintFiles(hintFiles);
        readCaskFiles(caskFiles);

        List<HintWeatherEntry> hintWeatherEntries = new LinkedList<>();
        Map<Long, MapValue> mapValues = new HashMap<>();
        int n = 0;
        StringBuffer fileName = new StringBuffer();
        File newFile = generateNewFileName(fileName);
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(newFile, "rw");
            for(Map.Entry<Long, Entry> mapEntry: latest.entrySet()){
                Entry entry = mapEntry.getValue();
                long pos = binaryWriter.writeEntry(randomAccessFile, entry);
                int valLength = entry.getValue().length;
                hintWeatherEntries.add(
                        new HintWeatherEntry(entry.getTimestamp(), (byte) valLength, (int) pos, entry.getKey()));
                mapValues.put(entry.getKey(),
                        new MapValue(newFile, valLength, (int) pos, entry.getTimestamp()));
                if(maximumSizeReached(newFile, randomAccessFile)){
                    // write hint file and update new file
                    writeHint(hintWeatherEntries, fileName);
                    newFile = generateNewFileName(fileName);
                    randomAccessFile.close();
                    randomAccessFile = new RandomAccessFile(newFile, "rw");
                }
            }
            writeHint(hintWeatherEntries, fileName);
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Map.Entry<Long, MapValue> entry: mapValues.entrySet()){
            MapValue oldMapValue = keyDir.get(entry.getKey());
            if(oldMapValue == null || oldMapValue.getTimestamp() < entry.getValue().getTimestamp())
                keyDir.put(entry.getKey(), entry.getValue());
        }

        for(File file: caskFiles)
            while(!file.delete());

        for(File file: hintFiles)
            while(!file.delete());

        for(Map.Entry<Long, Entry> entry:latest.entrySet()){
            try {
                System.out.println("ID = " + entry.getKey() + "\n\t VAL = " + AvroIO.deserialize(entry.getValue().getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readValue(MapValue mapValue) {
        BinaryReader binaryReader = new BinaryReader();
        try {
            RandomAccessFile activeFile = new RandomAccessFile(mapValue.getFileID(), "rw");
            return binaryReader.readEntry(activeFile, mapValue.getValuePosition()).getValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void writeHint(List<HintWeatherEntry> hintWeatherEntries, StringBuffer fileName) throws FileNotFoundException {
        if(hintWeatherEntries.isEmpty()) return;
        File hintFile = new File(BITCASK_DIRECTORY + "/" + fileName + ".hint");
        RandomAccessFile hintRandomFile = new RandomAccessFile(hintFile, "rw");
        for(HintWeatherEntry hintWeatherEntry: hintWeatherEntries)
            binaryWriter.writeHintEntry(hintRandomFile, hintWeatherEntry);
    }

    private File generateNewFileName(StringBuffer fileName) {
        File newFile;
        do {
            fileName.append(System.currentTimeMillis());
            newFile = new File(BITCASK_DIRECTORY + "/" + fileName + ".cask");
        } while (newFile.exists());
        return newFile;
    }

    private boolean maximumSizeReached(File fileID, RandomAccessFile file) throws IOException {
        if (file.length() >= FILE_THRESHOLD) {
            while (!fileID.renameTo(new File(BITCASK_DIRECTORY + "/" + System.currentTimeMillis() + ".cask"))){}
            file.close();
            return true;
        }
        return false;
    }

    private void readHintFiles(List<File> hintFiles) {
        for(File hintFile: hintFiles){
            for(Entry entry: readFromHint(hintFile)){
                put(entry);
            }
        }
    }

    private void readCaskFiles(List<File> caskFiles) {
        for(File caskFile: caskFiles){
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(caskFile, "r");
                Entry entry = binaryReader.readEntry(randomAccessFile);
                while(entry != null){
                    put(entry);
                    entry = binaryReader.readEntry(randomAccessFile);
                }
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Entry> readFromHint(File file){
        List<Entry> entries = new LinkedList<>();
        try {
            RandomAccessFile hintFile = new RandomAccessFile(file, "r");
            RandomAccessFile regularFile = new RandomAccessFile(hintToRegular(file.getName()), "r");
            HintEntry hintEntry = binaryReader.readHintEntry(hintFile);
            while(hintEntry != null){
                Entry entry = binaryReader.readEntry(regularFile, hintEntry.getValuePosition());
                entries.add(entry);
                hintEntry = binaryReader.readHintEntry(hintFile);
            }
            hintFile.close();
            regularFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    private File hintToRegular(String hintFileName){
        return new File(
            folder, 
            hintFileName.substring(0, hintFileName.lastIndexOf('.')) + ".cask");
    }

    private void put(Entry entry){
        Entry oldEntry = latest.get(entry.getKey());
        if(oldEntry != null && oldEntry.getTimestamp() >= entry.getTimestamp()) return;
        latest.put(entry.getKey(), entry);
    }

    public static void main(String[] args) {
        Compactor c = new Compactor(new ConcurrentHashMap<>());
        c.run();
    }


}
