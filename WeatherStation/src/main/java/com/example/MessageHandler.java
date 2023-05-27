package com.example;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;



public class MessageHandler {
    Schema schema;
    public MessageHandler(Schema schema){
        this.schema = schema;
    }
    public byte[] genericRecordToByteArray(GenericData.Record record, Schema schema) throws IOException {

        DatumWriter<GenericData.Record> datumWriter = new SpecificDatumWriter<>(schema);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(baos, null);
        datumWriter.write(record, encoder);
        encoder.flush();
        byte[] byteArray = baos.toByteArray();
        baos.close();
        return byteArray;
    }
    GenericData.Record createMessage(Schema schema,int messageCount,int station_id){
        GenericData.Record weatherData = new GenericData.Record(schema); 
        Random random = new Random();
        weatherData.put("station_id", station_id);
        weatherData.put("s_no", messageCount);
        String battery = randomBattery();
        
        weatherData.put("battery_status", battery);
        weatherData.put("status_timestamp", System.currentTimeMillis());

        GenericData.Record weather = new GenericData.Record(schema.getField("weather").schema());
        weather.put("humidity", random.nextInt(101));
        // weather.put("humidity", 90);
        weather.put("temperature", random.nextInt(40 + 20) - 20);
        weather.put("wind_speed", random.nextInt(120 - 0 + 1) + 0);

        weatherData.put("weather", weather);
        return weatherData;
    }
    private String randomBattery(){
        Random random = new Random();
        Double p  = random.nextDouble();
        if( p < 0.3 ) {  //you might want to cache the Random instance
            return "Low";
        }
        else if(p < .6){
            return "High";
        }
        else{
            return "Medium";
        }
    }
    GenericRecord getWeather(byte[] avroData) throws IOException{
        Decoder decoder = DecoderFactory.get().binaryDecoder(avroData, null);
        DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(schema);
        GenericRecord record = datumReader.read(null, decoder);
        return record;
    }
    public Record createMessage(Schema avroschema, int place,int messageCount, Map<String, Object> jsonResponse) {
        GenericData.Record weatherData = new GenericData.Record(schema); 
        weatherData.put("station_id", place);
        weatherData.put("s_no", messageCount);
        String battery = randomBattery();
        weatherData.put("battery_status", battery);
        long cur = System.currentTimeMillis();
        weatherData.put("status_timestamp", cur);

        GenericData.Record weather = new GenericData.Record(schema.getField("weather").schema());
        LocalTime currentTime = LocalTime.now();
        LinkedHashMap<String,ArrayList<Integer>>  hourly=(LinkedHashMap<String,ArrayList<Integer>>) jsonResponse.get("hourly");
        System.out.println();
        System.out.println();
        ArrayList<Integer> humidities = hourly.get("relativehumidity_2m");
        // Get the current hour from the LocalTime
        int idx = currentTime.getHour();
        weather.put("humidity", humidities.get(idx));
        LinkedHashMap<String, Integer> linkedHashMap = (LinkedHashMap<String, Integer>) jsonResponse.get("current_weather");
        weather.put("temperature", linkedHashMap.get("temperature"));
        weather.put("wind_speed", linkedHashMap.get("windspeed"));
        weatherData.put("weather", weather);
        return weatherData;
    }
}
