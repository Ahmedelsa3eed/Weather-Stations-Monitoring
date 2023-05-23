package org.example.archiving;

import org.apache.avro.generic.GenericRecord;
import org.example.io.AvroIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageArchiverTest {

    // @Test
    // void testArchivingMessage() throws IOException {
    //     MessageArchiver archiver = new MessageArchiver();
    //     AvroIO avroIO = new AvroIO();

    //     GenericRecord record = avroIO.readAvroRecord();

    //     archiver.archiveMessage(record);
    //     List<GenericRecord> records = archiver.readMessages();

    //     assertEquals(record, records.get(0));
    // }

}