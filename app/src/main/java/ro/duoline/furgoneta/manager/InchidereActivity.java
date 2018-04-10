package ro.duoline.furgoneta.manager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class InchidereActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    private EditText mNumerar, mCard, mCasa;
    private Button bMonetar;
    private CardView bInapoiAndSave;
    public static final int REQUEST_CODE = 90;
    private static final int LOADER_NEW_DOC = 28;
    private static  final int LOADER_GET_FISA_INCHIDERE = 29;
    private static  final int LOADER_SET_FISA_INCHIDERE = 30;
    boolean finalizat;
    boolean docNew = false;
    private TextView mDocType, mDocNo, mDocDate;
    int mDocID, mDocTypeID, mMonetarID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inchidere);
        mNumerar = (EditText) findViewById(R.id.etNumerar);
        mCard = (EditText) findViewById(R.id.etCard);
        mCasa = (EditText) findViewById(R.id.etCasa);
        bMonetar = (Button) findViewById(R.id.bMonetar);
        mDocType = (TextView) findViewById(R.id.tvDocType3);
        mDocNo = (TextView) findViewById(R.id.tvDocNo3);
        mDocDate = (TextView) findViewById(R.id.tvDocDate3);
        bInapoiAndSave = (CardView) findViewById(R.id.bInapoiFisa);
        mNumerar.setEnabled(false);

        mDocTypeID = SaveSharedPreferences.getDocumentTypeID(getApplicationContext());
        docNew = getIntent().getBooleanExtra("New", true);
        if(getIntent().hasExtra("finalizat")){
            finalizat = getIntent().getBooleanExtra("finalizat", false);
            mCasa.setEnabled(!finalizat);
            mCard.setEnabled(!finalizat);
        }
        bMonetar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InchidereActivity.this, MonetarActivity.class);
                startActivity(i);
            }
        });

        bInapoiAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put(Constants.JSON_ID, mDocID);
                cv.put(Constants.JSON_NUMERAR, TextUtils.isEmpty(mNumerar.getText().toString()) ? "0" : mNumerar.getText().toString());
                cv.put(Constants.JSON_CARD, TextUtils.isEmpty(mCard.getText().toString()) ? "0" : mCard.getText().toString());
                cv.put(Constants.JSON_SOLD_CASA, TextUtils.isEmpty(mCasa.getText().toString()) ? "0" : mCasa.getText().toString());
                new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_FISA_INCHIDERE, cv,
                        LOADER_SET_FISA_INCHIDERE, getApplicationContext(),
                        getSupportLoaderManager(), InchidereActivity.this);
            }
        });

        if (docNew) {
            mDocType.setText(SaveSharedPreferences.getDocumentType(getApplicationContext()));
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_TYPE, mDocTypeID);
            cv.put(Constants.JSON_LOCATIE, SaveSharedPreferences.getCurrentLocationId(getApplicationContext()));
            cv.put(Constants.JSON_ID, SaveSharedPreferences.getUserId(getApplicationContext()));
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_NEW_DOCUMENT, cv,
            LOADER_NEW_DOC, getApplicationContext(),
            getSupportLoaderManager(), InchidereActivity.this);
        } else {
            mDocType.setText(SaveSharedPreferences.getDocumentType(getApplicationContext()));
            mDocID = SaveSharedPreferences.getDocumentNo(getApplicationContext());
            mDocNo.setText("Nr. " + mDocID);
            mDocDate.setText("din " + SaveSharedPreferences.getDocumentDate(getApplicationContext()));
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_ID, SaveSharedPreferences.getDocumentNo(getApplicationContext()));
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_FISA_INCHIDERE, cv,
                    LOADER_GET_FISA_INCHIDERE, getApplicationContext(),
            getSupportLoaderManager(), InchidereActivity.this);
        }

    }



    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_NEW_DOC) {
            try {
                mDocID = jarray.getJSONObject(0).getInt(Constants.JSON_ID);
                mDocNo.setText("" + mDocID);
                SaveSharedPreferences.setDocumentNo(getApplicationContext(), mDocID);

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                mDocDate.setText(format.format(cal.getTime()));
                SaveSharedPreferences.setDocumentDate(getApplicationContext(), format.format(cal.getTime()));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(jarray != null && idLoader == LOADER_GET_FISA_INCHIDERE) {
            try {
               if(jarray.length() > 0){
                   mNumerar.setText("" + jarray.getJSONObject(0).getDouble(Constants.JSON_NUMERAR));
                   mCard.setText("" + jarray.getJSONObject(0).getDouble(Constants.JSON_CARD));
                   mCasa.setText("" + jarray.getJSONObject(0).getDouble(Constants.JSON_SOLD_CASA));
                   mMonetarID = jarray.getJSONObject(0).getInt(Constants.JSON_ID);
               }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(jarray != null && idLoader == LOADER_SET_FISA_INCHIDERE) {
            Intent i = new Intent(InchidereActivity.this, AllDocumentsActivity.class);
            startActivity(i);
        }
    }
}
