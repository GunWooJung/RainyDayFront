package cap.project.rainyday;

import static cap.project.rainyday.R.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cap.project.rainyday.R.id;
import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.model.User;
import cap.project.rainyday.tool.LoginSharedPreferences;
import cap.project.rainyday.tool.SortSharedPreferences;

public class MainPageActivity extends AppCompatActivity {

    private static long userId = 0;

    private String userName;

    public static long getUserId() {
        return userId;
    }

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    // 하단 바 구성요소
    LinearLayout homeLayout, todayLayout, plusLayout;
    ImageButton bell, menu, dots, todayImage, homeImage, plusImage, sort, backup;
    TextView todayText, homeText, plusText, username;


    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 클릭하면 다이얼로그를 닫음
                        dialog.dismiss();
                        FragmentManager fragmentManager = getSupportFragmentManager(); // 또는 getFragmentManager()을 사용할 수도 있습니다.
                        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container); // R.id.fragment_container는 프래그먼트가 호스팅되는 컨테이너의 ID입니다.

                        if (fragment instanceof HomeFragment) {
                            HomeFragment homeFragment = (HomeFragment) fragment;
                            homeFragment.restoreAndUpdateList(); // yourFunction은 실행하려는 함수명입니다.
                        }
                    }
                })
                .setNegativeButton("아니오", null)
                .create()
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.getIntExtra("showAdd", 0) == 1){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/?userId=" + userId;
                        List<Schedule> scheItems = new ArrayList<>();
                        URL obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                        // HTTP 요청 설정
                        con.setRequestMethod("GET");
                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
                            Gson gson = new Gson();
                            Log.d("ABC", response.toString());
                            JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

// 스케줄 배열을 리스트에 추가
                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                // Schedule 객체로 변환하여 리스트에 추가
                                Schedule schedule = gson.fromJson(jsonObject, Schedule.class);
                                Log.d("ssss", schedule.getTitle());
                                scheItems.add(schedule);
                            }
                            Collections.reverse(scheItems);
                            Intent intent = new Intent(MainPageActivity.this, RouteActivity.class);
                            intent.putExtra("scheduleId", scheItems.get(0).getScheduleId());
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            finish();
        }

        setContentView(layout.activity_main_page);
        userId = LoginSharedPreferences.getUserId(getApplicationContext());
        userName = LoginSharedPreferences.getUserName(getApplicationContext());
        Log.d("TEST", String.valueOf(userId));
        homeLayout = findViewById(id.homeLayout);
        todayLayout = findViewById(id.todayLayout);
        plusLayout = findViewById(id.plusLayout);

        bell = findViewById(id.bell);
        menu = findViewById(id.menu);
        dots = findViewById(id.dots);
        todayImage = findViewById(id.todayImage);
        homeImage = findViewById(id.homeImage);
        plusImage = findViewById(id.plusImage);
        backup = findViewById(id.backup);
        todayText = findViewById(id.todayText);
        homeText = findViewById(id.homeText);
        plusText = findViewById(id.plusText);
        username = findViewById(id.username);
        username.setText(userName + " 님 반갑습니다!");
        sort = findViewById(id.sort);
        // Find the views from the layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        backup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog("가장 최근에 삭제한 일정을 복구하시겠습니까?");
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 클릭한 아이템의 ID 가져오기
                int id = item.getItemId();

                // 클릭한 아이템에 따라 처리
                if (id == R.id.navigation_info) {
                    // "내 정보" 아이템을 클릭했을 때 수행할 작업
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                    builder.setTitle("내 정보");
                    builder.setMessage("이름 : " + userName);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                } else if (id == R.id.navigation_logout) {
                    // "로그아웃" 아이템을 클릭했을 때 수행할 작업
                    LoginSharedPreferences.saveUserId(getApplicationContext(), 0);
                    Intent intent = new Intent(MainPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    drawerLayout.closeDrawers();
                } else if (id == R.id.navigation_password) {
                    // "로그아웃" 아이템을 클릭했을 때 수행할 작업
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                    builder.setTitle("비밀번호 변경");
                    builder.setMessage("변경할 비밀 번호를 입력하세요.");

                    final EditText input = new EditText(MainPageActivity.this);
                    builder.setView(input);

                    builder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String userInput = input.getText().toString();
                            if (userInput.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "입력되지 않아 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // JSON 형식으로 데이터 생성
                                        User user = new User();
                                        user.setPassword(userInput);
                                        String json = user.toJson();

                                        // HTTP 요청 보내기
                                        try {
                                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/user/" + userId + "/password";
                                            URL obj = new URL(url);
                                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                            // HTTP 요청 설정
                                            con.setRequestMethod("PUT");
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
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "잘못된 요청입니다.", Toast.LENGTH_SHORT).show();
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

                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                // 추가적인 아이템에 대한 처리 추가 가능
                // 네비게이션 뷰 닫기
                return true;
            }
        });

        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, BellActivity.class);
                startActivity(intent);
            }
        });

        dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainPageActivity.this, v);
                // 팝업 메뉴에 아이템 추가
                popupMenu.getMenu().add("전체 일정 삭제");
                popupMenu.getMenu().add("가장 맑은 날 찾기");
                popupMenu.getMenu().add("놀러갈 곳 추천");
                popupMenu.getMenu().add("자주 방문한 장소 목록");

                // 팝업 메뉴 아이템 클릭 리스너 설정
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 클릭된 아이템에 따라 처리
                        switch (item.getTitle().toString()) {
                            case "전체 일정 삭제":
                                // 기능1 선택 시 실행할 코드
                                Toast.makeText(MainPageActivity.this, "기능1 선택", Toast.LENGTH_SHORT).show();
                                return true;
                            case "가장 맑은 날 찾기":
                                // 기능2 선택 시 실행할 코드
                                Toast.makeText(MainPageActivity.this, "기능2 선택", Toast.LENGTH_SHORT).show();
                                return true;
                            case "자주 방문한 장소 목록":
                                // 기능3 선택 시 실행할 코드
                                Toast.makeText(MainPageActivity.this, "기능3 선택", Toast.LENGTH_SHORT).show();
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

        plusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, ScheduleAddActivity.class);
                startActivity(intent);
            }
        });

        plusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, ScheduleAddActivity.class);
                startActivity(intent);
            }
        });
        homeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
            }
        });
        homeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
            }
        });


        todayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new WeatherFragment());
            }
        });

        todayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new WeatherFragment());
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRadioDialog();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view))) {
                    drawerLayout.closeDrawer(findViewById(R.id.nav_view));
                } else {
                    drawerLayout.openDrawer(findViewById(R.id.nav_view));
                }
            }
        });


        // 앱 시작 시 기본적으로 표시될 프래그먼트 설정
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(id.fragment_container, fragment);
        fragmentTransaction.commit();
    }


    private void openRadioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("정렬 기준");

        // 라디오 버튼을 추가합니다.
        String[] items = {"최근 등록 순", "가까운 일정 순"};
        int checkedItem = SortSharedPreferences.getSort(getApplicationContext()); // 기본 선택 항목 설정
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SortSharedPreferences.saveSort(getApplicationContext(), which);
                dialog.dismiss();

                FragmentManager fragmentManager = getSupportFragmentManager(); // 또는 getFragmentManager()을 사용할 수도 있습니다.
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container); // R.id.fragment_container는 프래그먼트가 호스팅되는 컨테이너의 ID입니다.

                if (fragment instanceof HomeFragment) {
                    HomeFragment homeFragment = (HomeFragment) fragment;
                    homeFragment.updateList(false); // yourFunction은 실행하려는 함수명입니다.
                }
            }
        });

        // "확인" 버튼을 눌렀을 때의 동작을 정의합니다.

        // 다이얼로그를 생성하고 표시합니다.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId == 0) {
            userId = LoginSharedPreferences.getUserId(getApplicationContext());
            userName = LoginSharedPreferences.getUserName(getApplicationContext());
        }
    }


}

