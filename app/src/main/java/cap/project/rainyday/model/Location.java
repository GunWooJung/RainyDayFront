package cap.project.rainyday.model;

import java.time.LocalDateTime;

public class Location {
    private String name;
    private double lat;
    private double lng;
    private String time;
    private String departTime;
    private String destTime;

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public String getDestTime() {
        return destTime;
    }

    public void setDestTime(String destTime) {
        this.destTime = destTime;
    }

    public int nx;
    public int ny;
    public String regioncode;

    public int getNx() {
        return nx;
    }

    public void setNx(int nx) {
        this.nx = nx;
    }

    public void setNy(int ny) {
        this.ny = ny;
    }

    public void setRegioncode(String regioncode) {
        this.regioncode = regioncode;
    }

    public int getNy() {
        return ny;
    }

    public String getRegioncode() {
        return regioncode;
    }

    private int durationMin = 0;

    // Getter and setter methods for all fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }


    public int getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
