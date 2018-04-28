package monash.smarterclient;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

public class RegisterActivity extends AppCompatActivity{
    private EditText mFirstNameView;
    private EditText mSurnameView;
    private EditText mDOBView;
    private EditText mAddressView;
    private EditText mPostcodeView;
    private EditText mEmailAddressView;
    private EditText mMobileView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private Spinner mNumberOfResidentView;
    private Spinner mEnergyProviderView;
    private UserRegister mRegstTask = null;
    private String LOCAL_HOST;

    private int newResID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstNameView = (EditText) findViewById(R.id.register_first_name);
        mSurnameView = (EditText) findViewById(R.id.register_surname);
        mDOBView = (EditText) findViewById(R.id.register_dob);
        mAddressView = (EditText) findViewById(R.id.register_address);
        mPostcodeView = (EditText) findViewById(R.id.register_postcode);
        mEmailAddressView = (EditText) findViewById(R.id.register_email_address);
        mMobileView = (EditText) findViewById(R.id.register_mobile);
        mNumberOfResidentView = (Spinner) findViewById(R.id.register_number_of_residents_spinner);
        mEnergyProviderView = (Spinner) findViewById(R.id.register_energy_provider_spinner);
        mUsernameView = (EditText) findViewById(R.id.register_username);
        mPasswordView = (EditText) findViewById(R.id.register_password);

        mDOBView.setInputType(InputType.TYPE_NULL);
        mDOBView.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if (hasFocus)
                    showDatePickerDialog();
            }
        });

        mDOBView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button mSubmitButton = (Button) findViewById(R.id.register_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });

        // Initialize local host IP address.
        LocalHostIPAddress ip = new LocalHostIPAddress();
        try {
            LOCAL_HOST = ip.getIPAddress();
        } catch (Exception e){
            e.printStackTrace();
        }

        // Initialize a new resid.
        SetResID setResID = new SetResID();

    }

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year+"/"+(monthOfYear+1)+"/"+dayOfMonth;
                mDOBView.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void attemptSubmit(){
        if (mRegstTask != null){
            return;
        }

//        Reset errors
        mFirstNameView.setError(null);
        mSurnameView.setError(null);
        mEmailAddressView.setError(null);
        mAddressView.setError(null);
        mMobileView.setError(null);
        mPostcodeView.setError(null);

//        Store values at the time of the register attempt.
        String firstName = mFirstNameView.getText().toString().trim();
        String surname = mSurnameView.getText().toString().trim();
        String dob = mDOBView.getText().toString().trim();
        String address = mAddressView.getText().toString().trim();
        String email = mEmailAddressView.getText().toString().trim();
        String mobile = mMobileView.getText().toString().trim();
        String postcode = mPostcodeView.getText().toString().trim();
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String regNumber = mNumberOfResidentView.getSelectedItem().toString();
        String energyProvider = mEnergyProviderView.getSelectedItem().toString();
        String passwordHash = MD5Parser.encode(password);

        boolean cancel = false;
        View focusView = null;

        //        Check for a valid first name.
        if (TextUtils.isEmpty(firstName)){
            mFirstNameView.setError(getString(R.string.error_invalid_first_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        //        Check for a valid surname.
        if (TextUtils.isEmpty(surname)){
            mSurnameView.setError(getString(R.string.error_invalid_surname));
            focusView = mSurnameView;
            cancel = true;
        }

        //        Check for a valid dob.
        if (TextUtils.isEmpty(dob)){
            mDOBView.setError(getString(R.string.error_invalid_dob));
            focusView = mDOBView;
            cancel = true;
        }

        //        Check for a valid address.
        if (TextUtils.isEmpty(address)){
            mAddressView.setError(getString(R.string.error_invalid_address));
            focusView = mAddressView;
            cancel = true;
        }

        //        Check for a valid postcode.
        if (TextUtils.isEmpty(postcode)){
            mPostcodeView.setError(getString(R.string.error_invalid_username));
            focusView = mPostcodeView;
            cancel = true;
        }

        //        Check for a valid email address.
        if (TextUtils.isEmpty(email)){
            mEmailAddressView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailAddressView;
            cancel = true;
        }

        //        Check for a valid mobile.
        if (TextUtils.isEmpty(mobile)){
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }

        //        Check for a valid user name.
        if (TextUtils.isEmpty(username)){
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        //  Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        } else {
            JSONObject resident = jsonResidentParser(address, dob, email, energyProvider, firstName,
                    surname, mobile, postcode, regNumber);
            JSONObject credential = jsonCredentialParser(username, passwordHash, resident);
            mRegstTask = new UserRegister(resident, credential);
        }


    }

    private JSONObject jsonResidentParser(String address, String dob, String email, String energyProvider,
                                          String firstName, String surname, String mobile, String postcode,
                                          String resNumber){

        JSONObject resident = new JSONObject();
        try {
            resident.put("address", address);
            resident.put("dob", dob);
            resident.put("email", email);
            resident.put("engProvdName", energyProvider);
            resident.put("firstName", firstName);
            resident.put("mobile", Integer.getInteger(mobile));
            resident.put("postcode", postcode);
            resident.put("resNumber", Integer.getInteger(resNumber));
            resident.put("surname", surname);
            resident.put("resid", newResID);
        } catch (JSONException e){
            return null;
        }
        return resident;
    }

    private JSONObject jsonCredentialParser(String username, String passwordHash, JSONObject resident){
        JSONObject credential = new JSONObject();
        try {
            credential.put("passwdHash", passwordHash);
            credential.put("userName", username);
            credential.put("resid", resident);
        } catch (JSONException e){
            return null;
        }
        return credential;
    }

    private int findGreatestResID(){
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
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            JSONArray allResult = new JSONArray(textResult);
            for (int i = 0; i < allResult.length(); i ++){
                JSONObject resident = allResult.getJSONObject(i);
                residArray[i] = resident.getInt("resid");
            }

            // Find out the greatest resid.
            for (int id: residArray){
                if (id >= greatestResID)
                    greatestResID = id;
            }

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (JSONException e3){
            e3.printStackTrace();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return greatestResID;
    }

    private void postRegisterData(JSONObject resident, JSONObject credential){
        final String BASE_URL = "http://" + LOCAL_HOST + ":8080/SmartER/webresources";
        final String credentialPostPath = "/smarter.credential/";
        final String residentPostPath = "/smarter.resident/";


    public class UserRegister extends AsyncTask<Void, Void, Void>{
        private JSONObject postResidentData;
        private JSONObject postCredentialData;

        UserRegister(JSONObject resident, JSONObject credential){
            postResidentData = resident;
            postCredentialData = credential;
        }

        // TODO post json data into credential and resident individually.
        @Override
        protected void doInBackground(Void... parmas){
            postRegisterData(postResidentData, postCredentialData);
        }

    }

    public class SetResID extends AsyncTask<Void, Void, Integer>{

        SetResID(){}

        @Override
        protected Integer doInBackground(Void... params){
            return  findGreatestResID();
        }

        @Override
        protected void doPostExecute(Integer resid){
            mRegstTask = null;
            if (resid != null)
                newResID = resid + 1;
        }
    }
}
