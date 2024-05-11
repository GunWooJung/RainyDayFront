package cap.project.rainyday.weather;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import cap.project.rainyday.weather.GetJson;
import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.WeatherCode;

public class midTermForecast {
    String type;
    LocalDateTime base; // 발표
    Location location;
    String serviceKey = new String("QD7zanvK2jd%2FgLcdz2nBxdtEq6Fysy0gY9Mz4YvPT7XizIYXPkOPvMwSeTHG%2BCDhQcuI5g%2BfE%2FU3u3NIY7lsFQ%3D%3D");

    public static JsonObject getJson(String api) {
        JsonObject responseJson = null;
        try {
            URI uri = new URI(api);
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            String jsonStr = sb.toString();

            // Gson을 사용하여 JSON 문자열을 JsonObject로 파싱
            Gson gson = new Gson();
            responseJson = gson.fromJson(jsonStr, JsonObject.class);
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(responseJson);
            System.err.println(api);
        }
        return responseJson;
    }

    public midTermForecast(Location _location)
    {
        location = _location;
        type = null;
    }

    public String getWeather_midTerm_text(String stnId) {
        //text 형식의 예보라 필요하지 않음
        //만약 사용한다면 stnId로 다른 코드가 필요
        base = LocalDateTime.now();
        if (base.getHour() < 6) {
            base = base.withHour(18).minusDays(1).withMinute(0);
        } else if (base.getHour() >= 6 && base.getHour() < 18) {
            base = base.withHour(6).withMinute(0);
        } else {
            base = base.withHour(18).withMinute(0);
        }
        type = "중기전망조회";
        int num = 1;
        String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidFcst?dataType=JSON&serviceKey=" + serviceKey + "&numOfRows=" + num + "pageNo=1&stnId=" + stnId + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        JsonObject responseJson = GetJson.getJson(api);
        return null;
                //(String) (((JsonObject) ((JsonArray) ((JsonObject) ((JsonObject) ((JsonObject) responseJson.get("response")).get("body")).get("items")).get("item")).get(0))).get("wfSv");
    }

    public MidTermWeather[] getWeather_midTerm_get_all() {
        // type = "중기육상예보조회";
        //강수 확률과 날씨(ex 구름많음 최대 10일)
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
        JsonObject responseJson = null;
        try {
            int num = 12;
            int pageNo = 1; // 1400의 예보라면 1페이지에 1500 2페이지에 1500식
            String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + location.regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            responseJson = GetJson.getJson(api);
            JsonObject item = (JsonObject) ((JsonArray) ((JsonObject) ((JsonObject) ((JsonObject) responseJson.get("response")).get("body")).get("items")).get("item")).get(0);
            for (int i = 0; i < 5; ++i) {
                weathr[i] = new MidTermWeather();
                weathr[i].base = base;
                weathr[i].rnStAm = item.get("rnSt" + (i + 3) + "Am").toString();
                weathr[i].rnStPm = item.get("rnSt" + (i + 3) + "Pm").toString();
                weathr[i].wfAm = getCodeMid(item.get("wf" + (i + 3) + "Am").toString());
                weathr[i].wfPm = getCodeMid(item.get("wf" + (i + 3) + "Pm").toString());
            }
            try {
                for (int i = 5; i < 8; ++i) {
                    weathr[i] = new MidTermWeather();
                    weathr[i].rnStAm = item.get("rnSt" + (i + 3)).toString();
                    weathr[i].rnStPm = item.get("rnSt" + (i + 3)).toString();
                    weathr[i].wfAm = getCodeMid(item.get("wf" + (i + 3)).toString());
                    weathr[i].wfPm = getCodeMid(item.get("wf" + (i + 3)).toString());
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            System.err.println(responseJson);
        }
        return weathr;
    }

    private WeatherCode getCodeMid(String weather) {
        if (weather.equals("맑음"))
            return WeatherCode.sunny;
        if (weather.equals("구름많음"))
            return WeatherCode.cloud;
        if (weather.equals("구름많고 비"))
            return WeatherCode.cloudRain;
        if (weather.equals("구름많고 눈"))
            return WeatherCode.cloudSnow;
        if (weather.equals("구름많고 비/눈"))
            return WeatherCode.cloudRS;
        if (weather.equals("구름많고 소나기"))
            return WeatherCode.cloudShower;
        if (weather.equals("흐림"))
            return WeatherCode.cloudy;
        if (weather.equals("흐리고 비"))
            return WeatherCode.cloudyRain;
        if (weather.equals("흐리고 눈"))
            return WeatherCode.cloudySnow;
        if (weather.equals("흐리고 비/눈"))
            return WeatherCode.cloudyRS;
        if (weather.equals("흐리고 소나기"))
            return WeatherCode.cloudyShower;
        return WeatherCode.sunny;
    }

    public midTermTemp[] getWeather_midTerm_highest_lowest_temp(String regId) {//중기육상예보조회와 다른 rdgId를 사용함
        // type = "중기기온조회";
        //예상기온
        base = LocalDateTime.now();
        if (base.getHour() < 6) {
            base = base.withHour(18).minusDays(1).withMinute(0);
        } else if (base.getHour() >= 6 && base.getHour() < 18) {
            base = base.withHour(6).withMinute(0);
        } else {
            base = base.withHour(18).withMinute(0);
        }
        JsonObject responseJson = null;
        midTermTemp[] temp = new midTermTemp[8];
        try {
            int num = 1000;
            int pageNo = 1;
            String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            responseJson = GetJson.getJson(api);
            JsonObject item = (JsonObject) ((JsonArray) ((JsonObject) ((JsonObject) ((JsonObject) responseJson.get("response")).get("body")).get("items")).get("item")).get(0);
            for (int i = 0; i < 5; ++i) {
                temp[i] = new midTermTemp();
                temp[i].max = Integer.parseInt(item.get("taMax" + (i + 3)).toString());
                temp[i].min = Integer.parseInt(item.get("taMax" + (i + 3)).toString());
            }
            try {
                for (int i = 5; i < 8; ++i) {
                    temp[i] = new midTermTemp();
                    temp[i].max = Integer.parseInt(item.get("taMax" + (i + 3)).toString());
                    temp[i].min = Integer.parseInt(item.get("taMax" + (i + 3)).toString());
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
            System.err.println(responseJson);
        }
        return temp;
    }

    // private void getWeather_midTerm_4()
    // {
    //     type = "중기해상예보조회";
    //     //regid 형식이 다름
    //     //바다의 날씨를 보는 것이니 크게 필요하지 않을 것
    //     int num = 12;
    //     int pageNo = 1; // 1400의 예보라면 1페이지에 1500 2페이지에 1500식
    //     String api = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidSeaFcst?serviceKey=" + serviceKey + "&dataType=JSON&numOfRows=" + num + "&pageNo=" + pageNo + "&regId=" + location.regId + "&base_date=" + "&tmFc=" + base.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
    //     JSONObject responseJson = GetJson.getJson(api);
    //     JSONObject temp;
    //     System.err.println(responseJson);
    // }
}