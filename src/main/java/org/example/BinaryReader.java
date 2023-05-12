package org.example;

import java.io.*;

public class BinaryReader {
    public void readEntry(String activeFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(activeFile));

            int num1 = readPascalInteger(in);
            int num2 = readPascalInteger(in);
            int num3 = readPascalInteger(in);
            int num4 = readPascalInteger(in);

            System.out.println(num1);
            System.out.println(num2);
            System.out.println(num3);
            System.out.println(num4);

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int readPascalInteger(DataInputStream in) throws IOException {
        byte length = in.readByte();
        int value;
        if (length == 1) {
            value = in.readByte();
        } else if (length == 2) {
            value = Short.reverseBytes(in.readShort());
        } else {
            value = Integer.reverseBytes(in.readInt());
        }
        return value;
    }
}
