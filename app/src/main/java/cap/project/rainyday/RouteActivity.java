package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import cap.project.rainyday.model.Route;

import cap.project.rainyday.weather.Location;
import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.midTermForecast;

public class RouteActivity extends AppCompatActivity {

    private long scheduleId;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        text = findViewById(R.id.tt);
        text.setText(String.valueOf(scheduleId));

    }

    @Override
    protected void onResume() {
        super.onResume();
        text.setText(String.valueOf(scheduleId));
    }

    /*
    protected void onResume() {
        super.onResume();
        routeList = new ArrayList<>();
        routeListFromBackend = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, routeList);
        listViewRoute = findViewById(R.id.routeListView);
        listViewRoute.setAdapter(adapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/route/load?scheduleId=" + scheduleId;
                    //String url = "http://192.168.219.153:80/schedule/load?userId="+userId;;
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

                            Route route = new Route();
                            long routeId = scheduleObject.get("routeId").getAsLong();
                            int departYear = scheduleObject.get("departYear").getAsInt();
                            int departMonth = scheduleObject.get("departMonth").getAsInt();
                            int departDay = scheduleObject.get("departDay").getAsInt();
                            int departHour = scheduleObject.get("departHour").getAsInt();
                            int departMinute = scheduleObject.get("departMinute").getAsInt();
                            String departName = scheduleObject.get("departName").getAsString();
                            double departLat = scheduleObject.get("departLat").getAsDouble();
                            double departLng = scheduleObject.get("departLng").getAsDouble();
                            String departAddress = scheduleObject.get("departAddress").getAsString();
                            int departNx = scheduleObject.get("departNx").getAsInt();
                            int departNy = scheduleObject.get("departNy").getAsInt();
                            String departRegioncode = scheduleObject.get("departRegioncode").getAsString();

                            int destYear = scheduleObject.get("destYear").getAsInt();
                            int destMonth = scheduleObject.get("destMonth").getAsInt();
                            int destDay = scheduleObject.get("destDay").getAsInt();
                            int destHour = scheduleObject.get("destHour").getAsInt();
                            int destMinute = scheduleObject.get("destMinute").getAsInt();
                            String destName = scheduleObject.get("destName").getAsString();
                            double destLat = scheduleObject.get("destLat").getAsDouble();
                            double destLng = scheduleObject.get("destLng").getAsDouble();
                            String destAddress = scheduleObject.get("destAddress").getAsString();
                            int destNx = scheduleObject.get("destNx").getAsInt();
                            int destNy = scheduleObject.get("destNy").getAsInt();
                            String destRegioncode = scheduleObject.get("destRegioncode").getAsString();

                            route.setRouteId(routeId);
                            route.setDepartYear(departYear);
                            route.setDepartMonth(departMonth);
                            route.setDepartDay(departDay);
                            route.setDepartHour(departHour);
                            route.setDepartMinute(departMinute);
                            route.setDepartName(departName);
                            route.setDepartLat(departLat);
                            route.setDepartLng(departLng);
                            route.setDepartAddress(departAddress);
                            route.setDepartNx(departNx);
                            route.setDepartNy(departNy);
                            route.setDepartRegioncode(departRegioncode);
                            route.setDestYear(destYear);
                            route.setDestMonth(destMonth);
                            route.setDestDay(destDay);
                            route.setDestHour(destHour);
                            route.setDestMinute(destMinute);
                            route.setDestName(destName);
                            route.setDestLat(destLat);
                            route.setDestLng(destLng);
                            route.setDestAddress(destAddress);
                            route.setDestNx(destNx);
                            route.setDestNy(destNy);
                            route.setDestRegioncode(destRegioncode);
                            routeListFromBackend.add(route);
                            routeList.add("로딩 중");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                            String s[] = new String[2];
                            s[0] = "";
                            s[1] = "";
                            for (int k = 0; k < 2; k++) {


                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime dateTime;
                                Location location;
                                if(k==0) {
                                    dateTime = LocalDateTime.of(departYear, departMonth, departDay, departHour + 1, 0);
                                    location = new Location(departNx,departNy,departRegioncode);
                                }
                                else{
                                    dateTime = LocalDateTime.of(destYear, destMonth, destDay, destHour + 1, 0);
                                    location = new Location(destNx,destNy,destRegioncode);
                                }
                                Duration duration = Duration.between(now, dateTime);
                                long daysDifference = duration.toDays(); // Day 차이
                                try {
                                    if (dateTime.isAfter(now)) {
                                        if (daysDifference >= 0 && daysDifference <= 3) {
                                            ShortTermForeacast weather_f = new ShortTermForeacast(location);
                                            ShortTermWeather weather_s[] = weather_f.getWeather();
                                            //저는 for문으로 모두 출력했지만 첫번째 인덱스의 시간과 날짜를 보고 몇시간 후인지 전인지 보고 인덱스를 더하고 몇일 뒤인지에 따라 24만큼 인덱스를 더해서 빠르게 접근가능합니다
                                            for (ShortTermWeather weather : weather_s) {
                                                if(!weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm")).
                                                        equals(dateTime.format(DateTimeFormatter.ofPattern("ddHHmm")))){
                                                   continue;
                                                }
                                                s[k] += "날짜 : ";
                                                s[k]  += weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm"));
                                                s[k]  += "TMP 온도 : ";
                                                s[k]  += weather.tmp;
                                                s[k]  += ", POP 강수확률 : ";
                                                s[k]  += weather.pop;
                                                s[k]  += ", PCP 강수량 : ";
                                                s[k]  += weather.pcp;
                                                s[k]  += "\n";
                                            }
                                        } else if (daysDifference >= 4 && daysDifference <= 10) {
                                            midTermForecast midTerm = new midTermForecast(LocalDateTime.now(), location);
                                            //중기 예보의 경우 0600 1800에만 발표
                                            // midTerm.getWeather_midTerm();
                                            MidTermWeather weather_m[] = midTerm.getWeather_midTerm_get_all();
                                            //0 인덱스부터 3일후 7인덱스가 10일 후 입니다

                                            for (int j = 0; j < 8; ++j) {
                                                if(daysDifference == (j+3)) {
                                                    if(dateTime.getHour() <=12) {
                                                        s[k] += (j + 3) + "일 후 오전 강수확률 :" + weather_m[j].rnStAm + "\n";
                                                        s[k] += (j + 3) + "일 후 오전 날씨 :" + weather_m[j].wfAm + "\n";
                                                    }
                                                    else {
                                                        s[k] += (j + 3) + "일 후 오후 강수확률 :" + weather_m[j].rnStPm + "\n";
                                                        s[k] += (j + 3) + "일 후 오후 날씨 :" + weather_m[j].wfPm;
                                                    }
                                                }
                                            }
                                        } else {
                                            s[k]  = "10일 이후 날씨는 제공되지 않습니다.";
                                        }
                                    } else {
                                        s[k]  = "현재 시간보다 이전입니다.";
                                    }
                                } catch (Exception e) {
                                    Log.d("abc", e.toString());
                                    s[k]  = "해당 장소를 조회할 수 없습니다.";
                                }

                            }
                            routeList.set(i , "---------\n"+route.toStringDepart() + s[0]+
                                    "\n\n"+route.toStringDest() + s[1]+"\n---------");
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
}
