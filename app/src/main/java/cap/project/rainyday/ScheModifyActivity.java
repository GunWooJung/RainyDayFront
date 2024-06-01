package cap.project.rainyday;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cap.project.rainyday.model.Location;
import cap.project.rainyday.model.ScheduleEnroll;
import cap.project.rainyday.tool.LoginSharedPreferences;

public class ScheModifyActivity extends AppCompatActivity {

    private static long userId = 0;
    ImageButton back;
    TextView departDate, departTime;
    TextView departPlace, destPlace;

    Button stopoverAdd, close, enroll;

    int departYear, departMonth, departDay, departHour, departMinute;
    LinearLayout container;

    LinearLayout searchBoxLayout;
    List<Location> locationList;

    private int layoutCount = 0;

    private long scheduleId;

    private String title;

    private String hash;
    ImageButton dots;


    private void showDatePickerDialog() {
        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 선택한 날짜 TextView에 표시
                        departDate.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                        departYear = year;
                        departMonth = month + 1;
                        departDay = dayOfMonth;
                    }
                }, year, month, dayOfMonth);

        // DatePickerDialog 표시
        datePickerDialog.show();
    }

    // TimePickerDialog 표시 메서드
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = LoginSharedPreferences.getUserId(getApplicationContext());
        setContentView(R.layout.activity_schedule_modify);

        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        title = intent.getStringExtra("title");
        hash = intent.getStringExtra("hash");

        back = findViewById(R.id.back);
        departDate = findViewById(R.id.depart_date);
        departTime = findViewById(R.id.depart_time);
        container = findViewById(R.id.container);
        stopoverAdd = findViewById(R.id.stopover_add);
        searchBoxLayout = findViewById(R.id.moreBox);
        searchBoxLayout.setVisibility(View.GONE);
        departPlace = findViewById(R.id.depart_place);
        destPlace = findViewById(R.id.dest_place);
        dots = findViewById(R.id.dots);
        TextInputLayout textInputLayout_title = findViewById(R.id.titleLayout);
        TextInputLayout textInputLayout_hash = findViewById(R.id.hashLayout);
        TextInputEditText titleInput = (TextInputEditText) textInputLayout_title.getEditText();
        TextInputEditText hashInput = (TextInputEditText) textInputLayout_hash.getEditText();
        titleInput.setText(title);
        hashInput.setText(hash);

        enroll = findViewById(R.id.sche_enroll);

        close = findViewById(R.id.close);

        dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ScheModifyActivity.this, v);
                // 팝업 메뉴에 아이템 추가
                popupMenu.getMenu().add("가장 맑은 날 찾기");
                popupMenu.getMenu().add("놀러갈 곳 추천");
                popupMenu.getMenu().add("자주 방문한 장소 목록");

                // 팝업 메뉴 아이템 클릭 리스너 설정
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 클릭된 아이템에 따라 처리
                        switch (item.getTitle().toString()) {

                            case "가장 맑은 날 찾기":
                                // 기능2 선택 시 실행할 코드
                                Intent intent = new Intent(ScheModifyActivity.this, CleanDayActivity.class);
                                startActivity(intent);
                                Toast.makeText(ScheModifyActivity.this, "가장 맑은 날 찾기", Toast.LENGTH_SHORT).show();
                                return true;
                            case "놀러갈 곳 추천":
                                // 기능2 선택 시 실행할 코드
                                Intent intent2 = new Intent(ScheModifyActivity.this, ReviewActivity.class);
                                startActivity(intent2);
                                Toast.makeText(ScheModifyActivity.this, "놀러갈 곳 추천", Toast.LENGTH_SHORT).show();
                                return true;
                            case "자주 방문한 장소 목록":
                                Intent intent3 = new Intent(ScheModifyActivity.this, VisitCountActivity.class);
                                startActivity(intent3);
                                Toast.makeText(ScheModifyActivity.this, "자주 방문한 장소 목록", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                // 팝업 메뉴 표시
                popupMenu.show();
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
                    TextView which = findViewById(R.id.which);
                    String whichText = which.getText().toString();
                    if (whichText.indexOf("출발") != -1) {
                        Location location = new Location();
                        location.setName(place.getName());
                        location.setLat(place.getLatLng().latitude);
                        location.setLng(place.getLatLng().longitude);
                        locationList.add(location);
                        departPlace.setText(place.getName());

                    } else if (whichText.indexOf("도착") != -1) {
                        Location location = new Location();
                        location.setName(place.getName());
                        location.setLat(place.getLatLng().latitude);
                        location.setLng(place.getLatLng().longitude);
                        locationList.add(location);
                        destPlace.setText(place.getName());

                    } else if (whichText.indexOf("경유") != -1) {
                        for (int i = 0; i < container.getChildCount(); i++) {
                            View childView = container.getChildAt(i);

                            // 자식이 리니어 레이아웃인 경우에만 처리합니다.
                            if (childView instanceof LinearLayout) {
                                LinearLayout childLinearLayout = (LinearLayout) childView;
                                TextView stopoverPlace = childLinearLayout.findViewById(R.id.stopover_place);
                                int v = (int) stopoverPlace.getTag();
                                if (whichText.split("경유지")[1].split(" ")[0].equals(String.valueOf(v))) {
                                    Location location = new Location();
                                    location.setName(place.getName());
                                    location.setLat(place.getLatLng().latitude);
                                    location.setLng(place.getLatLng().longitude);
                                    locationList.add(location);
                                    stopoverPlace.setText(place.getName());
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onError(Status status) {
                Log.d("err", "err");
            }
        });

        stopoverAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    layoutCount++;
                    LayoutInflater inflater = LayoutInflater.from(ScheModifyActivity.this);
                    View stopover = inflater.inflate(R.layout.stopover_list_one, null);
                    TextView stopoverPlace = stopover.findViewById(R.id.stopover_place);
                    TextView title = stopover.findViewById(R.id.stopover_title);
                    TextView searchTitle = stopover.findViewById(R.id.searchTitle);
                    ImageButton stopover_close = stopover.findViewById(R.id.stopover_close);
                    title.setText("경유지" + layoutCount + " 정보");
                    stopoverPlace.setTag(layoutCount);
                    searchTitle.setText("경유지" + layoutCount + " 검색");

                    stopover_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 클릭된 버튼의 부모 레이아웃을 찾아서 삭제합니다.
                            ViewGroup parent = (ViewGroup) stopover.getParent();
                            parent.removeView(stopover);
                        }
                    });
                    // 버튼에 터치 이벤트 추가
                    stopoverPlace.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            autocompleteSupportFragment.setText("");
                            titleInput.clearFocus();
                            hashInput.clearFocus();
                            for (int i = 0; i < container.getChildCount(); i++) {
                                View childView = container.getChildAt(i);
                                // 자식이 리니어 레이아웃인 경우에만 처리합니다.
                                if (childView instanceof LinearLayout) {
                                    LinearLayout childLinearLayout = (LinearLayout) childView;
                                    TextView time = childLinearLayout.findViewById(R.id.stopover_time);
                                    time.clearFocus();
                                }
                            }
                            int clickedLayoutTag = (int) v.getTag();
                            if (searchBoxLayout.getVisibility() == View.VISIBLE) {
                                TextView which = findViewById(R.id.which);
                                if (which.getText().equals("경유지" + clickedLayoutTag + " 검색")) {
                                    searchBoxLayout.setVisibility(View.GONE);
                                } else {
                                    which.setText("경유지" + clickedLayoutTag + " 검색");
                                }
                            } else {
                                TextView which = findViewById(R.id.which);
                                which.setText("경유지" + clickedLayoutTag + " 검색");
                                searchBoxLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    // 메인 레이아웃에 추가
                    container.addView(stopover);
                    Log.d("gogo","1");
                } catch (Exception e) {
                    Log.d("zzzz", e.toString());
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView which = findViewById(R.id.which);
                which.setText("(선택된 장소 : 없음)");
                searchBoxLayout.setVisibility(View.GONE);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        departDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Time 입력 위젯에 대한 클릭 이벤트 설정
        departTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        departPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleInput.clearFocus();
                hashInput.clearFocus();
                autocompleteSupportFragment.setText("");
                for (int i = 0; i < container.getChildCount(); i++) {
                    View childView = container.getChildAt(i);
                    // 자식이 리니어 레이아웃인 경우에만 처리합니다.
                    if (childView instanceof LinearLayout) {
                        LinearLayout childLinearLayout = (LinearLayout) childView;
                        TextView time = childLinearLayout.findViewById(R.id.stopover_time);
                        time.clearFocus();
                    }
                }
                if (searchBoxLayout.getVisibility() == View.VISIBLE) {
                    TextView which = findViewById(R.id.which);
                    if (which.getText().equals("출발지 검색")) {
                        searchBoxLayout.setVisibility(View.GONE);
                    } else {
                        which.setText("출발지 검색");

                    }
                } else {
                    TextView which = findViewById(R.id.which);
                    which.setText("출발지 검색");

                    searchBoxLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        destPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleInput.clearFocus();
                hashInput.clearFocus();
                autocompleteSupportFragment.setText("");
                for (int i = 0; i < container.getChildCount(); i++) {
                    View childView = container.getChildAt(i);
                    // 자식이 리니어 레이아웃인 경우에만 처리합니다.
                    if (childView instanceof LinearLayout) {
                        LinearLayout childLinearLayout = (LinearLayout) childView;
                        TextView time = childLinearLayout.findViewById(R.id.stopover_time);
                        time.clearFocus();
                    }
                }
                if (searchBoxLayout.getVisibility() == View.VISIBLE) {
                    TextView which = findViewById(R.id.which);
                    if (which.getText().equals("도착지 검색")) {
                        searchBoxLayout.setVisibility(View.GONE);
                    } else {
                        which.setText("도착지 검색");

                    }
                } else {
                    TextView which = findViewById(R.id.which);
                    which.setText("도착지 검색");

                    searchBoxLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isEmpty = false;
                ScheduleEnroll scheduleEnroll = new ScheduleEnroll();

                String title = titleInput.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    isEmpty = true;
                }
                String hash = hashInput.getText().toString();
                if (TextUtils.isEmpty(hash)) {
                    hash = "";
                }
                scheduleEnroll.setScheduleId(scheduleId);
                scheduleEnroll.setTitle(title);
                scheduleEnroll.setHashTag(hash);
                List<Location> locations = new ArrayList<>();
                String departname = departPlace.getText().toString();
                String destname = destPlace.getText().toString();
                if (TextUtils.isEmpty(departname) || TextUtils.isEmpty(destname)) {
                    isEmpty = true;
                }

                for (Location loc : locationList) {
                    if (loc.getName().equals(departname)) {
                        Location location = new Location();
                        location.setName(departname);
                        location.setLat(loc.getLat());
                        location.setLng(loc.getLng());
                        if (TextUtils.isEmpty(departDate.getText().toString()) || TextUtils.isEmpty(departTime.getText().toString())) {
                            isEmpty = true;
                            break;
                        }
                        location.setDepartTime(LocalDateTime.of(departYear, departMonth, departDay, departHour, departMinute, 0).toString());
                        Log.d("eee", location.getDepartTime());
                        locations.add(location);
                        break;
                    }
                }

                for (int i = 0; i < container.getChildCount(); i++) {
                    View childView = container.getChildAt(i);

                    // 자식이 리니어 레이아웃인 경우에만 처리합니다.
                    if (childView instanceof LinearLayout) {
                        LinearLayout childLinearLayout = (LinearLayout) childView;
                        TextView stopoverPlace = childLinearLayout.findViewById(R.id.stopover_place);
                        String name = stopoverPlace.getText().toString();
                        for (Location loc : locationList) {
                            if (loc.getName().equals(name)) {
                                Location location = new Location();
                                location.setName(name);
                                location.setLat(loc.getLat());
                                location.setLng(loc.getLng());
                                EditText time = childLinearLayout.findViewById(R.id.stopover_time);
                                if (TextUtils.isEmpty(time.getText().toString())) {
                                    isEmpty = true;
                                    break;
                                }
                                int duration = Integer.parseInt(time.getText().toString());
                                location.setDurationMin(60 * duration);
                                locations.add(location);
                                break;
                            }
                        }
                    }
                }
                for (Location loc : locationList) {
                    if (loc.getName().equals(destname)) {
                        Location location = new Location();
                        location.setName(destname);
                        location.setLat(loc.getLat());
                        location.setLng(loc.getLng());
                        locations.add(location);
                        break;
                    }
                }
                scheduleEnroll.setLocations(locations);
                if (isEmpty) {
                    showFailedDialog("입력되지 않은 칸이 존재합니다.");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // JSON 형식으로 데이터 생성

                            String json = scheduleEnroll.toString();
                            Log.d("gogo", json);
                            // HTTP 요청 보내기
                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/?userId=" + userId;

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
                                        showToast("일정이 수정되었습니다.");
                                        SharedPreferences share = getSharedPreferences("schedule" + scheduleId, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = share.edit();
                                        editor.putInt("schedule" +scheduleId, 0);
                                        editor.apply();
                                        Intent intent = new Intent(ScheModifyActivity.this, MainPageActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        intent.putExtra("showAdd", 0);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showFailedDialog("일정 등록에 실패하였습니다.");
                                    }
                                });
                            } else {
                                Log.d("http code : ", String.valueOf(responseCode));
                            }
                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).

                        start();

            }
        });



        locationList = new ArrayList<>();
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
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                            if (i == 0) {
                                Location location = new Location();
                                location.setName(scheduleObject.get("name").getAsString());
                                location.setTime(scheduleObject.get("departTime").getAsString());
                                location.setLat(scheduleObject.get("lat").getAsDouble());
                                location.setLng(scheduleObject.get("lng").getAsDouble());
                                locationList.add(location);
                            } else if (i == jsonArray.size() - 1) {
                                Location location = new Location();
                                location.setName(scheduleObject.get("name").getAsString());
                                location.setTime(scheduleObject.get("destTime").getAsString());
                                location.setLat(scheduleObject.get("lat").getAsDouble());
                                location.setLng(scheduleObject.get("lng").getAsDouble());
                                locationList.add(location);
                            } else {
                                Location location = new Location();
                                location.setName(scheduleObject.get("name").getAsString());
                                LocalDateTime startTime = LocalDateTime.parse(scheduleObject.get("destTime").getAsString());
                                LocalDateTime endTime = LocalDateTime.parse(scheduleObject.get("departTime").getAsString());
                                Duration duration = Duration.between(startTime, endTime);


                                location.setTime(String.valueOf((int) duration.toHours()));
                               // location.setTime(scheduleObject.get("departTime").getAsString());
                                location.setLat(scheduleObject.get("lat").getAsDouble());
                                location.setLng(scheduleObject.get("lng").getAsDouble());
                                locationList.add(location);
                            }
                        }
                        for (int i = 0; i < locationList.size(); i++) {

                            int finalI = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalI == 0) {
                                        departPlace.setText(locationList.get(finalI).getName());
                                        LocalDateTime tempTime = LocalDateTime.parse(locationList.get(finalI).getTime());
                                        int hourOfDay = tempTime.getHour();
                                        int minute = tempTime.getMinute();
                                        departTime.setText(hourOfDay + "시 " + minute + "분");
                                        departHour = hourOfDay;
                                        departMinute = minute;
                                        int year =  tempTime.getYear();
                                        int month =  tempTime.getMonthValue()-1;
                                        int dayOfMonth = tempTime.getDayOfMonth();
                                        departDate.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                                        departYear = year;
                                        departMonth = month + 1;
                                        departDay = dayOfMonth;
                                    } else if (finalI == locationList.size() - 1) {
                                        destPlace.setText(locationList.get(finalI).getName());
                                    } else {
                                        layoutCount++;
                                         LayoutInflater inflater = LayoutInflater.from(ScheModifyActivity.this);
                                        View stopover = inflater.inflate(R.layout.stopover_list_one, null);
                                        TextView stopoverPlace = stopover.findViewById(R.id.stopover_place);
                                        TextView title = stopover.findViewById(R.id.stopover_title);
                                        TextView searchTitle = stopover.findViewById(R.id.searchTitle);
                                        EditText hourTime = stopover.findViewById(R.id.stopover_time);
                                        ImageButton stopover_close = stopover.findViewById(R.id.stopover_close);
                                        title.setText("경유지" + layoutCount + " 정보");
                                        stopoverPlace.setTag(layoutCount);
                                        stopoverPlace.setText(locationList.get(finalI).getName());
                                        searchTitle.setText("경유지" + layoutCount + " 검색");
                                        hourTime.setText(locationList.get(finalI).getTime());

                                        stopover_close.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                // 클릭된 버튼의 부모 레이아웃을 찾아서 삭제합니다.
                                                ViewGroup parent = (ViewGroup) stopover.getParent();
                                                parent.removeView(stopover);
                                            }
                                        });
                                        // 버튼에 터치 이벤트 추가
                                        stopoverPlace.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                autocompleteSupportFragment.setText("");
                                                int clickedLayoutTag = (int) v.getTag();
                                                if (searchBoxLayout.getVisibility() == View.VISIBLE) {
                                                    TextView which = findViewById(R.id.which);
                                                    if (which.getText().equals("경유지" + clickedLayoutTag + " 검색")) {
                                                        searchBoxLayout.setVisibility(View.GONE);
                                                    } else {
                                                        which.setText("경유지" + clickedLayoutTag + " 검색");
                                                    }
                                                } else {
                                                    TextView which = findViewById(R.id.which);
                                                    which.setText("경유지" + clickedLayoutTag + " 검색");

                                                    searchBoxLayout.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });

                                        container.addView(stopover);
                                        Log.d("gogo","2");
                                    }
                                }
                            });


                        }

                    }

                } catch (
                        Exception e) {
                    Log.d("eeee", e.toString());
                }
            }
        }).

                start();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
