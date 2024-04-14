package cap.project.rainyday.weather;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class GetJson {
    public static JSONObject getJson(String api) {
        JSONObject responseJson = null;
        try {
            URI uri = new URI(api);
            URL url = uri.toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int a;
            while ((a = br.read()) != -1) {
                sb.append((char) a);
            }
            br.close();
            responseJson = new JSONObject(sb.toString());
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
            System.err.println(api);
        }
        return responseJson;
    }
}
