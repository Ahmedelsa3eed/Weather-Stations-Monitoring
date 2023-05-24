package org.example;

import org.example.io.AvroIO;
import org.example.io.BinaryReader;
import org.example.model.Entry;
import org.example.model.HintEntry;
import org.example.model.MapValue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverBitcask{
    private final String bitcaskDirectory;
    private final BinaryReader binaryReader = new BinaryReader();

    public RecoverBitcask(String bitcaskDirectory) {
        this.bitcaskDirectory = bitcaskDirectory;
    }

    public ConcurrentHashMap<Long, MapValue> recover() throws IOException {
        ConcurrentHashMap<Long, MapValue> keyDir = new ConcurrentHashMap<>();
        File folder = new File(bitcaskDirectory);
        List<File> caskFiles = new LinkedList<>(), hintFiles = new LinkedList<>();
        // Get finished files to be operated on
        for(File file: Objects.requireNonNull(folder.listFiles())){
            if(!file.isFile()) continue;
            String fileName = file.getName();
            String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
            // split files by extension name
            String extension = fileName.substring(fileName.lastIndexOf('.')+1);
            if(extension.equals("hint")){
                hintFiles.add(file);
//                caskFiles.remove(hintToRegular(fileName));      // Remove cask file if hint exists
                System.out.println("File " + fileName);
            }

            if(
                    extension.equals("cask")
//               && !hintFiles.contains(new File(folder, nameWithoutExtension + ".hint"))   // Skip cask file if hint exists
            ){
                caskFiles.add(file);
                System.out.println("File " + fileName);
            }
        }

        if(caskFiles.isEmpty()) return keyDir;

        readHintFiles(keyDir, hintFiles);
        readCaskFiles(keyDir, folder, caskFiles, hintFiles);

        for(Map.Entry<Long, MapValue> entry: keyDir.entrySet()){
            try {
                System.out.println("ID = " + entry.getKey() + "\n\t VAL = " + AvroIO.deserialize(get(keyDir, entry.getKey())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return keyDir;
    }

    public byte[] get(ConcurrentHashMap<Long, MapValue> keyDir, Long key) {
        MapValue mapValue = keyDir.get(key);
        System.out.println("Reading from file " + mapValue.getFileID().getName());
        try {
            return readValue(mapValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] readValue(MapValue mapValue) throws IOException {
        BinaryReader binaryReader = new BinaryReader();
        RandomAccessFile activeFile = new RandomAccessFile(mapValue.getFileID(), "rw");
        byte[] res = binaryReader.readEntry(activeFile, mapValue.getValuePosition()).getValue();
        activeFile.close();
        return res;
    }

    private void readHintFiles(ConcurrentHashMap<Long, MapValue> keyDir, List<File> hintFiles) throws IOException {
        for(File hintFile: hintFiles){
            File caskFile = hintToRegular(hintFile.getName());
            RandomAccessFile randomHintFile = new RandomAccessFile(hintFile, "r");
            HintEntry hintEntry = binaryReader.readHintEntry(randomHintFile);
            while(hintEntry != null){
                HintEntry entry = binaryReader.readHintEntry(randomHintFile);
                MapValue value = new MapValue(caskFile, entry.getValueSize() , entry.getValuePosition(), entry.getTimestamp());
                MapValue oldValue = keyDir.get(entry.getKey());
                if(oldValue == null || oldValue.getTimestamp() < value.getTimestamp())
                    keyDir.put(entry.getKey(), value);
                hintEntry = binaryReader.readHintEntry(randomHintFile);
            }
            randomHintFile.close();
        }
    }

    private void readCaskFiles(ConcurrentHashMap<Long, MapValue> keyDir, File folder, List<File> caskFiles, List<File> hintFiles) {
        for(File caskFile: caskFiles){
            if(hintFiles.contains(new File(folder, getNameWithoutExtension(caskFile) + ".hint")))
                continue;
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(caskFile, "r");
                Entry entry = binaryReader.readEntry(randomAccessFile);
                while(entry != null){
                    MapValue value = new MapValue(
                            caskFile,
                            entry.getValue().length,
                            randomAccessFile.getFilePointer() - entry.toByteArray().length,
                            entry.getTimestamp());
                    MapValue oldValue = keyDir.get(entry.getKey());
                    if(oldValue == null || oldValue.getTimestamp() < value.getTimestamp())
                        keyDir.put(entry.getKey(), value);
                    entry = binaryReader.readEntry(randomAccessFile);
                }
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File hintToRegular(String hintFileName){
        return new File(hintFileName.substring(0, hintFileName.lastIndexOf('.')) + ".cask");
    }

    private String getNameWithoutExtension(File caskFile) {
        return caskFile.getName().substring(0, caskFile.getName().lastIndexOf('.'));
    }

    public static void main(String[] args) throws IOException {
        RecoverBitcask recoverBitcask = new RecoverBitcask("bitcask");
        recoverBitcask.recover();
    }

}
