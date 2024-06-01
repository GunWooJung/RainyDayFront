package cap.project.rainyday;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewActivity extends AppCompatActivity {
    ImageButton back;

    Button backButton;

    TextView locationName, locationContents, reviewTime;
    ImageButton refresh;

    String current;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        back = findViewById(R.id.backhome);
        backButton = findViewById(R.id.backbutton);
        locationName = findViewById(R.id.location);
        locationContents = findViewById(R.id.contents);
        refresh = findViewById(R.id.refresh);
        reviewTime = findViewById(R.id.reviewTime);

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

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshReview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshReview();
    }

    private void refreshReview(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/review";
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
                        if(response.toString().isEmpty()){
                            return;
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(response.toString());
                        String location = rootNode.get("location").asText();
                        String contents = rootNode.get("contents").asText();
                        String receviedTime = rootNode.get("receviedTime").asText();
                        LocalDateTime time = LocalDateTime.parse(receviedTime);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("작성 일자 : yyyy년 MM월 dd일");
                        // 형식에 맞게 포맷팅
                        String formattedDateTime = time.format(formatter);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                current = location;
                                locationName.setText(location);
                                locationContents.setText(contents);
                                reviewTime.setText(formattedDateTime);
                            }
                        });
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
