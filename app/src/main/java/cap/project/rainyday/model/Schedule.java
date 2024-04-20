package cap.project.rainyday.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class Schedule {

        private long scheduleId;

        private String title;

        private User userId;

        private String departName;

        private String departTime;

        private String hashTag;
        public static Schedule fromJson(String jsonString) {
                Gson gson = new Gson();
                return gson.fromJson(jsonString, Schedule.class);
        }

        public long getScheduleId() {
                return scheduleId;
        }

        public void setScheduleId(long scheduleId) {
                this.scheduleId = scheduleId;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getDepartName() {
                return departName;
        }

        public void setDepartName(String departName) {
                this.departName = departName;
        }

        public String getDepartTime() {
                return departTime;
        }

        public void setDepartTime(String departTime) {
                this.departTime = departTime;
        }

        public String getHashTag() {
                return hashTag;
        }

        public void setHashTag(String hashTag) {
                this.hashTag = hashTag;
        }
}