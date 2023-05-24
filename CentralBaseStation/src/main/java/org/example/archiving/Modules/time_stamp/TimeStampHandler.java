package org.example.archiving.Modules.time_stamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class TimeStampHandler {
    private LocalDateTime getLocalTimeFromTimeStamp(long timeStamp){
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault());
        return date;
    }
    public boolean isSameDay(long time_stamp, LocalDateTime localDateTime2){
        // SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        LocalDateTime localDateTime1 = getLocalTimeFromTimeStamp(time_stamp); 
        String str1 = localDateTime1.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)); 
        String str2 = localDateTime2.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)); 
        return str1.equals(str2);
    }
    public boolean isSameDay(LocalDateTime localDateTime1, LocalDateTime localDateTime2){
        // SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        String str1 = localDateTime1.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)); 
        String str2 = localDateTime2.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)); 
        return str1.equals(str2);
    }
}
