package org.example.model;

public class Message {
    private Long stationId;
    private Long statusNumber;
    private String batteryStatus;
    private Long statusTimestamp;
    private Weather weather;

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public Long getStatusNumber() {
        return statusNumber;
    }

    public void setStatusNumber(Long statusNumber) {
        this.statusNumber = statusNumber;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public Long getStatusTimestamp() {
        return statusTimestamp;
    }

    public void setStatusTimestamp(Long statusTimestamp) {
        this.statusTimestamp = statusTimestamp;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
