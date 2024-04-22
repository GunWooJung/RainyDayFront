package cap.project.rainyday;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cap.project.rainyday.model.Schedule;

public interface ItemClickListener {
    void onItemClick(Schedule item);
    void deleteItemClick( Schedule item, int position);
}
