package cap.project.rainyday.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Route {

    private long routeId;




    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }


    public void setDepartYear(int departYear) {
        this.departYear = departYear;
    }

    public void setDepartMonth(int departMonth) {
        this.departMonth = departMonth;
    }

    public void setDepartDay(int departDay) {
        this.departDay = departDay;
    }

    public void setDepartHour(int departHour) {
        this.departHour = departHour;
    }

    public void setDepartMinute(int departMinute) {
        this.departMinute = departMinute;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public void setDepartLat(double departLat) {
        this.departLat = departLat;
    }

    public void setDepartLng(double departLng) {
        this.departLng = departLng;
    }

    public void setDepartAddress(String departAddress) {
        this.departAddress = departAddress;
    }

    public void setDepartNx(int departNx) {
        this.departNx = departNx;
    }

    public void setDepartNy(int departNy) {
        this.departNy = departNy;
    }

    public void setDepartRegioncode(String departRegioncode) {
        this.departRegioncode = departRegioncode;
    }

    public void setDestYear(int destYear) {
        this.destYear = destYear;
    }

    public void setDestMonth(int destMonth) {
        this.destMonth = destMonth;
    }

    public void setDestDay(int destDay) {
        this.destDay = destDay;
    }

    public void setDestHour(int destHour) {
        this.destHour = destHour;
    }

    public void setDestMinute(int destMinute) {
        this.destMinute = destMinute;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public void setDestLng(double destLng) {
        this.destLng = destLng;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public void setDestNx(int destNx) {
        this.destNx = destNx;
    }

    public void setDestNy(int destNy) {
        this.destNy = destNy;
    }

    public void setDestRegioncode(String destRegioncode) {
        this.destRegioncode = destRegioncode;
    }

    private int departYear;

    private int departMonth;


    private int departDay;


    private int departHour;


    private int departMinute;


    private String departName;


    private double departLat;


    private double departLng;


    private String departAddress;


    private int departNx;


    private int departNy;


    private String departRegioncode;



    private int destYear;


    private int destMonth;


    private int destDay;


    private int destHour;


    private int destMinute;


    private String destName;


    private double destLat;


    private double destLng;


    private String destAddress;


    private int destNx;

    private int destNy;

    private String destRegioncode;

    public String toStringDepart() {

        LocalDateTime departDateTime = LocalDateTime.of(departYear, departMonth, departDay, departHour, departMinute);
        // 도착 시간 LocalDateTime 객체 생성

        // 출발지 정보 문자열 생성
        String departInfo = "출발지: " + departName + "\n" +
                "출발 시간: " + departDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")) + "\n" +
                "출발지 위도: " + departLat + "\n" +
                "출발지 경도: " + departLng + "\n" +
                "출발지 주소: " + departAddress + "\n" +
                "출발지 Nx: " + departNx + "\n" +
                "출발지 Ny: " + departNy + "\n" +
                "출발지 지역코드: " + departRegioncode + "\n";

        // 도착지 정보 문자열 생성

        // 출발지 정보와 도착지 정보를 결합하여 반환
        return departInfo ;
    }

    public String toStringDest() {

        LocalDateTime destDateTime = LocalDateTime.of(destYear, destMonth, destDay, destHour, destMinute);

        // 출발지 정보 문자열 생성
        String destInfo = "도착지: " + destName + "\n" +
                "(예상)도착 시간: " + destDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")) + "\n" +
                "도착지 위도: " + destLat + "\n" +
                "도착지 경도: " + destLng + "\n" +
                "도착지 주소: " + destAddress + "\n" +
                "도착지 Nx: " + destNx + "\n" +
                "도착지 Ny: " + destNy + "\n" +
                "도착지 지역코드: " + destRegioncode + "\n";

        // 출발지 정보와 도착지 정보를 결합하여 반환
        return  destInfo;
    }
}
