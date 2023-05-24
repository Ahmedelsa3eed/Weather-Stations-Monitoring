package org.example.archiving.Modules.DTO;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import org.example.archiving.Modules.entity.WeatherData;
import org.example.archiving.Modules.entity.Weather;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class WeatherDataDTO {
    final Schema schema;

    public WeatherData map(byte[] data) throws IOException{
        if(data == null){
            return null;
        }
        else{
            GenericRecord avroWRecord = getWeather(data);
            GenericRecord avroWeather = ((GenericRecord) avroWRecord.get("weather"));
            Weather weather = Weather.builder()
            .humidity((int)avroWeather.get("humidity"))
            .temperature((int)avroWeather.get("temperature"))
            .wind_speed((int)avroWeather.get("wind_speed"))
            .build();
            WeatherData weatherData = WeatherData.builder()
            .weather(weather)
           
            .battery_status((String) avroWRecord.get("battery_status").toString())
            .s_no((Long)avroWRecord.get("s_no"))
            .station_id(((Long) avroWRecord.get("station_id")))
            .status_timestamp( (long) avroWRecord.get("status_timestamp"))
            .build();
            return weatherData;
        }
    }
    private GenericRecord getWeather(byte[] avroData) throws IOException{
        Decoder decoder = DecoderFactory.get().binaryDecoder(avroData, null);
        DatumReader<GenericRecord> datumReader = new SpecificDatumReader<>(schema);
        GenericRecord record = datumReader.read(null, decoder);
        return record;
    }
}
