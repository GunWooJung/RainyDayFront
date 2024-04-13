package cap.project.rainyday;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cap.project.rainyday.data.model.Schedule;

public class HomeFragment extends Fragment {

    private ArrayList<String> scheduleList;
    private ArrayAdapter<String> adapter;
    private ListView listViewSchedule;

    private ArrayList<Schedule> scheduleListFromBackend;

    private long userId;

    View view;

    public HomeFragment(long userId) {
        new HomeFragment();
        this.userId = userId;
    }
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        // 액티비티가 화면에 나타날 때마다 실행할 코드 작성
        // 예: 데이터 새로고침, 사용자 인터페이스 업데이트 등

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        scheduleList = new ArrayList<>();
        scheduleListFromBackend = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, scheduleList);
        listViewSchedule = view.findViewById(R.id.scheduleListView);
        listViewSchedule.setAdapter(adapter);
        listViewSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(requireActivity(), RouteActivity.class);
                long scheduleId = scheduleListFromBackend.get(position).getScheduleId();
                Log.d("gogo" , String.valueOf(scheduleId));
                intent.putExtra("scheduleId", scheduleId);
                startActivity(intent);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/load?userId="+userId;
                    //String url = "http://192.168.219.153:80/schedule/load?userId="+userId;;
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

                        // JSON 데이터 파싱 및 리스트뷰에 추가
                        JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject scheduleObject = jsonArray.get(i).getAsJsonObject();
                            long scheduleId = scheduleObject.get("scheduleId").getAsLong();
                            String title = scheduleObject.get("title").getAsString();
                            Schedule schedule = new Schedule();
                            schedule.setScheduleId(scheduleId);
                            schedule.setTitle(title);
                            scheduleListFromBackend.add(schedule);
                            scheduleList.add(schedule.toString());
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
}