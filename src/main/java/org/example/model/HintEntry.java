package org.example.model;

public interface HintEntry {
    public byte[] toByteArray();

    private void fromByteArray(byte[] bytes){};

    public long getValuePosition();

    public long getTimestamp();

    public long getKey();

}
