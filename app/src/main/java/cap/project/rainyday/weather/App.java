import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cap.project.rainyday.weather.ShortTermWeather;
import cap.project.rainyday.weather.WeatherCode;
/*
public class App {
        public static void main(String[] args)
        {
                // test_short();
                // test_mid();
                // test_text();
                // test_maxmin();
                // test_getImageNum();
	}

        static void test_short()
        {
                ShortTermForeacast weather_f = new ShortTermForeacast(new Location(55, 127, "11D20501"));
                long startTime = System.currentTimeMillis();
                ShortTermWeather weather_s[] =  weather_f.getWeather();
                long endTime = System.currentTimeMillis();
                long timeDiff = endTime - startTime;
                System.out.println("Program Execution Time: " + timeDiff + "ms");
                //저는 for문으로 모두 출력했지만 첫번째 인덱스의 시간과 날짜를 보고 몇시간 후인지 전인지 보고 인덱스를 더하고 몇일 뒤인지에 따라 24만큼 인덱스를 더해서 빠르게 접근가능합니다
                for(ShortTermWeather weather : weather_s)
                {
                        System.out.println("날짜 : " + weather.fcst.format(DateTimeFormatter.ofPattern("ddHHmm")));
                        System.out.println("TMP 온도 : " + weather.tmp + ", POP 강수확률 : " + weather.pop + ", PCP 강수량 : " + weather.pcp + " , 날씨 : " + weather.wCode);
                }
        }
        static void test_mid()
        {
                midTermForecast midTerm = new midTermForecast(new Location(55, 127, "11B00000"));
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

        static void test_text()
        {
                midTermForecast midTerm = new midTermForecast(new Location(55, 127, "11B00000"));
                String text = midTerm.getWeather_midTerm_text("108");
                System.out.println(text);
        }
        static void test_maxmin()
        {
                midTermForecast midTerm = new midTermForecast(new Location(55, 127, "11B10101"));
                midTermTemp tmp[] = midTerm.getWeather_midTerm_highest_lowest_temp("11B10101");
                for(int i = 0; i < 8; ++i)
                {
                        System.out.println(i + "일후 최소 : " + tmp[i].min);
                        System.out.println(i + "일후 최대 : " + tmp[i].max);
                }
        }

        static void test_getImageNum()
        {
                System.out.println(getImageNum.getNum(WeatherCode.sunny));
        }
} 
//-Dfile.encoding=UTF-8 한글 꺠지면 컴파일시 추가

 */