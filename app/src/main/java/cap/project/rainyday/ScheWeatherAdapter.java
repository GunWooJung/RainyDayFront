package cap.project.rainyday;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cap.project.rainyday.model.Stopover;
import cap.project.rainyday.model.Weather;
import cap.project.rainyday.weather.getImageNum;

public class ScheWeatherAdapter extends RecyclerView.Adapter<ScheWeatherAdapter.ViewHolder> {

    private List<Weather> Items;
    private WeatherClickListener listener;

    public ScheWeatherAdapter(List<Weather> Items, WeatherClickListener listener) {
        this.Items = Items;
        this.listener = listener;
    }

    public void setItems(List<Weather> items) {
        Items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_weather_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather item = Items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView location_name;
        private TextView location_time;
        private TextView location_temperature;
        private TextView rain_amount;
        private TextView rain_percent;
        private TextView weather_info;

        private ImageView weather_icon;

        private TextView type;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location_name = itemView.findViewById(R.id.location_name);
            location_time = itemView.findViewById(R.id.location_time);
            location_temperature = itemView.findViewById(R.id.location_temperature);
            rain_amount = itemView.findViewById(R.id.rain_amount);
            rain_percent = itemView.findViewById(R.id.rain_percent);
            weather_info = itemView.findViewById(R.id.weather_info);
            weather_icon = itemView.findViewById(R.id.weather_icon);
            type = itemView.findViewById(R.id.type);
            itemView.setOnClickListener(this);
        }

        public void bind(Weather item ,int position) {
            if(position == 0){
                type.setText("출발지");
            }
            else if(position == Items.size()-1){
                type.setText("도착지");

            }
            else{
                type.setText("경유지");
            }
            if(item.getType() == 0) {   //단기
                location_name.setText(item.getLocation());
                location_time.setText(item.getTime());
                location_temperature.setText("온도 : " + item.getTemperature());
                rain_amount.setText("강수량 : " + item.getRainyAmount());
                rain_percent.setText("강수 확률 : " + item.getRainyPercent());

                int imageNum = getImageNum.getNum(item.getWeatherInfo());
                switch (imageNum){
                    case 0:
                        weather_info.setText("맑음");
                        weather_icon.setImageResource(R.drawable.ic_0);
                        break;
                    case 1:
                        weather_info.setText("구름많음");
                        weather_icon.setImageResource(R.drawable.ic_1);
                        break;
                    case 2:
                        weather_info.setText("비");
                        weather_icon.setImageResource(R.drawable.ic_2);
                        break;
                    case 3:
                        weather_info.setText("눈");
                        weather_icon.setImageResource(R.drawable.ic_3);
                        break;
                    case 4:
                        weather_info.setText("비/눈");
                        weather_icon.setImageResource(R.drawable.ic_4);
                        break;
                    case 5:
                        weather_info.setText("소나기");
                        weather_icon.setImageResource(R.drawable.ic_5);
                        break;
                    default:
                        weather_info.setText("맑음");
                        weather_icon.setImageResource(R.drawable.ic_0);
                }

            }
            else if(item.getType() == 1) {   //중기
                location_name.setText(item.getLocation());
                location_time.setText(item.getTime());
                rain_percent.setText("강수 확률 : " + item.getRainyPercent());

                location_temperature.setVisibility(View.GONE);
                rain_amount.setVisibility(View.GONE);
                int imageNum = getImageNum.getNum(item.getWeatherInfo());
                switch (imageNum){
                    case 0:
                        weather_info.setText("맑음");
                        weather_icon.setImageResource(R.drawable.ic_0);
                        break;
                    case 1:
                        weather_info.setText("구름많음");
                        weather_icon.setImageResource(R.drawable.ic_1);
                        break;
                    case 2:
                        weather_info.setText("비");
                        weather_icon.setImageResource(R.drawable.ic_2);
                        break;
                    case 3:
                        weather_info.setText("눈");
                        weather_icon.setImageResource(R.drawable.ic_3);
                        break;
                    case 4:
                        weather_info.setText("비/눈");
                        weather_icon.setImageResource(R.drawable.ic_4);
                        break;
                    case 5:
                        weather_info.setText("소나기");
                        weather_icon.setImageResource(R.drawable.ic_5);
                        break;
                    default:
                        weather_info.setText("맑음");
                        weather_icon.setImageResource(R.drawable.ic_0);
                }
            }
            else if(item.getType() == 2) {   //10일후
                location_name.setText(item.getLocation());
                location_time.setText(item.getTime());
                rain_percent.setText("10일 이후 날씨는 제공되지 않습니다.");
                weather_info.setVisibility(View.GONE);
                location_temperature.setVisibility(View.GONE);
                rain_amount.setVisibility(View.GONE);
                weather_icon.setVisibility(View.GONE);
            }
            else if(item.getType() == 3) {   //과거
                location_name.setText(item.getLocation());
                location_time.setText(item.getTime());
                rain_percent.setText("과거 날씨는 제공되지 않습니다.");
                weather_info.setVisibility(View.GONE);
                location_temperature.setVisibility(View.GONE);
                rain_amount.setVisibility(View.GONE);
                weather_icon.setVisibility(View.GONE);
            }
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Weather item = Items.get(position);
            Log.d("aa","a");
            listener.onItemClick(item);
        }

    }
}
