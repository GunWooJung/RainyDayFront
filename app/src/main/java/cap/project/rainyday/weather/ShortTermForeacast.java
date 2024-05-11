package cap.project.rainyday.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import cap.project.rainyday.weather.GetJson;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.WeatherCode;

public class ShortTermForeacast {
    Location location;
    String serviceKey = "QD7zanvK2jd%2FgLcdz2nBxdtEq6Fysy0gY9Mz4YvPT7XizIYXPkOPvMwSeTHG%2BCDhQcuI5g%2BfE%2FU3u3NIY7lsFQ%3D%3D";

    public ShortTermForeacast(Location _location) {
        location = _location;
    }

    public void setLocation(Location _location) {
        location = _location;
    }

    public ShortTermWeather[] getWeather() {
        LocalDateTime base = getTime();
        Vector<ShortTermWeather> weatherVector = new Vector<ShortTermWeather>();
        String api = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=9999&pageNo=1&base_date=" + base.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "&base_time=" + base.format(DateTimeFormatter.ofPattern("HHmm")) + "&nx=" + location.nx + "&ny=" + location.ny;
        JsonObject responseJson = GetJson.getJson(api);
        JsonArray item = responseJson.getAsJsonObject("response").getAsJsonObject("body").getAsJsonObject("items").getAsJsonArray("item");
        JsonObject tempJson = item.get(0).getAsJsonObject();
        ShortTermWeather tempWeather = getNewShortTermForecast(tempJson, base);
        int sky = 0;
        int pty = 0;
        for (int i = 0; i < 999; ++i) {
            try {
                tempJson = item.get(i).getAsJsonObject();
            } catch (Exception e) {
                break;
            }
            if (!tempWeather.fcst.format(DateTimeFormatter.ofPattern("HHmm")).equals(tempJson.get("fcstTime").getAsString())) {
                weatherVecotrADD(weatherVector, tempWeather, sky, pty);
                sky = 0;
                pty = 0;
                weatherVector.add(tempWeather);
                tempWeather = getNewShortTermForecast(tempJson, base);
            }
            if (tempJson.get("category").getAsString().equals("TMP"))
                tempWeather.tmp = tempJson.get("fcstValue").getAsInt();
            else if (tempJson.get("category").getAsString().equals("POP"))
                tempWeather.pop = tempJson.get("fcstValue").getAsInt();
            else if (tempJson.get("category").getAsString().equals("PCP"))
                tempWeather.pcp = tempJson.get("fcstValue").getAsString();
            else if (tempJson.get("category").getAsString().equals("SKY"))
                sky = tempJson.get("fcstValue").getAsInt();
            else if (tempJson.get("category").getAsString().equals("PTY"))
                pty = tempJson.get("fcstValue").getAsInt();
        }
        weatherVecotrADD(weatherVector, tempWeather, sky, pty);
        ShortTermWeather weather[] = new ShortTermWeather[weatherVector.size()];
        for (int i = 0; i < weatherVector.size(); ++i) {
            weather[i] = weatherVector.get(i);
        }
        return weather;
    }

    private LocalDateTime getTime() {
        LocalDateTime base = LocalDateTime.now();
        if (base.getHour() % 3 != 2) {
            base = base.minusHours(base.getHour() % 3 + 1);
        } else if (base.getMinute() < 10) {
            base = base.minusHours(3);
        }
        base = base.withMinute(0);
        return base;
    }

    private ShortTermWeather getNewShortTermForecast(JsonObject tempJson, LocalDateTime base) {
        ShortTermWeather tempWeather = new ShortTermWeather();
        tempWeather.fcst = LocalDateTime.of(
                Integer.parseInt(tempJson.get("fcstDate").getAsString()) / 10000,
                (Integer.parseInt(tempJson.get("fcstDate").getAsString()) % 10000) / 100,
                (Integer.parseInt(tempJson.get("fcstDate").getAsString()) % 10000) % 100,
                Integer.parseInt(tempJson.get("fcstTime").getAsString()) / 100,
                0
        );
        tempWeather.base = base;
        return tempWeather;
    }

    private void weatherVecotrADD(Vector<ShortTermWeather> weatherVector, ShortTermWeather tempWeather, int sky, int pty) {
        if (sky == 0)
            tempWeather.wCode = WeatherCode.sunny;
        else if (sky == 3) {
            switch (pty) {
                case 0:
                    tempWeather.wCode = WeatherCode.cloud;
                    break;
                case 1:
                    tempWeather.wCode = WeatherCode.cloudRain;
                    break;
                case 2:
                    tempWeather.wCode = WeatherCode.cloudRS;
                    break;
                case 3:
                    tempWeather.wCode = WeatherCode.cloudSnow;
                    break;
                case 4:
                    tempWeather.wCode = WeatherCode.cloudShower;
                    break;
                default:
                    break;
            }
        } else {
            switch (pty) {
                case 0:
                    tempWeather.wCode = WeatherCode.cloudy;
                    break;
                case 1:
                    tempWeather.wCode = WeatherCode.cloudyRain;
                    break;
                case 2:
                    tempWeather.wCode = WeatherCode.cloudyRS;
                    break;
                case 3:
                    tempWeather.wCode = WeatherCode.cloudySnow;
                    break;
                case 4:
                    tempWeather.wCode = WeatherCode.cloudyShower;
                    break;
                default:
                    break;
            }
        }
        weatherVector.add(tempWeather);
    }
}
