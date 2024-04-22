package cap.project.rainyday;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ScheduleAddActivity extends AppCompatActivity {
    private long userId;
    private void showLoginFailedDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 클릭하면 다이얼로그를 닫음
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);
        Intent intent = getIntent();
        userId = intent.getLongExtra("userId", 0);
        TextInputLayout textInputLayout = findViewById(R.id.titleInputLayout);
        TextInputEditText textInput = (TextInputEditText) textInputLayout.getEditText();

        Button addScheduleButton = findViewById(R.id.addScheduleButton);
        addScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = textInput.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    showLoginFailedDialog("입력되지 않은 칸이 존재합니다.");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // JSON 형식으로 데이터 생성

                        String json = String.format("{ \"title\" : \"%s\" }", title);
                        // HTTP 요청 보내기
                        try {
                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/create?userId="+userId;

                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            // HTTP 요청 설정
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            con.setDoOutput(true);

                            // JSON 데이터 전송
                            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8); // UTF-8로 인코딩된 바이트 배열 얻기
                            wr.write(jsonBytes, 0, jsonBytes.length); // 바이트 배열을 전송
                            wr.flush();
                            wr.close();

                            // 응답 받기
                            int responseCode = con.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("일정이 등록되었습니다.");
                                        finish();

                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoginFailedDialog("일정 등록에 실패하였습니다.");

                                    }
                                });
                            } else {
                                Log.d("http code : ", String.valueOf(responseCode));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });

    }
}