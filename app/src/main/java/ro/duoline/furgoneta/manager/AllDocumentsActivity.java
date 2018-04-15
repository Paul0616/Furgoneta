package ro.duoline.furgoneta.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;
import ro.duoline.furgoneta.services.SendFCM;

/**
 * The type All documents activity.
 */
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
    private static final int LOADER_DOC_STATUS = 27;
    /**
     * The constant adapter.
     */
//
//    private static final int LOADER_DELETE_USER= 12;
//    private static final int LOADER_USERS_STATUS = 13;
    AllDocumentsActivity.DocumentsAdapter adapter;

    /**
     *
     * @param savedInstanceState
     */
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
                SaveSharedPreferences.setCurrentLocation(getApplicationContext(), mLocatiiUser.getSelectedItem().toString());
               // String loc = mLocatiiUser.getSelectedItem().toString();
                //intent.putExtra(Constants.JSON_LOCATIE, loc);
                SaveSharedPreferences.setCurrentLocationId(getApplicationContext(), currentLocationId);
               // intent.putExtra("locId", currentLocationId);
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
                Set<String> setAssociatedLocations = new HashSet<String>();
                for(int i = 0; i < jarray.length(); i++){
                    int loc = jarray.getJSONObject(i).getInt(Constants.JSON_LOCATIE);
                    String locden = jarray.getJSONObject(i).getString(Constants.JSON_NAME);
                    mAssociatedLocations.add(locden);
                    mAssociatedLocationsIds.add(loc);
                    setAssociatedLocations.add(locden);
                }
                SaveSharedPreferences.setAssociatedlocations(getApplicationContext(), setAssociatedLocations);

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
        if(jarray != null && idLoader == LOADER_DOC_STATUS) {
//            todayDocuments = jarray;
//            adapter = new AllDocumentsActivity.DocumentsAdapter(todayDocuments);
//            rvDocuments.setAdapter(adapter);
            adapter.setJsonArray(jarray);
            int doctypeid = SaveSharedPreferences.getDocumentTypeID(getApplicationContext());
            if(doctypeid == 1) {
                String locatia = SaveSharedPreferences.getCurrentLocation(getApplicationContext());
                int docno = SaveSharedPreferences.getDocumentNo(getApplicationContext());
                String datasiora = SaveSharedPreferences.getDocumentDate(getApplicationContext());

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("to", "/topics/news");
                    JSONObject jObj = new JSONObject();
                    jObj.accumulate("sound", "default");
                    jObj.accumulate("body", "Document: " + docno + "/" + datasiora + "\n" + locatia);
                    jObj.accumulate("title", "FISA APROVIZIONARE NOUA");
                    jObj.accumulate("location", locatia);
                    jsonObject.accumulate("notification", jObj);
                    jsonObject.accumulate("data", jObj);
                    SendFCM sendFCM = new SendFCM(jsonObject, getApplicationContext());
                    sendFCM.sendMessage();

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

        }
    }

    /**
     * The type Documents adapter.
     */
    class DocumentsAdapter extends RecyclerView.Adapter<AllDocumentsActivity.DocumentsAdapter.ItemViewHolder>{

        private JSONArray mJarr;

        /**
         * Instantiates a new Documents adapter.
         *
         * @param list the list
         */
        public DocumentsAdapter(JSONArray list){
            this.mJarr = list;
        }

        /**
         * Set json array.
         *
         * @param list the list
         */
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
                int docType = mJarr.getJSONObject(position).getInt(Constants.JSON_ID_TIP_DOC);

                switch (docType){
                    case 1:
                    holder.container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.aproviz));
                    break;
                    case 2:
                    holder.container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bon));
                    break;
                    case 3:
                        holder.container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.fisa));
                        break;
                    case 4:
                        holder.container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.inventar));
                    break;
                }
                holder.tvType.setText(
                        mJarr.getJSONObject(position).getString(Constants.JSON_TYPE).toUpperCase());
                holder.tvDocNo.setText(mJarr.getJSONObject(position).getString(Constants.JSON_ID) + " din ");
                holder.tvDay.setText(mJarr.getJSONObject(position).getString(Constants.JSON_DAY));
                holder.tvHour.setText(mJarr.getJSONObject(position).getString(Constants.JSON_HOUR));
                boolean status = mJarr.getJSONObject(position).getInt(Constants.JSON_STATUS) == 1 ? true : false;
                if(status){
                    holder.bFinalizare.setVisibility(View.GONE);
                    holder.bEdit.setVisibility(View.GONE);
                    holder.bDelete.setVisibility(View.GONE);
                    holder.ivDone.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        /**
         * The type Item view holder.
         */
        class ItemViewHolder extends RecyclerView.ViewHolder{
            /**
             * The Tv doc no.
             */
            TextView tvDocNo, /**
             * The Tv type.
             */
            tvType, /**
             * The Tv hour.
             */
            tvHour, /**
             * The Tv day.
             */
            tvDay;
            /**
             * The B edit.
             */
            ImageView bEdit, /**
             * The B delete.
             */
            bDelete, /**
             * The Iv done.
             */
            ivDone;
            /**
             * The B finalizare.
             */
            CardView bFinalizare;
            /**
             * The Container.
             */
            ConstraintLayout container;

            /**
             * Instantiates a new Item view holder.
             *
             * @param view the view
             */
            public ItemViewHolder(View view){
                super(view);
                tvDocNo = view.findViewById(R.id.tvNrDocument);
                tvType = view.findViewById(R.id.tvDocType);
                tvHour = view.findViewById(R.id.tvOra);
                tvDay = view.findViewById(R.id.tvdata);
                bEdit = view.findViewById(R.id.bDocEdit);
                bDelete = view.findViewById(R.id.bDocDelete);
                bFinalizare = (CardView) view.findViewById(R.id.bFinalizare);
                ivDone = (ImageView) view.findViewById(R.id.ivDone);
                container = (ConstraintLayout) view.findViewById(R.id.documentsContainer);
                bEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    setPref();
                    Intent intent;
                    if(SaveSharedPreferences.getDocumentTypeID(getApplicationContext()) == 3){
                        intent = new Intent(AllDocumentsActivity.this, InchidereActivity.class);
                    } else {
                        intent = new Intent(AllDocumentsActivity.this, DocumentViewActivity.class);
                    }
                    intent.putExtra("New", false);
                    intent.putExtra("finalizat", false);
                    startActivity(intent);
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
                bFinalizare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ContentValues cv = new ContentValues();
                            int docId = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            cv.put(Constants.JSON_ID, docId);
                            cv.put(Constants.JSON_LOCATIE, currentLocationId);
                            int idUser = SaveSharedPreferences.getUserId(getApplication());
                            cv.put(Constants.JSON_USER_ID, Integer.toString(idUser));
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_DOC_STATUS, cv,
                                    LOADER_DOC_STATUS, getApplicationContext(),
                                    getSupportLoaderManager(), AllDocumentsActivity.this);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                ivDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPref();
                        Intent intent;
                        if(SaveSharedPreferences.getDocumentTypeID(getApplicationContext()) == 3){
                            intent = new Intent(AllDocumentsActivity.this, InchidereActivity.class);
                        } else {
                            intent = new Intent(AllDocumentsActivity.this, DocumentViewActivity.class);
                        }
                        intent.putExtra("New", false);
                        intent.putExtra("finalizat", true);
                        startActivity(intent);
                    }
                });

            }
            private void setPref(){
                try {
                    SaveSharedPreferences.setDocumentType(getApplicationContext(), mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_TYPE).toUpperCase());
                    SaveSharedPreferences.setDocumentTypeID(getApplicationContext(), mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID_TIP_DOC));
                    SaveSharedPreferences.setDocumentNo(getApplicationContext(), mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID));
                    SaveSharedPreferences.setDocumentDate(getApplicationContext(), mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_DAY));
                } catch (JSONException e){
                    e.printStackTrace();
                }

            }

        }
    }
}
