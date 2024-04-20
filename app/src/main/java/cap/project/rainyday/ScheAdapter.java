package cap.project.rainyday;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.bind(item);
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            departName =  itemView.findViewById(R.id.departname);
            departTime = itemView.findViewById(R.id.departTime);
            DDAY =  itemView.findViewById(R.id.dday);
            hashTag = itemView.findViewById(R.id.tag);
            itemView.setOnClickListener(this);
        }

        public void bind(Schedule item) {

            LocalDateTime time = LocalDateTime.parse(item.getDepartTime());
            LocalDateTime currentTime = LocalDateTime.now();
            Duration duration = Duration.between(time, currentTime);
            long daysDifference = ChronoUnit.DAYS.between(currentTime, time);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
            String formattedTime = time.format(formatter);

            title.setText(item.getTitle());
            departName.setText(item.getDepartName());
            departTime.setText(formattedTime);
            Log.d("aaaa", String.valueOf(daysDifference));
            DDAY.setText("D - "+String.valueOf(daysDifference));
            hashTag.setText(item.getHashTag());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Schedule item = Items.get(position);
            listener.onItemClick(item);
        }
    }
}
