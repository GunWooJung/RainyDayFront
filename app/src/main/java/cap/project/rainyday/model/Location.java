package cap.project.rainyday.model;

import java.time.LocalDateTime;

public class Location {
    private String name;

    private double lat;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public LocalDateTime getDepartTime() {
        return departTime;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setDepartTime(LocalDateTime departTime) {
        this.departTime = departTime;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    private double lng;

    private LocalDateTime departTime;

    private int durationMin = 0;

}
