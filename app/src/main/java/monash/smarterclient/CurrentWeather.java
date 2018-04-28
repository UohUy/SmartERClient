package monash.smarterclient;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CurrentWeather extends AppCompatActivity {
    private View vCurrentTemp;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vCurrentTemp = (TextView) findViewById(R.id.temp_view);
        return vCurrentTemp;
    }

    public void getData(){
        GetCurrentWeather getCurrentWeather = new GetCurrentWeather();
        getCurrentWeather.execute();
    }

    public class GetCurrentWeather extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... voids) {
            String url = "http://api.openweathermap.org/data/2.5/weather?" +
                    "q=Melbourne&APPID=b74aa320ab64e346ba616a06060dfff0";
            HttpURLConnection connection;
            BufferedReader br = null;
            StringBuilder stringResult = new StringBuilder();
            String currentWeather = null;
            // Get weather info from openweathermap.org
            try {
                URL realURL = new URL(url);
                connection = (HttpURLConnection) realURL.openConnection();
                connection.setRequestMethod("GET");
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null)
                    stringResult.append(line);
                String result = stringResult.toString();
                JSONObject jsonObject = new JSONObject(result);
                currentWeather = jsonObject.getJSONObject("main").getDouble("temp")
                        - 273.15 + "\\u2103";
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (JSONException e4) {
                e4.printStackTrace();
            } catch (NullPointerException e5) {
                e5.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e2) {
                    e2.printStackTrace();
                }
            }
            return currentWeather;
        }

//        @Override
//        protected void onPostExecute(String currentWeather) {
//            vCurrentTemp.setText(currentWeather);
//        }
    }
}
