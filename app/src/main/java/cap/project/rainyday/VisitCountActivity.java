package cap.project.rainyday;

import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cap.project.rainyday.model.VisitLocation;
import cap.project.rainyday.tool.LocationManager;

public class VisitCountActivity extends AppCompatActivity {
    ImageButton back;

    Button backButton;

    private ListView listView;
    private ArrayList<String> locationList;
    private ArrayAdapter<String> adapter;

    ImageView trash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitcount);
        back = findViewById(R.id.backhome);
        backButton = findViewById(R.id.backbutton);
        listView = findViewById(R.id.listView);
        locationList = new ArrayList<>();
        trash = findViewById(R.id.trash2);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext());
                builder2.setTitle("방문 기록 삭제")
                        .setMessage("방문 기록을 정말로 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocationManager.deleteAllSavedLocationInformation(getApplicationContext());
                                locationList = new ArrayList<>();
                                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, locationList);
                                listView.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton("아니오", null) // 사용자가 "아니오"를 선택한 경우 아무 작업도 수행하지 않음
                        .show();
            }
        });
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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);

        // Set the adapter to the ListView


        // Example usage of loading all location visit counts
        HashMap<String, Integer> allLocations = LocationManager.loadAllLocationVisitCounts(this);
        List<VisitLocation> temp = new ArrayList<>();
        // Display loaded visit counts (optional)
        for (Map.Entry<String, Integer> entry : allLocations.entrySet()) {
            VisitLocation v = new VisitLocation();
            v.setName(entry.getKey());
            v.setVisit(entry.getValue());
            temp.add(v);
        }
        Collections.sort(temp);
        for(VisitLocation loc : temp){
            locationList.add(loc.toString());
        }
        listView.setAdapter(adapter);
    }
}
