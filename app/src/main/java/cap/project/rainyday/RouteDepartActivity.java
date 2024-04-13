package cap.project.rainyday;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RouteDepartActivity extends AppCompatActivity {
    private TextView dateTextView;
    private TextView timeTextView;

    private TextView selectedView;

    private long scheduleId;

    private int departYear;
    private int departMonth;
    private int departDay;
    private int departHour;
    private int departMinute;

    String departName;
    double departLat;
    double departLng;

    String departAddress;

    Boolean selectedPlace = false;
    Boolean selectedDate= false;

    Boolean selectedTime= false;

    private void showDatePickerDialog () {
        // 현재 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 선택한 날짜 TextView에 표시
                        dateTextView.setText("날짜 : "+year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                        departYear = year;
                        departMonth = month+1;
                        departDay = dayOfMonth;
                        selectedDate = true;
                    }
                }, year, month, dayOfMonth);

        // DatePickerDialog 표시
        datePickerDialog.show();
    }

    // TimePickerDialog 표시 메서드
    private void showTimePickerDialog () {
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
                        timeTextView.setText("시간 : "+hourOfDay + "시 " + minute + "분");
                        departHour = hourOfDay;
                        departMinute = minute;
                        selectedTime = true;
                    }
                }, hour, minute, true); // 마지막 매개변수는 24시간 표시 여부

        // TimePickerDialog 표시
        timePickerDialog.show();
    }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_depart);
        dateTextView = findViewById(R.id.date_depart);
        timeTextView = findViewById(R.id.time_depart);
        selectedView = findViewById(R.id.selected_depart);
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBI_xKrXy81n7ELWopYZi15QMKJ0rQrL6Q");
        }
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_depart);
        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        autocompleteSupportFragment.setPlaceFields(fields);

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (place.getLatLng() != null) {
                    departName = place.getName();
                    departLat = place.getLatLng().latitude;
                    departLng = place.getLatLng().longitude;
                    departAddress = place.getAddress();
                    selectedView.setText("선택된 장소 : "+departName);
                    selectedPlace = true;
                }
            }

            @Override
            public void onError(Status status) {
                Log.d("err", "err");
            }
        });

        Button button = findViewById(R.id.button_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDate == false || selectedPlace == false || selectedTime == false) {
                    showLoginFailedDialog("입력되지 않은 칸이 존재합니다.");
                    return;
                }
                Intent intent = new Intent(RouteDepartActivity.this, RouteDestActivity.class);
                intent.putExtra("scheduleId", scheduleId);
                intent.putExtra("departYear", departYear);
                intent.putExtra("departMonth", departMonth);
                intent.putExtra("departDay", departDay);
                intent.putExtra("departHour", departHour);
                intent.putExtra("departMinute", departMinute);
                intent.putExtra("departName", departName);
                intent.putExtra("departLat", departLat);
                intent.putExtra("departLng", departLng);
                intent.putExtra("departAddress", departAddress);
                startActivity(intent);
            }
        });



        // Date 입력 위젯에 대한 클릭 이벤트 설정
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Time 입력 위젯에 대한 클릭 이벤트 설정
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // DatePickerDialog 표시 메서드

    }
}
