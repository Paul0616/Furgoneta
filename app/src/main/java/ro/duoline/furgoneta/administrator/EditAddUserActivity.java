package ro.duoline.furgoneta.administrator;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;

public class EditAddUserActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished, AdapterView.OnItemSelectedListener {
    private List<String> mRolesList;
    private List<Integer> mRoleIds;
    private List<String> mAssociatedLocationsIds;
    private int currentRoleID = 1;
    private int userId = 0;
    private String currentRole;
    private Spinner mRoles;
    private AutoCompleteTextView mNume, mPrenume;
    private EditText mTelefon, mEmail, mNickname, mLocatiiUser;
    private TextInputEditText mPassword;
    private CardView bSave;
    private ImageView bAddLocations;
    private Button bSMS;
    String phoneNo, message;


    private static final int LOADER_ROLES = 14;
    private static final int LOADER_SET_USER = 15;
    private static final int LOADER_GET_USER_LOCATIONS = 17;
    public static final int MY_PERMISSION_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_add_user);
        mAssociatedLocationsIds = new ArrayList<String>();
        //UI references
        mRoles = (Spinner) findViewById(R.id.spinnerRoles);
        mRoles.setOnItemSelectedListener(this);
        mNume = (AutoCompleteTextView) findViewById(R.id.nume_user);
        mPrenume = (AutoCompleteTextView) findViewById(R.id.prenume_user);
        mTelefon = (EditText) findViewById(R.id.telefon_user);
        mEmail = (EditText) findViewById(R.id.email_user);
        mLocatiiUser = (EditText) findViewById(R.id.locatii_user);
        bSave = (CardView) findViewById(R.id.card_view);
        mNickname = (EditText) findViewById(R.id.nickname_user);
        mPassword = (TextInputEditText) findViewById(R.id.pasword_user);
        bSMS = (Button) findViewById(R.id.bSMS);
        bAddLocations = (ImageView) findViewById(R.id.bAddLocation);
        String generatedPassword = UUID.randomUUID().toString();
        mPassword.setText(generatedPassword.substring(generatedPassword.length()-7));
        if(null != getIntent().getExtras()){
            mNume.setText(getIntent().getStringExtra(Constants.JSON_LAST_NAME));
            mPrenume.setText(getIntent().getStringExtra(Constants.JSON_FIRST_NAME));
            mTelefon.setText(getIntent().getStringExtra(Constants.JSON_PHONE));
            mEmail.setText(getIntent().getStringExtra(Constants.JSON_EMAIL));
            mNume.setText(getIntent().getStringExtra(Constants.JSON_LAST_NAME));
            currentRole = getIntent().getStringExtra(Constants.JSON_ROLE);
            mNickname.setText(getIntent().getStringExtra(Constants.JSON_USERNAME));
            mPassword.setText(getIntent().getStringExtra(Constants.JSON_PASSWORD));
            userId = getIntent().getIntExtra(Constants.JSON_ID, 0);
            if(getIntent().hasExtra(Constants.JSON_LOCATIE)){
                try {
                    JSONArray jsonArray = new JSONArray(getIntent().getStringExtra(Constants.JSON_LOCATIE));
                    mAssociatedLocationsIds.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mAssociatedLocationsIds.add(jsonArray.getJSONObject(i).getString(Constants.JSON_LOCATIE));
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
        mLocatiiUser.setText(getLocationNumber(mAssociatedLocationsIds.size()));
        mPrenume.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(TextUtils.isEmpty(mNickname.getText().toString())) {
                        String nickname = mPrenume.getText().toString().toLowerCase();
                        mNickname.setText(nickname);
                    }
                    mTelefon.requestFocus();
                    //attemptSave();
                    return true;
                }
                return false;
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });
        bAddLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditAddUserActivity.this, AddUserLocationsActivity.class);
                Bundle b = new Bundle();
                b.putString(Constants.JSON_FIRST_NAME, mPrenume.getText().toString());
                b.putString(Constants.JSON_LAST_NAME, mNume.getText().toString());
                b.putString(Constants.JSON_ROLE, currentRole);
                b.putString(Constants.JSON_PHONE, mTelefon.getText().toString());
                b.putString(Constants.JSON_EMAIL, mEmail.getText().toString());
                b.putString(Constants.JSON_USERNAME, mNickname.getText().toString());
                b.putString(Constants.JSON_PASSWORD, mPassword.getText().toString());
                b.putInt(Constants.JSON_ID, userId);
                i.putExtras(b);
                if(mAssociatedLocationsIds.size() > 0) {
                    i.putStringArrayListExtra("locatii", (ArrayList<String>) mAssociatedLocationsIds);
                }
                startActivity(i);
            }
        });

        bSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
            }
        });
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_ROLES, null,
                LOADER_ROLES, getApplicationContext(),
                getSupportLoaderManager(), EditAddUserActivity.this);
    }



    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_ROLES) {
            try{
                mRolesList = new ArrayList<String>();
                mRoleIds = new ArrayList<Integer>();
                for(int i = 0; i<jarray.length(); i++){
                    mRolesList.add(jarray.getJSONObject(i).getString(Constants.JSON_ROLE).toString());
                    mRoleIds.add(jarray.getJSONObject(i).getInt(Constants.JSON_ID));
                }
                ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter(this,
                        R.layout.spinner_row, mRolesList);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                mRoles.setAdapter(spinnerAdapter);
                int idspinner = Constants.getSpinnerIndex(mRoles, currentRole);
                mRoles.setSelection(idspinner);
                if(userId != 0) {
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.JSON_ID, userId);
                    new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_USER_LOCATIONS, cv,
                            LOADER_GET_USER_LOCATIONS, getApplicationContext(),
                            getSupportLoaderManager(), EditAddUserActivity.this);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jarray != null && idLoader == LOADER_SET_USER) {

            Intent intent = new Intent(EditAddUserActivity.this, AddUsersActivity.class);
            startActivity(intent);

        }
        if(jarray != null && idLoader == LOADER_GET_USER_LOCATIONS) {
            try{
                for(int i = 0; i < jarray.length(); i++){
                    int loc = jarray.getJSONObject(i).getInt(Constants.JSON_LOCATIE);
                    mAssociatedLocationsIds.add(Integer.toString(loc));
                }
                mLocatiiUser.setText(getLocationNumber(mAssociatedLocationsIds.size()));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        for(int i = 0; i < mRolesList.size(); i++){
            if(item.equals(mRolesList.get(i))){
                currentRoleID = mRoleIds.get(i);
                return;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Nothing
    }
    private String getLocationNumber(int number){
        return number + " locatii asociate userului";
    }


    protected void sendSMSMessage(){
        phoneNo = mTelefon.getText().toString();
        message = "Datele contului tau FURGONETA:\nusername: " + mNickname.getText().toString() +
                "\npassword: " + mPassword.getText().toString();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode) {
           case MY_PERMISSION_REQUEST_SEND_SMS: {
               if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   SmsManager smsManager = SmsManager.getDefault();
                   smsManager.sendTextMessage(phoneNo, null, message, null, null);
                   Toast.makeText(getApplicationContext(), "SMS trimis.", Toast.LENGTH_LONG).show();
               } else {
                   Toast.makeText(getApplicationContext(), "SMS esuat, incearca din nou.", Toast.LENGTH_LONG).show();
                   return;
               }
           }
       }
    }

    private void attemptSave() {


        // Reset errors.
        mNume.setError(null);
        mPrenume.setError(null);
        mTelefon.setError(null);
        mEmail.setError(null);
        mNickname.setError(null);

        // Store values at the time of the login attempt.
        String nume = mNume.getText().toString();
        String prenume = mPrenume.getText().toString();
        String telefon = mTelefon.getText().toString();
        String email = mEmail.getText().toString();
        String nickname = mNickname.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(nume)) {
            mNume.setError("Numele e obligatoriu");
            focusView = mNume;
            cancel = true;
        } else if (TextUtils.isEmpty(prenume)) {
            mPrenume.setError("Prenumele e obligatoriu");
            focusView = mPrenume;
            cancel = true;
        } else if (!isTelefonValid(telefon)) {
            mTelefon.setError("Nr. telefon invalid");
            focusView = mTelefon;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError("Email invalid");
            focusView = mEmail;
            cancel = true;
        } else if (TextUtils.isEmpty(nickname)) {
            mNickname.setError("Nickname obligatoriu");
            focusView = mNickname;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_LAST_NAME, mNume.getText().toString());
            cv.put(Constants.JSON_FIRST_NAME, mPrenume.getText().toString());
            cv.put(Constants.JSON_EMAIL, mEmail.getText().toString());
            cv.put(Constants.JSON_PHONE, mTelefon.getText().toString());
            cv.put(Constants.JSON_ROLE, Integer.toString(currentRoleID));
            cv.put(Constants.JSON_USERNAME, mNickname.getText().toString());
            cv.put(Constants.JSON_PASSWORD, mPassword.getText().toString());
            if(userId != 0){
                cv.put(Constants.JSON_ID, Integer.toString(userId));
            } else {
                JSONArray jarr = new JSONArray();
                try {
                    for (String loc : mAssociatedLocationsIds) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(Constants.JSON_LOCATIE, loc);
                        jarr.put(jsonObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cv.put(Constants.JSON_LOCATIE, jarr.toString());
            }

            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_USER, cv,
                    LOADER_SET_USER, getApplicationContext(), getSupportLoaderManager(), EditAddUserActivity.this);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");

    }

    private boolean isTelefonValid(String telefon) {
        //TODO: Replace this with your own logic
        //return Patterns.PHONE.matcher(telefon).matches();
        String tel = PhoneNumberUtils.normalizeNumber(telefon);

        if(TextUtils.isEmpty(telefon)){
          return false;
        } else if(!tel.substring(0,1).equals("0")){
            return false;
        } else if(tel.length() != 10){
            return false;
        } else return PhoneNumberUtils.isGlobalPhoneNumber(tel);
    }
}
