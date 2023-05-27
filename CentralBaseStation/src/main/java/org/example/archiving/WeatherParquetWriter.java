// package org.example.archiving;

// import org.apache.avro.Schema;
// import org.apache.avro.generic.GenericRecord;
// import org.apache.hadoop.fs.Path;
// import org.apache.parquet.avro.AvroParquetWriter;
// import org.apache.parquet.hadoop.ParquetFileReader;
// import org.apache.parquet.hadoop.ParquetWriter;
// import org.apache.hadoop.conf.Configuration;
// import org.apache.parquet.hadoop.util.HadoopInputFile;
// import org.apache.parquet.schema.MessageType;
// import org.example.io.AvroIO;

// import java.io.IOException;


// public class WeatherParquetWriter {
//     ParquetWriter<GenericRecord> writer;

//     public WeatherParquetWriter(Path outputPath, Schema schema) throws IOException {
//         try (ParquetWriter<GenericRecord> writer = AvroParquetWriter
//                 .<GenericRecord>builder(outputPath)
//                 .withSchema(schema)
//                 .withConf(new Configuration())
//                 .build()) {
//             this.writer = writer;
//         }
//     }

//     public void write(GenericRecord record) throws IOException {
//         writer.write(record);
//     }

//     public void closeWriter() throws IOException {
//         writer.close();
//     }

//     public static void isCompaitable(Path outputPath, Schema avroSchema) throws IOException {
//         // Retrieve the Parquet schema
//         Configuration conf = new Configuration();
//         try (ParquetFileReader reader = ParquetFileReader.open(HadoopInputFile.fromPath(outputPath, conf))) {
//             MessageType parquetSchema = reader.getFooter().getFileMetaData().getSchema();
//             // Compare the schemas for compatibility
// //            String avroSchemaString = "{\"type\":\"record\",\"name\":\"WeatherMessage\",\"fields\":[{\"name\":\"stationId\",\"type\":\"long\"},{\"name\":\"serialNumber\",\"type\":\"long\"},{\"name\":\"batteryStatus\",\"type\":\"string\"},{\"name\":\"statusTimestamp\",\"type\":\"long\"},{\"name\":\"weather\",\"type\":{\"type\":\"record\",\"name\":\"Weather\",\"fields\":[{\"name\":\"humidity\",\"type\":\"int\"},{\"name\":\"temperature\",\"type\":\"int\"},{\"name\":\"windSpeed\",\"type\":\"int\"}]}}]}";
//             System.out.println(parquetSchema.toString());
//             boolean isCompatible = avroSchema.equals(new Schema.Parser().parse(parquetSchema.toString()));

//             if (isCompatible) {
//                 System.out.println("Avro schema is compatible with Parquet schema.");
//             } else {
//                 System.out.println("Avro schema is not compatible with Parquet schema.");
//             }
//         }
//     }

//     public static void main(String[] args) throws IOException {
//         Path archivePath = new Path("Archive/weather.parquet");
//         AvroIO avroIO = new AvroIO();
//         Schema schema = avroIO.getSchema();
//         WeatherParquetWriter.isCompaitable(archivePath, schema);
//     }
// }
