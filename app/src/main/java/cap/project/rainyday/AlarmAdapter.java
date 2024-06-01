package cap.project.rainyday;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import cap.project.rainyday.model.AlarmMessage;
import cap.project.rainyday.model.Schedule;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<AlarmMessage> Items;

    public AlarmAdapter(List<AlarmMessage> Items) {
        // this.context = context;
        this.Items = Items;
    }
    public void setItems(List<AlarmMessage> items) {
        this.Items = items;
    }

    @NonNull
    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bell_list, parent, false);
        return new AlarmAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AlarmAdapter.ViewHolder holder, int position) {
        AlarmMessage item = Items.get(position);
        holder.bind(item, position);
    }
    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView bell_sche_title, bell_explain, bell_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bell_sche_title = itemView.findViewById(R.id.bell_sche_title);
            bell_explain = itemView.findViewById(R.id.bell_explain);
            bell_time = itemView.findViewById(R.id.bell_time);
            Log.d("AAA1", "a");

        }
        public void bind(AlarmMessage item, int position) {
            try {
                bell_sche_title.setText(item.getTitle());
                bell_explain.setText(item.getContent());
                Log.d("AAA1", item.getTitle());
                Log.d("AAA1", item.getContent());
                LocalDateTime time = LocalDateTime.parse(item.getReceviedTime());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd일 HH시 mm분");
                String formattedDateTime = time.format(formatter);
                bell_time.setText(formattedDateTime);
                Log.d("AAA1",formattedDateTime);
            }catch (Exception e){
                Log.d("AAA", e.toString());
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
