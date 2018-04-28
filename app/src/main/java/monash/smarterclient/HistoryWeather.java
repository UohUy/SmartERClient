package e.monash.smarterclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HistoryWeather {



    public void cityCodeParser(String city) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader("~/app/libs/city.list.json"));
            String s ;
            String cityCodeString = null;
            while ((s = br.readLine()) != null) {
                 System.out.println(s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    System.out.print(jsonObject);
                    JSONArray jsonArray = jsonObject.getJSONArray(city);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e1) {
            System.out.print("Json file not found.");
        } catch (IOException e2){
            System.out.print("IOException");
        }
    }
}
