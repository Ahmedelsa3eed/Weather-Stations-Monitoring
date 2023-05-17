package org.example;

public interface BitcaskIF {
    public String get(String key);
    public void put(String key, String value);
    public void merge();
}
