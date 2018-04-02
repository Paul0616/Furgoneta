package ro.duoline.furgoneta.manager;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;
import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

public class ProductsListActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished {
    int mDocID, mDocTypeID;
    private RecyclerView rvProductslist;
    private CardView bBackAndSave;
    private List<Integer> mListOfChoosedProducts;
    private static final int LOADER_LIST_OF_PRODUCTS = 23;
    ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        mDocID = getIntent().getIntExtra(Constants.JSON_ID, 0);
        mDocTypeID = getIntent().getIntExtra(Constants.JSON_TYPE, 0);
        rvProductslist = (RecyclerView) findViewById(R.id.rvProductsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvProductslist.setLayoutManager(linearLayoutManager);
        rvProductslist.setHasFixedSize(true);
        JSONArray jarr = new JSONArray();
        adapter = new ProductsAdapter(jarr);
        rvProductslist.setAdapter(adapter);
        bBackAndSave = (CardView) findViewById(R.id.bInapoi);
        mListOfChoosedProducts = new ArrayList<Integer>();
        ContentValues cv = new ContentValues();
        switch (mDocTypeID){
            case 1:
                cv.put(Constants.JSON_TYPE, "aprovizionare");
                break;
            case 2:
                cv.put(Constants.JSON_TYPE, "consum");
                break;
            case 4:
                cv.put(Constants.JSON_TYPE, "inventar");
                break;
        }
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_LIST_OF_PRODUCTS, cv,
                LOADER_LIST_OF_PRODUCTS, getApplicationContext(),
                getSupportLoaderManager(), ProductsListActivity.this);
        bBackAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = mListOfChoosedProducts.toString();
                Toast.makeText(getApplicationContext(), test, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && idLoader == LOADER_LIST_OF_PRODUCTS){
            adapter.setJsonArray(jarray);
        }
    }

    class ProductsAdapter extends RecyclerView.Adapter<ProductsListActivity.ProductsAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public ProductsAdapter(JSONArray list){
            this.mJarr = list;
        }

        public void setJsonArray(JSONArray list){
            mJarr = list;
            notifyDataSetChanged();
        }

        @Override
        public ProductsListActivity.ProductsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.products_item, parent, false);
            return new ProductsListActivity.ProductsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductsListActivity.ProductsAdapter.ItemViewHolder holder, int position) {
            try {
                // String test = mJarr.getJSONObject(position).getString(Constants.JSON_TYPE.toString().toUpperCase());
                holder.tvProduct.setText(mJarr.getJSONObject(position).getString(Constants.JSON_PRODUCT));
                holder.tvUM.setText(mJarr.getJSONObject(position).getString(Constants.JSON_UM));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView tvProduct, tvUM;
            CheckBox cb;

            public ItemViewHolder(View view){
                super(view);
                tvProduct = view.findViewById(R.id.tvProdus);
                tvUM = view.findViewById(R.id.tvUM);
                cb = view.findViewById(R.id.cb);

                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //mListOfChoosedProducts.clear();
                        try {
                            if (((CheckBox) view).isChecked()) {
                                mListOfChoosedProducts.add(mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID));
                            } else {
                                mListOfChoosedProducts.remove(new Integer(mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID)));
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
