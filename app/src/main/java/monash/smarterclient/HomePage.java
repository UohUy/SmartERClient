package monash.smarterclient;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

public class HomePage extends Fragment {
    View vHomePage;
    private TextView weatherView;
    private TextView welcomeView;
    private String username;
    private String address;
    private String postcode;
    private int resid;
    private String result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        vHomePage = inflater.inflate(R.layout.fragment_main, container, false);

//        Get username and resident ID from last activity via Intent
        Intent intent = getActivity().getIntent();
        resid = intent.getIntExtra("resid", 0);
        username = intent.getStringExtra("username");
        address = intent.getStringExtra("address");
        postcode = intent.getStringExtra("postcode");

//        Initialize Views
        weatherView = (TextView) vHomePage.findViewById(R.id.weather_view);
        welcomeView = (TextView) vHomePage.findViewById(R.id.welcome_view);

//        Set welcome text to the user
        String welcome = "Welcome, " + username;
        welcomeView.setText(welcome);

//        Set up current weather
        CurrentWeather cw = new CurrentWeather(generateQuery());
        cw.execute((Void) null);

        return vHomePage;
    }

    private String generateQuery(){
        String location[] = address.split(",");
        String street[] = location[0].split(" ");
        String query = "";
        for (String piece: street) {
            query += piece.trim();
            query += "+";
        }
        query += postcode;
        return query;
    }

    private String findLocationByAddress(String addressQuery) {
        String GOOGLE_MAP_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String API_KEY = "AIzaSyBge-JRuaDpcdIr1YW6Fs5zEhsuUFFZieY";
        URL url;
        HttpURLConnection connection = null;
        String textResult = "";
        String coordinateQuery = "";
        Double coordinate[] = new Double[2];

        try {
            url = new URL( GOOGLE_MAP_BASE_URL + addressQuery + "&key=" + API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            JSONObject jsonObjectLocation = new JSONObject(textResult);
            JSONArray jsonArrayResult = jsonObjectLocation.getJSONArray("results");
            JSONObject geometry = jsonArrayResult.getJSONObject(0).getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            coordinateQuery += "lat=" + location.getDouble("lat") + "&lon=" + location.getDouble("lng");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        } catch (NullPointerException e3) {
            e3.printStackTrace();
            return null;
        } catch (JSONException e4) {
            e4.printStackTrace();
            return null;
        } finally {
            connection.disconnect();
        }
        return coordinateQuery;
    }

    private String findWeatherByLocation(String locationQuery){
        String temperature;
        String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
        String API_KEY = "b74aa320ab64e346ba616a06060dfff0";
        URL url;
        HttpURLConnection connection = null;
        String textResult = "";

        try {
            url = new URL( WEATHER_BASE_URL + locationQuery + "&APPID=" + API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            JSONObject jsonObjectWeather = new JSONObject(textResult);
            JSONObject main = jsonObjectWeather.getJSONObject("main");
            double temp = main.getDouble("temp") - 271.35;
            DecimalFormat df = new DecimalFormat("#.##");
            temperature = df.format(temp) + "\u2103";

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        } catch (NullPointerException e3) {
            e3.printStackTrace();
            return null;
        } catch (JSONException e4) {
            e4.printStackTrace();
            return null;
        } finally {
            connection.disconnect();
        }
        return temperature;
    }

    public class CurrentWeather extends AsyncTask<Void, Void, String>{
        private String addressQuery;
        final private TextView weatherView;

        CurrentWeather(String query){
            addressQuery = query;
            weatherView = (TextView) vHomePage.findViewById(R.id.weather_view);
        }

        @Override
        protected String doInBackground(Void... params){
            String locationQuery = findLocationByAddress(addressQuery);
            return findWeatherByLocation(locationQuery);
        }

        @Override
        protected void onPostExecute(String temperature){
            if (temperature != null)
                weatherView.setText(temperature);
            else {
                String info = "Not success on weather request";
                weatherView.setText(info);
            }
        }
    }


}

