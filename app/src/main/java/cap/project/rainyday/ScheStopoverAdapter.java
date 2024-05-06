package cap.project.rainyday;

import android.content.DialogInterface;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.model.Stopover;

public class ScheStopoverAdapter extends RecyclerView.Adapter<ScheStopoverAdapter.ViewHolder> {

    private List<Stopover> Items;
    private StopoverClickListener listener;

    public ScheStopoverAdapter(List<Stopover> Items, StopoverClickListener listener) {
        this.Items = Items;
        this.listener = listener;
    }

    public void setItems(List<Stopover> items) {
        Items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stopover_list_one, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stopover item = Items.get(position);
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
            super(itemView);/*
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
            });*/
        }

        public void bind(Stopover item ,int position) {
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Stopover item = Items.get(position);
            listener.onItemClick(item);
        }


        public void deleteItemClick() {
            int position = getAdapterPosition();
            Stopover item = Items.get(position);
            listener.deleteItemClick(item, position);
        }

    }
}
