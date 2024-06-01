package cap.project.rainyday;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cap.project.rainyday.model.DetailWeather;
import cap.project.rainyday.model.Location;
import cap.project.rainyday.model.Route;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.model.Weather;
import cap.project.rainyday.tool.LocationManager;
import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.midTermForecast;

public class RouteActivity extends AppCompatActivity implements WeatherClickListener {

    private long scheduleId;
    private String temptitle;
    private String temphash;
    ImageButton back;
    Button backbutton;
    ImageView write, trash;

    private RecyclerView recyclerView;

    private RecyclerView recyclerViewDetail;

    private static ScheWeatherAdapter adapter;
    private static CurWeatherAdapter adapterDetail;
    private ProgressBar loadingProgressBar;
    private List<Weather> weatherItems;
    private List<DetailWeather> detailWeather;

    private List<Location> FromBackend;
    LinearLayout moreBoxLayout;
    TextView detailName;
    TextView title, hash;

    Button close;

    private void increaseLocationCount(Context context, long notificationId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/" + notificationId;
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
                        //List<String> list = new ArrayList<>();
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                            String loationName = scheduleObject.get("name").getAsString();
                            //list.add(loationName);
                            LocationManager.incrementLocationVisitCount(context, loationName);
                        }
                    }

                } catch (ProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_weather);
        Intent intent = getIntent();
        int FromAlarm = intent.getIntExtra("FromAlarm", 0);
        scheduleId = intent.getLongExtra("scheduleId", 0);
        if(FromAlarm == 1){
            increaseLocationCount(this, scheduleId);
            Toast.makeText(this, "방문 횟수가 기록되었습니다.", Toast.LENGTH_LONG).show();

        }

        back = findViewById(R.id.backhome);
        backbutton = findViewById(R.id.backbutton);
        recyclerView = findViewById(R.id.weatherList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetail = findViewById(R.id.detail);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheWeatherAdapter(new ArrayList<>(), this);
        adapterDetail = new CurWeatherAdapter(getApplicationContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerViewDetail.setAdapter(adapterDetail);
        trash = findViewById(R.id.trash);
        moreBoxLayout = findViewById(R.id.moreBox);
        moreBoxLayout.setVisibility(View.GONE);
        close = findViewById(R.id.close);
        loadingProgressBar = findViewById(R.id.progressBar);
        loadingProgressBar.setVisibility(View.VISIBLE);
        detailWeather = new ArrayList<>();
        detailName = findViewById(R.id.name);
        title = findViewById(R.id.title);
        hash = findViewById(R.id.hash);
        write = findViewById(R.id.write);

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteActivity.this, ScheModifyActivity.class);
                intent.putExtra("scheduleId", scheduleId);
                intent.putExtra("title", temptitle);
                intent.putExtra("hash", temphash);
                startActivity(intent);

                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView which = findViewById(R.id.which);
                //which.setText("(선택된 장소 : 없음)");
                moreBoxLayout.setVisibility(View.GONE);
            }
        });

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

        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext());
                builder2.setTitle("일정 삭제")
                        .setMessage("일정을 정말로 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 사용자가 "예"를 선택한 경우
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/" + scheduleId;

                                            URL obj = new URL(url);
                                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                            // HTTP 요청 설정
                                            con.setRequestMethod("DELETE");
                                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                                            int responseCode = con.getResponseCode();
                                            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                                                // 정상적인 응답일 때만 데이터를 읽어옴
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                // 응답이 200이 아닌 경우 에러 처리
                                                Log.e("err", "HTTP error code: " + responseCode);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).start();

                                finish();
                            }
                        })
                        .setNegativeButton("아니오", null) // 사용자가 "아니오"를 선택한 경우 아무 작업도 수행하지 않음
                        .show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Schedule temp;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // JSON 형식으로 데이터 생성

                    // HTTP 요청 보내기
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/title/"+scheduleId;

                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // HTTP 요청 설정
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    // 응답 받기
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        // 응답 데이터 읽기
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(response.toString());
                        temptitle = rootNode.get("title").asText();
                        temphash = rootNode.get("hashTag").asText();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                title.setText(temptitle);
                                hash.setText(temphash);
                            }
                        });
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }
        }).

                start();

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
                                dateTime = dateTime.plusHours(1);
                                if (dateTime.isAfter(now)) {
                                    if (daysDifference >= 0 && daysDifference <= 2) {
                                        ShortTermForeacast weather_f = new ShortTermForeacast(weatherLocation);
                                        ShortTermWeather weather_s[] = weather_f.getWeather();
                                        DetailWeather detailItem = new DetailWeather();
                                        detailItem.setIndex(index);
                                        Boolean first = false;
                                        int count = 0;
                                        String tempWeather = "";
                                        //저는 for문으로 모두 출력했지만 첫번째 인덱스의 시간과 날짜를 보고 몇시간 후인지 전인지 보고 인덱스를 더하고 몇일 뒤인지에 따라 24만큼 인덱스를 더해서 빠르게 접근가능합니다
                                        for (int k = 0; k < weather_s.length; k++) {
                                            if (first == false) {
                                                ShortTermWeather weather = weather_s[k];
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

                                                //detailItem.weather.add(weatherItem);
                                                first = true;
                                                tempWeather = String.valueOf(weather.fcst);

                                            } else if (first == true && count <= 5) {
                                                ShortTermWeather weather = weather_s[k];
                                                Weather weatherItem2 = new Weather();
                                                if (tempWeather.equals(String.valueOf(weather.fcst)))
                                                    continue;
                                                weatherItem2.setTemperature(weather.tmp + "ºC");
                                                weatherItem2.setTime(weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(0, 2) +
                                                        "일 " + weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(2, 4) +
                                                        "시 " + weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm")).substring(4, 6) +
                                                        "분");

                                                weatherItem2.setRainyPercent(weather.pop + "%");
                                                weatherItem2.setRainyAmount(weather.pcp);
                                                weatherItem2.setWeatherInfo(weather.wCode);
                                                weatherItem2.setType(0);
                                                detailItem.weather.add(weatherItem2);
                                                count++;

                                                tempWeather = String.valueOf(weather.fcst);
                                            } else {
                                                break;
                                            }
                                        }
                                        detailItem.setType(0);
                                        detailWeather.add(detailItem);
                                    } else if (daysDifference >= 3 && daysDifference <= 10) {
                                        DetailWeather detailItem = new DetailWeather();
                                        detailItem.setIndex(index);
                                        midTermForecast midTerm = new midTermForecast(weatherLocation);
                                        //중기 예보의 경우 0600 1800에만 발표
                                        // midTerm.getWeather_midTerm();
                                        MidTermWeather weather_m[] = midTerm.getWeather_midTerm_get_all();

                                        //0 인덱스부터 3일후 7인덱스가 10일 후 입니다
                                        LocalDateTime nowDate = LocalDateTime.now();
                                        nowDate = nowDate.plusDays(3);
                                        nowDate = nowDate.withHour(5).withMinute(0).withSecond(0).withNano(0);
                                        for (int j = 0; j < 8; ++j) {
                                            if (j >= 0 && j <= 4) {

                                                    Weather weatherItem2 = new Weather();
                                                    weatherItem2.setTime(nowDate.format(DateTimeFormatter.ofPattern("dd")) +
                                                            "일 오전");
                                                    weatherItem2.setRainyPercent(weather_m[j].rnStAm + "%");
                                                    weatherItem2.setWeatherInfo(weather_m[j].wfAm);
                                                    weatherItem2.setRainyAmount("");
                                                    weatherItem2.setTemperature("");
                                                    weatherItem2.setType(1);
                                                    detailItem.weather.add(weatherItem2);
                                                    nowDate = nowDate.plusHours(12);

                                                    Weather weatherItem3 = new Weather();
                                                weatherItem3.setTime(nowDate.format(DateTimeFormatter.ofPattern("dd")) +
                                                            "일 오후");
                                                weatherItem3.setRainyPercent(weather_m[j].rnStPm + "%");
                                                weatherItem3.setWeatherInfo(weather_m[j].wfPm);
                                                weatherItem3.setRainyAmount("");
                                                weatherItem3.setTemperature("");
                                                weatherItem3.setType(1);
                                                    detailItem.weather.add(weatherItem3);
                                                    nowDate = nowDate.plusHours(12);

                                            } else {
                                                Weather weatherItem2 = new Weather();
                                                weatherItem2.setTime(nowDate.format(DateTimeFormatter.ofPattern("dd")) +
                                                        "일");
                                                weatherItem2.setRainyPercent(weather_m[j].rnStAm + "%");
                                                weatherItem2.setWeatherInfo(weather_m[j].wfAm);
                                                weatherItem2.setRainyAmount("");
                                                weatherItem2.setTemperature("");
                                                weatherItem2.setType(1);
                                                detailItem.weather.add(weatherItem2);
                                                nowDate = nowDate.plusHours(24);
                                            }


                                            if (daysDifference == (j + 3)) {
                                                if (dateTime.getHour() < 12) {
                                                    weatherItem.setRainyPercent(weather_m[j].rnStAm + "%");
                                                    weatherItem.setWeatherInfo(weather_m[j].wfAm);
                                                    weatherItem.setType(1);
                                                    weatherItems.add(weatherItem);
                                                    //break;
                                                } else {
                                                    weatherItem.setRainyPercent(weather_m[j].rnStPm + "%");
                                                    weatherItem.setWeatherInfo(weather_m[j].wfPm);
                                                    weatherItem.setType(1);
                                                    weatherItems.add(weatherItem);
                                                    //break;
                                                }
                                            }

                                        }

                                        detailItem.setType(1);
                                        detailWeather.add(detailItem);
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
                                loadingProgressBar.setVisibility(View.GONE);
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
        if (moreBoxLayout.getVisibility() == View.VISIBLE) {
            if ((int) moreBoxLayout.getTag() == item.getIndex()) {
                moreBoxLayout.setVisibility(View.GONE);
            } else {
                moreBoxLayout.setTag(item.getIndex());
                detailName.setText("더보기(" + item.getLocation() + ")");
                for (DetailWeather d : detailWeather) {
                    if (d.getIndex() == item.getIndex()) {
                        adapterDetail.setWeatherList(d.weather);
                        adapterDetail.notifyDataSetChanged();
                        break;
                    }
                }
            }
        } else if (moreBoxLayout.getVisibility() == View.GONE) {
            moreBoxLayout.setVisibility(View.VISIBLE);
            moreBoxLayout.setTag(item.getIndex());
            detailName.setText("더보기(" + item.getLocation() + ")");
            for (DetailWeather d : detailWeather) {
                if (d.getIndex() == item.getIndex()) {
                    adapterDetail.setWeatherList(d.weather);
                    adapterDetail.notifyDataSetChanged();
                    break;
                }
            }
        }
    }
}