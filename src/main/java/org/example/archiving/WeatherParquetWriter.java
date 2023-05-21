package org.example.archiving;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;

import java.io.IOException;


public class WeatherParquetWriter {
    ParquetWriter<GenericRecord> writer;

    public WeatherParquetWriter(Path outputPath, Schema schema) throws IOException {
        try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
                .<GenericRecord>builder(outputPath)
                .withSchema(schema)
                .withWriteMode(ParquetFileWriter.Mode.OVERWRITE)
                .build()) {
            this.writer = writer;
        }
    }

    public void write(GenericRecord record) throws IOException {
        writer.write(record);
    }

    public void closeWriter() throws IOException {
        writer.close();
    }
}
