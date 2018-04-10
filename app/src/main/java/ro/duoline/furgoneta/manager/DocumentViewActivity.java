package ro.duoline.furgoneta.manager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class DocumentViewActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    boolean docNew = false;
    private static final int LOADER_NEW_DOC = 22;
    private static final int LOADER_DOC_PRODUCTS = 25;
    private static final int LOADER_SET_QUANTITY = 26;
    private TextView mDocType, mDocNo, mDocDate;
    int mDocID, mDocTypeID;
    private RecyclerView rvDoc;
    private ArrayList<Integer> mListProductsId;
    DocumentAdapter adapter;
    boolean finalizat;
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
        mDocNo = (TextView) findViewById(R.id.tvDocNo3);
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

        if(getIntent().hasExtra("finalizat")){
          finalizat = getIntent().getBooleanExtra("finalizat", false);
          fab.setVisibility(finalizat ? View.INVISIBLE : View.VISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocumentViewActivity.this, ProductsListActivity.class);
                intent.putIntegerArrayListExtra("productsList", mListProductsId);
                startActivity(intent);
            }
        });
        if (docNew) {
            mDocType.setText(SaveSharedPreferences.getDocumentType(getApplicationContext()));
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
        if(jarray != null && idLoader == LOADER_SET_QUANTITY) {
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
            View view = layoutInflater.inflate(R.layout.products_doc_consum_item, parent, false);
            return new DocumentViewActivity.DocumentAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DocumentViewActivity.DocumentAdapter.ItemViewHolder holder, int position) {
            try {
                // String test = mJarr.getJSONObject(position).getString(Constants.JSON_TYPE.toString().toUpperCase());
                holder.tvProduct.setText(mJarr.getJSONObject(position).getString(Constants.JSON_PRODUCT));
                holder.tvUM.setText(mJarr.getJSONObject(position).getString(Constants.JSON_UM));
                holder.tvQty.setText(Double.toString(mJarr.getJSONObject(position).getDouble(Constants.JSON_QUANTITY)));
                holder.tvMotiv.setText(mJarr.getJSONObject(position).getString(Constants.JSON_MOTIV));

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvProduct, tvUM, tvQty, tvMotiv;


            public ItemViewHolder(View view){
                super(view);
                tvProduct = view.findViewById(R.id.tvProdus);
                tvUM = view.findViewById(R.id.tvUM);
                tvQty = view.findViewById(R.id.tvCantitate);
                tvMotiv = view.findViewById(R.id.tvMotiv);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    if(!finalizat) {
                        try {
                            int productId = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            double qty = mJarr.getJSONObject(getAdapterPosition()).getDouble(Constants.JSON_QUANTITY);
                            String motiv = mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_MOTIV);
                            LayoutInflater li = LayoutInflater.from(getApplicationContext());
                            int docId = SaveSharedPreferences.getDocumentTypeID(getApplicationContext());
                            View promptView;
                            switch (docId){
                                case 1:
                                    promptView = li.inflate(R.layout.prompt_edit_quantity, null);
                                    addQuantitySupply(promptView, Double.toString(qty), productId, mDocID, docId);
                                    break;
                                case 2:
                                    promptView = li.inflate(R.layout.prompt_edit_quantity_motiv, null);
                                    addQuantityConsum(promptView, Double.toString(qty), motiv, productId, mDocID);
                                    break;
                                case 4:
                                    promptView = li.inflate(R.layout.prompt_edit_quantity, null);
                                    addQuantitySupply(promptView, Double.toString(qty), productId, mDocID, docId);
                                    break;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    }
                });
            }
            private void addQuantityConsum(View view, String qty, String motivtxt, final int idProdus, final int idDocument) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentViewActivity.this);
                builder.setView(view);
                final EditText quantity = (EditText) view.findViewById(R.id.editTextDialogQuantity);
                final EditText motiv = (EditText) view.findViewById(R.id.editTextDialogMotiv);
                if (!TextUtils.isEmpty(qty)){
                    quantity.setText(qty);
                    motiv.setText(motivtxt);
                }
                builder.setTitle("Cantitatea si motivul consumului:");
                builder.setIcon(R.drawable.ingredients);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Double newQty = Double.parseDouble(quantity.getText().toString());
                        String newMotiv = motiv.getText().toString();
                        if (!TextUtils.isEmpty(quantity.getText().toString())){
                            ContentValues cv = new ContentValues();
                            cv.put(Constants.JSON_QUANTITY, newQty);
                            cv.put(Constants.JSON_MOTIV, newMotiv);
                            if (idProdus != 0 && idDocument != 0){
                                cv.put(Constants.JSON_ID, Integer.toString(idProdus));
                                cv.put(Constants.JSON_ID_TIP_DOC, Integer.toString(idDocument));
                            }
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_QUANTITY, cv,
                                    LOADER_SET_QUANTITY, getApplicationContext(),
                                    getSupportLoaderManager(), DocumentViewActivity.this);
                        }
                    }
                });
                builder.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

            private void addQuantitySupply(View view, String qty, final int idProdus, final int idDocument, final int docType) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentViewActivity.this);
                builder.setView(view);
                final EditText quantity = (EditText) view.findViewById(R.id.editTextDialogQuantity);

                if (!TextUtils.isEmpty(qty)){
                    quantity.setText(qty);

                }
                if(docType == 1) {
                    builder.setTitle("Cantitatea dorita:");
                } else if (docType == 4) {
                    builder.setTitle("Cantitatea gasita la inventar:");
                }
                builder.setIcon(R.drawable.ingredients);
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Double newQty = Double.parseDouble(quantity.getText().toString());

                        if (!TextUtils.isEmpty(quantity.getText().toString())){
                            ContentValues cv = new ContentValues();
                            cv.put(Constants.JSON_QUANTITY, newQty);
                            if (idProdus != 0 && idDocument != 0){
                                cv.put(Constants.JSON_ID, Integer.toString(idProdus));
                                cv.put(Constants.JSON_ID_TIP_DOC, Integer.toString(idDocument));
                            }
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_QUANTITY, cv,
                                    LOADER_SET_QUANTITY, getApplicationContext(),
                                    getSupportLoaderManager(), DocumentViewActivity.this);
                        }
                    }
                });
                builder.setNegativeButton("Anuleaza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }

        }
    }

}
