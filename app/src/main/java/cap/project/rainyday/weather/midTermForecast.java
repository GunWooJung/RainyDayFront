package cap.project.rainyday.weather;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cap.project.rainyday.weather.MidTermWeather;

public class midTermForecast {

    String type;
    LocalDateTime fcst; //예보
    LocalDateTime base; // 발표
    Location location;
    String serviceKey = "QD7zanvK2jd%2FgLcdz2nBxdtEq6Fysy0gY9Mz4YvPT7XizIYXPkOPvMwSeTHG%2BCDhQcuI5g%2BfE%2FU3u3NIY7lsFQ%3D%3D";

    public midTermForecast(LocalDateTime _base, Location _location) {
        base = _base;
        location = _location;
        type = null;
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

    public MidTermWeather[] getWeather_midTerm_get_all() {
        base = LocalDateTime.now();
        if (base.getHour() >= 18) {
            base = base.withHour(18).withMinute(0);
        } else if (base.getHour() < 6) {
            base = base.minusDays(1).withHour(18).withMinute(0);
        } else {
            base = base.withHour(6).withMinute(0);
        }
        base = base.withMinute(0);
        MidTermWeather[] weathr = new MidTermWeather[8];
        JSONObject responseJson = null;
        try {
            type = "중기육상예보조회";
            int num = 12;
            int pageNo = 1;
            String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + location.regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            Log.d("qq", api);
            responseJson = getJson(api);
            JSONObject item = responseJson.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item").getJSONObject(0);
            for (int i = 0; i < 5; ++i) {
                weathr[i] = new MidTermWeather();
                weathr[i].base = base;
                weathr[i].rnStAm = item.getString("rnSt" + (i + 3) + "Am");
                weathr[i].rnStPm = item.getString("rnSt" + (i + 3) + "Pm");
                weathr[i].wfAm = item.getString("wf" + (i + 3) + "Am");
                weathr[i].wfPm = item.getString("wf" + (i + 3) + "Pm");
            }
            try {
                for (int i = 5; i < 8; ++i) {
                    weathr[i] = new MidTermWeather();
                    weathr[i].rnStAm = item.getString("rnSt" + (i + 3));
                    weathr[i].rnStPm = item.getString("rnSt" + (i + 3));
                    weathr[i].wfAm = item.getString("wf" + (i + 3));
                    weathr[i].wfPm = item.getString("wf" + (i + 3));
                }
            } catch (Exception e) {}
        } catch (Exception e) {
            System.err.println(responseJson);
        }
        return weathr;
    }

    private MidTermWeather getWeatherMidTerm2Date(LocalDateTime date) throws JSONException {
        int num = 12;
        int pageNo = 1;
        String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + location.regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        JSONObject responseJson = getJson(api);
        JSONObject item = responseJson.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item").getJSONObject(0);
        System.err.println(ChronoUnit.DAYS.between(base, date));
        System.err.println(date.format(DateTimeFormatter.ofPattern("a")));
        MidTermWeather midTermWeather = new MidTermWeather();
        return midTermWeather;
    }

    private void getWeatherMidTerm3() {
        type = "중기기온조회";
        int num = 12;
        int pageNo = 1;
        String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + location.regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        JSONObject responseJson = getJson(api);
        JSONObject temp;
        System.err.println(responseJson);
    }

    private void getWeatherMidTerm4() {
        type = "중기해상예보조회";
        int num = 12;
        int pageNo = 1;
        String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidSeaFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + location.regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        JSONObject responseJson = getJson(api);
        JSONObject temp;
        System.err.println(responseJson);
    }
}
