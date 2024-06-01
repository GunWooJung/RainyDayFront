package cap.project.rainyday;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cap.project.rainyday.model.AlarmMessage;
import cap.project.rainyday.tool.LoginSharedPreferences;

public class BellActivity extends AppCompatActivity {

    private long userId;

    ImageButton back;

    Button backButton;
    private static AlarmAdapter adapter;
    private RecyclerView recyclerView;

    private List<AlarmMessage> alarmList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell);
        userId = LoginSharedPreferences.getUserId(getApplicationContext());
        back = findViewById(R.id.backhome);
        backButton = findViewById(R.id.backbutton);
        recyclerView = findViewById(R.id.bell_list_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlarmAdapter(new ArrayList<>());
        alarmList = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        alarmList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/alarm?userId=" + userId;
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
                        Gson gson = new Gson();
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject alarmObject = jsonArray.get(i).getAsJsonObject();
                            AlarmMessage alarm = gson.fromJson(alarmObject, AlarmMessage.class);
                            alarmList.add(alarm);
                            Log.d("AAA", alarm.getTitle()+alarm.getReceviedTime()+alarm.getContent());
                        }
                    }

                    // UI 업데이트는 UI 스레드에서 수행
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Collections.reverse(alarmList); // 리스트를 역순으로 정렬
                            adapter.setItems(alarmList);
                            adapter.notifyDataSetChanged();
                            Log.d("AAA","AA");
                            Log.d("AAA", String.valueOf(alarmList.size()));
                        }
                    });
                } catch (Exception e) {
                    Log.d("erse", e.toString());
                }
            }
        }).start();

    }
}
