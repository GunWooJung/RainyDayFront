package cap.project.rainyday.model;

import java.util.ArrayList;
import java.util.List;

public class DetailWeather {

    public int index;

    public int type;
    public String explain;

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public DetailWeather() {
        this.weather = new ArrayList<>();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public List<Weather> weather;
}
