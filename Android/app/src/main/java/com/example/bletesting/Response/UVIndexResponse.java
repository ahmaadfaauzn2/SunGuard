package com.example.bletesting.Response;

import java.util.List;

public class UVIndexResponse {
    private boolean ok;
    private double latitude;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public UVNow getNow() {
        return now;
    }

    public void setNow(UVNow now) {
        this.now = now;
    }

    private double longitude;
    private UVNow now;
    private List<UVForecast> forecast;

    // Getter and setter for all fields

    public static class UVNow {
        private String time;
        private double uvi;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getUvi() {
            return uvi;
        }

        public void setUvi(double uvi) {
            this.uvi = uvi;
        }
// Getter and setter
    }

    public List<UVForecast> getForecast() {
        return forecast;
    }

    public void setForecast(List<UVForecast> forecast) {
        this.forecast = forecast;
    }

    public static class UVForecast {
        private String time;
        private double uvi;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getUvi() {
            return uvi;
        }

        public void setUvi(double uvi) {
            this.uvi = uvi;
        }
// Getter and setter
    }
}
