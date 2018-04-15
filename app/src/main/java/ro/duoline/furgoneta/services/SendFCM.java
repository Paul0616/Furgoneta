package ro.duoline.furgoneta.services;

import android.content.Context;
import android.os.AsyncTask;

import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import ro.duoline.furgoneta.Utils.SaveSharedPreferences;

/**
 * Created by Paul on 13/04/2018.
 */

public class SendFCM {
    private Context context;
    private JSONObject jsonObject;
    private static final String url = "https://fcm.googleapis.com/fcm/send";
    private static final String token = "AAAA2_KUgZo:APA91bF46ZCKHMVnu6V9B7lur069MobrEIwSyRXa1u3jGMy8PBspDZI73-RgrB_HrM9zUJke5Kote7VzlZIuGr7UbIj9VcKqz-g03T1CIU3t4rXH_dq1SrN7S96hIkXyguEDPdNAk3zX";

    public SendFCM(JSONObject jsonObject, Context context) {
        this.jsonObject = jsonObject;
        this.context = context;
    }

    public void sendMessage(){

            new HttpAsyncTask().execute(url);


    }


    public static String POST(String url, JSONObject continut, String auth){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            // 3. convert JSONObject to JSON to String
            String json = continut.toString();
            //String json = EntityUtils.toString()

            // 4. set json to StringEntity
            //StringEntity se = new UrlEncodedFormEntity(continut, HTTP.UTF_8);
            StringEntity se = new StringEntity(json, HTTP.UTF_8);

            // 5. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Authorization", "key=" + auth);
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);

            }else
                result = "Did not work!";

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 11. return result
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0],jsonObject,token);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, "Notificarea " + result + " a fost trimisa catre sofer", Toast.LENGTH_LONG).show();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
