package ro.duoline.furgoneta.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class AllDocumentsActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished, AdapterView.OnItemSelectedListener{
    private RecyclerView rvDocuments;
    private JSONArray todayDocuments;
    private List<String> mAssociatedLocations;
    private List<Integer> mAssociatedLocationsIds;
    private Spinner mLocatiiUser;
    private int currentLocationId;
   // private int currentDocTypeId = 1;
    private static final int LOADER_TODAY_DOCUMENTS = 21;
    private static final int LOADER_USER_LOCATIONS = 20;
    private static final int LOADER_DEL_DOCUMENT = 23;
//
//    private static final int LOADER_DELETE_USER= 12;
//    private static final int LOADER_USERS_STATUS = 13;
    AllDocumentsActivity.DocumentsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvDocuments = (RecyclerView) findViewById(R.id.rvDocuments);
        mLocatiiUser = (Spinner) findViewById(R.id.spinnerLocatii);
        mLocatiiUser.setOnItemSelectedListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDocuments.setLayoutManager(linearLayoutManager);
        rvDocuments.setHasFixedSize(true);
        todayDocuments = new JSONArray();
        adapter = new AllDocumentsActivity.DocumentsAdapter(todayDocuments);
        rvDocuments.setAdapter(adapter);
        mAssociatedLocations = new ArrayList<String>();
        mAssociatedLocationsIds = new ArrayList<Integer>();
        ContentValues cv = new ContentValues();
        cv.put(Constants.JSON_ID, Integer.toString(SaveSharedPreferences.getUserId(getApplicationContext())));
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_USER_LOCATIONS, cv,
                LOADER_USER_LOCATIONS, getApplicationContext(),
                getSupportLoaderManager(), AllDocumentsActivity.this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllDocumentsActivity.this, MenuManagerActivity.class);
                String loc = mLocatiiUser.getSelectedItem().toString();
                intent.putExtra(Constants.JSON_LOCATIE, loc);
                intent.putExtra("locId", currentLocationId);
               // intent.putExtra(Constants.JSON_TYPE, currentDocTypeId);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentLocationId = mAssociatedLocationsIds.get(position);
        ContentValues cv = new ContentValues();
        cv.put(Constants.JSON_ID, Integer.toString(SaveSharedPreferences.getUserId(getApplicationContext())));
        cv.put(Constants.JSON_LOCATIE, currentLocationId);
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_TODAY_DOCUMENTS, cv,
                LOADER_TODAY_DOCUMENTS, getApplicationContext(),
                getSupportLoaderManager(), AllDocumentsActivity.this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_USER_LOCATIONS) {
            try{
                mAssociatedLocations.clear();
                mAssociatedLocationsIds.clear();
                for(int i = 0; i < jarray.length(); i++){
                    int loc = jarray.getJSONObject(i).getInt(Constants.JSON_LOCATIE);
                    String locden = jarray.getJSONObject(i).getString(Constants.JSON_NAME);
                    mAssociatedLocations.add(locden);
                    mAssociatedLocationsIds.add(loc);
                }
                ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter(this,
                        R.layout.spinner_row, mAssociatedLocations);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
                mLocatiiUser.setAdapter(spinnerAdapter);
                currentLocationId = mAssociatedLocationsIds.get(0);
                ContentValues cv = new ContentValues();
                cv.put(Constants.JSON_ID, Integer.toString(SaveSharedPreferences.getUserId(getApplicationContext())));
                cv.put(Constants.JSON_LOCATIE, currentLocationId);
                new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_TODAY_DOCUMENTS, cv,
                        LOADER_TODAY_DOCUMENTS, getApplicationContext(),
                        getSupportLoaderManager(), AllDocumentsActivity.this);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        if(jarray != null && idLoader == LOADER_TODAY_DOCUMENTS) {
//            todayDocuments = jarray;
//            adapter = new AllDocumentsActivity.DocumentsAdapter(todayDocuments);
//            rvDocuments.setAdapter(adapter);
            adapter.setJsonArray(jarray);
        }
        if(jarray != null && idLoader == LOADER_DEL_DOCUMENT) {
//            todayDocuments = jarray;
//            adapter = new AllDocumentsActivity.DocumentsAdapter(todayDocuments);
//            rvDocuments.setAdapter(adapter);
            adapter.setJsonArray(jarray);
        }
    }

    class DocumentsAdapter extends RecyclerView.Adapter<AllDocumentsActivity.DocumentsAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public DocumentsAdapter(JSONArray list){
            this.mJarr = list;
        }

        public void setJsonArray(JSONArray list){
            mJarr = list;
            notifyDataSetChanged();
        }

        @Override
        public AllDocumentsActivity.DocumentsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.documents_item, parent, false);
            return new AllDocumentsActivity.DocumentsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AllDocumentsActivity.DocumentsAdapter.ItemViewHolder holder, int position) {
            try {
               // String test = mJarr.getJSONObject(position).getString(Constants.JSON_TYPE.toString().toUpperCase());
                holder.tvType.setText(
                        mJarr.getJSONObject(position).getString(Constants.JSON_TYPE.toString()).toUpperCase());
                holder.tvDocNo.setText(mJarr.getJSONObject(position).getString(Constants.JSON_ID.toString()) + " din ");
                holder.tvDay.setText(mJarr.getJSONObject(position).getString(Constants.JSON_DAY.toString()));
                holder.tvHour.setText(mJarr.getJSONObject(position).getString(Constants.JSON_HOUR.toString()));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvDocNo, tvType, tvHour, tvDay;
            ImageView bEdit, bDelete;

            public ItemViewHolder(View view){
                super(view);
                tvDocNo = view.findViewById(R.id.tvNrDocument);
                tvType = view.findViewById(R.id.tvTipDocument);
                tvHour = view.findViewById(R.id.tvOra);
                tvDay = view.findViewById(R.id.tvdata);
                bEdit = view.findViewById(R.id.bDocEdit);
                bDelete = view.findViewById(R.id.bDocDelete);

                bEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent = new Intent(AllDocumentsActivity.this, DocumentViewActivity.class);
                            intent.putExtra("New", false);
                            intent.putExtra(Constants.JSON_TYPE, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_TYPE).toUpperCase());
                            intent.putExtra(Constants.JSON_ID_TIP_DOC, mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID_TIP_DOC));
                            intent.putExtra(Constants.JSON_ID, mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID));
                            intent.putExtra(Constants.JSON_DAY, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_DAY));
                            startActivity(intent);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                bDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ContentValues cv = new ContentValues();
                            int delUser = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            cv.put(Constants.JSON_ID, Integer.toString(delUser));
                            cv.put(Constants.JSON_LOCATIE, currentLocationId);
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_DEL_DOCUMENT, cv,
                                    LOADER_DEL_DOCUMENT, getApplicationContext(),
                                    getSupportLoaderManager(), AllDocumentsActivity.this);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });


            }

        }
    }
}
