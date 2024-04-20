package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.view.menu.MenuAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.tool.LoginSharedPreferences;

public class HomeFragment extends Fragment implements ItemClickListener  {

    private RecyclerView recyclerView;
    private ScheAdapter adapter;
    private List<Schedule> scheItems;

    private long userId;

    View view;

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
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (scheItems == null) {
            scheItems = new ArrayList<>();
        } else {
            scheItems.clear(); // 기존 데이터를 지웁니다.
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/?userId="+userId;

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
                        Log.d("ABC" , response.toString());
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

// 스케줄 배열을 리스트에 추가
                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();
                            // Schedule 객체로 변환하여 리스트에 추가
                            Schedule schedule = gson.fromJson(jsonObject, Schedule.class);
                            Log.d("ssss", schedule.getTitle());
                            scheItems.add(schedule);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setItems(scheItems);
                                adapter.notifyDataSetChanged();
                            }
                        });

                    } else {
                        // 응답이 200이 아닌 경우 에러 처리
                        Log.e("err", "HTTP error code: " + responseCode);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onItemClick(Schedule item) {

    }
}