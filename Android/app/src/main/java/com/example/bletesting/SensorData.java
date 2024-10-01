package com.example.bletesting;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SensorData {
    private ArrayList<String> temperatureData;
    private ArrayList<String> pressureData;
    private ArrayList<String> altitudeData;
    private ArrayList<String> uvIndexData;

    private ArrayList<Date> time = new ArrayList<Date>();
    private ArrayList<String> readingTimesFormatted;

    private String temperature;
    private String pressure;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    private String altitude;
    private String uvIndex;


    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    public SensorData(ArrayList<String> temperatureData, ArrayList<String> pressureData, ArrayList<String> altitudeData, ArrayList<String> uvIndexData,
                      String temperature, String pressure, String altitude, String uvIndex) {
        this.temperatureData = temperatureData;
        this.pressureData = pressureData;
        this.altitudeData = altitudeData;
        this.uvIndexData = uvIndexData;
        this.temperature = temperature;
        this.pressure = pressure;
        this.altitude = altitude;
        this.uvIndex = uvIndex;
//        this.timestamp = timestamp;
    }


    public ArrayList<String> getTemperatureData() {
        return temperatureData;
    }

    public void setTemperatureData(ArrayList<String> temperatureData) {
        this.temperatureData = temperatureData;
    }

    public ArrayList<String> getPressureData() {
        return pressureData;
    }

    public void setPressureData(ArrayList<String> pressureData) {
        this.pressureData = pressureData;
    }

    public ArrayList<String> getAltitudeData() {
        return altitudeData;
    }

    public void setAltitudeData(ArrayList<String> altitudeData) {
        this.altitudeData = altitudeData;
    }

    public ArrayList<String> getUvIndexData() {
        return uvIndexData;
    }

    public void setUvIndexData(ArrayList<String> uvIndexData) {
        this.uvIndexData = uvIndexData;
    }

    public ArrayList<Date> getTime() {
        return time;
    }

    public void setTime(ArrayList<Date> time) {
        this.time = time;
    }

    public ArrayList<String> getReadingTimesFormatted() {
        return readingTimesFormatted;
    }

    public void setReadingTimesFormatted(ArrayList<String> readingTimesFormatted) {
        this.readingTimesFormatted = readingTimesFormatted;
    }

    public SimpleDateFormat getDateFormatter() {
        return dateFormatter;
    }

    public void setDateFormatter(SimpleDateFormat dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public String  getTemperatureAvg() {
        return String.valueOf(calculateAverage(temperatureData));
    }

    public String getPressureAvg() {
        return String.valueOf(calculateAverage(pressureData));
    }

    public String getAltitudeAvg() {
        return String.valueOf(calculateAverage(altitudeData));
    }

    public String getUvIndexAvg() {
        return String.valueOf(calculateAverage(uvIndexData));
    }


    // Helper method to calculate average
    private float calculateAverage(ArrayList<String> data) {
        float sum = 0;
        if (!data.isEmpty()) {
            for (String num : data) {
                sum += Float.parseFloat(num);
            }
            return sum / data.size();
        }
        System.out.println(sum);
        return sum;
    }
}
