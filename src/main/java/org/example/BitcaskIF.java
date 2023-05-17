package org.example;

public interface BitcaskIF {
    public byte[] get(Long key);
    public void put(Long key, byte[] value);
    public void merge();
}
