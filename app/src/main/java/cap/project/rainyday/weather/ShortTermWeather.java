package cap.project.rainyday.weather;

import java.time.LocalDateTime;

public class ShortTermWeather {
    public int tmp; // 온도
    public int pop; // 강수확률
    public String pcp; // 강수량
    public LocalDateTime fcst; //예보
    public LocalDateTime base; // 발표
    public WeatherCode wCode;
}
