package ro.duoline.furgoneta.administrator;

import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;

public class AddProductsActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    private RecyclerView rvProduse;
    private JSONArray setProduse;
    private static final int LOADER_ALL_PRODUCTS = 7;
    private static final int LOADER_ADD_PRODUCT = 8;
    private static final int LOADER_DELETE_PRODUCT= 9;
    private static final int LOADER_CHECK_PRODUCT= 10;


    AddProductsActivity.ProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvProduse = (RecyclerView) findViewById(R.id.rvProduse);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvProduse.setLayoutManager(linearLayoutManager);
        rvProduse.setHasFixedSize(true);
        setProduse = new JSONArray();
        adapter = new AddProductsActivity.ProductsAdapter(setProduse);
        rvProduse.setAdapter(adapter);
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_ALL_PRODUCTS, null,
                LOADER_ALL_PRODUCTS, getApplicationContext(), getSupportLoaderManager(), AddProductsActivity.this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptView = li.inflate(R.layout.prompt_edit_produse, null);
                addNewProduct(promptView, "", "", 0);

            }
        });
    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && jarray.length() > 0) {
            setProduse = jarray;
            adapter = new AddProductsActivity.ProductsAdapter(setProduse);
            rvProduse.setAdapter(adapter);
        }
    }

    private void addNewProduct(View view, String textProdus, String textUM, final int idForUpdate) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductsActivity.this);
        builder.setView(view);
        final EditText produs = (EditText) view.findViewById(R.id.editTextDialogProdus);
        final EditText um = (EditText) view.findViewById(R.id.editTextDialogUM);
        if (!TextUtils.isEmpty(textProdus) || !TextUtils.isEmpty(textUM)){
            produs.setText(textProdus);
            um.setText(textUM);
        }
        builder.setTitle("Adauga Produs");
        builder.setIcon(R.drawable.ingredients);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newProdus = produs.getText().toString();
                String newUM = um.getText().toString();
                if (!TextUtils.isEmpty(newProdus) && !TextUtils.isEmpty(newUM)){
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.JSON_PRODUCT, newProdus);
                    cv.put(Constants.JSON_UM, newUM);
                    if (idForUpdate != 0){
                        cv.put(Constants.JSON_ID, Integer.toString(idForUpdate));
                    }
                    new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_PRODUCT, cv,
                            LOADER_ADD_PRODUCT, getApplicationContext(),
                            getSupportLoaderManager(), AddProductsActivity.this);
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

    class ProductsAdapter extends RecyclerView.Adapter<AddProductsActivity.ProductsAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public ProductsAdapter(JSONArray list){
            this.mJarr = list;
        }

        @Override
        public AddProductsActivity.ProductsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.produse_item, parent, false);
            return new AddProductsActivity.ProductsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddProductsActivity.ProductsAdapter.ItemViewHolder holder, int position) {
            try {
                holder.prodItem.setText(
                        mJarr.getJSONObject(position).getString(Constants.JSON_PRODUCT.toString()));
                holder.prodUM.setText(mJarr.getJSONObject(position).getString(Constants.JSON_UM.toString()));
                int ap = mJarr.getJSONObject(position).getInt(Constants.JSON_SUPPLY.toString());
                holder.cbAp.setChecked((ap == 1) ? true : false);
                int bon = mJarr.getJSONObject(position).getInt(Constants.JSON_CONSUM.toString());
                holder.cbBon.setChecked((bon == 1) ? true : false);
                int inv = mJarr.getJSONObject(position).getInt(Constants.JSON_FIXTURES.toString());
                holder.cbInv.setChecked((inv == 1) ? true : false);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView prodItem, prodUM;
            ImageView bEdit, bDelete;
            CheckBox cbAp, cbBon, cbInv;
            public ItemViewHolder(View view){
                super(view);
                prodItem = view.findViewById(R.id.tvProdus);
                prodUM = view.findViewById(R.id.tvUM);
                bEdit = view.findViewById(R.id.bProdEdit);
                bDelete = view.findViewById(R.id.bProdDelete);
                cbAp = (CheckBox) view.findViewById(R.id.cbAp);
                cbBon = (CheckBox) view.findViewById(R.id.cbBon);
                cbInv = (CheckBox) view.findViewById(R.id.cbInv);
                bEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            LayoutInflater li = LayoutInflater.from(getApplicationContext());
                            View promptView = li.inflate(R.layout.prompt_edit_produse, null);
                            addNewProduct(promptView,
                                    mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_PRODUCT).toString(),
                                    mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_UM).toString(),
                                    mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID));
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
                            int delProduct = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            cv.put(Constants.JSON_ID, Integer.toString(delProduct));
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.DELETE_PRODUCT, cv,
                                    LOADER_DELETE_PRODUCT, getApplicationContext(),
                                    getSupportLoaderManager(), AddProductsActivity.this);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                cbAp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCheckBoxClicked(v);
                    }
                });
                cbBon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCheckBoxClicked(v);
                    }
                });
                cbInv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCheckBoxClicked(v);
                    }
                });

            }
            private void onCheckBoxClicked(View view){
                boolean checked = ((CheckBox) view).isChecked();
                try {
                    ContentValues cv = new ContentValues();
                    int id = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                    cv.put(Constants.JSON_ID, Integer.toString(id));
                    switch (view.getId()) {
                        case R.id.cbAp:
                            cv.put(Constants.JSON_SUPPLY, Integer.toString(checked ? 1 : 0));
                            break;
                        case R.id.cbBon:
                            cv.put(Constants.JSON_CONSUM, Integer.toString(checked ? 1 : 0));
                            break;
                        case R.id.cbInv:
                            cv.put(Constants.JSON_FIXTURES, Integer.toString(checked ? 1 : 0));
                            break;
                        default:
                            cv = null;
                    }
                    new LoadFromUrl(Constants.BASE_URL_STRING, Constants.CHECK_PRODUCT, cv,
                            LOADER_CHECK_PRODUCT, getApplicationContext(),
                            getSupportLoaderManager(), AddProductsActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
