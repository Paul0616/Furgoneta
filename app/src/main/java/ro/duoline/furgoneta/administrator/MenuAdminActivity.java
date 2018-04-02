package ro.duoline.furgoneta.administrator;

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


public class MenuAdminActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    CardView mBProduse, mBDocumente, mBLocatii, mBUseri;

    private static final int LOADER_CHECK_LOCATII = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_admin);
        mBLocatii = (CardView) findViewById(R.id.cb_locatii);
        mBProduse = (CardView) findViewById(R.id.cb_produse);
        mBUseri = (CardView) findViewById(R.id.cb_useri);
        mBDocumente = (CardView) findViewById(R.id.cb_documente);
        //setTitle("Meniu Administrator");
        mBLocatii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, AddLocationActivity.class);
                startActivity(intent);
            }
        });
        mBDocumente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBUseri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, AddUsersActivity.class);
                startActivity(intent);
            }
        });
        mBProduse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAdminActivity.this, AddProductsActivity.class);
                startActivity(intent);
            }
        });
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_LOCATIONS_NUM, null,
                LOADER_CHECK_LOCATII, getApplicationContext(),
                getSupportLoaderManager(), MenuAdminActivity.this);

    }


    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_CHECK_LOCATII) {
            try {
                int resultLoc = jarray.getJSONObject(0).getInt(Constants.JSON_RESULT);
                int resultProd = jarray.getJSONObject(1).getInt(Constants.JSON_RESULT);
                if (resultLoc == 0) {
                    Constants.setCardEnabled(mBUseri, false);
                } else {
                    Constants.setCardEnabled(mBUseri, true);
                }
                if (resultProd == 0) {
                    Constants.setCardEnabled(mBDocumente, false);
                } else {
                    Constants.setCardEnabled(mBDocumente, true);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
}
