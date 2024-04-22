package cap.project.rainyday;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import cap.project.rainyday.model.Schedule;

public class ScheAdapter extends RecyclerView.Adapter<ScheAdapter.ViewHolder> {

    private List<Schedule> Items;
    private ItemClickListener listener;

    public ScheAdapter(List<Schedule> Items, ItemClickListener listener) {
        this.Items = Items;
        this.listener = listener;
    }

    public void setItems(List<Schedule> items) {
        Items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sche_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule item = Items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView departName;
        private TextView departTime;
        private TextView DDAY;
        private TextView hashTag;

        private ImageButton sche_dots;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            departName = itemView.findViewById(R.id.departname);
            departTime = itemView.findViewById(R.id.departTime);
            DDAY = itemView.findViewById(R.id.dday);
            hashTag = itemView.findViewById(R.id.tag);
            itemView.setOnClickListener(this);
            sche_dots = itemView.findViewById(R.id.sche_dots);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    Schedule item = Items.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("일정 삭제")
                            .setMessage(item.getTitle()+" 일정을 정말로 삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 사용자가 "예"를 선택한 경우
                                    deleteItemClick();
                                }
                            })
                            .setNegativeButton("아니오", null) // 사용자가 "아니오"를 선택한 경우 아무 작업도 수행하지 않음
                            .show();
                    return true;
                }
            });
        }

        public void bind(Schedule item ,int position) {
            LocalDateTime time = LocalDateTime.parse(item.getDepartTime());
            LocalDateTime currentTime = LocalDateTime.now();
            long daysDifference = ChronoUnit.DAYS.between(time.toLocalDate(), currentTime.toLocalDate());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
            String formattedTime = time.format(formatter);

            title.setText(item.getTitle());
            departName.setText(item.getDepartName());
            departTime.setText(formattedTime + " 출발");
            Log.d("aaaa", String.valueOf(daysDifference));
            String ddaytoString;
            if (daysDifference > 0) {
                ddaytoString = "D + " + String.valueOf(daysDifference);
            } else if (daysDifference == 0) {
                ddaytoString = "DDAY";
            } else {
                daysDifference = -daysDifference;
                ddaytoString = "D -  " + String.valueOf(daysDifference);
            }

            DDAY.setText(ddaytoString);
            hashTag.setText(item.getHashTag());


            sche_dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    // 팝업 메뉴에 아이템 추가
                    popupMenu.getMenu().add("비 예보 설정");
                    popupMenu.getMenu().add("일정 보기");
                    popupMenu.getMenu().add("일정 수정");
                    popupMenu.getMenu().add("일정 삭제");
                    popupMenu.getMenu().add("공유 하기");
                    // 팝업 메뉴 아이템 클릭 리스너 설정
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // 클릭된 아이템에 따라 처리
                            switch (menuItem.getTitle().toString()) {
                                case "일정 보기":
                                    // 기능1 선택 시 실행할 코드
                                    listener.onItemClick(item);
                                    return true;
                                case "비 예보 설정":
                                    // 기능1 선택 시 실행할 코드
                                    Toast.makeText(v.getContext(), item.getScheduleId() + "비 예보 설정", Toast.LENGTH_SHORT).show();
                                    return true;
                                case "일정 수정":
                                    // 기능2 선택 시 실행할 코드
                                    Toast.makeText(v.getContext(), item.getScheduleId() + "일정 수정", Toast.LENGTH_SHORT).show();
                                    return true;
                                case "공유 하기":
                                    // 기능2 선택 시 실행할 코드
                                    Toast.makeText(v.getContext(), item.getScheduleId() + "공유하기", Toast.LENGTH_SHORT).show();
                                    return true;
                                case "일정 삭제":
                                    // 기능3 선택 시 실행할 코드
                                   // Toast.makeText(v.getContext(), item.getScheduleId() + "일정 삭제", Toast.LENGTH_SHORT).show();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setTitle("일정 삭제")
                                            .setMessage(item.getTitle()+" 일정을 정말로 삭제하시겠습니까?")
                                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 사용자가 "예"를 선택한 경우
                                                    deleteItemClick();
                                                }
                                            })
                                            .setNegativeButton("아니오", null) // 사용자가 "아니오"를 선택한 경우 아무 작업도 수행하지 않음
                                            .show();
                                    Log.d("ABE" , "a0");
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

        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Schedule item = Items.get(position);
            listener.onItemClick(item);
        }


        public void deleteItemClick() {
            int position = getAdapterPosition();
            Schedule item = Items.get(position);
            listener.deleteItemClick(item, position);
            Log.d("ABE" , "a1");
        }

    }
}
