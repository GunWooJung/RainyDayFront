package cap.project.rainyday.model;

import com.google.gson.Gson;

public class User {
    private String id;
    private String password;
    private String name;

    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static User fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    // User 객체를 JSON 문자열로 변환하는 메서드
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
