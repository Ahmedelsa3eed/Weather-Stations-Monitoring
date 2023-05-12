package org.example;


/**
 * With each write, a new entry is appended to the active file.
 * */
public class Entry {
    int keySize;
    int valueSize;
    int key;
    int value;

    public Entry(int keySize, int valueSize, int key, int value) {
        this.keySize = keySize;
        this.valueSize = valueSize;
        this.key = key;
        this.value = value;
    }
}
