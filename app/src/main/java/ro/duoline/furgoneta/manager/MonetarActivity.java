package ro.duoline.furgoneta.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;
import ro.duoline.furgoneta.databinding.ActivityMonetarBinding;

public class MonetarActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnFocusChangeListener, LoadFromUrl.LoadFromUrlFinished{
    ActivityMonetarBinding monetarBinding;
    private static  final int LOADER_SET_NUMERAR = 31;
    private static  final int LOADER_GET_MONETAR = 32;
    float total = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        monetarBinding = DataBindingUtil.setContentView(this, R.layout.activity_monetar);
        int docId = SaveSharedPreferences.getDocumentNo(getApplicationContext());
        ContentValues cv = new ContentValues();
        cv.put(Constants.JSON_ID, docId);
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_MONETAR, cv,
                LOADER_GET_MONETAR, getApplicationContext(),
                getSupportLoaderManager(), MonetarActivity.this);


        monetarBinding.bPreluare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", Float.toString(total));
               int docId = SaveSharedPreferences.getDocumentNo(getApplicationContext());
                ContentValues cv = new ContentValues();
                cv.put(Constants.JSON_ID, docId);
                cv.put(Constants.JSON_NUMERAR, Float.toString(total));
                try {
                    JSONObject jobj = new JSONObject();
                    jobj.accumulate(Constants.JSON_BANI_1,
                            TextUtils.isEmpty(monetarBinding.et1B.getText().toString()) ? "0" : monetarBinding.et1B.getText().toString());
                    jobj.accumulate(Constants.JSON_BANI_5,
                            TextUtils.isEmpty(monetarBinding.et5B.getText().toString()) ? "0" : monetarBinding.et5B.getText().toString());
                    jobj.accumulate(Constants.JSON_BANI_10,
                            TextUtils.isEmpty(monetarBinding.et10B.getText().toString()) ? "0" : monetarBinding.et10B.getText().toString());
                    jobj.accumulate(Constants.JSON_BANI_50,
                            TextUtils.isEmpty(monetarBinding.et50B.getText().toString()) ? "0" : monetarBinding.et50B.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_1,
                            TextUtils.isEmpty(monetarBinding.et1L.getText().toString()) ? "0" : monetarBinding.et1L.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_5,
                            TextUtils.isEmpty(monetarBinding.et5L.getText().toString()) ? "0" : monetarBinding.et5L.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_10,
                            TextUtils.isEmpty(monetarBinding.et10L.getText().toString()) ? "0" : monetarBinding.et10L.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_50,
                            TextUtils.isEmpty(monetarBinding.et50L.getText().toString()) ? "0" : monetarBinding.et50L.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_100,
                            TextUtils.isEmpty(monetarBinding.et100L.getText().toString()) ? "0" : monetarBinding.et100L.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_200,
                            TextUtils.isEmpty(monetarBinding.et200L.getText().toString()) ? "0" : monetarBinding.et200L.getText().toString());
                    jobj.accumulate(Constants.JSON_LEI_500,
                            TextUtils.isEmpty(monetarBinding.et500L.getText().toString()) ? "0" : monetarBinding.et500L.getText().toString());
                    cv.put("monetar", jobj.toString());

                } catch (JSONException e){
                    e.printStackTrace();
                }
                new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_NUMERAR, cv,
                        LOADER_SET_NUMERAR, getApplicationContext(),
                        getSupportLoaderManager(), MonetarActivity.this);

            }
        });
        //User press NEXT or DONE
        monetarBinding.et500L.setOnEditorActionListener(this);
        monetarBinding.et200L.setOnEditorActionListener(this);
        monetarBinding.et100L.setOnEditorActionListener(this);
        monetarBinding.et50L.setOnEditorActionListener(this);
        monetarBinding.et10L.setOnEditorActionListener(this);
        monetarBinding.et5L.setOnEditorActionListener(this);
        monetarBinding.et1L.setOnEditorActionListener(this);
        monetarBinding.et50B.setOnEditorActionListener(this);
        monetarBinding.et10B.setOnEditorActionListener(this);
        monetarBinding.et5B.setOnEditorActionListener(this);
        monetarBinding.et1B.setOnEditorActionListener(this);
        //User change focus
        monetarBinding.et500L.setOnFocusChangeListener(this);
        monetarBinding.et200L.setOnFocusChangeListener(this);
        monetarBinding.et100L.setOnFocusChangeListener(this);
        monetarBinding.et50L.setOnFocusChangeListener(this);
        monetarBinding.et10L.setOnFocusChangeListener(this);
        monetarBinding.et5L.setOnFocusChangeListener(this);
        monetarBinding.et1L.setOnFocusChangeListener(this);
        monetarBinding.et50B.setOnFocusChangeListener(this);
        monetarBinding.et10B.setOnFocusChangeListener(this);
        monetarBinding.et5B.setOnFocusChangeListener(this);
        monetarBinding.et1B.setOnFocusChangeListener(this);
    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_SET_NUMERAR) {
            Intent returnIntent = new Intent(MonetarActivity.this, InchidereActivity.class);
            returnIntent.putExtra("New", false);
            startActivity(returnIntent);
        }
        if(jarray != null && idLoader == LOADER_GET_MONETAR) {
            try {
                monetarBinding.et1B.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_BANI_1));
                attemptGetTotal(monetarBinding.et1B, monetarBinding.et1B.getText().toString());
                monetarBinding.et5B.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_BANI_5));
                attemptGetTotal(monetarBinding.et5B, monetarBinding.et5B.getText().toString());
                monetarBinding.et10B.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_BANI_10));
                attemptGetTotal(monetarBinding.et10B, monetarBinding.et10B.getText().toString());
                monetarBinding.et50B.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_BANI_50));
                attemptGetTotal(monetarBinding.et50B, monetarBinding.et50B.getText().toString());
                monetarBinding.et1L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_1));
                attemptGetTotal(monetarBinding.et1L, monetarBinding.et1L.getText().toString());
                monetarBinding.et5L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_5));
                attemptGetTotal(monetarBinding.et5L, monetarBinding.et5L.getText().toString());
                monetarBinding.et10L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_10));
                attemptGetTotal(monetarBinding.et10L, monetarBinding.et10L.getText().toString());
                monetarBinding.et50L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_50));
                attemptGetTotal(monetarBinding.et50L, monetarBinding.et50L.getText().toString());
                monetarBinding.et100L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_100));
                attemptGetTotal(monetarBinding.et100L, monetarBinding.et100L.getText().toString());
                monetarBinding.et200L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_200));
                attemptGetTotal(monetarBinding.et200L, monetarBinding.et200L.getText().toString());
                monetarBinding.et500L.setText("" + jarray.getJSONObject(0).getInt(Constants.JSON_LEI_500));
                attemptGetTotal(monetarBinding.et500L, monetarBinding.et500L.getText().toString());

            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public  boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_NEXT) {
            return attemptGetTotal(v, v.getText().toString());
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus){
            TextView tv = (TextView) v;
            attemptGetTotal(tv, tv.getText().toString());
        }
    }

    private boolean attemptGetTotal(TextView v, String text){
        boolean error = false;
        v.setError(null);
        int numBuc = 0;
        if(!TextUtils.isEmpty(text)) {
            try {
                numBuc = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                v.setError("Te rog sa introduci o valoare numerica intreaga!");
                error = true;
            }
        }
        if(v == monetarBinding.et500L){
            monetarBinding.tv500L.setText("" + Constants.round(500 * numBuc, 2));
        }
        if(v == monetarBinding.et200L){
            monetarBinding.tv200L.setText("" + Constants.round(200 * numBuc,2));
        }
        if(v == monetarBinding.et100L){
            monetarBinding.tv100L.setText("" + Constants.round(100 * numBuc, 2));
        }
        if(v == monetarBinding.et50L){
            monetarBinding.tv50L.setText("" + Constants.round(50 * numBuc, 2));
        }
        if(v == monetarBinding.et10L){
            monetarBinding.tv10L.setText("" + Constants.round(10 * numBuc, 2));
        }
        if(v == monetarBinding.et5L){
            monetarBinding.tv5L.setText("" + Constants.round(5 * numBuc, 2));
        }
        if(v == monetarBinding.et1L){
            monetarBinding.tv1L.setText("" + Constants.round(1 * numBuc, 2));
        }
        if(v == monetarBinding.et50B){
            monetarBinding.tv50B.setText("" + Constants.round((float)(0.5 * numBuc), 2));
        }
        if(v == monetarBinding.et10B){
            monetarBinding.tv10B.setText("" + Constants.round((float)(0.1 * numBuc), 2));
        }
        if(v == monetarBinding.et5B){
            monetarBinding.tv5B.setText("" + Constants.round((float)(0.05 * numBuc), 2));
        }
        if(v == monetarBinding.et1B){
            monetarBinding.tv1B.setText("" + Constants.round((float)(0.01 * numBuc), 2));
        }
        setTotal();
        return error;
    }

    private void setTotal(){
        try{
            float subtotal = 0;
            float partial = 0;
            partial = Float.parseFloat(monetarBinding.tv500L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv200L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv100L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv50L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv10L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv5L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv1L.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv50B.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv10B.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv5B.getText().toString());
            subtotal = subtotal + partial;
            partial = Float.parseFloat(monetarBinding.tv1B.getText().toString());
            subtotal = subtotal + partial;
            total = Constants.round(subtotal, 2);
            monetarBinding.tvTotal.setText("TOTAL: " + subtotal +" LEI");
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
    }
}
