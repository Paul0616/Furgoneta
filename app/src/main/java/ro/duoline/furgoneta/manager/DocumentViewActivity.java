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
import android.widget.CheckBox;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class DocumentViewActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    boolean docNew = false;
    private static final int LOADER_NEW_DOC = 22;
    private static final int LOADER_DOC_PRODUCTS = 25;
    private TextView mDocType, mDocNo, mDocDate;
    int mDocID, mDocTypeID;
    private RecyclerView rvDoc;
    private ArrayList<Integer> mListProductsId;
    DocumentAdapter adapter;
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
        mDocTypeID = SaveSharedPreferences.getDocumentTypeID(getApplicationContext());
        rvDoc = (RecyclerView) findViewById(R.id.rvDoc);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDoc.setLayoutManager(linearLayoutManager);
        rvDoc.setHasFixedSize(true);
        JSONArray jarr = new JSONArray();
        adapter = new DocumentAdapter(jarr);
        rvDoc.setAdapter(adapter);
        mListProductsId = new ArrayList<Integer>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocumentViewActivity.this, ProductsListActivity.class);
                intent.putIntegerArrayListExtra("productsList", mListProductsId);
                startActivity(intent);
            }
        });
        if (docNew) {
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_TYPE, mDocTypeID);
            cv.put(Constants.JSON_LOCATIE, SaveSharedPreferences.getCurrentLocationId(getApplicationContext()));
            cv.put(Constants.JSON_ID, SaveSharedPreferences.getUserId(getApplicationContext()));
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_NEW_DOCUMENT, cv,
                    LOADER_NEW_DOC, getApplicationContext(),
                    getSupportLoaderManager(), DocumentViewActivity.this);
        } else {

            mDocType.setText(SaveSharedPreferences.getDocumentType(getApplicationContext()));
            mDocID = SaveSharedPreferences.getDocumentNo(getApplicationContext());
            mDocNo.setText("Nr. " + mDocID);
            mDocDate.setText("din " + SaveSharedPreferences.getDocumentDate(getApplicationContext()));
            ContentValues cv = new ContentValues();
            cv.put(Constants.JSON_ID, SaveSharedPreferences.getDocumentNo(getApplicationContext()));
            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_DOCUMENT_PRODUCTS, cv,
                    LOADER_DOC_PRODUCTS, getApplicationContext(),
                    getSupportLoaderManager(), DocumentViewActivity.this);

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
        if(jarray != null && idLoader == LOADER_DOC_PRODUCTS) {
            adapter.setJsonArray(jarray);
            try {
                mListProductsId = new ArrayList<Integer>();
                for(int i = 0; i < jarray.length(); i++) {
                    mListProductsId.add(jarray.getJSONObject(i).getInt(Constants.JSON_ID));
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class DocumentAdapter extends RecyclerView.Adapter<DocumentViewActivity.DocumentAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public DocumentAdapter(JSONArray list){
            this.mJarr = list;
        }

        public void setJsonArray(JSONArray list){
            mJarr = list;
            notifyDataSetChanged();
        }

        @Override
        public DocumentViewActivity.DocumentAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.products_doc_item, parent, false);
            return new DocumentViewActivity.DocumentAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DocumentViewActivity.DocumentAdapter.ItemViewHolder holder, int position) {
            try {
                // String test = mJarr.getJSONObject(position).getString(Constants.JSON_TYPE.toString().toUpperCase());
                holder.tvProduct.setText(mJarr.getJSONObject(position).getString(Constants.JSON_PRODUCT));
                holder.tvUM.setText(mJarr.getJSONObject(position).getString(Constants.JSON_UM));
                holder.tvQty.setText(Double.toString(mJarr.getJSONObject(position).getDouble(Constants.JSON_QUANTITY)));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvProduct, tvUM, tvQty;


            public ItemViewHolder(View view){
                super(view);
                tvProduct = view.findViewById(R.id.tvProdus);
                tvUM = view.findViewById(R.id.tvUM);
                tvQty = view.findViewById(R.id.tvCantitate);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

        }
    }

}
