package cap.project.rainyday.model;

import cap.project.rainyday.weather.WeatherCode;

public class Weather {

    private String location;

    private String time;

    public int index = 99999;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int type; // 0 = 단기, 1 = 중기 , 2 = 10일이후, 3= 현재보다 이전, 4 = 조회불가

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getRainyPercent() {
        return rainyPercent;
    }

    public void setRainyPercent(String rainyPercent) {
        this.rainyPercent = rainyPercent;
    }

    public WeatherCode getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(WeatherCode weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public String getRainyAmount() {
        return rainyAmount;
    }

    public void setRainyAmount(String rainyAmount) {
        this.rainyAmount = rainyAmount;
    }

    private String temperature;

    private String rainyPercent;

    private WeatherCode weatherInfo;

    private String rainyAmount;
}
