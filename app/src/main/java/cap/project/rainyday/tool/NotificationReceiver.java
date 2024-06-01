package cap.project.rainyday.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cap.project.rainyday.LoginActivity;
import cap.project.rainyday.MainActivity;
import cap.project.rainyday.MainPageActivity;
import cap.project.rainyday.R;
import cap.project.rainyday.RegisterActivity;
import cap.project.rainyday.RouteActivity;
import cap.project.rainyday.model.AlarmMessage;
import cap.project.rainyday.model.User;

public class NotificationReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";

    @Override
    public void onReceive(Context context, Intent intent) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 안드로이드 오레오 버전 대응
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        String title = intent.getStringExtra("title");

        Long id = intent.getLongExtra("schedule", 0);

        int int_id = Math.toIntExact(id);
        // 알림창 클릭 시 지정된 activity 화면으로 이동
        Intent intent2 = new Intent(context, RouteActivity.class);
        intent2.putExtra("scheduleId", id);
        intent2.putExtra("FromAlarm", 1);
        // FLAG_UPDATE_CURRENT 및 FLAG_IMMUTABLE 플래그 사용
        PendingIntent pendingIntent = PendingIntent.getActivity(context, int_id, intent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d("adg", title + id);

        Intent yesIntent = new Intent(context, NotificationActionReceiver.class);
        yesIntent.setAction("YES_ACTION");
        yesIntent.putExtra("notification_id", id);
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(
                context, int_id, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // "아니오" 버튼 클릭 시 실행될 Intent 설정
        Intent noIntent = new Intent(context, NotificationActionReceiver.class);
        noIntent.setAction("NO_ACTION");
        noIntent.putExtra("notification_id", id);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(
                context, int_id, noIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        // 알림창 제목
        builder.setContentTitle("일정 알림"); // 회의명 노출
        builder.setSmallIcon(R.drawable.rainyday_icon);
        // 알림창 터치 시 자동 삭제


        builder.addAction(0, "아니오", noPendingIntent);
        builder.addAction(0, "예", yesPendingIntent);
        builder.setAutoCancel(true);
        builder.setContentText(title + " 출발하셨나요?\n날씨를 보여려면 터치하세요.");
        builder.setContentIntent(pendingIntent);

        SharedPreferences share = context.getSharedPreferences("schedule" + id, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt("schedule" + id, 0);
        editor.apply();
        // 푸시알림 빌드
        Notification notification = builder.build();
        // NotificationManager를 이용하여 푸시 알림 보내기
        manager.notify(int_id, notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // JSON 형식으로 데이터 생성
                    AlarmMessage alarm = new AlarmMessage();
                    alarm.setTitle("일정 알림");
                    alarm.setContent(title + " 출발하셨나요?");

                    long userId = LoginSharedPreferences.getUserId(context);
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/alarm/?userId=" + userId;
                    Log.d("AAAA",url);
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // HTTP 요청 설정
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setDoOutput(true);
                    String json = alarm.toJson();
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8); // UTF-8로 인코딩된 바이트 배열 얻기
                    wr.write(jsonBytes, 0, jsonBytes.length); // 바이트 배열을 전송
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    Log.d("AAA", "Response Code: " + responseCode);

                } catch (Exception e) {
                    Log.d("eaarse",e.toString());
                }
            }
        }).start();
    }

}
