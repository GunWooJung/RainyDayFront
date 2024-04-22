package cap.project.rainyday.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
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

        public static class ScheduleComparator implements Comparator<Schedule> {
                @Override
                public int compare(Schedule s1, Schedule s2) {
                        LocalDateTime currentTime = LocalDateTime.now();
                        LocalDateTime time1 = LocalDateTime.parse(s1.getDepartTime());
                        LocalDateTime time2 = LocalDateTime.parse(s2.getDepartTime());

                        // 현재 시간과의 차이 계산
                        long diff1 = Duration.between(currentTime, time1).toMillis();
                        long diff2 = Duration.between(currentTime, time2).toMillis();

                        // 가까운 시간 순으로 정렬
                        if(diff1 == diff2){
                                return 0;
                        }
                        else if (diff1 > 0 && diff2 > 0) {
                                if(diff1 < diff2) return -1;
                                else return 1;
                        }
                        else if (diff1 < 0 && diff2 > 0) {
                                return 1;
                        }
                        else if (diff1 > 0 && diff2 < 0) {
                                return -1;
                        }
                        else if (diff1 < 0 && diff2 < 0) {
                                if(diff1 < diff2) return 1;
                                else return -1;
                        }
                        else {
                                return 0;
                        }
                }
        }
}