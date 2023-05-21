package org.example.archiving;

import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WeatherParquetReader {
    ParquetReader<GenericRecord> reader;

    public WeatherParquetReader(Path inputPath) {
        createReader(inputPath);
    }

    void createReader(Path inputPath) {
        try (ParquetReader<GenericRecord> reader = AvroParquetReader
                .<GenericRecord>builder(inputPath)
                .build()) {
            this.reader = reader;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GenericRecord> read() throws IOException{
        List<GenericRecord> records = new ArrayList<>();
        try {
            for (GenericRecord value = reader.read(); value != null; value = reader.read()) {
                records.add(value);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return records;
    }
}
