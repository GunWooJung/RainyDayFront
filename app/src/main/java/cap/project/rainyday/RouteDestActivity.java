package cap.project.rainyday;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RouteDestActivity extends AppCompatActivity {

    private TextView selectedView;

    private long scheduleId;

    private int departYear;
    private int departMonth;
    private int departDay;
    private int departHour;
    private int departMinute;

    private String departName;
    private double departLat;
    private double departLng;

    private String departAddress;

    Boolean selectedPlace = false;

    String destName;
    double destLat ;
    double destLng;

    String destAddress;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_dest);

        selectedView = findViewById(R.id.selected_dest);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        departYear = intent.getIntExtra("departYear", 0);
        departMonth = intent.getIntExtra("departMonth", 0);
        departDay = intent.getIntExtra("departDay", 0);
        departHour = intent.getIntExtra("departHour", 0);
        departMinute = intent.getIntExtra("departMinute", 0);
        departName = intent.getStringExtra("departName");
        departLat = intent.getDoubleExtra("departLat",0);
        departLng = intent.getDoubleExtra("departLng", 0);
        departAddress = intent.getStringExtra("departAddress");

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBI_xKrXy81n7ELWopYZi15QMKJ0rQrL6Q");
        }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_dest);

        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        autocompleteFragment.setPlaceFields(fields);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                // 오류 처리
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (place.getLatLng() != null) {
                    destName = place.getName();
                    destLat = place.getLatLng().latitude;
                    destLng = place.getLatLng().longitude;
                    destAddress = place.getAddress();
                    selectedView.setText("선택된 장소 : "+destName);
                    selectedPlace = true;
                }
            }
        });

        Button button = findViewById(R.id.button_enroll);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlace == false ) {
                    showLoginFailedDialog("입력되지 않은 칸이 존재합니다.");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                        // JSON 형식으로 데이터 생성

                            String json = String.format("{\n" +
                                    "  \"departYear\": %d,\n" +
                                    "  \"departMonth\": %d,\n" +
                                    "  \"departDay\": %d,\n" +
                                    "  \"departHour\": %d,\n" +
                                    "  \"departMinute\": %d,\n" +
                                    "  \"departName\": \"%s\",\n" +
                                    "  \"departLat\": %f,\n" +
                                    "  \"departLng\": %f,\n" +
                                    "  \"departAddress\": \"%s\",\n" +
                                    "  \"destName\": \"%s\",\n" +
                                    "  \"destLat\": %f,\n" +
                                    "  \"destLng\": %f,\n" +
                                    "  \"destAddress\": \"%s\"\n" +
                                    "}",
                                    departYear, departMonth, departDay, departHour, departMinute,
                                    departName,
                                    departLat, departLng,
                                    departAddress == null ? "비공개" : departAddress,
                                    destName,
                                    destLat, destLng,
                                    destAddress == null ? "비공개" : destAddress);

                        Log.d("gogo" , json);
                        // HTTP 요청 보내기
                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/route/create?scheduleId="+scheduleId;
                           //String url = "http://10.210.133.57/route/create?scheduleId="+scheduleId;

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
                                        showToast("경로가 등록되었습니다.");
                                        Intent intent = new Intent(RouteDestActivity.this, RouteActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("scheduleId", scheduleId);
                                        startActivity(intent);
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
