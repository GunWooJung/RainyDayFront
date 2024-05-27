package cap.project.rainyday.model;

import com.google.gson.Gson;

import java.util.List;

public class ScheduleEnroll {

    private long scheduleId;

    public long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    private String title;
    private String hashTag;
    private List<Location> locations;

    // Getter and setter methods for all fields
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

}
