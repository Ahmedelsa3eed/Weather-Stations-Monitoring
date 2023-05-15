package org.example;


/**
 * With each write, a new entry is appended to the active file.
 * */
public class Entry {
    String key;
    String value;

    public Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
