package org.example.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteUtils {
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static int bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    public static long longFromCompressedBytes(byte[] bytes){
        byte[] fullBytes = new byte[8];
        System.arraycopy(bytes, 0, fullBytes, 8 - bytes.length, bytes.length);
        return bytesToLong(fullBytes);
    }

    public static byte[] compressLong(long l){
        int leadingZeros = Long.numberOfLeadingZeros(l);
        byte[] bytes = ByteUtils.longToBytes(l);
        return Arrays.copyOfRange(bytes, leadingZeros/8, 8);
    }
}
