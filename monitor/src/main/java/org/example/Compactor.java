package org.example;

import org.example.model.Entry;
import org.example.model.MapValue;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Compactor implements Runnable{
    private final ConcurrentHashMap<Long, MapValue> keyDir;
    private final static String bitcaskDir = "bitcask";
    private final static String currentFileName = "current.bitcask";

    public Compactor(ConcurrentHashMap<Long, MapValue> keyDir) {
        this.keyDir = keyDir;
    }

    @Override
    public void run() {
        Map<Long, Entry> latest = new HashMap<>();
        File folder = new File(bitcaskDir);
        List<File> files = new LinkedList<>();
        // Get finished files to be operated on
        for(File file: Objects.requireNonNull(folder.listFiles())){
            if(file.isFile() && !file.getName().equals(currentFileName)){
                files.add(file);
                System.out.println("File " + file.getName());
            }
        }


    }

    public static void main(String[] args) {
        Compactor c = new Compactor(null);
        c.run();
    }


}
