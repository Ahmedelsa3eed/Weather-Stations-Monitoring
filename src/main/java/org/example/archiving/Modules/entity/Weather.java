package org.example.archiving.Modules.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Weather implements Serializable {
    private int humidity;
    private int temperature;
    private int wind_speed;
}