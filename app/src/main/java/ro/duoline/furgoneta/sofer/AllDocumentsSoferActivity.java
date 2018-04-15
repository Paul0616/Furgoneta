package ro.duoline.furgoneta.sofer;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;


import java.util.HashSet;
import java.util.Set;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;
import ro.duoline.furgoneta.manager.DocumentViewActivity;
import ro.duoline.furgoneta.manager.InchidereActivity;


/**
 * The type All documents activity.
 */
public class AllDocumentsSoferActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    private RecyclerView rvDocuments;
    private JSONArray todayDocuments;

    private Spinner mLocatiiUser;

    private static final int LOADER_TODAY_DOCUMENTS = 21;
    private static final int LOADER_SET_BOUGHT = 33;
    private static final int LOADER_USER_LOCATIONS = 34;

    AllDocumentsSoferActivity.DocumentsAdapter adapter;

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
        mLocatiiUser.setVisibility(View.GONE);
        //          mLocatiiUser.setOnItemSelectedListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDocuments.setLayoutManager(linearLayoutManager);
        rvDocuments.setHasFixedSize(true);
        todayDocuments = new JSONArray();
        adapter = new AllDocumentsSoferActivity.DocumentsAdapter(todayDocuments);
        rvDocuments.setAdapter(adapter);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       fab.setVisibility(View.INVISIBLE);
        ContentValues cv = new ContentValues();
        cv.put(Constants.JSON_ID, Integer.toString(SaveSharedPreferences.getUserId(getApplicationContext())));
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_USER_LOCATIONS, cv,
                LOADER_USER_LOCATIONS, getApplicationContext(),
                getSupportLoaderManager(), AllDocumentsSoferActivity.this);


    }


    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader != LOADER_USER_LOCATIONS) {
            adapter.setJsonArray(jarray);
        }
        if(jarray != null && idLoader == LOADER_USER_LOCATIONS) {
            Set<String> setAssociatedLocations = new HashSet<String>();
            try {
                for (int i = 0; i < jarray.length(); i++) {
                    //int loc = jarray.getJSONObject(i).getInt(Constants.JSON_LOCATIE);
                    String locden = jarray.getJSONObject(i).getString(Constants.JSON_NAME);
                    setAssociatedLocations.add(locden);
                }
                SaveSharedPreferences.setAssociatedlocations(getApplicationContext(), setAssociatedLocations);
            } catch (JSONException e){
                e.printStackTrace();
            }
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_SOFER_DOCUMENTS, null,
                    LOADER_TODAY_DOCUMENTS, getApplicationContext(),
                    getSupportLoaderManager(), AllDocumentsSoferActivity.this);
        }
    }

    /**
     * The type Documents adapter.
     */
    class DocumentsAdapter extends RecyclerView.Adapter<AllDocumentsSoferActivity.DocumentsAdapter.ItemViewHolder>{

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
        public AllDocumentsSoferActivity.DocumentsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.documents_with_location_item, parent, false);
            return new AllDocumentsSoferActivity.DocumentsAdapter.ItemViewHolder(view);
        }


        @Override
        public void onBindViewHolder(AllDocumentsSoferActivity.DocumentsAdapter.ItemViewHolder holder, int position) {
            try {
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
                holder.tvLocatia.setText(mJarr.getJSONObject(position).getString(Constants.JSON_LOCATIE));
                holder.cbVizualizat.setChecked(false);

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }


        class ItemViewHolder extends RecyclerView.ViewHolder{

            TextView tvDocNo, tvType, tvHour, tvDay, tvLocatia;
            ImageView ivDone;
            CheckBox cbVizualizat;


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
                tvLocatia = view.findViewById(R.id.tvLocatiaDoc);
                tvDay = view.findViewById(R.id.tvdata);
                cbVizualizat = view.findViewById(R.id.cbVizualizat);

                ivDone = (ImageView) view.findViewById(R.id.ivDone);
                container = (ConstraintLayout) view.findViewById(R.id.documentsContainer);

                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPref();
                        Intent intent;
                        if(SaveSharedPreferences.getDocumentTypeID(getApplicationContext()) == 3){
                            intent = new Intent(AllDocumentsSoferActivity.this, InchidereActivity.class);
                        } else {
                            intent = new Intent(AllDocumentsSoferActivity.this, DocumentViewActivity.class);
                        }
                        intent.putExtra("New", false);
                        intent.putExtra("finalizat", true);
                        startActivity(intent);
                    }
                });
                cbVizualizat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((CheckBox) v).isChecked()){
                            try {
                                confirmVizualizat(mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID));
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
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

            private void confirmVizualizat(final int docId) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AllDocumentsSoferActivity.this);

                builder.setTitle("Confirma vizualizarea Fisei de aprovizionare:");
                builder.setIcon(R.drawable.ingredients);
                builder.setCancelable(false);
                builder.setMessage("Daca confirmi vizualizarea atunci aceasta Fisa de Aprovizionare NU va mai putea fi vazuta de tine ci doar de userii cu rol de administrator!!!");
                builder.setPositiveButton("CONFIRMA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       ContentValues cv = new ContentValues();
                       cv.put(Constants.JSON_ID, docId);
                        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_BOUGHT, cv,
                                LOADER_SET_BOUGHT, getApplicationContext(),
                                getSupportLoaderManager(), AllDocumentsSoferActivity.this);
                    }
                });
                builder.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cbVizualizat.setChecked(false);
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

        }
    }
}
