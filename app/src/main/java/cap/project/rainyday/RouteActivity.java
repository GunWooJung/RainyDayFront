package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import cap.project.rainyday.model.Location;
import cap.project.rainyday.model.Route;

import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.midTermForecast;

public class RouteActivity extends AppCompatActivity {

    private long scheduleId;
    private ArrayList<String> routeList;
    private ArrayAdapter<String> adapter;
    private ListView listViewRoute;

    private ArrayList<Location> FromBackend;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_weather);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        back = findViewById(R.id.backhome);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        routeList = new ArrayList<>();
        FromBackend = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routeList);
        listViewRoute = findViewById(R.id.routeListView);
        listViewRoute.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/" + scheduleId;
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // HTTP 요청 설정
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json");

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 정상적인 응답일 때만 데이터를 읽어옴
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        // 응답 데이터 읽기
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        Log.d("resp", response.toString());
                        // JSON 데이터 파싱 및 리스트뷰에 추가
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                            Location location = new Location();
                            location.setName(scheduleObject.get("name").getAsString());
                            if (i == 0) {
                                location.setDepartTime(scheduleObject.get("departTime").getAsString());
                            } else if (i == jsonArray.size() - 1) {
                                location.setDestTime(scheduleObject.get("destTime").getAsString());
                            } else {
                                location.setDepartTime(scheduleObject.get("departTime").getAsString());
                                location.setDestTime(scheduleObject.get("destTime").getAsString());
                            }
                            location.setLat(scheduleObject.get("lat").getAsDouble());
                            location.setLng(scheduleObject.get("lng").getAsDouble());
                            location.setNx(scheduleObject.get("nx").getAsInt());
                            location.setNy(scheduleObject.get("ny").getAsInt());
                            location.setRegioncode(scheduleObject.get("regioncode").getAsString());

                            FromBackend.add(location);
                        }
                        Boolean first = true;
                        for (int i = 0; i < FromBackend.size(); i++) {

                            String result = "";
                            LocalDateTime now = LocalDateTime.now();
                            LocalDateTime dateTime = LocalDateTime.now();
                            Location location = FromBackend.get(i);
                            cap.project.rainyday.weather.Location weatherLocation = new cap.project.rainyday.weather.Location(
                                    location.getNx(), location.getNy(), location.getRegioncode()
                            );
                            Duration duration; //= Duration.between(now, dateTime);
                            long daysDifference = 0; // = duration.toDays(); // Day 차이
                            if (i == 0) {
                                result += "출발지\n";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                dateTime = LocalDateTime.parse(location.getDepartTime(), formatter);
                                duration = Duration.between(now, dateTime);
                                daysDifference = duration.toDays(); // Day 차이
                            } else if (i == FromBackend.size() - 1) {
                                result += "목적지\n";
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                dateTime = LocalDateTime.parse(location.getDestTime(), formatter);
                                duration = Duration.between(now, dateTime);
                                daysDifference = duration.toDays(); // Day 차이
                            } else {
                                if (first == true) {
                                    first = false;
                                    i--;
                                    result += "경유지 도착\n";
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                    dateTime = LocalDateTime.parse(location.getDestTime(), formatter);
                                    duration = Duration.between(now, dateTime);
                                    daysDifference = duration.toDays(); // Day 차이
                                } else if (first == false) {
                                    first = true;
                                    result += "경유지 출발\n";
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                    dateTime = LocalDateTime.parse(location.getDepartTime(), formatter);
                                    duration = Duration.between(now, dateTime);
                                    daysDifference = duration.toDays(); // Day 차이
                                }
                            }
                            try {
                                if (dateTime.isAfter(now)) {
                                    if (daysDifference >= 0 && daysDifference <= 3) {
                                        ShortTermForeacast weather_f = new ShortTermForeacast(weatherLocation);
                                        ShortTermWeather weather_s[] = weather_f.getWeather();
                                        //저는 for문으로 모두 출력했지만 첫번째 인덱스의 시간과 날짜를 보고 몇시간 후인지 전인지 보고 인덱스를 더하고 몇일 뒤인지에 따라 24만큼 인덱스를 더해서 빠르게 접근가능합니다
                                        for (ShortTermWeather weather : weather_s) {
                                            if (!weather.fcst.format(DateTimeFormatter.ofPattern("ddHH")).
                                                    equals(dateTime.format(DateTimeFormatter.ofPattern("ddHH")))) {
                                                // Log.d("lista", dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")));
                                                continue;
                                            }
                                            //    Log.d("lista", dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")));
                                            result += "장소 : " + location.getName();
                                            result += "\n날짜 : " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(0, 2) +
                                                    "일 " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(2, 4) +
                                                    "시 " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(4, 6) +
                                                    "분";
                                            result += "\n온도 : " + weather.tmp+"ºC";
                                            result += "\n강수확률 : " + weather.pop + "%";
                                            if (weather.pcp.equals("강수없음")) {
                                                result += "\n강수량 : " + weather.pcp;
                                            } else {
                                                result += "\n강수량 : " + weather.pcp;
                                            }
                                        }
                                    } else if (daysDifference >= 4 && daysDifference <= 10) {
                                        midTermForecast midTerm = new midTermForecast(LocalDateTime.now(), weatherLocation);
                                        //중기 예보의 경우 0600 1800에만 발표
                                        // midTerm.getWeather_midTerm();
                                        MidTermWeather weather_m[] = midTerm.getWeather_midTerm_get_all();
                                        //0 인덱스부터 3일후 7인덱스가 10일 후 입니다

                                        for (int j = 0; j < 8; ++j) {
                                            if (daysDifference == (j + 3)) {
                                                result += "장소 : " + location.getName();
                                                result += "\n날짜 : " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(0, 2) +
                                                        "일 " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(2, 4) +
                                                        "시 " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(4, 6) +
                                                        "분";
                                                result += "\n강수확률 : " + weather_m[j].rnStAm + "%";
                                                ;
                                                result += "\n날씨 : " + weather_m[j].wfAm + "\n";
                                            }
                                        }
                                    } else {
                                        result = "10일 이후 날씨는 제공되지 않습니다.";
                                    }
                                } else {
                                    result = "현재 시간보다 이전입니다.";
                                }
                            } catch (Exception e) {
                                Log.d("abc", e.toString());
                                result = "해당 장소를 조회할 수 없습니다.";
                            }
                            routeList.add(result);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }


                } catch (Exception e) {
                    Log.d("eeee", e.toString());
                }
            }
        }).start();
    }
}