/*
package cap.project.rainyday.weather;

import org.json.JSONException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cap.project.rainyday.weather.MidTermWeather;
import cap.project.rainyday.weather.ShortTermForeacast;
import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.midTermForecast;

public class App {

    public static void main(String[] args) throws JSONException {
        // System.out.println("한글");
        ShortTermForeacast weather_f = new ShortTermForeacast(new Location(55, 127, "11D20501"));
        ShortTermWeather weather_s[] =  weather_f.getWeather();
        //저는 for문으로 모두 출력했지만 첫번째 인덱스의 시간과 날짜를 보고 몇시간 후인지 전인지 보고 인덱스를 더하고 몇일 뒤인지에 따라 24만큼 인덱스를 더해서 빠르게 접근가능합니다
        for(ShortTermWeather weather : weather_s)
        {
            System.out.print("날짜 : ");
            System.out.println(weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm")));
            System.out.print("TMP 온도 : ");
            System.out.print(weather.tmp);
            System.out.print(", POP 강수확률 : ");
            System.out.print(weather.pop);
            System.out.print(", PCP 강수량 : ");
            System.out.print(weather.pcp);
            System.out.println();
        }
        midTermForecast midTerm = new midTermForecast(LocalDateTime.of(2024, 4, 12, 18, 0), new Location(55, 127, "11B00000"));
        //중기 예보의 경우 0600 1800에만 발표
        // midTerm.getWeather_midTerm();
        MidTermWeather weather_m[] = midTerm.getWeather_midTerm_get_all();
        //0 인덱스부터 3일후 7인덱스가 10일 후 입니다
        for(int i = 0; i < 8; ++i)
        {
            System.out.println((i + 3) + "일 후 오전 강수확률 :" + weather_m[i].rnStAm);
            System.out.println((i + 3) + "일 후 오전 날씨 :" + weather_m[i].wfAm);
            System.out.println((i + 3) + "일 후 오후 강수확률 :" + weather_m[i].rnStPm);
            System.out.println((i + 3) + "일 후 오후 날씨 :" + weather_m[i].wfPm);
        }
    }
}
//-Dfile.encoding=UTF-8 한글 꺠지면 컴파일시 추가*/
