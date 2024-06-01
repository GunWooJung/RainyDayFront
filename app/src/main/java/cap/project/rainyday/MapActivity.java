package cap.project.rainyday;

import static java.lang.System.exit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.MarkerIcons;

import android.location.Location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;

    private long scheduleId;

    List<cap.project.rainyday.model.Location> FromBackend;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ImageButton btnMyLocation;
    private NaverMap naverMap; // naverMap을 멤버 변수로 선언합니다.

    private Marker curMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        FragmentManager fm = getSupportFragmentManager();
        Intent intent = getIntent();
        scheduleId = intent.getLongExtra("scheduleId", 0);
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }


        mapFragment.getMapAsync((OnMapReadyCallback) this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); // fusedLocationProviderClient 초기화
        btnMyLocation = findViewById(R.id.button_current);
        btnMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationUpdate();
            }
        });

        // 권한 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdate();
            }
        }
    }


    private void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            moveCameraToMyLocation(latLng);
                        }
                    }
                });
    }

    private void moveCameraToMyLocation(LatLng latLng) {
        if (naverMap != null) {
            naverMap.moveCamera(CameraUpdate.scrollTo(latLng));
            if(curMarker != null){
                curMarker.setMap(null);
            }
            Marker marker = new Marker();
            marker.setPosition(latLng);
            marker.setMap(naverMap);
            curMarker = marker;
        }
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap; // 멤버 변수에 네이버 맵 객체를 저장합니다.

        new Thread() {
            public void run() {
                PathOverlay path = new PathOverlay();

                path.setWidth(20);//경로선 두깨
                path.setOutlineWidth(3);//경로선 테두리 두깨

                //패턴
//        path.setPatternImage(OverlayImage.fromResource(R.drawable.path_pattern));
//        path.setPatternInterval(10);

                //색상
                path.setColor(Color.GREEN);
                path.setPassedColor(Color.GRAY);

                //테두리 색상
                path.setOutlineColor(Color.WHITE);
                path.setPassedOutlineColor(Color.GREEN);

                Thread thread = new Thread(new Runnable() {
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
                                FromBackend = new ArrayList<>();
                                // JSON 데이터 파싱 및 리스트뷰에 추가
                                JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                                    cap.project.rainyday.model.Location location = new cap.project.rainyday.model.Location();
                                    location.setName(scheduleObject.get("name").getAsString());
                                    location.setLat(scheduleObject.get("lat").getAsDouble());
                                    location.setLng(scheduleObject.get("lng").getAsDouble());
                                    FromBackend.add(location);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    thread.start();
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                List<Location> 안에 lat lng
                /*
                double SX = 126.97714;
                double SY = 37.57152;
                double MX = 126.97822;
                double MY = 37.55855;
                double EX = 126.973858;
                double EY = 37.556250;
                List<LatLng> latLngList = new ArrayList<LatLng>();
                latLngList.add(new LatLng(SY, SX));
                setRoute(SX, SY, MX, MY, latLngList);
                setRoute(MX, MY, EX, EY, latLngList);

                 */

                List<LatLng> latLngList = new ArrayList<LatLng>();
                latLngList.add(new LatLng(FromBackend.get(0).getLat(), FromBackend.get(0).getLng()));
                for (int i = 0; i < FromBackend.size() - 1; i++) {
                    setRoute(FromBackend.get(i).getLng(),
                            FromBackend.get(i).getLat(),
                            FromBackend.get(i + 1).getLng(),
                            FromBackend.get(i + 1).getLat(),
                            latLngList);
                }
                LatLng newPosition = new LatLng(FromBackend.get(0).getLat(), FromBackend.get(0).getLng()); // 새로운 위치의 좌표
                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(newPosition);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (cap.project.rainyday.model.Location loc : FromBackend) {
                            LatLng location = new LatLng(loc.getLat(), loc.getLng());
                            Marker marker = new Marker();
                            marker.setPosition(location);
                            marker.setMap(naverMap);
                        }
                        naverMap.moveCamera(cameraUpdate);
                        path.setCoords(latLngList);
                        path.setMap(naverMap);
                    }
                });


            }
        }.start();
    }

    public static void setRoute(double SX, double SY, double EX, double EY, List<LatLng> latLngList) {
        String apiKey = "aQq8jNKXiksJaM1z4rqeo6vLiWEbTTSqc7PrJVfc";
        String urlInfo = null;
        try {
            urlInfo = "https://api.odsay.com/v1/api/searchPubTransPathT?SX=" + SX + "&SY=" + SY + "&EX=" + EX + "&EY=" + EY + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
        } catch (Exception e) {
        }

        JsonObject responseJson = getJson(urlInfo);
        if (responseJson.get("error") != null) {
            setWalking(SX, SY, EX, EY, latLngList);
            latLngList.add(new LatLng(EY, EX));
            return;
        }
        JsonObject temp = null;
        JsonArray array = null;
        Long totalTime; //소요시간(분)
        try {
            temp = (JsonObject) responseJson.get("result");
            array = temp.get("path").getAsJsonArray();
            temp = (JsonObject) array.get(0);
            temp = (JsonObject) temp.get("info");
            totalTime = temp.get("totalTime").getAsLong();
            try {
                urlInfo = "https://api.odsay.com/v1/api/loadLane?lang=0&mapObject=0:0@" + temp.get("mapObj") + "&apiKey=" + URLEncoder.encode(apiKey, "UTF-8");
            } catch (Exception e) {
            }
            responseJson = getJson(urlInfo);
            temp = (JsonObject) responseJson.get("result");
            array = temp.get("lane").getAsJsonArray();
            ;
            temp = (JsonObject) array.get(0);
            array = temp.get("section").getAsJsonArray();
            temp = (JsonObject) array.get(0);
            array = temp.get("graphPos").getAsJsonArray();
        } catch (Exception e) {
        }
        int i = 0;
        double MX = 0;
        double MY = 0;
//        latLngList.add(new LatLng(SY, SX));
        while (true) {
            try {
                temp = (JsonObject) array.get(i);
                MX = temp.get("x").getAsDouble();
                MY = temp.get("y").getAsDouble();
                if (i == 0) {
                    setWalking(SX, SY, MX, MY, latLngList);
                }
                latLngList.add(new LatLng(MY, MX));
            } catch (Exception e) {
                Log.v("test", e.toString());
                break;
            }
            ++i;
        }
        setWalking(MX, MY, EX, EY, latLngList);
        latLngList.add(new LatLng(EY, EX));
    }

    public static void setWalking(double SX, double SY, double EX, double EY, List<LatLng> latLngList) {
        String urlInfo = null;
        try {
            urlInfo = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&startX=" + SX + "&startY=" + SY + "&endX=" + EX + "&endY=" + EY + "&startName=%EC%B6%9C%EB%B0%9C&endName=%EC%B6%9C%EB%B0%9C" + "&appKey=" + URLEncoder.encode("f80KkwhNn5aVgaMxxlPV55iVYmAMHKdD9SBXX3bL", "UTF-8");
        } catch (Exception e) {
        }
        Log.d("zz", urlInfo);
        JsonObject responseJson = getJson(urlInfo);
        JsonArray array = responseJson.getAsJsonArray("features");
        int i = 0;
        int j;
        JsonObject temp = array.get(0).getAsJsonObject();
        JsonArray tempCords;
        JsonArray tempCord;
        String point = "Point";
        double MX;
        double MY;
        while (temp != null) {
            JsonObject geometry = temp.getAsJsonObject("geometry");
            tempCords = geometry.getAsJsonArray("coordinates");
            if (point.equals(geometry.getAsJsonPrimitive("type").getAsString())) {
                MX = tempCords.get(0).getAsDouble();
                MY = tempCords.get(1).getAsDouble();
                latLngList.add(new LatLng(MY, MX));
            } else {
                tempCord = tempCords.get(0).getAsJsonArray();
                j = 0;
                while (tempCords != null) {
                    MX = tempCord.get(0).getAsDouble();
                    MY = tempCord.get(1).getAsDouble();
                    latLngList.add(new LatLng(MY, MX));
                    try {
                        tempCords = tempCords.get(++j).getAsJsonArray();
                    } catch (Exception e) {
                        tempCords = null;
                        break;
                    }
                }
            }
            try {
                temp = array.get(++i).getAsJsonObject();
            } catch (Exception e) {
                temp = null;
                break;
            }
        }
    }

    public static JsonObject getJson(String api) {
        JsonObject responseJson = null;
        try {
            URI uri = new URI(api);
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int a;
            while ((a = br.read()) != -1) {
                sb.append((char) a);
            }
            br.close();
            JsonParser jsonParser = new JsonParser();
            Object obj = jsonParser.parse(sb.toString());
            responseJson = (JsonObject) obj;
        } catch (Exception e) {
            Log.e("NetworkTask", "Error", e);
        }
        return responseJson;
    }


}

