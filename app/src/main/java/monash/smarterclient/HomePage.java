package monash.smarterclient;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomePage extends Fragment {
    View vHomePage;
    private TextView weatherView;
    private TextView welcomeView;
    private String firstName;
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
        firstName = intent.getStringExtra("firstName");
        address = intent.getStringExtra("address");
        postcode = intent.getStringExtra("postcode");

//        Initialize Views
        weatherView = (TextView) vHomePage.findViewById(R.id.weather_view);
        welcomeView = (TextView) vHomePage.findViewById(R.id.welcome_view);

//        Set welcome text to the user
        String welcome = "Welcome, " + firstName;
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



    public class CurrentWeather extends AsyncTask<Void, Void, String>{
        private String addressQuery;
        final private TextView weatherView;
        private HTTPRequest httpRequest;

        CurrentWeather(String query){
            addressQuery = query;
            weatherView = (TextView) vHomePage.findViewById(R.id.weather_view);
            httpRequest = new HTTPRequest();
        }

        @Override
        protected String doInBackground(Void... params){
            String locationQuery = httpRequest.findLocationByAddress(addressQuery);
            return httpRequest.findWeatherByLocation(locationQuery);
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

