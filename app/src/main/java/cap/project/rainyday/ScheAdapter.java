package cap.project.rainyday;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.tool.NotificationReceiver;

public class ScheAdapter extends RecyclerView.Adapter<ScheAdapter.ViewHolder> {

    private List<Schedule> Items;
    private ItemClickListener listener;

    //private Context context;

    public ScheAdapter(List<Schedule> Items, ItemClickListener listener) {
        // this.context = context;
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



    public void cancelAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title;
        private TextView departName;
        private TextView departTime;
        private TextView DDAY;
        private TextView hashTag;

        private ImageButton sche_dots;
        ImageButton map, bellButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            departName = itemView.findViewById(R.id.departname);
            departTime = itemView.findViewById(R.id.departTime);
            DDAY = itemView.findViewById(R.id.dday);
            hashTag = itemView.findViewById(R.id.tag);
            itemView.setOnClickListener(this);
            sche_dots = itemView.findViewById(R.id.sche_dots);
            map = itemView.findViewById(R.id.mapButton);
            bellButton = itemView.findViewById(R.id.bellButton);


            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Schedule item = Items.get(position);
                    listener.onItemMapClick(item);
                    // 기능1 선택 시 실행할 코드
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    Schedule item = Items.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("일정 삭제")
                            .setMessage(item.getTitle() + " 일정을 정말로 삭제하시겠습니까?")
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

        public void bind(Schedule item, int position) {
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
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("schedule" + item.getScheduleId(), Context.MODE_PRIVATE);
            int bell_on = sharedPreferences.getInt("schedule" + item.getScheduleId(), 0);
            if (bell_on == 1) {
                bellButton.setImageResource(R.drawable.ic_bellon);
            }
            else{
                bellButton.setImageResource(R.drawable.ic_belloff);
            }

            sche_dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                    // 팝업 메뉴에 아이템 추가
                    //popupMenu.getMenu().add("비 예보 설정");
                    popupMenu.getMenu().add("일정 보기");
                    popupMenu.getMenu().add("일정 수정");
                    popupMenu.getMenu().add("일정 삭제");
                    popupMenu.getMenu().add("공유 하기");
                    popupMenu.getMenu().add("리뷰 남기기");
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

                                case "리뷰 남기기":
                                    // 기능1 선택 시 실행할 코드
                                    listener.onItemReview(item);
                                    return true;
                                case "일정 수정":
                                    // 기능2 선택 시 실행할 코드
                                    listener.onItemModify(item);
                                    return true;
                                case "공유 하기":
                                    // 기능2 선택 시 실행할 코드
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setTitle("일정 공유하기");
                                    builder.setMessage("공유할 사람의 이름을 입력하세요.");

                                    final EditText input = new EditText(v.getContext());
                                    builder.setView(input);

                                    builder.setPositiveButton("공유", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String userInput = input.getText().toString();
                                            if (userInput.isEmpty()) {
                                                Toast.makeText(v.getContext(), "입력되지 않아 취소되었습니다.", Toast.LENGTH_SHORT).show();
                                            } else {

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        // HTTP 요청 보내기
                                                        try {
                                                            String url = "http://ec2-54-144-194-174.compute-1.amazonaws.com/schedule/" + item.getScheduleId() + "/share?name=" + userInput;
                                                            URL obj = new URL(url);
                                                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                                                            // HTTP 요청 설정
                                                            con.setRequestMethod("GET");
                                                            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                                                            // 응답 받기
                                                            int responseCode = con.getResponseCode();
                                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                ((Activity) v.getContext()).runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(v.getContext(), "일정이 공유되었습니다.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else {
                                                                ((Activity) v.getContext()).runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext());
                                                                        builder2.setTitle("공유 실패")
                                                                                .setMessage("올바르지 않은 이름입니다.")
                                                                                .setPositiveButton("예", null)
                                                                                .show();
                                                                    }
                                                                });

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
                                    return true;
                                case "일정 삭제":
                                    // 기능3 선택 시 실행할 코드
                                    // Toast.makeText(v.getContext(), item.getScheduleId() + "일정 삭제", Toast.LENGTH_SHORT).show();

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(v.getContext());
                                    builder2.setTitle("일정 삭제")
                                            .setMessage(item.getTitle() + " 일정을 정말로 삭제하시겠습니까?")
                                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // 사용자가 "예"를 선택한 경우
                                                    deleteItemClick();
                                                }
                                            })
                                            .setNegativeButton("아니오", null) // 사용자가 "아니오"를 선택한 경우 아무 작업도 수행하지 않음
                                            .show();
                                    //Log.d("ABE" , "a0");
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

            bellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("schedule" + item.getScheduleId(), Context.MODE_PRIVATE);
                    int bell_on = sharedPreferences.getInt("schedule" + item.getScheduleId(), 0);

                    if (bell_on == 1) {
                        SharedPreferences share = itemView.getContext().getSharedPreferences("schedule" + item.getScheduleId(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();
                        editor.putInt("schedule" + item.getScheduleId(), 0);
                        editor.apply();

                        bellButton.setImageResource(R.drawable.ic_belloff);
                        cancelAlarm(itemView.getContext(), (int) item.getScheduleId());
                        Toast.makeText(v.getContext(), "일정 알림이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    } else if (bell_on == 0) {
                        SharedPreferences share = itemView.getContext().getSharedPreferences("schedule" + item.getScheduleId(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = share.edit();
                        editor.putInt("schedule" + item.getScheduleId(), 1);
                        editor.apply();

                        bellButton.setImageResource(R.drawable.ic_bellon);
                        LocalDateTime departTime = LocalDateTime.parse(item.getDepartTime());
                        if (itemView.getContext() instanceof MainPageActivity) {
                            ((MainPageActivity) itemView.getContext()).scheduleNotification(itemView.getContext(), departTime, item.getTitle(), item.getScheduleId());

                        } else {
                            // 현재 컨텍스트가 MainActivity가 아닌 경우
                        }
                        Toast.makeText(v.getContext(), "일정 알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                    }

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
            Log.d("ABE", "a1");
        }

    }
}
