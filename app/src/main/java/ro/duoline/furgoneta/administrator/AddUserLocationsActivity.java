package ro.duoline.furgoneta.administrator;


import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;

public class AddUserLocationsActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{

    private RecyclerView recycler;
    private CardView bInapoi;
    private List<Integer> mAssociatedLocationsIds;
    private static final int LOADER_ALL_LOCATIONS = 16;
    private static final int LOADER_SET_USER_LOCATIONS = 18;
    AddUserLocationsActivity.LocationsAdapter adapter;
    private JSONArray setLocatii;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_locations);
        bInapoi = (CardView) findViewById(R.id.bInapoi);
        recycler = (RecyclerView) findViewById(R.id.rvUserLocations);

        mAssociatedLocationsIds = new ArrayList<Integer>();
        if(null != getIntent()){
            b = new Bundle();
            b.putString(Constants.JSON_FIRST_NAME, getIntent().getStringExtra(Constants.JSON_FIRST_NAME));
            b.putString(Constants.JSON_LAST_NAME, getIntent().getStringExtra(Constants.JSON_LAST_NAME));
            b.putString(Constants.JSON_ROLE, getIntent().getStringExtra(Constants.JSON_ROLE));
            b.putString(Constants.JSON_PHONE, getIntent().getStringExtra(Constants.JSON_PHONE));
            b.putString(Constants.JSON_EMAIL, getIntent().getStringExtra(Constants.JSON_EMAIL));
            b.putString(Constants.JSON_USERNAME, getIntent().getStringExtra(Constants.JSON_USERNAME));
            b.putString(Constants.JSON_PASSWORD, getIntent().getStringExtra(Constants.JSON_PASSWORD));
            b.putInt(Constants.JSON_ID, getIntent().getIntExtra(Constants.JSON_ID, 0));
        }
        if(null != getIntent().getStringArrayListExtra("locatii")){
            List<String> temp = new ArrayList<String>();
            temp = getIntent().getStringArrayListExtra("locatii");
            for(int i = 0; i<temp.size(); i++){
                mAssociatedLocationsIds.add(Integer.parseInt(temp.get(i)));

            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        bInapoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndBack();
            }
        });
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_ALL_LOCATIONS, null,
                LOADER_ALL_LOCATIONS, getApplicationContext(),
                getSupportLoaderManager(), AddUserLocationsActivity.this);
    }

    private void saveAndBack(){
        if(b.getInt(Constants.JSON_ID) != 0) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_ID, b.getInt(Constants.JSON_ID));
            JSONArray jarr = new JSONArray();
            try {
                for (int loc : mAssociatedLocationsIds) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.JSON_LOCATIE, "" + loc);
                    jarr.put(jsonObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            cv.put(Constants.JSON_LOCATIE, jarr.toString());
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_USER_LOCATIONS, cv,
                    LOADER_SET_USER_LOCATIONS, getApplicationContext(),
                    getSupportLoaderManager(), AddUserLocationsActivity.this);
        } else {
            Intent intent = new Intent(AddUserLocationsActivity.this, EditAddUserActivity.class);
            JSONArray jarr = new JSONArray();
            try {
                for (int loc : mAssociatedLocationsIds) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.JSON_LOCATIE, "" + loc);
                    jarr.put(jsonObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.putExtras(b);
            intent.putExtra(Constants.JSON_LOCATIE, jarr.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        saveAndBack();
    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && jarray.length() > 0 && idLoader == LOADER_ALL_LOCATIONS) {
            setLocatii = jarray;
            adapter = new AddUserLocationsActivity.LocationsAdapter(setLocatii);
            recycler.setAdapter(adapter);

        }
        if(jarray != null && idLoader == LOADER_SET_USER_LOCATIONS) {
            Intent intent = new Intent(AddUserLocationsActivity.this, EditAddUserActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    class LocationsAdapter extends RecyclerView.Adapter<AddUserLocationsActivity.LocationsAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public LocationsAdapter(JSONArray list){
            this.mJarr = list;
        }

        @Override
        public AddUserLocationsActivity.LocationsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.locatii_user_item, parent, false);
            return new AddUserLocationsActivity.LocationsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddUserLocationsActivity.LocationsAdapter.ItemViewHolder holder, int position) {
            try {
                holder.locItem.setText(mJarr.getJSONObject(position).getString(Constants.JSON_LOCATIE).toString());
                int currLoc = mJarr.getJSONObject(position).getInt(Constants.JSON_ID);
                if(mAssociatedLocationsIds.contains(currLoc)){
                    holder.cbPickedLocation.setChecked(true);
                } else {
                    holder.cbPickedLocation.setChecked(false);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView locItem;
            CheckBox cbPickedLocation;

            public ItemViewHolder(View view){
                super(view);
                locItem = view.findViewById(R.id.tvLocatieUser);
                cbPickedLocation = view.findViewById(R.id.cbUserLocation);
                cbPickedLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int currLoc = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            if (!cbPickedLocation.isChecked()) {
                                if (mAssociatedLocationsIds.contains(currLoc)) {
                                    mAssociatedLocationsIds.remove(mAssociatedLocationsIds.indexOf(currLoc));
                                }
                            } else {
                                if (!mAssociatedLocationsIds.contains(currLoc)) {
                                    mAssociatedLocationsIds.add(currLoc);
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
