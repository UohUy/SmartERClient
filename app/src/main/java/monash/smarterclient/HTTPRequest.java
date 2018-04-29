package monash.smarterclient;

import android.support.annotation.Nullable;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

public class HTTPRequest {
    private static String LOCAL_HOST = "118.139.55.123";

    // Http requests in Register
    protected static int findGreatestResID() {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodResidPath = "/smarter.resident/";

        URL residUrl;
        HttpURLConnection connection = null;
        String textResult;
        int residArray[];
        int greatestResID = -1;
        try {
            // Get resident ID from database.
            residUrl = new URL(BASE_URL + methodResidPath);
            connection = (HttpURLConnection) residUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStreamResID = new Scanner(connection.getInputStream());
            textResult = "";
            while (inStreamResID.hasNextLine()) {
                textResult += inStreamResID.nextLine();
            }

            // Assign values of all resid into a array.
            JSONArray allResult = new JSONArray(textResult);
            residArray = new int[allResult.length()];
            for (int i = 0; i < allResult.length(); i++) {
                JSONObject resident = allResult.getJSONObject(i);
                residArray[i] = resident.getInt("resid");
            }

            // Find out the greatest resid.
            for (int id : residArray) {
                if (id >= greatestResID)
                    greatestResID = id;
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3) {
            e3.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return greatestResID;
    }

    protected static int postResidentData(String resident) {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String residentPostPath = "/smarter.resident/";

        URL residentUrl;
        HttpURLConnection connection = null;
        PrintWriter out;
        Resident resident1;
        int code = -2;

        try {
            residentUrl = new URL(BASE_URL + residentPostPath);
            connection = (HttpURLConnection) residentUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setFixedLengthStreamingMode(resident.getBytes().length);
//            connection.setFixedLengthStreamingMode(stringStream.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            out = new PrintWriter(connection.getOutputStream());
            out.print(resident);
            out.close();
            code = connection.getResponseCode();
            Log.i("error", Integer.toString(code));
        }catch(MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return code;
    }

    protected static int postCredentialData(String credential) {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String credentialPostPath = "/smarter.credential";

        URL credentialUrl;
        HttpURLConnection connection = null;
        PrintWriter out;
        String stringStream;
        int code = -1;

        try {
            credentialUrl = new URL(BASE_URL + credentialPostPath);
            connection = (HttpURLConnection) credentialUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setFixedLengthStreamingMode(credential.getBytes("UTF-8").length);
//            connection.setFixedLengthStreamingMode(stringStream.getBytes().length);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            out = new PrintWriter(connection.getOutputStream());
            out.print(credential);
            out.close();
            Log.i("error", Integer.toString(code));
            code = connection.getResponseCode();
        } catch(MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return code;
    }

    protected static boolean isEmailExist(String givenEmail) {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodResidPath = "/smarter.resident/findByEmail/";

        URL emailUrl;
        HttpURLConnection connection = null;
        String textResult;
        boolean emailExist = true;

        try {
            // Get resident ID from database.
            emailUrl = new URL(BASE_URL + methodResidPath + givenEmail);
            connection = (HttpURLConnection) emailUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStreamResID = new Scanner(connection.getInputStream());
            textResult = "";
            while (inStreamResID.hasNextLine()) {
                textResult += inStreamResID.nextLine();
            }

            // Check is the email address exist or not.
            JSONArray allResult = new JSONArray(textResult);
            if (allResult.length() == 0)
                emailExist = false;


        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3) {
            e3.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return emailExist;
    }

    protected static boolean isUsernameExist(String givenUsername) {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodUsernamePath = "/smarter.credential/findByUserName/";

        URL emailUrl;
        HttpURLConnection connection = null;
        String textResult;
        boolean usernameExist = true;

        try {
            // Get resident ID from database.
            emailUrl = new URL(BASE_URL + methodUsernamePath + givenUsername);
            connection = (HttpURLConnection) emailUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStreamResID = new Scanner(connection.getInputStream());
            textResult = "";
            while (inStreamResID.hasNextLine()) {
                textResult += inStreamResID.nextLine();
            }

            // Check is the username exist or not.
            JSONArray allResult = new JSONArray(textResult);
            if (allResult.length() == 0)
                usernameExist = false;


        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3) {
            e3.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return usernameExist;
    }

    // Http requests in Login.
    protected static String findAllByPasswordHash(String passwordHash){
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodPath = "/smarter.credential/findByPasswdHash/";

        URL url;
        HttpURLConnection connection = null;
        String textResult = "";
        try {
            url = new URL(BASE_URL + methodPath + passwordHash);
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

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult;
    }

    @Nullable
    protected static String findLocationByAddress(String addressQuery) {
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

    // Http requests in Home page.
    @Nullable
    protected static String findWeatherByLocation(String locationQuery){
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

    // Http requests in Map fragment.
    protected static String[] findAllAddressAndPostcode(){
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodPath = "/smarter.resident/";

        URL url;
        HttpURLConnection connection = null;
        String textResult = "";
        String addressArray[] = null;
        try {
            url = new URL(BASE_URL + methodPath);
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
            JSONArray allResult = new JSONArray(textResult);
            addressArray = new String[allResult.length()];
            for (int i = 0; i < allResult.length(); i ++){
                JSONObject resident = allResult.getJSONObject(i);
                String temp = resident.getString("address");
                temp += ",";
                temp += resident.getString("postcode");
                addressArray[i] = temp;
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3) {
            e3.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return addressArray;
    }

    protected static LatLng getLatLng(String addressQuery) {
        String GOOGLE_MAP_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String API_KEY = "AIzaSyBge-JRuaDpcdIr1YW6Fs5zEhsuUFFZieY";
        URL url;
        HttpURLConnection connection = null;
        String textResult = "";
        LatLng latLng = new LatLng();

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
            latLng.setLatitude(location.getDouble("lat"));
            latLng.setLongitude(location.getDouble("lng"));
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
        return latLng;
    }

    protected static float[] findDailyUsageByResidAndDate(int resid, String date){
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodPath = "/smarter.elecusage/findDailyUsageByResidAndDate/";

        URL url;
        HttpURLConnection connection = null;
        String textResult;
        float usageArray[] = new float[3];
        try {
            // Get resident ID from database.
            url = new URL(BASE_URL + methodPath + resid + "/" + date);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStreamResID = new Scanner(connection.getInputStream());
            textResult = "";
            while (inStreamResID.hasNextLine()) {
                textResult += inStreamResID.nextLine();
            }
            JSONObject result = new JSONArray(textResult).getJSONObject(0);
            String temp;
            temp = result.getString("fridge");
            usageArray[0] = Float.valueOf(temp);
            temp = result.getString("aircon");
            usageArray[1] = Float.valueOf(temp);
            temp = result.getString("washingmachine");
            usageArray[2] = Float.valueOf(temp);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3) {
            e3.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return usageArray;
    }
}

