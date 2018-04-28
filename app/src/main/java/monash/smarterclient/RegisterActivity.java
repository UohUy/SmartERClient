package monash.smarterclient;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
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
    private HTTPRequest httpRequest;

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

        httpRequest = (HTTPRequest) new HTTPRequest();

        mDOBView.setInputType(InputType.TYPE_NULL);
        mDOBView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
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

        // Initialize a new resid.
         SetResID setResID = new SetResID();

    }

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                mDOBView.setText(date);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void attemptSubmit() {
        if (mRegstTask != null) {
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
        int mobile = Integer.valueOf(mMobileView.getText().toString().trim());
        String postcode = mPostcodeView.getText().toString().trim();
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        int regNumber = Integer.valueOf(mNumberOfResidentView.getSelectedItem().toString());
        String energyProvider = mEnergyProviderView.getSelectedItem().toString();
        String passwordHash = MD5Parser.encode(password);

        boolean cancel = false;
        View focusView = null;

        //        Check for a valid first name.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_first_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        //        Check for a valid surname.
        if (TextUtils.isEmpty(surname)) {
            mSurnameView.setError(getString(R.string.error_invalid_surname));
            focusView = mSurnameView;
            cancel = true;
        }

        //        Check for a valid dob.
        if (TextUtils.isEmpty(dob)) {
            mDOBView.setError(getString(R.string.error_invalid_dob));
            focusView = mDOBView;
            cancel = true;
        }

        //        Check for a valid address.
        if (TextUtils.isEmpty(address)) {
            mAddressView.setError(getString(R.string.error_invalid_address));
            focusView = mAddressView;
            cancel = true;
        }

        //        Check for a valid postcode.
        if (TextUtils.isEmpty(postcode)) {
            mPostcodeView.setError(getString(R.string.error_invalid_username));
            focusView = mPostcodeView;
            cancel = true;
        }

        //        Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailAddressView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailAddressView;
            cancel = true;
        }

        //        Check for a valid mobile.
        if (mobile == 0) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }

        //        Check for a valid user name.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        //  Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            JSONObject resident = jsonResidentParser(address, dob, email, energyProvider, firstName,
                    surname, mobile, postcode, regNumber);
            JSONObject credential = jsonCredentialParser(username, passwordHash, resident);
            mRegstTask = new UserRegister(resident, credential, username, address, postcode);
        }


    }

    private JSONObject jsonResidentParser(String address, String dob, String email, String energyProvider,
                                          String firstName, String surname, int mobile, String postcode,
                                          int resNumber) {

        JSONObject resident = new JSONObject();
        try {
            resident.put("address", address);
            resident.put("dob", dob);
            resident.put("email", email);
            resident.put("engProvdName", energyProvider);
            resident.put("firstName", firstName);
            resident.put("mobile", mobile);
            resident.put("postcode", postcode);
            resident.put("resNumber", resNumber);
            resident.put("surname", surname);
            resident.put("resid", newResID);
        } catch (JSONException e) {
            return null;
        }
        return resident;
    }

    private JSONObject jsonCredentialParser(String username, String passwordHash, JSONObject resident) {
        JSONObject credential = new JSONObject();
        try {
            credential.put("passwdHash", passwordHash);
            credential.put("userName", username);
            credential.put("resid", resident);
        } catch (JSONException e) {
            return null;
        }
        return credential;
    }




    public class UserRegister extends AsyncTask<Void, Void, Integer> {
        private JSONObject postResidentData;
        private JSONObject postCredentialData;
        private String mUsername;
        private String mAddress;
        private String mPostcode;

        UserRegister(JSONObject resident, JSONObject credential, String username, String addresss,
                     String postcode) {
            postResidentData = resident;
            postCredentialData = credential;
            mUsername = username;
            mAddress = addresss;
            mPostcode = postcode;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return httpRequest.postRegisterData(postResidentData, postCredentialData);
        }

        @Override
        protected void onPostExecute(Integer responseCode){
            if (responseCode == 204){
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("resid", newResID);
                intent.putExtra("username", mUsername);
                intent.putExtra("address", mAddress);
                intent.putExtra("postcode", mPostcode);
                startActivity(intent);
            } else {
                mFirstNameView.setError("Http request goes wrong.");
                mFirstNameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mRegstTask = null;
        }

    }

    public class SetResID extends AsyncTask<Void, Void, Integer> {

            SetResID() {
            }

            @Override
            protected Integer doInBackground(Void... params) {

                return httpRequest.findGreatestResID();
            }

            @Override
            protected void onPostExecute(Integer resid) {
                mRegstTask = null;
                if (resid != null)
                    newResID = resid + 1;
            }
        }
}

