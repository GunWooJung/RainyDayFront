package cap.project.rainyday.weather;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetJson {
    public static JsonObject getJson(String api) {
        JsonObject responseJson = null;
        try {
            URI uri = new URI(api);
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            String jsonStr = sb.toString();

            // Gson을 사용하여 JSON 문자열을 JsonObject로 파싱
            Gson gson = new Gson();
            responseJson = gson.fromJson(jsonStr, JsonObject.class);
        } catch (Exception e) {
            Log.d("API", e.toString());
        }

        return responseJson;
    }
}
