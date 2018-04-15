package ro.duoline.furgoneta.administrator;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.DataFormatException;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;
import ro.duoline.furgoneta.manager.DocumentViewActivity;
import ro.duoline.furgoneta.manager.InchidereActivity;


/**
 * The type All documents activity.
 */
public class ViewAllDocumentsActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    private RecyclerView rvDocuments;
    private JSONArray todayDocuments;

//    private Spinner mLocatiiUser;

    private static final int LOADER_ALL_DOCUMENTS = 35;
    private static final int LOADER_SET_BOUGHT = 36;


    ViewAllDocumentsActivity.DocumentsAdapter adapter;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_admin);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvDocuments = (RecyclerView) findViewById(R.id.rvAllDocuments);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDocuments.setLayoutManager(linearLayoutManager);
        rvDocuments.setHasFixedSize(true);
        todayDocuments = new JSONArray();
        adapter = new ViewAllDocumentsActivity.DocumentsAdapter(todayDocuments);
        rvDocuments.setAdapter(adapter);


        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_ADMIN_VIEW_DOCS, null,
                LOADER_ALL_DOCUMENTS, getApplicationContext(),
                getSupportLoaderManager(), ViewAllDocumentsActivity.this);


    }


    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null ) {
            adapter.setJsonArray(jarray);
        }

    }

    /**
     * The type Documents adapter.
     */
    class DocumentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private JSONArray mJarr;
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

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
        public int getItemViewType(int position) {
            if(isPositiomHeader(position)){
                return TYPE_HEADER;
            } else {
                return TYPE_ITEM;
            }
        }

        private boolean isPositiomHeader(int position){
            try{
                return mJarr.getJSONObject(position).getBoolean(Constants.JSON_HEADER);
            } catch (JSONException e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_ITEM) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.documents_with_location_item, parent, false);
                return new ViewAllDocumentsActivity.DocumentsAdapter.ItemViewHolder(view);
            } else if(viewType == TYPE_HEADER){
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View view = layoutInflater.inflate(R.layout.documents_header, parent, false);
                return new ViewAllDocumentsActivity.DocumentsAdapter.HeaderViewHolder(view);
            }

            throw new RuntimeException("there is no type that match the type " + viewType);
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof ItemViewHolder) {
                try {
                    int docType = mJarr.getJSONObject(position).getInt(Constants.JSON_ID_TIP_DOC);

                    switch (docType) {
                        case 1:
                            ((ItemViewHolder) holder).container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.aproviz));
                            break;
                        case 2:
                            ((ItemViewHolder) holder).container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bon));
                            break;
                        case 3:
                            ((ItemViewHolder) holder).container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.fisa));
                            break;
                        case 4:
                            ((ItemViewHolder) holder).container.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.inventar));
                            break;
                    }
                    ((ItemViewHolder) holder).tvType.setText(
                            mJarr.getJSONObject(position).getString(Constants.JSON_TYPE).toUpperCase());
                    ((ItemViewHolder) holder).tvDocNo.setText(mJarr.getJSONObject(position).getString(Constants.JSON_ID) + " din ");
                    ((ItemViewHolder) holder).tvDay.setText(mJarr.getJSONObject(position).getString(Constants.JSON_DAY));
                    ((ItemViewHolder) holder).tvHour.setText(mJarr.getJSONObject(position).getString(Constants.JSON_HOUR));
                    ((ItemViewHolder) holder).tvLocatia.setText(mJarr.getJSONObject(position).getString(Constants.JSON_LOCATIE));
                    ((ItemViewHolder) holder).cbVizualizat.setChecked(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(holder instanceof HeaderViewHolder) {
                try{
                    String date = mJarr.getJSONObject(position).getString(Constants.JSON_DAY);
                    Locale locale = new Locale("Romanian", "ro_RO");
                    SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", locale);
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");
                    Date date1 = formatter1.parse(date);
                    String dataString = formatter.format(date1);
                    ((HeaderViewHolder) holder).tvDocDate.setText(dataString);
                } catch (JSONException e){
                    e.printStackTrace();
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder{
            TextView tvDocDate;
            public HeaderViewHolder(View view){
                super(view);
                tvDocDate = view.findViewById(R.id.tvDocDataHeader);
            }
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
                cbVizualizat.setVisibility(View.GONE);
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPref();
                        Intent intent;
                        if(SaveSharedPreferences.getDocumentTypeID(getApplicationContext()) == 3){
                            intent = new Intent(ViewAllDocumentsActivity.this, InchidereActivity.class);
                        } else {
                            intent = new Intent(ViewAllDocumentsActivity.this, DocumentViewActivity.class);
                        }
                        intent.putExtra("New", false);
                        intent.putExtra("finalizat", true);
                        startActivity(intent);
                    }
                });
//

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
