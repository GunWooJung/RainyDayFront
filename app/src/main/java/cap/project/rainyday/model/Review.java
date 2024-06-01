package cap.project.rainyday.model;

import com.google.gson.Gson;

public class Review {
    private String location;

    private String contents;
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
