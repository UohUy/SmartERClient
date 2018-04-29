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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    private String firstName;
    private String surname;
    private String dob;
    private String address;
    private String email;
    private long mobile;
    private String postcode;
    private String username;
    private String password;
    private int regNumber;
    private String energyProvider;
    private String passwordHash;

    private int newResID;
    private boolean cancel = false;
    private View focusView = null;

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
        setResID.execute((Void) null);

    }

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
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
        firstName = mFirstNameView.getText().toString().trim();
        surname = mSurnameView.getText().toString().trim();
        dob = mDOBView.getText().toString().trim() + "T00:00:00+10:00";
        address = mAddressView.getText().toString().trim();
        email = mEmailAddressView.getText().toString().trim();
        String inputMobile = mMobileView.getText().toString().trim();
        //        Check for a valid mobile.
        if (inputMobile.isEmpty()) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        } else
            mobile = Long.valueOf(inputMobile);
        postcode = mPostcodeView.getText().toString().trim();
        username = mUsernameView.getText().toString().trim();
        password = mPasswordView.getText().toString().trim();
        regNumber = Integer.valueOf(mNumberOfResidentView.getSelectedItem().toString());
        energyProvider = mEnergyProviderView.getSelectedItem().toString();
        passwordHash = MD5Parser.encode(password);


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
        } else {
            CheckEmailExistance checkEmailExistance = new CheckEmailExistance(email);
            checkEmailExistance.execute((Void) null);
        }

        //        Check for a valid user name.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        } else{
            CheckUsernameExistance checkUsernameExistance = new CheckUsernameExistance(username);
            checkUsernameExistance.execute((Void) null);
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
            Resident resident = new Resident(newResID, address, dob, email, firstName, mobile,
                    surname, regNumber, energyProvider, postcode);
            String residentString = stringJsonResidentParser(resident);
            String credentialString = stringJsonCredentialParser(resident);
            mRegstTask = new UserRegister(residentString, credentialString);
            mRegstTask.execute((Void) null);
        }


    }

    private String stringJsonResidentParser(Object resident) {
        Gson gson = new Gson();
        return gson.toJson(resident);
    }

    private String stringJsonCredentialParser(Object resident) {
        SimpleDateFormat spdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = new Date();
        Credential credential = new Credential(username, passwordHash, spdf.format(date)
                + "T00:00:00+10:00", resident);
        Gson gson = new Gson();
        return gson.toJson(credential);
    }

    public class UserRegister extends AsyncTask<Void, Void, Integer>    {
        private String  postResidentData;
        private String  postCredentialData;

        UserRegister(String resident, String credential) {
            postResidentData = resident;
            postCredentialData = credential;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int code = HTTPRequest.postResidentData(postResidentData);
            if (code == 204)
                code = HTTPRequest.postCredentialData(postCredentialData);
            return code;
        }

        @Override
        protected void onPostExecute(Integer responseCode){
            String error = "";
            switch (responseCode){
                case 204:
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("resid", newResID);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("address", address);
                    intent.putExtra("postcode", postcode);
                    startActivity(intent);
                    break;
                case -2:
                    error = "Resident post goes wrong";
                    break;
                case -1:
                    error = "Credential post goes wrong";
                    break;
                default:
                    error = "Http status: " + responseCode;
                    break;
            }

            if (responseCode != 204){
                mFirstNameView.setError(error);
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

                return HTTPRequest.findGreatestResID();
            }

            @Override
            protected void onPostExecute(Integer resid) {
                mRegstTask = null;
                if (resid != null)
                    newResID = resid + 1;
            }
    }


    public class CheckEmailExistance extends AsyncTask<Void, Void, Boolean>{
        private String inputEmail;
        CheckEmailExistance(String input){
            inputEmail = input;
        }

        @Override
        protected Boolean doInBackground(Void... params){
            return HTTPRequest.isEmailExist(inputEmail);
        }

        @Override
        protected void onPostExecute(Boolean exist){
            if (exist) {
                mEmailAddressView.setError("Exist email address");
                cancel = true;
                focusView = mEmailAddressView;
            }
        }
    }

    public class CheckUsernameExistance extends AsyncTask<Void, Void, Boolean>{
        private String inputUsername;
        CheckUsernameExistance(String input){
            inputUsername = input;
        }

        @Override
        protected Boolean doInBackground(Void... params){
            return HTTPRequest.isUsernameExist(inputUsername);
        }

        @Override
        protected void onPostExecute(Boolean exist){
            if (exist) {
                mUsernameView.setError("Exist username");
                cancel = true;
                focusView = mUsernameView;
            }
        }
    }

}

