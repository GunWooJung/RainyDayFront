package cap.project.rainyday;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.tool.LoginSharedPreferences;
import cap.project.rainyday.tool.SortSharedPreferences;

public class HomeFragment extends Fragment implements ItemClickListener {

    private RecyclerView recyclerView;
    private static ScheAdapter adapter;
    private List<Schedule> scheItems;

    private long userId;

    View view;

    Button addbutton;
    TextView addtextView;

    public static ScheAdapter getAdapter() {
        return adapter;
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 클릭하면 다이얼로그를 닫음
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("아니오", null)
                .create()
                .show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        userId = MainPageActivity.getUserId();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        addbutton = view.findViewById(R.id.addList);
        addtextView = view.findViewById(R.id.noListText);
        addbutton.setVisibility(View.GONE);
        addtextView.setVisibility(View.GONE);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateList(true);
    }

    @Override
    public void onItemClick(Schedule item) {
        Intent intent = new Intent(getActivity(), RouteActivity.class);
        intent.putExtra("scheduleId", item.getScheduleId());
        startActivity(intent);
    }


    @Override
    public void deleteItemClick(Schedule item, int position) {
        Log.d("ABE", "a2");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/" + item.getScheduleId();

                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // HTTP 요청 설정
                    con.setRequestMethod("DELETE");
                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                        // 정상적인 응답일 때만 데이터를 읽어옴

                        updateList(true);


                    } else {
                        // 응답이 200이 아닌 경우 에러 처리
                        Log.e("err", "HTTP error code: " + responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }


    public void updateList(Boolean fetchBackend) {
        int sort = SortSharedPreferences.getSort(getActivity().getApplicationContext());

        if(fetchBackend == false){
            if(sort == 0){ // "최근 등록 순"
                Collections.reverse(scheItems);
            }
            else if(sort == 1){ // "가까운 일정 순"
                Collections.sort(scheItems, new Schedule.ScheduleComparator());
            }
            if(scheItems.size() != 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addbutton.setVisibility(View.GONE);
                        addtextView.setVisibility(View.GONE);
                        adapter.setItems(scheItems);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scheItems.clear();
                        adapter.setItems(scheItems);
                        adapter.notifyDataSetChanged();
                        addbutton.setVisibility(View.VISIBLE);
                        addtextView.setVisibility(View.VISIBLE);

                    }
                });
            }
            return;
        }

        if (scheItems == null) {
            scheItems = new ArrayList<>();
        } else {
            scheItems.clear(); // 기존 데이터를 지웁니다.
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/?userId=" + userId;

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
                        if(sort == 0){ // "최근 등록 순"
                            Collections.reverse(scheItems);
                        }
                        else if(sort == 1){ // "가까운 일정 순"
                            Collections.sort(scheItems, new Schedule.ScheduleComparator());
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addbutton.setVisibility(View.GONE);
                                addtextView.setVisibility(View.GONE);
                                adapter.setItems(scheItems);
                                adapter.notifyDataSetChanged();

                            }
                        });

                    }
                    else if(responseCode == HttpURLConnection.HTTP_NO_CONTENT){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scheItems.clear();
                                adapter.setItems(scheItems);
                                adapter.notifyDataSetChanged();
                                addbutton.setVisibility(View.VISIBLE);
                                addtextView.setVisibility(View.VISIBLE);

                            }
                        });
                    }
                    else {
                        // 응답이 200이 아닌 경우 에러 처리
                        Log.e("err", "HTTP error code: " + responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void restoreAndUpdateList() {
        int sort = SortSharedPreferences.getSort(getActivity().getApplicationContext());
        if (scheItems == null) {
            scheItems = new ArrayList<>();
        } else {
            scheItems.clear(); // 기존 데이터를 지웁니다.
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/trash/?userId=" + userId;

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
                        if(sort == 0){ // "최근 등록 순"
                            Collections.reverse(scheItems);
                        }
                        else if(sort == 1){ // "가까운 일정 순"
                            Collections.sort(scheItems, new Schedule.ScheduleComparator());
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addbutton.setVisibility(View.GONE);
                                addtextView.setVisibility(View.GONE);
                                adapter.setItems(scheItems);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                    else if(responseCode == HttpURLConnection.HTTP_NO_CONTENT){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addbutton.setVisibility(View.GONE);
                                addtextView.setVisibility(View.GONE);
                                adapter.setItems(scheItems);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        showDialog("최근에 삭제된 일정이 없습니다.");
                    }
                    else {
                        // 응답이 200이 아닌 경우 에러 처리
                        Log.e("err", "HTTP error code: " + responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}