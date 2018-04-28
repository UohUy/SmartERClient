package monash.smarterclient;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class HTTPRequest {
    private String LOCAL_HOST;

    HTTPRequest(){
        // Initialize local host IP address.
        LocalHostIPAddress ip = new LocalHostIPAddress();
        try {
            LOCAL_HOST = ip.getIPAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int findGreatestResID() {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String methodCountPath = "/smarter.credential/count";
        final String methodResidPath = "/smarter.resident";

        URL countUrl, residUrl;
        HttpURLConnection connection = null;
        String textResult = "";
        int count = 0;
        int residArray[];
        int greatestResID = -1;

        try {
            // Get count number of resident in database for initialize the length of resid array.
            countUrl = new URL(BASE_URL + methodCountPath);
            connection = (HttpURLConnection) countUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner inStream = new Scanner(connection.getInputStream());
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            count = Integer.getInteger(textResult);

            // Get resident ID from database.
            residArray = new int[count];
            residUrl = new URL(BASE_URL + methodResidPath);
            connection = (HttpURLConnection) residUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            inStream = new Scanner(connection.getInputStream());
            textResult = "";
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            JSONArray allResult = new JSONArray(textResult);
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

    protected int postRegisterData(JSONObject resident, JSONObject credential) {
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String credentialPostPath = "/smarter.credential/";
        final String residentPostPath = "/smarter.resident/";

        URL residentUrl;
        URL credentialUrl;
        HttpURLConnection connection = null;
        PrintWriter out = null;
        int code = 0;

        try {
            residentUrl = new URL(BASE_URL + residentPostPath);
            connection = (HttpURLConnection) residentUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            out = new PrintWriter(connection.getOutputStream());
            out.print(resident.toString());
            code = connection.getResponseCode();
            out.close();
            Log.i("error", Integer.toString(code));

            credentialUrl = new URL(BASE_URL + credentialPostPath);
            connection = (HttpURLConnection) credentialUrl.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            out = new PrintWriter(connection.getOutputStream());
            out.print(credential.toString());
            code = connection.getResponseCode();
            out.close();
            Log.i("error", Integer.toString(code));
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

    protected String findAllByPasswordHash(String passwordHash){
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



}
