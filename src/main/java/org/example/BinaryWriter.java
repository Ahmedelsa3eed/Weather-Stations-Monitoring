package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BinaryWriter {
    public void writeEntry(Entry entry, String activeFile) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(activeFile));

            //write the Entry in Pascal-style format
            writePascalInteger(out, entry.keySize);
            writePascalInteger(out, entry.valueSize);
            writePascalInteger(out, entry.key);
            writePascalInteger(out, entry.value);

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePascalString(DataOutputStream out, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        out.writeByte(bytes.length);
        out.write(bytes);
    }

    /**
     *  writes the int value in little-endian format with the appropriate length prefix
     * */
    private void writePascalInteger(DataOutputStream out, int value) throws IOException {
        if (value >= -127 && value <= 127) {
            out.writeByte(1);
            out.writeByte(value);
        } else if (value >= -32767 && value <= 32767) {
            out.writeByte(2);
            out.writeShort(Short.reverseBytes((short)value));
        } else {
            out.writeByte(4);
            out.writeInt(Integer.reverseBytes(value));
        }
    }
}
