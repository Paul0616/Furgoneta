package ro.duoline.furgoneta.Utils;

/**
 * Created by Paul on 26/03/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.Scanner;
import java.util.Set;

public class LoadFromUrl implements LoaderManager.LoaderCallbacks<String>{


    private static int loaderId;
    private LoaderManager loaderManager;
    private JSONArray jArray;
    private Context context;
    final private LoadFromUrlFinished mFinishLoadingHandler;

    public LoadFromUrl(String base, String filename, ContentValues param, int loaderId,
                       Context context, LoaderManager loaderManager, LoadFromUrlFinished mFinishLoadingHandler){
        this.loaderId = loaderId;
        this.loaderManager = loaderManager;
        this.context = context;
        this.mFinishLoadingHandler = mFinishLoadingHandler;
        makeURLConnection(makeURL(base, filename, param), this.loaderId);
    }



    public interface LoadFromUrlFinished {
        void jsonLoadFinish(JSONArray jarray, int idLoader);
    }


    private void makeURLConnection(URL queryURL, int loaderID){
        Bundle queryBundle = new Bundle();
        queryBundle.putString("link",queryURL.toString());
        Loader<String> queryLoader = loaderManager.getLoader(loaderID);
        if(queryLoader == null){
            loaderManager.initLoader(loaderID, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderID, queryBundle, this);
        }
    }

    private URL makeURL(String base, String file, ContentValues parameters){
        Uri.Builder builder = new Uri.Builder();
        Uri bultUri;

        if (parameters != null){
            builder = Uri.parse(base).buildUpon().appendPath(file);
            Set<String> set = parameters.keySet();
            for(String s : set) {
                builder = builder.appendQueryParameter(s, parameters.getAsString(s));
            }
            bultUri = builder.build();
        } else {
            bultUri = Uri.parse(base).buildUpon().appendPath(file).build();
        }
        URL queryURL;
        try {
            queryURL = new URL(bultUri.toString());
            return queryURL;
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }    catch (IOException e) {
            e.printStackTrace();
            return null;
        }    finally

        {
            urlConnection.disconnect();
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(context) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String queryURLString = args.getString("link");
                if(queryURLString == null || queryURLString == "") return null;
                try{
                    URL queryURL = new URL(queryURLString);
                    String result = getResponseFromHttpUrl(queryURL);

                    return result;
                } catch (IOException e){

                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            if (loader.getId() == loaderId) {
                jArray = null;
                if (data != null) {
                    jArray = new JSONArray(data);
                }
                mFinishLoadingHandler.jsonLoadFinish(jArray, loader.getId());
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}


