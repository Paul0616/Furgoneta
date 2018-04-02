package ro.duoline.furgoneta.administrator;

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
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import ro.duoline.furgoneta.R;
import ro.duoline.furgoneta.Utils.Constants;
import ro.duoline.furgoneta.Utils.LoadFromUrl;

public class AddUsersActivity extends AppCompatActivity implements LoadFromUrl.LoadFromUrlFinished{
    private RecyclerView rvUsers;
    private JSONArray setUseri;
    private static final int LOADER_ALL_USERS = 11;

    private static final int LOADER_DELETE_USER= 12;
    private static final int LOADER_USERS_STATUS = 13;



    AddUsersActivity.UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvUsers = (RecyclerView) findViewById(R.id.rvUseri);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvUsers.setLayoutManager(linearLayoutManager);
        rvUsers.setHasFixedSize(true);
        setUseri = new JSONArray();
        adapter = new AddUsersActivity.UserAdapter(setUseri);
        rvUsers.setAdapter(adapter);
        new LoadFromUrl(Constants.BASE_URL_STRING, Constants.GET_ALL_USERS, null,
                LOADER_ALL_USERS, getApplicationContext(),
                getSupportLoaderManager(), AddUsersActivity.this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddUsersActivity.this, EditAddUserActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void jsonLoadFinish(JSONArray jarray, int idLoader) {
        if(jarray != null && jarray.length() > 0) {
            setUseri = jarray;
            adapter = new AddUsersActivity.UserAdapter(setUseri);
            rvUsers.setAdapter(adapter);
        }
    }



    class UserAdapter extends RecyclerView.Adapter<AddUsersActivity.UserAdapter.ItemViewHolder>{

        private JSONArray mJarr;
        public UserAdapter(JSONArray list){
            this.mJarr = list;
        }

        @Override
        public AddUsersActivity.UserAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.useri_item, parent, false);
            return new AddUsersActivity.UserAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddUsersActivity.UserAdapter.ItemViewHolder holder, int position) {
            try {
                holder.userItem.setText(
                        mJarr.getJSONObject(position).getString(Constants.JSON_LAST_NAME.toString()) + " " +
                        mJarr.getJSONObject(position).getString(Constants.JSON_FIRST_NAME.toString()));
                int status = mJarr.getJSONObject(position).getInt(Constants.JSON_STATUS);
                int color1 = getResources().getColor(R.color.colorPrimary, null);
                int color2 = getResources().getColor(R.color.hintcolor, null);
                holder.userItem.setTextColor((status == 1) ? color1 : color2);
                holder.cbInactiv.setChecked((status == 1) ? false : true);
                holder.tvRol.setText(mJarr.getJSONObject(position).getString(Constants.JSON_ROLE.toString()));

            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mJarr.length();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            TextView userItem, tvRol;
            ImageView bEdit, bDelete;
            CheckBox cbInactiv;
            public ItemViewHolder(View view){
                super(view);
                userItem = view.findViewById(R.id.tvUsername);
                tvRol = view.findViewById(R.id.tvRol);
                cbInactiv = view.findViewById(R.id.cbInactiv);
                bEdit = view.findViewById(R.id.bProdEdit);
                bDelete = view.findViewById(R.id.bProdDelete);

                bEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Bundle b = new Bundle();
                            b.putString(Constants.JSON_FIRST_NAME, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_FIRST_NAME));
                            b.putString(Constants.JSON_LAST_NAME, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_LAST_NAME));
                            b.putString(Constants.JSON_ROLE, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_ROLE));
                            b.putString(Constants.JSON_PHONE, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_PHONE));
                            b.putString(Constants.JSON_EMAIL, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_EMAIL));
                            b.putString(Constants.JSON_USERNAME, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_USERNAME));
                            b.putString(Constants.JSON_PASSWORD, mJarr.getJSONObject(getAdapterPosition()).getString(Constants.JSON_PASSWORD));
                            b.putInt(Constants.JSON_ID, mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID));
                            Intent intent = new Intent(AddUsersActivity.this, EditAddUserActivity.class);
                            intent.putExtras(b);
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
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.DELETE_USER, cv,
                                    LOADER_DELETE_USER, getApplicationContext(),
                                    getSupportLoaderManager(), AddUsersActivity.this);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
                cbInactiv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            ContentValues cv = new ContentValues();
                            int id = mJarr.getJSONObject(getAdapterPosition()).getInt(Constants.JSON_ID);
                            cv.put(Constants.JSON_ID, Integer.toString(id));
                            boolean checked = cbInactiv.isChecked();
                            cv.put(Constants.JSON_STATUS, Integer.toString(checked ? 0 : 1));
                            new LoadFromUrl(Constants.BASE_URL_STRING, Constants.USER_STATUS, cv,
                                    LOADER_USERS_STATUS, getApplicationContext(),
                                    getSupportLoaderManager(), AddUsersActivity.this);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });

            }

        }
    }

}
