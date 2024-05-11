package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cap.project.rainyday.model.Location;
import cap.project.rainyday.model.Route;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.model.Weather;
import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.midTermForecast;

public class RouteActivity extends AppCompatActivity implements WeatherClickListener {

    private long scheduleId;
    ImageButton back;
    Button backbutton;

    private RecyclerView recyclerView;
    private static ScheWeatherAdapter adapter;
    private ProgressBar loadingProgressBar;
    private List<Weather> weatherItems;

    private List<Location> FromBackend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_weather);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        back = findViewById(R.id.backhome);
        backbutton = findViewById(R.id.backbutton);
        recyclerView = findViewById(R.id.weatherList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheWeatherAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadData();

    }

    private void LoadData() {
        if (FromBackend == null) {
            FromBackend = new ArrayList<>();
        } else {
            FromBackend.clear(); // 기존 데이터를 지웁니다.
        }
        if (weatherItems == null) {
            weatherItems = new ArrayList<>();
        } else {
            weatherItems.clear(); // 기존 데이터를 지웁니다.
        }
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
                        // JSON 데이터 파싱 및 리스트뷰에 추가
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                            if (i == 0) {
                                Location location = new Location();
                                location.setName(scheduleObject.get("name").getAsString());
                                location.setTime(scheduleObject.get("departTime").getAsString());
                                location.setLat(scheduleObject.get("lat").getAsDouble());
                                location.setLng(scheduleObject.get("lng").getAsDouble());
                                location.setNx(scheduleObject.get("nx").getAsInt());
                                location.setNy(scheduleObject.get("ny").getAsInt());
                                location.setRegioncode(scheduleObject.get("regioncode").getAsString());
                                FromBackend.add(location);
                            } else if (i == jsonArray.size() - 1) {
                                Location location = new Location();
                                location.setName(scheduleObject.get("name").getAsString());
                                location.setTime(scheduleObject.get("destTime").getAsString());
                                location.setLat(scheduleObject.get("lat").getAsDouble());
                                location.setLng(scheduleObject.get("lng").getAsDouble());
                                location.setNx(scheduleObject.get("nx").getAsInt());
                                location.setNy(scheduleObject.get("ny").getAsInt());
                                location.setRegioncode(scheduleObject.get("regioncode").getAsString());
                                FromBackend.add(location);
                            } else {


                                Location locationDest = new Location();
                                locationDest.setName(scheduleObject.get("name").getAsString());
                                locationDest.setTime(scheduleObject.get("destTime").getAsString());
                                locationDest.setLat(scheduleObject.get("lat").getAsDouble());
                                locationDest.setLng(scheduleObject.get("lng").getAsDouble());
                                locationDest.setNx(scheduleObject.get("nx").getAsInt());
                                locationDest.setNy(scheduleObject.get("ny").getAsInt());
                                locationDest.setRegioncode(scheduleObject.get("regioncode").getAsString());
                                FromBackend.add(locationDest);
                                
                                Location locationDepart = new Location();
                                locationDepart.setName(scheduleObject.get("name").getAsString());
                                locationDepart.setTime(scheduleObject.get("departTime").getAsString());
                                locationDepart.setLat(scheduleObject.get("lat").getAsDouble());
                                locationDepart.setLng(scheduleObject.get("lng").getAsDouble());
                                locationDepart.setNx(scheduleObject.get("nx").getAsInt());
                                locationDepart.setNy(scheduleObject.get("ny").getAsInt());
                                locationDepart.setRegioncode(scheduleObject.get("regioncode").getAsString());
                                FromBackend.add(locationDepart);

                            }


                        }
                        List<Thread> threadList = new ArrayList<>();
                        for (int i = 0; i < FromBackend.size(); i++) {
                            final int index = i;
                            Location location = FromBackend.get(i);
                            Thread thread = new Thread(() -> {
                                cap.project.rainyday.weather.Location weatherLocation = new cap.project.rainyday.weather.Location(
                                        location.getNx(), location.getNy(), location.getRegioncode()
                                );
                                LocalDateTime now = LocalDateTime.now();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                LocalDateTime dateTime = LocalDateTime.parse(location.getTime(), formatter);
                                Duration duration = Duration.between(now, dateTime);
                                long daysDifference = duration.toDays(); // Day 차이
                                Weather weatherItem = new Weather();
                                weatherItem.setIndex(index);
                                weatherItem.setLocation(location.getName());
                                weatherItem.setTime(dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(0, 2) +
                                        "일 " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(2, 4) +
                                        "시 " + dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(4, 6) +
                                        "분");

                                if (dateTime.isAfter(now)) {
                                    if (daysDifference >= 0 && daysDifference <= 2) {
                                        ShortTermForeacast weather_f = new ShortTermForeacast(weatherLocation);
                                        ShortTermWeather weather_s[] = weather_f.getWeather();
                                        //저는 for문으로 모두 출력했지만 첫번째 인덱스의 시간과 날짜를 보고 몇시간 후인지 전인지 보고 인덱스를 더하고 몇일 뒤인지에 따라 24만큼 인덱스를 더해서 빠르게 접근가능합니다
                                        for (ShortTermWeather weather : weather_s) {
                                            if (!weather.fcst.format(DateTimeFormatter.ofPattern("ddHH")).
                                                    equals(dateTime.format(DateTimeFormatter.ofPattern("ddHH")))) {
                                                continue;
                                            }

                                            weatherItem.setTemperature(weather.tmp + "ºC");
                                            weatherItem.setRainyPercent(weather.pop + "%");
                                            weatherItem.setRainyAmount(weather.pcp);
                                            weatherItem.setWeatherInfo(weather.wCode);
                                            weatherItem.setType(0);
                                            weatherItems.add(weatherItem);
                                            break;
                                        }
                                    } else if (daysDifference >= 3 && daysDifference <= 10) {
                                        midTermForecast midTerm = new midTermForecast(weatherLocation);
                                        //중기 예보의 경우 0600 1800에만 발표
                                        // midTerm.getWeather_midTerm();
                                        MidTermWeather weather_m[] = midTerm.getWeather_midTerm_get_all();
                                        //0 인덱스부터 3일후 7인덱스가 10일 후 입니다

                                        for (int j = 0; j < 8; ++j) {
                                            if (daysDifference == (j + 3)) {
                                                weatherItem.setRainyPercent(weather_m[j].rnStAm + "%");
                                                weatherItem.setWeatherInfo(weather_m[j].wfAm);
                                                weatherItem.setType(1);
                                                weatherItems.add(weatherItem);
                                                break;
                                            }
                                        }
                                    } else {
                                        weatherItem.setType(2); //10일 이후
                                        weatherItems.add(weatherItem);
                                    }
                                } else {
                                    weatherItem.setType(3); //현재보다 이전
                                    weatherItems.add(weatherItem);
                                }
                            });
                            threadList.add(thread);
                            thread.start();
                        }
                        for (Thread thread : threadList) {
                            try {
                                thread.join(); // 다음 스레드가 실행되기 전에 현재 스레드가 종료될 때까지 기다립니다.
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Collections.sort(weatherItems, Comparator.comparingInt(Weather::getIndex));
                                adapter.setItems(weatherItems);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("eeee", e.toString());
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(Weather item) {

    }
}