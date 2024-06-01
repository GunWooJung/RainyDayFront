package cap.project.rainyday.tool;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cap.project.rainyday.LoginActivity;
import cap.project.rainyday.MainPageActivity;
import cap.project.rainyday.RegisterActivity;
import cap.project.rainyday.ScheModifyActivity;
import cap.project.rainyday.model.Location;
import cap.project.rainyday.model.User;

// 새로운 BroadcastReceiver 클래스 추가
public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            long notificationId = intent.getLongExtra("notification_id", -1);

            if (notificationId != -1) {
                notificationManager.cancel((int) notificationId);
            }

            switch (intent.getAction()) {
                case "YES_ACTION":
                    // "예" 버튼을 눌렀을 때 실행할 코드
                    Log.d("NotificationActionReceiver", notificationId + "Yes button clicked");
                    increaseLocationCount(context, notificationId);
                    Toast.makeText(context, "방문 횟수가 기록되었습니다.", Toast.LENGTH_LONG).show();
                    break;
                case "NO_ACTION":
                    // "아니오" 버튼을 눌렀을 때 실행할 코드
                    Log.d("NotificationActionReceiver", "No button clicked");
                    Toast.makeText(context, "취소되었습니다.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Log.e("NotificationActionReceiver", "Unknown action");

            }
        } else {
            Log.e("NotificationActionReceiver", "Intent or action is null");
        }
    }

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
}