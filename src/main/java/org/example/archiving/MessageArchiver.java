package org.example.archiving;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.example.io.AvroIO;

import java.io.IOException;
import java.util.List;


public class MessageArchiver {
    private Schema schema;
    private WeatherParquetWriter writer;
    private Path archivePath = new Path("Archive/weather.parquet");

    public MessageArchiver() throws IOException {
        AvroIO avroIO = new AvroIO();
        schema = avroIO.getSchema();
        writer = new WeatherParquetWriter(archivePath, schema);
    }

    public void archiveMessage(GenericRecord record) throws IOException {
        writer.write(record);
        writer.closeWriter(); // TODO: Close after exceeding a certain size
    }

    /**
     * For testing purposes
     * */
    public List<GenericRecord> readMessages() throws IOException {
        WeatherParquetReader reader = new WeatherParquetReader(archivePath);
        return reader.read();
    }

    private void partition() {
        // TODO Partition the data by year, month, and day
        // Write the data to a partitioned parquet file
//        URI partition = URI.create("2020-01-01T00");
//        String path = partition.getPath();
//        File parentFolder = new File(path);
//        parentFolder.mkdirs();
//        File partitionFile = new File(parentFolder, "parquet0000");
//        Path filePath = new Path(partitionFile.toURI());
    }

    public static void main(String[] args) throws IOException {
//        MessageArchiver archiver = new MessageArchiver();
//        AvroIO avroIO = new AvroIO();
//        GenericRecord record = avroIO.readAvroRecord();
//
//        archiver.archiveMessage(record);

//        List<GenericRecord> records = archiver.readMessages();
//
//        if (record == records.get(0)) {
//            System.out.println("Success!");
//        } else {
//            System.out.println("Failure!");
//        }
    }
}
