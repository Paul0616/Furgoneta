package ro.duoline.furgoneta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;



import android.os.Build;
import android.os.Bundle;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;



import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;
import ro.duoline.furgoneta.administrator.MenuAdminActivity;
import ro.duoline.furgoneta.manager.AllDocumentsActivity;
import ro.duoline.furgoneta.manager.MenuManagerActivity;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int LOADER_USER_STATUS = 1;
    private static final int LOADER_CHECK_USER = 2;


    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    TextView mCardTitle;
    CardView bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.email);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textinputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textinputLayoutPassword);
       // setTitle("");


        mPasswordView = (EditText) findViewById(R.id.password);
        mCardTitle = (TextView) findViewById(R.id.cardTitle);
        bLogout = (CardView) findViewById(R.id.bLogout);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        CardView mUserSignInButton = (CardView) findViewById(R.id.bInapoi);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String rol = SaveSharedPreferences.getRol(getApplicationContext());
                if(SaveSharedPreferences.getUserId(getApplicationContext()) == 0){
                    attemptLogin();
                } else {
                    if(SaveSharedPreferences.getRol(getApplicationContext()).equals("administrator")) {
                        Intent i = new Intent(LoginActivity.this, MenuAdminActivity.class);
                        startActivity(i);
                    } else if(SaveSharedPreferences.getRol(getApplicationContext()).equals("manager")) {
                        Intent i = new Intent(LoginActivity.this, AllDocumentsActivity.class);
                        startActivity(i);
                    } else if(SaveSharedPreferences.getRol(getApplicationContext()).equals("sofer")) {

                    }
                }

            }
        });
        bLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreferences.setUser(getApplicationContext(), "", 0, "", "", "");
                setLogout();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        getUser(getApplicationContext());
    }
    private void setLogin(){
        textInputLayoutEmail.setVisibility(View.GONE);
        textInputLayoutPassword.setVisibility(View.GONE);
        mCardTitle.setText("START");
        bLogout.setVisibility(View.VISIBLE);
    }
    private void setLogout(){
        textInputLayoutEmail.setVisibility(View.VISIBLE);
        textInputLayoutPassword.setVisibility(View.VISIBLE);
        mCardTitle.setText("Autentificare");
        mPasswordView.setText("");
        mUserNameView.setText("");
        bLogout.setVisibility(View.GONE);
    }
    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_USER_STATUS) {
            try {
                int result = jarray.getJSONObject(0).getInt(Constants.JSON_RESULT);
                if (result == 1) {
                    int status = jarray.getJSONObject(1).getInt(Constants.JSON_STATUS);
                    if (status == 0){
                        showProgress(false);
                        Toast.makeText(getApplicationContext(),
                                "Userul " + SaveSharedPreferences.getFullname(getApplicationContext()) + " temporar inactiv.", Toast.LENGTH_LONG)
                                .show();
                        SaveSharedPreferences.setUser(getApplicationContext(), "", 0, "", "", "");
                        setLogout();
                    } else {
                        showProgress(false);
//                        Intent i = new Intent(LoginActivity.this, MenuAdminActivity.class);
//                        startActivity(i);
                        setLogin();
                    }

                } else {
                    showProgress(false);
                    Toast.makeText(getApplicationContext(),
                            "Userul " + SaveSharedPreferences.getFullname(getApplicationContext()) + " a fost sters din Baza de Date", Toast.LENGTH_LONG)
                            .show();
                    SaveSharedPreferences.setUser(getApplicationContext(), "", 0, "", "", "");
                    setLogout();
                }

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(jarray != null && idLoader == LOADER_CHECK_USER) {
            try {
                int result = jarray.getJSONObject(0).getInt(Constants.JSON_RESULT);
                if (result == 1) {
                    String email = jarray.getJSONObject(3).getString(Constants.JSON_EMAIL);
                    int userid = jarray.getJSONObject(1).getInt(Constants.JSON_USER_ID);
                    String rol = jarray.getJSONObject(2).getString(Constants.JSON_ROLE);
                    String last = jarray.getJSONObject(5).getString(Constants.JSON_LAST_NAME);
                    String first = jarray.getJSONObject(4).getString(Constants.JSON_FIRST_NAME);
                    SaveSharedPreferences.setUser(getApplicationContext(),
                            jarray.getJSONObject(3).getString(Constants.JSON_EMAIL),
                            jarray.getJSONObject(1).getInt(Constants.JSON_USER_ID),
                            jarray.getJSONObject(2).getString(Constants.JSON_ROLE),
                            jarray.getJSONObject(5).getString(Constants.JSON_LAST_NAME),
                            jarray.getJSONObject(4).getString(Constants.JSON_FIRST_NAME));
                    String[] strings = {mUserNameView.getText().toString()};
                    addUsernameToAutoComplete(strings);
                    showProgress(false);
                    setLogin();

                } else if (result == 2){
                    showProgress(false);
                    mUserNameView.setError(getString(R.string.error_no_email));
                    mUserNameView.requestFocus();
                } else if (result == 3){
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                } else if (result == 4){
                    showProgress(false);
                    mPasswordView.setText("");
                    mUserNameView.setText("");
                    Toast.makeText(getApplicationContext(), "Acest user este temporar inactiv", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private void getUser(Context ctx) {
        int userId = SaveSharedPreferences.getUserId(ctx);
        if(userId > 0){
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_USER_ID, Integer.toString(userId));
            showProgress(true);
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_USER_STATUS, cv,
                    LOADER_USER_STATUS, ctx, getSupportLoaderManager(), LoginActivity.this);
        }



    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mUserNameView.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_USERNAME, mUserNameView.getText().toString());
            cv.put(Constants.JSON_PASSWORD, mPasswordView.getText().toString());
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_LOGIN, cv,
                    LOADER_CHECK_USER, getApplicationContext(), getSupportLoaderManager(), LoginActivity.this);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void addUsernameToAutoComplete(String[] emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUserNameView.setAdapter(adapter);
    }

}

