package cap.project.rainyday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cap.project.rainyday.model.Weather;
import cap.project.rainyday.weather.getImageNum;

public class CurWeatherAdapter extends RecyclerView.Adapter<CurWeatherAdapter.ViewHolder> {
    private Context context;
    private List<Weather> weatherList;

    public CurWeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    public void setWeatherList(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @Override
    public void onBindViewHolder(@NonNull CurWeatherAdapter.ViewHolder holder, int position) {
        Weather item = weatherList.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_weather_list, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationTime, locationTemperature, rainAmount, rainPercent, weatherInfo;
        ImageView weatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTime = itemView.findViewById(R.id.location_time);
            locationTemperature = itemView.findViewById(R.id.location_temperature);
            rainAmount = itemView.findViewById(R.id.rain_amount);
            rainPercent = itemView.findViewById(R.id.rain_percent);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
            weatherInfo = itemView.findViewById(R.id.weather_info);

        }

        public void bind(Weather item, int position) {


            Weather curWeatherItem = weatherList.get(position);

            locationTime.setText(item.getTime());
            if(item.getType() == 0){
                locationTemperature.setText("온도 : " + item.getTemperature());
                rainAmount.setText("강수량 : " + item.getRainyAmount());
                rainPercent.setText("강수 확률 : " + item.getRainyPercent());
            }
            else if(item.getType() == 9){
                locationTemperature.setText("");
                rainAmount.setText("");
                rainPercent.setText("강수 확률 : " + item.getRainyPercent());
            }
            else{
                locationTemperature.setText("");
                rainAmount.setText("");
                rainPercent.setText("강수 확률 : " + item.getRainyPercent());
            }




            int imageNum = getImageNum.getNum(item.getWeatherInfo());
            switch (imageNum) {
                case 0:
                    weatherInfo.setText("맑음");
                    weatherIcon.setImageResource(R.drawable.ic_0);
                    break;
                case 1:
                    weatherInfo.setText("구름많음");
                    weatherIcon.setImageResource(R.drawable.ic_1);
                    break;
                case 2:
                    weatherInfo.setText("비");
                    weatherIcon.setImageResource(R.drawable.ic_2);
                    break;
                case 3:
                    weatherInfo.setText("눈");
                    weatherIcon.setImageResource(R.drawable.ic_3);
                    break;
                case 4:
                    weatherInfo.setText("비/눈");
                    weatherIcon.setImageResource(R.drawable.ic_4);
                    break;
                case 5:
                    weatherInfo.setText("소나기");
                    weatherIcon.setImageResource(R.drawable.ic_5);
                    break;
                default:
                    weatherInfo.setText("조회불가");
                    weatherIcon.setImageResource(R.drawable.ic_0);
            }
        }
        
    }
}