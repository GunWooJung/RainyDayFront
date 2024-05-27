package cap.project.rainyday;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cap.project.rainyday.model.ConvertGRID;
import cap.project.rainyday.model.LatXLngY;
import cap.project.rainyday.model.Weather;
import cap.project.rainyday.weather.Location;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.getImageNum;

import android.Manifest;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WeatherFragment extends Fragment {

    private RecyclerView recyclerView;
    private static CurWeatherAdapter weatherAdapter;
    private List<Weather> weatherItemList;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationProviderClient;

    double curLat, curLng;

    TextView time, temp, percent, amount, info;
    ImageView icon;
    String cTime = "", cTemp = "", cAmount = "", cPercent = "";
    int cInfo = 0;

    LinearLayout curLayout;
    ProgressBar progressBar;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // FusedLocationProviderClient 초기화
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_cur);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        weatherAdapter = new CurWeatherAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(weatherAdapter);
        time = view.findViewById(R.id.cur_time);
        temp = view.findViewById(R.id.temp);
        percent = view.findViewById(R.id.rain_percent);
        amount = view.findViewById(R.id.rain_amount);
        info = view.findViewById(R.id.cur_weather_info);
        icon = view.findViewById(R.id.cur_weather_icon);
        curLayout = view.findViewById(R.id.mainContainer);
        curLayout.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.loader);
        progressBar.setVisibility(View.VISIBLE);
        if (fusedLocationProviderClient == null && getActivity() != null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        }
        Log.d("AAA", "x" + curLat + ",y" + curLng);
        getLocationPermission();
        return view;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                getLastLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getLastLocation() {
        if (getActivity() == null || fusedLocationProviderClient == null) {
            Log.d("AAB", "Activity or FusedLocationProviderClient is null");
            return;
        }

        try {
            if (locationPermissionGranted) {

                Task<android.location.Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<android.location.Location>() {
                    @Override
                    public void onComplete(@NonNull Task<android.location.Location> task) {
                        if (task.isSuccessful()) {
                            android.location.Location location = task.getResult();
                            if (location != null) {
                                curLat = location.getLatitude();
                                curLng = location.getLongitude();
                                Log.d("AAB", "Location obtained: Latitude - " + curLat + ", Longitude - " + curLng);


                                weatherItemList = new ArrayList<>();
                                Thread thread = new Thread(() -> {
                                    // 위치 정보를 사용하세요.
                                    LatXLngY xy = ConvertGRID.convertGRID(curLat, curLng);
                                    cap.project.rainyday.model.Location loc = new cap.project.rainyday.model.Location();
                                    loc.setNx((int) xy.x);
                                    loc.setNy((int) xy.y);
                                    loc.setRegioncode(null);
                                    cap.project.rainyday.weather.Location curLocation = new cap.project.rainyday.weather.Location(
                                            loc.getNx(), loc.getNy(), loc.getRegioncode()
                                    );
                                    ShortTermForeacast weather_f = new ShortTermForeacast(curLocation);
                                    ShortTermWeather weather_s[] = weather_f.getWeather();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd일 HH시 mm분");
                                    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd일 HH시");
                                    String check = null;
                                    LocalDateTime now = LocalDateTime.now();
                                    now = now.plusHours(1);


                                    for (ShortTermWeather weather : weather_s) {
                                        if (check != null && weather.fcst.format(formatter).equals(check))
                                            continue;

                                        Weather weatherItem = new Weather();
                                        weatherItem.setTime(weather.fcst.format(formatter));
                                        weatherItem.setTemperature(weather.tmp + "ºC");
                                        weatherItem.setRainyPercent(weather.pop + "%");
                                        weatherItem.setRainyAmount(weather.pcp);
                                        weatherItem.setWeatherInfo(weather.wCode);
                                        check = weather.fcst.format(formatter);
                                        weatherItemList.add(weatherItem);

                                        if (weather.fcst.format(formatter2).equals(now.format(formatter2))) {

                                            // 어댑터에 변경사항 알리기
                                            cTime = now.minusHours(1).format(formatter);
                                            cTemp = weatherItem.getTemperature();
                                            cAmount = weatherItem.getRainyAmount();
                                            cPercent = weatherItem.getRainyPercent();

                                            int imageNum = getImageNum.getNum(weatherItem.getWeatherInfo());
                                            switch (imageNum) {
                                                case 0:
                                                    cInfo = 0;
                                                    break;
                                                case 1:
                                                    cInfo = 1;
                                                    break;
                                                case 2:
                                                    cInfo = 2;
                                                    break;
                                                case 3:
                                                    cInfo = 3;
                                                    break;
                                                case 4:
                                                    cInfo = 4;
                                                    break;
                                                case 5:
                                                    cInfo = 5;
                                                    break;
                                            }

                                        }
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            time.setText(cTime);
                                            temp.setText(cTemp);
                                            amount.setText(cAmount);
                                            percent.setText(cPercent);

                                            switch (cInfo) {
                                                case 0:
                                                    info.setText("맑음");
                                                    icon.setImageResource(R.drawable.ic_0);
                                                    break;
                                                case 1:
                                                    info.setText("구름많음");
                                                    icon.setImageResource(R.drawable.ic_1);
                                                    break;
                                                case 2:
                                                    info.setText("비");
                                                    icon.setImageResource(R.drawable.ic_2);
                                                    break;
                                                case 3:
                                                    info.setText("눈");
                                                    icon.setImageResource(R.drawable.ic_3);
                                                    break;
                                                case 4:
                                                    info.setText("비/눈");
                                                    icon.setImageResource(R.drawable.ic_4);
                                                    break;
                                                case 5:
                                                    info.setText("소나기");
                                                    icon.setImageResource(R.drawable.ic_5);
                                                    break;
                                                default:
                                                    info.setText("맑음");
                                                    icon.setImageResource(R.drawable.ic_0);
                                            }
                                            progressBar.setVisibility(View.GONE);
                                            weatherAdapter.setWeatherList(weatherItemList);
                                            weatherAdapter.notifyDataSetChanged();
                                            curLayout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                });
                                thread.start();

                            }
                        }
                    }
                });
            }
        }catch (SecurityException e) {
            Log.e("WeatherFragment", "SecurityException: " + e.getMessage());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // onAttach에서 초기화되었는지 확인
    }
}
