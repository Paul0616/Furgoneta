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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;

public class AddLocationActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{

    private RecyclerView rvLocatii;
    private JSONArray setLocatii;
    private static final int LOADER_ALL_LOCATIONS = 4;
    private static final int LOADER_ADD_LOCATION = 5;
    private static final int LOADER_DELETE_LOCATION = 6;
   // private String newLocation;

    LocationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvLocatii = (RecyclerView) findViewById(R.id.rvLocatii);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvLocatii.setLayoutManager(linearLayoutManager);
        rvLocatii.setHasFixedSize(true);
        setLocatii = new JSONArray();
        adapter = new LocationsAdapter(setLocatii);
        rvLocatii.setAdapter(adapter);
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_ALL_LOCATIONS, null,
            LOADER_ALL_LOCATIONS, getApplicationContext(), getSupportLoaderManager(), AddLocationActivity.this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View promptView = li.inflate(R.layout.prompt_edit_text, null);
                addNewLocation(promptView, "", 0);

            }
        });
    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && jarray.length() > 0) {
            setLocatii = jarray;
            adapter = new LocationsAdapter(setLocatii);
            rvLocatii.setAdapter(adapter);
        }
    }

    private void addNewLocation(View view, String text, final int idForUpdate) {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddLocationActivity.this);
        builder.setView(view);
        final EditText userInput = (EditText) view.findViewById(R.id.editTextDialogUserInput);
        if (!TextUtils.isEmpty(text)){
            userInput.setText(text);
        }
        builder.setTitle("Adauga Locatie");
        builder.setIcon(R.drawable.ic_location_on_black_24dp);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newLocation = userInput.getText().toString();
                if (!TextUtils.isEmpty(newLocation)){
                    ContentValues cv = new ContentValues();
                    cv.put(Constants.JSON_LOCATIE, newLocation);
                    if (idForUpdate != 0){
                        cv.put(Constants.JSON_ID, Integer.toString(idForUpdate));
                    }
                    new LoadFromUrl(Constants.BASE_URL_STRING, Constants.SET_LOCATION, cv,
                            LOADER_ADD_LOCATION, getApplicationContext(),
                            getSupportLoaderManager(), AddLocationActivity.this);
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

    class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public LocationsAdapter(JSONArray list){
            this.mJarr = list;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.locatii_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            try {
                holder.locItem.setText(mJarr.getJSONObject(position).getString(Constants.JSON_LOCATIE).toString());
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
            ImageView bEdit, bDelete;
            public ItemViewHolder(View view){
                super(view);
                locItem = view.findViewById(R.id.tvLocatie);
                bEdit = view.findViewById(R.id.bLocEdit);
                bDelete = view.findViewById(R.id.bLocDelete);
                bEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            LayoutInflater li = LayoutInflater.from(getApplicationContext());
                            View promptView = li.inflate(R.layout.prompt_edit_text, null);
                            addNewLocation(promptView,
                                    mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_LOCATIE).toString(),
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
                            int delLocation = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            cv.put(Constants.JSON_ID, Integer.toString(delLocation));
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.DELETE_LOCATION, cv,
                                    LOADER_DELETE_LOCATION, getApplicationContext(),
                                    getSupportLoaderManager(), AddLocationActivity.this);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

}
