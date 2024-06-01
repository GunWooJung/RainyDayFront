package cap.project.rainyday.model;

import com.google.gson.Gson;

import java.time.LocalDateTime;

public class AlarmMessage {

    private long alarmId;



    private String title;

    public long getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(long alarmId) {
        this.alarmId = alarmId;
    }

    public String getReceviedTime() {
        return receviedTime;
    }

    public void setReceviedTime(String receviedTime) {
        this.receviedTime = receviedTime;
    }

    private String receviedTime;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
