package cap.project.rainyday;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cap.project.rainyday.model.ConvertGRID;
import cap.project.rainyday.model.LatXLngY;
import cap.project.rainyday.model.Weather;
import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.midTermForecast;


public class CleanDayActivity extends AppCompatActivity {
    ImageButton back;

    Button backButton, search;

    int departHour, departMinute;

    String location;
    double lat, lng;

    TextView departPlace, departTime, loading;

    LinearLayout searchBoxLayout;

    private RecyclerView recyclerView;
    private static CurWeatherAdapter weatherAdapter;
    private List<Weather> weatherItemList;

    ProgressBar progressBar2;

    private void showTimePickerDialog() {
        // 현재 시간 가져오기
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // TimePickerDialog 생성
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // 선택한 시간 TextView에 표시
                        departTime.setText(hourOfDay + "시 " + minute + "분");
                        departHour = hourOfDay;
                        departMinute = minute;
                    }
                }, hour, minute, true); // 마지막 매개변수는 24시간 표시 여부

        // TimePickerDialog 표시
        timePickerDialog.show();
    }

    private void showFailedDialog(String message) {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleanday);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        back = findViewById(R.id.backhome);
        backButton = findViewById(R.id.backbutton);
        departPlace = findViewById(R.id.depart_place);
        searchBoxLayout = findViewById(R.id.searchBox);
        searchBoxLayout.setVisibility(View.GONE);
        departTime = findViewById(R.id.depart_time);
        recyclerView = findViewById(R.id.cleanRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherAdapter = new CurWeatherAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(weatherAdapter);
        weatherItemList = new ArrayList<>();
        search = findViewById(R.id.search);

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


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBI_xKrXy81n7ELWopYZi15QMKJ0rQrL6Q");
        }
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_search);
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        autocompleteSupportFragment.setPlaceFields(fields);

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (place.getLatLng() != null) {

                    location = place.getName();
                    lat = place.getLatLng().latitude;
                    lng = place.getLatLng().longitude;
                    autocompleteSupportFragment.setText("");
                    departPlace.setText(location);
                }
            }

            @Override
            public void onError(Status status) {
                Log.d("err", "err");
            }
        });

        departPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (searchBoxLayout.getVisibility() == View.VISIBLE) {
                    searchBoxLayout.setVisibility(View.GONE);
                } else {
                    searchBoxLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        departTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.VISIBLE);
                    }
                });

                try {
                    Boolean isEmpty = false;
                    if (TextUtils.isEmpty(departPlace.getText().toString())
                            || TextUtils.isEmpty(departTime.getText().toString())) {
                        isEmpty = true;
                    }
                    if (isEmpty) {
                        showFailedDialog("입력되지 않은 칸이 존재합니다.");
                        loading.setVisibility(View.GONE);
                        return;
                    }
                   Toast.makeText(CleanDayActivity.this, "날씨 정보를 불러옵니다..", Toast.LENGTH_SHORT).show();


                    weatherItemList = new ArrayList<>();
                    final String[] regionCode = new String[1];
                    int nx, ny;
                    Thread t1 = new Thread(new Runnable() {
                        @Override
                        public void run() {


                            try {

                                String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/location?lat=" + lat + "&lng=" + lng;
                                URL obj = new URL(url);
                                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                                Log.d("ssss", url);
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
                                    regionCode[0] = response.toString();
                                    Log.d("ssss", response.toString());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("ssss", e.toString());
                            }
                        }
                    });

                    t1.start();
                    t1.join();
                    LatXLngY xy = ConvertGRID.convertGRID(lat, lng);
                    nx = (int) xy.x;
                    ny = (int) xy.y;

                    cap.project.rainyday.weather.Location weatherLocation = new cap.project.rainyday.weather.Location(
                            nx, ny, regionCode[0]
                    );

                    Thread t2 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ShortTermForeacast weather_f = new ShortTermForeacast(weatherLocation);
                                ShortTermWeather weather_s[] = weather_f.getWeather();

                                for (int k = 0; k < weather_s.length; k++) {

                                    ShortTermWeather weather = weather_s[k];
                                    String str = String.format("%02d", departHour);
                                    if (!weather.fcst.format(DateTimeFormatter.ofPattern("HH")).
                                            equals(str)) {
                                        continue;
                                    }
                                    Weather weatherItem = new Weather();
                                    String str2 = weather.fcst.format(DateTimeFormatter.ofPattern("dd일"));
                                    weatherItem.setTime(str2);
                                    weatherItem.setTemperature(weather.tmp + "ºC");
                                    weatherItem.setRainyPercent(weather.pop + "%");
                                    weatherItem.setRainyAmount(weather.pcp);
                                    weatherItem.setWeatherInfo(weather.wCode);
                                    weatherItem.setType(9);

                                    weatherItemList.add(weatherItem);
                                    k++;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t2.start();

                    Thread t3 = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            midTermForecast midTerm = new midTermForecast(weatherLocation);
                            //중기 예보의 경우 0600 1800에만 발표
                            // midTerm.getWeather_midTerm();
                            MidTermWeather weather_m[] = midTerm.getWeather_midTerm_get_all();

                            //0 인덱스부터 3일후 7인덱스가 10일 후 입니다
                            LocalDateTime nowDate = LocalDateTime.now();
                            nowDate = nowDate.plusDays(3);
                            nowDate = nowDate.withHour(departHour).withMinute(0).withSecond(0).withNano(0);
                            for (int j = 0; j < 8; ++j) {
                                if (j >= 0 && j <= 4) {

                                    if (departHour < 12) {
                                        Weather weatherItem2 = new Weather();
                                        weatherItem2.setTime(nowDate.format(DateTimeFormatter.ofPattern("dd일")));
                                        weatherItem2.setRainyPercent(weather_m[j].rnStAm + "%");
                                        weatherItem2.setWeatherInfo(weather_m[j].wfAm);
                                        weatherItem2.setRainyAmount("");
                                        weatherItem2.setTemperature("");
                                        weatherItem2.setType(9);
                                        weatherItemList.add(weatherItem2);
                                        nowDate = nowDate.plusDays(1);
                                    } else {
                                        Weather weatherItem3 = new Weather();
                                        weatherItem3.setTime(nowDate.format(DateTimeFormatter.ofPattern("dd일")));
                                        weatherItem3.setRainyPercent(weather_m[j].rnStPm + "%");
                                        weatherItem3.setWeatherInfo(weather_m[j].wfPm);
                                        weatherItem3.setRainyAmount("");
                                        weatherItem3.setTemperature("");
                                        weatherItem3.setType(9);
                                        weatherItemList.add(weatherItem3);
                                        nowDate = nowDate.plusDays(1);
                                    }
                                } else {
                                    Weather weatherItem2 = new Weather();
                                    weatherItem2.setTime(nowDate.format(DateTimeFormatter.ofPattern("dd일")));
                                    weatherItem2.setRainyPercent(weather_m[j].rnStAm + "%");
                                    weatherItem2.setWeatherInfo(weather_m[j].wfAm);
                                    weatherItem2.setRainyAmount("");
                                    weatherItem2.setTemperature("");
                                    weatherItem2.setType(9);
                                    weatherItemList.add(weatherItem2);
                                    nowDate = nowDate.plusHours(24);
                                }
                            }
                        }
                    });
                    t3.start();
                    t2.join();
                    t3.join();
                    Collections.sort(weatherItemList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            weatherAdapter.setWeatherList(weatherItemList);
                            weatherAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (InterruptedException e) {
                    Log.d("ssss", e.toString());
                }

            }
        });

    }

    private void LoadData() throws InterruptedException {


    }
}
