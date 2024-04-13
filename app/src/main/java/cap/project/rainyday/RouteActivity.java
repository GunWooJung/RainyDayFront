package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cap.project.rainyday.data.model.Route;
import cap.project.rainyday.data.model.Schedule;

public class RouteActivity extends AppCompatActivity  {

    private ArrayList<String> routeList;
    private ArrayAdapter<String> adapter;
    private ListView listViewRoute;

    private ArrayList<Route> routeListFromBackend;
    private long scheduleId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            // + 메뉴 터치시 이동
            Intent intent = new Intent(RouteActivity.this, RouteDepartActivity.class);
            intent.putExtra("scheduleId", scheduleId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
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
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/route/load?scheduleId="+scheduleId;
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
                            routeList.add(route.toString());
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        // 응답이 200이 아닌 경우 에러 처리
                        Log.e("err", "HTTP error code: " + responseCode);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
