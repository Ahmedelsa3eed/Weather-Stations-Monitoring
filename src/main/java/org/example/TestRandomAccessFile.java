package org.example;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TestRandomAccessFile {
    public static void main(String[] args) throws IOException {
        String data = "Hello, world!";
        File file = new File("example.txt");

        // Create a RandomAccessFile and write the data to the file
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.write(data.getBytes());

        // write another string
        String data2 = "read me!";
        raf.write(data2.getBytes());

        // Get the position of the written data
        long position = raf.getFilePointer() - data2.length();

        raf.seek(position);

        // Print the contents of the file
        byte[] buffer = new byte[(int) data2.length()];
        raf.read(buffer);
        System.out.println(new String(buffer));

        // Close the RandomAccessFile
        raf.close();

        // Print the position of the written data
        System.out.println("Position of written data: " + position);
    }
}
