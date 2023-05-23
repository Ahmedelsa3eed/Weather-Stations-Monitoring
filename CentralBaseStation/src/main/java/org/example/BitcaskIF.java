package org.example;

import org.apache.avro.generic.GenericRecord;

public interface BitcaskIF {
    public byte[] get(Long key);
    public void put(byte[] serializedMessage);
    public void merge();
}
