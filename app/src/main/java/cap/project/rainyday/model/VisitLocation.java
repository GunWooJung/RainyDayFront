package cap.project.rainyday.model;

import androidx.annotation.NonNull;

public class VisitLocation implements Comparable {

    String name;

    int visit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    @NonNull
    @Override
    public String toString() {
        return "["+this.name+"]   방문 "+visit+"회";
    }


    @Override
    public int compareTo(Object o) {
        if(o instanceof VisitLocation) {
            if (this.visit > ((VisitLocation) o).getVisit())
                return -1;
        }
        return 1;
    }
}
