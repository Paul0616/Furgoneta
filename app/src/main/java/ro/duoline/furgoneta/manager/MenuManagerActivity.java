package ro.duoline.furgoneta.manager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class MenuManagerActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    CardView mBSupply, mBConsum, mBFixture, mBDayClose;
    String mLocatie;
    int mLocatieId;
     private static final int LOADER_DOC_AVAILABLE = 19;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_manager);

        mBSupply = (CardView) findViewById(R.id.cb_aprovizionare);
        mBConsum = (CardView) findViewById(R.id.cb_bon);
        mBFixture = (CardView) findViewById(R.id.cb_inventar);
        mBDayClose = (CardView) findViewById(R.id.cb_incheiere);
        mLocatie = SaveSharedPreferences.getCurrentLocation(getApplicationContext());//getIntent().getStringExtra(Constants.JSON_LOCATIE);
        mLocatieId = SaveSharedPreferences.getCurrentLocationId(getApplicationContext());//getIntent().getIntExtra("locId", 0);



        //setTitle("Meniu Administrator");
        mBSupply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuManagerActivity.this, DocumentViewActivity.class);
                intent.putExtra("New", true);
                SaveSharedPreferences.setDocumentType(getApplicationContext(), "FISA APROVIZIONARE");//intent.putExtra(Constants.JSON_TYPE, "FISA APROVIZIONARE");
                SaveSharedPreferences.setDocumentTypeID(getApplicationContext(), 1);//intent.putExtra(Constants.JSON_ID_TIP_DOC, 1);
                intent.putExtra("locId", mLocatieId);
                startActivity(intent);
            }
        });
        mBConsum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuManagerActivity.this, DocumentViewActivity.class);
                intent.putExtra("New", true);
                SaveSharedPreferences.setDocumentType(getApplicationContext(), "BON DE CONSUM");//intent.putExtra(Constants.JSON_TYPE, "FISA APROVIZIONARE");
                SaveSharedPreferences.setDocumentTypeID(getApplicationContext(), 2);//intent.putExtra(Constants.JSON_ID_TIP_DOC, 1);
                intent.putExtra("locId", mLocatieId);
                startActivity(intent);
            }
        });
        mBDayClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuManagerActivity.this, InchidereActivity.class);
                intent.putExtra("New", true);
                SaveSharedPreferences.setDocumentType(getApplicationContext(), "FISA INCHEIERE ZI");
                SaveSharedPreferences.setDocumentTypeID(getApplicationContext(), 3);
                intent.putExtra("locId", mLocatieId);
                startActivity(intent);
            }
        });
        mBFixture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuManagerActivity.this, DocumentViewActivity.class);
                intent.putExtra("New", true);
                SaveSharedPreferences.setDocumentType(getApplicationContext(), "INVENTAR");//intent.putExtra(Constants.JSON_TYPE, "FISA APROVIZIONARE");
                SaveSharedPreferences.setDocumentTypeID(getApplicationContext(), 4);//intent.putExtra(Constants.JSON_ID_TIP_DOC, 1);
                intent.putExtra("locId", mLocatieId);
                startActivity(intent);
            }
        });
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_IS_DOC_AVAILABLE, null,
                LOADER_DOC_AVAILABLE, getApplicationContext(),
                getSupportLoaderManager(), MenuManagerActivity.this);

    }


    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {

        if(jarray != null && idLoader == LOADER_DOC_AVAILABLE) {
            try {
                boolean resultAp = jarray.getJSONObject(0).getBoolean(Constants.JSON_RESULT);
                boolean resultCo = jarray.getJSONObject(1).getBoolean(Constants.JSON_RESULT);
                boolean resultInv = jarray.getJSONObject(2).getBoolean(Constants.JSON_RESULT);
                boolean resultFisa = jarray.getJSONObject(3).getBoolean(Constants.JSON_RESULT);
                Constants.setCardEnabled(mBSupply, resultAp);
                Constants.setCardEnabled(mBConsum, resultCo);
                Constants.setCardEnabled(mBFixture, resultInv);
                Constants.setCardEnabled(mBDayClose, resultFisa);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
