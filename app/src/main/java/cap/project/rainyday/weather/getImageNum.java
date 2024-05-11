package cap.project.rainyday.weather;

import cap.project.rainyday.weather.WeatherCode;

public class getImageNum {
    static public int getNum(WeatherCode code)
    {
        if (code == WeatherCode.sunny)
            return 0;
        if (code == WeatherCode.cloud || code == WeatherCode.cloudy)
            return 1;
        if (code == WeatherCode.cloudRain || code == WeatherCode.cloudyRain)
            return 2;
        if (code == WeatherCode.cloudSnow || code == WeatherCode.cloudySnow)
            return 3;
        if (code == WeatherCode.cloudRS || code == WeatherCode.cloudyRS)
            return 4;
        if (code == WeatherCode.cloudShower || code == WeatherCode.cloudyShower)
            return 5;
        return 0;
    }
}