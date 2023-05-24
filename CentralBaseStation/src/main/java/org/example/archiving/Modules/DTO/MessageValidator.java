package org.example.archiving.Modules.DTO;

import java.time.LocalDateTime;

import org.example.archiving.Modules.entity.WeatherData;
import org.example.archiving.Modules.time_stamp.TimeStampHandler;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class MessageValidator {
    private final TimeStampHandler timeStampHandler;

    private boolean validateBattary(String battery){
        return !(battery.equalsIgnoreCase("low") 
        || battery.equalsIgnoreCase("high")
        ||battery.equalsIgnoreCase("medium"));
    }
    public boolean notValidate(WeatherData weatherData) {
      return  !timeStampHandler.isSameDay(weatherData.getStatus_timestamp(), LocalDateTime.now())
      ||weatherData.getS_no() <= 0 
      ||validateBattary(weatherData.getBattery_status());
         
    }
    
}
