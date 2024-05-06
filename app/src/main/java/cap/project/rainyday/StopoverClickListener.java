package cap.project.rainyday;

import cap.project.rainyday.model.Schedule;
import cap.project.rainyday.model.Stopover;

public interface StopoverClickListener {

    void onItemClick(Stopover item);
    void deleteItemClick(Stopover item, int position);
}
