package cap.project.rainyday;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cap.project.rainyday.model.User;
import cap.project.rainyday.tool.LoginSharedPreferences;

public class LoginActivity extends AppCompatActivity {

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
        long id = LoginSharedPreferences.getUserId(getApplicationContext());
        Log.d("TAG", id + "");
        if (id > 0) {
            Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
            startActivity(intent);
            finish();
        }
            setContentView(R.layout.activity_login);
            // 로그인 버튼
            Button button_login = findViewById(R.id.button_login);
            TextInputLayout textInputLayout_id = findViewById(R.id.user_id_layout);
            TextInputLayout textInputLayout_password = findViewById(R.id.user_password_layout);
            // 로그인 입력 박스
            TextInputEditText user_id = (TextInputEditText) textInputLayout_id.getEditText();
            TextInputEditText user_password = (TextInputEditText) textInputLayout_password.getEditText();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 가입 요청 시작
                String id = user_id.getText().toString();
                String password = user_password.getText().toString();
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
                    showLoginFailedDialog("입력되지 않은 칸이 존재합니다.");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // JSON 형식으로 데이터 생성
                        User user = new User(id, password);
                        String json = user.toJson();

                        // HTTP 요청 보내기
                        try {
                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/user/login";

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
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                                String inputLine;
                                StringBuilder response = new StringBuilder();

                                // 응답 데이터 읽기
                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                                in.close();
                                // 응답 Body에서 long 값 읽기
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode rootNode = objectMapper.readTree(response.toString());
                                long userId = rootNode.get("userId").asLong();
                                String userName = rootNode.get("userName").asText();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("로그인에 성공했습니다.");
                                        Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
                                        LoginSharedPreferences.saveUserId(getApplicationContext(), userId);
                                        LoginSharedPreferences.saveUserName(getApplicationContext(), userName);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoginFailedDialog("아이디 혹은 비밀번호가 일치하지 않습니다.");

                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoginFailedDialog("잘못된 요청입니다.");

                                    }
                                });
                            } else {
                                Log.d("login", "로그인 실패");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });


        // 회원가입 버튼
        Button button_register = findViewById(R.id.button_goto_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}