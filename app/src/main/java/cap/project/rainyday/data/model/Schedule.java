package cap.project.rainyday.data.model;

import androidx.annotation.NonNull;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class Schedule {
    private long scheduleId;
    private String title;

    public Schedule(long id, String title) {
        this.scheduleId = id;
        this.title =title;
    }

    public Schedule() {
    }

    public void setScheduleId(long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getScheduleId() {
        return scheduleId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}