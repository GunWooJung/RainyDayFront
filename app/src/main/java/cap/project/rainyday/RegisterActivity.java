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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import cap.project.rainyday.model.User;

public class RegisterActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_register);

        // 회원가입 버튼
        Button button_register = findViewById(R.id.button_register);
        TextInputLayout textInputLayout_id = findViewById(R.id.edit_id_layout);
        TextInputLayout textInputLayout_password =  findViewById(R.id.edit_password_layout);
        TextInputLayout textInputLayout_user_name =  findViewById(R.id.edit_name_layout);
        // 로그인 입력 박스
        TextInputEditText  user_id = (TextInputEditText) textInputLayout_id.getEditText();
        TextInputEditText user_password = (TextInputEditText) textInputLayout_password.getEditText();
        TextInputEditText user_name = (TextInputEditText) textInputLayout_user_name.getEditText();

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = user_id.getText().toString();
                String password = user_password.getText().toString();
                String name = user_name.getText().toString();
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                    showLoginFailedDialog("입력되지 않은 칸이 존재합니다.");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // JSON 형식으로 데이터 생성
                            User user = new User(id, password, name);
                            String json = user.toJson();
                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/user";

                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                            // HTTP 요청 설정
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            con.setDoOutput(true);

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
                                        showToast("회원 가입에 성공했습니다.");
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoginFailedDialog("이미 존재하는 아이디입니다.");
                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLoginFailedDialog("잘못된 요청입니다.");
                                    }
                                });
                            }
                            else {
                                Log.d("회원가입", "회원가입 실패");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        // 이미 회원이시라구요? --> 로그인 화면으로
        Button button_login = findViewById(R.id.button_already_member);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}