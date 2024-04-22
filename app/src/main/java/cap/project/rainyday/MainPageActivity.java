package cap.project.rainyday;

import static cap.project.rainyday.R.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cap.project.rainyday.R.id;
import cap.project.rainyday.tool.LoginSharedPreferences;
import cap.project.rainyday.tool.SortSharedPreferences;

public class MainPageActivity extends AppCompatActivity {

    private static long userId = 0;

    private String userName;

    public static long getUserId() {
        return userId;
    }

    // 하단 바 구성요소
    LinearLayout homeLayout, todayLayout, plusLayout;
    ImageButton bell, menu, dots, todayImage, homeImage, plusImage, sort;
    TextView todayText, homeText, plusText, username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        todayText = findViewById(id.todayText);
        homeText = findViewById(id.homeText);
        plusText = findViewById(id.plusText);
        username = findViewById(id.username);
        username.setText(userName+" 님 반갑습니다!");
        sort = findViewById(R.id.sort);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 네비게이션 뷰를 표시합니다.

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
                popupMenu.getMenu().add("기능1");
                popupMenu.getMenu().add("기능2");
                popupMenu.getMenu().add("기능3");
                // 팝업 메뉴 아이템 클릭 리스너 설정
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 클릭된 아이템에 따라 처리
                        switch (item.getTitle().toString()) {
                            case "기능1":
                                // 기능1 선택 시 실행할 코드
                                Toast.makeText(MainPageActivity.this, "기능1 선택", Toast.LENGTH_SHORT).show();
                                return true;
                            case "기능2":
                                // 기능2 선택 시 실행할 코드
                                Toast.makeText(MainPageActivity.this, "기능2 선택", Toast.LENGTH_SHORT).show();
                                return true;
                            case "기능3":
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

