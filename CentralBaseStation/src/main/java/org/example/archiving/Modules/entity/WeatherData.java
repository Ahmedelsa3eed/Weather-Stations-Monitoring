package org.example.archiving.Modules.entity;
import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Builder
public class WeatherData implements Serializable {
   
    private Integer station_id;
    private long s_no;
    private String battery_status;
    private long status_timestamp;
    private Weather weather;
}
