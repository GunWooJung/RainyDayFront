package cap.project.rainyday.weather;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShortTermForeacast {
    String type;
    int tmp; // 온도
    int pop; // 강수확률
    String pcp; // 강수량
    LocalDateTime fcst; //예보
    LocalDateTime base; // 발표
    Location location;
    String serviceKey = "QD7zanvK2jd%2FgLcdz2nBxdtEq6Fysy0gY9Mz4YvPT7XizIYXPkOPvMwSeTHG%2BCDhQcuI5g%2BfE%2FU3u3NIY7lsFQ%3D%3D";

    public ShortTermForeacast() {
        type = null;
    }

    public ShortTermForeacast(Location _location) {
        location = _location;
        type = null;
    }

    public int getTmp() {
        return tmp;
    }

    public int getPop() {
        return pop;
    }

    public String getPcp() {
        return pcp;
    }

    public void setType(String _type) {
        type = _type;
    }

    public void setFcst(LocalDateTime _fcst) {
        fcst = _fcst;
    }

    public void setBase(LocalDateTime _base) {
        base = _base;
    }

    public void setLocation(Location _location) {
        location = _location;
    }

    private JSONObject getJson(String api) {
        JSONObject responseJson = null;
        try {
            URL url = new URL(api);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            responseJson = new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(api);
        }
        return responseJson;
    }

    public ShortTermWeather[] getWeather() throws JSONException {
        base = LocalDateTime.now();
        if (base.getHour() % 3 != 2) {
            base = base.minusHours(base.getHour() % 3 + 1);
        } else if (base.getMinute() < 10) {
            base = base.minusHours(3);
        }
        base = base.withMinute(0);
        int num = 9999;
        int pageNo = 1;
        String api;
        JSONObject responseJson;
        JSONObject tempJson;
        ShortTermWeather weather[];
        Vector<ShortTermWeather> weatherVector = new Vector<ShortTermWeather>();
        ShortTermWeather tempWeather;
        JSONArray item;
        api = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&base_date=" + base.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "&base_time=" + base.format(DateTimeFormatter.ofPattern("HHmm")) + "&nx=" + location.nx + "&ny=" + location.ny;
        responseJson = getJson(api);
        item = responseJson.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
        tempJson = item.getJSONObject(0);
        tempWeather = new ShortTermWeather();
        tempWeather.fcst = LocalDateTime.of(Integer.parseInt((String) tempJson.get("fcstDate")) / 10000,
                (Integer.parseInt((String) tempJson.get("fcstDate")) % 10000) / 100,
                (Integer.parseInt((String) tempJson.get("fcstDate")) % 10000) % 100,
                Integer.parseInt((String) tempJson.get("fcstTime")) / 100, 0);
        tempWeather.base = base;
            for (int i = 0; i < num; ++i) {
                try {
                    tempJson = item.getJSONObject(i);
                } catch (Exception e) {
                    break;
                }
                if (!tempWeather.fcst.format(DateTimeFormatter.ofPattern("HHmm"))
                        .equals(tempJson.get("fcstTime"))) {
                    weatherVector.add(tempWeather);
                    tempWeather = new ShortTermWeather();
                    tempWeather.fcst = LocalDateTime.of(Integer.parseInt((String) tempJson.get("fcstDate")) / 10000,
                            (Integer.parseInt((String) tempJson.get("fcstDate")) % 10000) / 100,
                            (Integer.parseInt((String) tempJson.get("fcstDate")) % 10000) % 100,
                            Integer.parseInt((String) tempJson.get("fcstTime")) / 100, 0);
                    tempWeather.base = base;
                }
                if (tempJson.get("category").equals("TMP"))
                    tempWeather.tmp = Integer.valueOf((String) (tempJson.get("fcstValue")));
                if (tempJson.get("category").equals("POP"))
                    tempWeather.pop = Integer.valueOf((String) (tempJson.get("fcstValue")));
                if (tempJson.get("category").equals("PCP"))
                    tempWeather.pcp = (String) (tempJson.get("fcstValue"));
        }
        weatherVector.add(tempWeather);
        weather = new ShortTermWeather[weatherVector.size()];
        for (int i = 0; i < weatherVector.size(); ++i) {
            weather[i] = weatherVector.get(i);
        }
        return weather;
    }
}
