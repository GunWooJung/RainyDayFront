package cap.project.rainyday.weather;

import java.time.LocalDateTime;

public class MidTermWeather {
    public String wfAm;
    public String wfPm;
    public String rnStAm;
    public String rnStPm;
    public LocalDateTime base;
    public LocalDateTime fcst;

    public MidTermWeather(){}
    public MidTermWeather(String _wfAm, String _wfPm, String _rnStAm, String _rnStPm)
    {
        wfAm = _wfAm;//날씨
        wfPm = _wfPm;
        rnStAm = _rnStAm;//강수확률
        rnStPm = _rnStPm;
    }
}
