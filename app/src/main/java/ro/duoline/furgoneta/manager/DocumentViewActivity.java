package ro.duoline.furgoneta.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class DocumentViewActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    boolean docNew = false;
    private static final int LOADER_NEW_DOC = 22;
    private TextView mDocType, mDocNo, mDocDate;
    int mDocID, mDocTypeID;

   // String mDocTypeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constants.JSON_LOCATIE));

        mDocType = (TextView) findViewById(R.id.tvDocType);
        mDocNo = (TextView) findViewById(R.id.tvDocNo);
        mDocDate = (TextView) findViewById(R.id.tvDocDate);
        docNew = getIntent().getBooleanExtra("New", true);
        mDocTypeID = getIntent().getIntExtra(Constants.JSON_ID_TIP_DOC, 0);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocumentViewActivity.this, ProductsListActivity.class);
                intent.putExtra(Constants.JSON_ID, mDocID);
                intent.putExtra(Constants.JSON_TYPE,  mDocTypeID);
                startActivity(intent);
            }
        });
        if (docNew) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_TYPE, mDocTypeID);
            cv.put(Constants.JSON_LOCATIE, getIntent().getIntExtra("locId", 0));
            cv.put(Constants.JSON_ID, SaveSharedPreferences.getUserId(getApplicationContext()));
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_NEW_DOCUMENT, cv,
                    LOADER_NEW_DOC, getApplicationContext(),
                    getSupportLoaderManager(), DocumentViewActivity.this);
        } else {

            mDocType.setText(getIntent().getStringExtra(Constants.JSON_TYPE));
            mDocID = getIntent().getIntExtra(Constants.JSON_ID, 0);
            mDocNo.setText("Nr. " + mDocID);
            mDocDate.setText("din " + getIntent().getStringExtra(Constants.JSON_DAY));

        }
    }
    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_NEW_DOC) {
            try {
                mDocID = jarray.getJSONObject(0).getInt(Constants.JSON_ID);
                mDocNo.setText("" + mDocID);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                mDocDate.setText(format.format(cal.getTime()));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
