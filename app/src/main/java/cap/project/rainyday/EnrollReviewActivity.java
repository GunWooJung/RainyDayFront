package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cap.project.rainyday.model.Review;
import cap.project.rainyday.tool.LoginSharedPreferences;

public class EnrollReviewActivity extends AppCompatActivity {
    ImageButton back;

    Button enrollButton;

    private long scheduleId;

    RadioGroup radioGroup;

    List<String> locations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_review);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);

        back = findViewById(R.id.backhome);
        enrollButton = findViewById(R.id.enrollbutton);
        TextInputLayout textInputLayout_id = findViewById(R.id.textInputLayoutContent);

        TextInputEditText content = (TextInputEditText) textInputLayout_id.getEditText();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentText = content.getText().toString();
                if (TextUtils.isEmpty(contentText)) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) { // 선택된 라디오 버튼이 있는 경우
                    // ID를 사용하여 선택된 라디오 버튼 가져오기
                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                    // 선택된 라디오 버튼의 텍스트나 다른 속성에 접근할 수 있음
                    String selectedText = selectedRadioButton.getText().toString();
                    requestReviewToServer(selectedText);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "장소를 선택해주세요.", Toast.LENGTH_LONG).show();
                }

            }
        });

        radioGroup = findViewById(R.id.radioGroup);

    }

    private void requestReviewToServer(String selectedText) {
        TextInputLayout textInputLayout_id = findViewById(R.id.textInputLayoutContent);

        TextInputEditText content = (TextInputEditText) textInputLayout_id.getEditText();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // JSON 형식으로 데이터 생성
                    Review review = new Review();
                    review.setLocation(selectedText);
                    review.setContents(content.getText().toString());

                    long userId = LoginSharedPreferences.getUserId(getApplicationContext());
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/review/?userId=" + userId;
                    Log.d("AAAA", url);
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // HTTP 요청 설정
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setDoOutput(true);
                    String json = review.toJson();
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8); // UTF-8로 인코딩된 바이트 배열 얻기
                    wr.write(jsonBytes, 0, jsonBytes.length); // 바이트 배열을 전송
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "리뷰가 등록되었습니다.", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                    Log.d("AAA", "Response Code: " + responseCode);

                } catch (Exception e) {
                    Log.d("eaarse", e.toString());
                }
            }
        }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (radioGroup.getChildCount() != 0) {
            return;
        }
        locations = new ArrayList<>();
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
                        //List<String> list = new ArrayList<>();
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                            String loationName = scheduleObject.get("name").getAsString();
                            locations.add(loationName);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < locations.size(); i++) {
                                RadioButton radioButton = new RadioButton(getApplicationContext());
                                radioButton.setText(locations.get(i));
                                radioButton.setId(i); // 각 라디오 버튼마다 고유한 ID 설정
                                radioGroup.addView(radioButton); // 라디오 그룹에 라디오 버튼 추가
                            }
                        }
                    });

                } catch (ProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
