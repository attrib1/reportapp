package com.kisscompany.reportapp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.IOUtils;
import com.google.api.services.storage.StorageScopes;
import com.kisscompany.reportapp.adapter.NewFeed_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.http.Url;

/**
 * Created by chanpc on 8/18/2016.
 */
public class getFeedInfo extends AsyncTask<String,String,String> {


    Activity act;
    ListView listV;
    SwipeRefreshLayout refresh;
    public getFeedInfo(Activity a,ListView list,SwipeRefreshLayout r)
    {
        act = a;
        listV = list;
        refresh = r;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
            HttpTransport httpTransport= new com.google.api.client.http.javanet.NetHttpTransport();
            AssetManager am = act.getAssets();
            InputStream inputStream = am.open("Traffy-f869c3fe8e95.p12"); //you should not put the key in assets in prod version.
            File file =stream2file(inputStream);
            JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId("storage@traffy-cloud.iam.gserviceaccount.com")
                    .setServiceAccountScopes(scopes)
                    .setServiceAccountPrivateKeyFromP12File(file)
                    .build();
            String URI = "https://storage.googleapis.com/" + "traffy_image"+"/"+"353086.jpg";
            HttpRequestFactory requestFactory = httpTransport.createRequestFactory(credential);
            GenericUrl url2 = new GenericUrl(URI);
            HttpRequest get = requestFactory.buildGetRequest(url2);
            HttpResponse response2 = get.execute();
            final Bitmap bm = BitmapFactory.decodeStream(response2.getContent());

            final List<PostClass> posts = new ArrayList<PostClass>();
            posts.add(new PostClass(bm));
            posts.add(null);
            final ListAdapter adapter = new NewFeed_Adapter(act,posts);
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listV.setAdapter(adapter);
                    refresh.setRefreshing(false);
                }
            });
            response2.disconnect();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
           /* while((line = reader.readLine())!=null)
            {
                buff.append(line);
            }
            JSONArray JArray = new JSONArray(buff.toString());
            for(int i = 0 ; i < JArray.length();i++) {
                JSONObject JObject = JArray.getJSONObject(i);
                String picture = JObject.getString("picture");
                String name = JObject.getString("Name");
                int like = JObject.getInt("like");
                String content = JObject.getString("content");
                String stat = JObject.getString("status");
                Date d = new Date(JObject.getLong("time_Stamp") * 1000);
                SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss a");
                String date = format.format(d);
                // re establish image url
                connection.disconnect();*/
            /*BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine())!=null)
            {
                buff.append(line);
            }
            Log.d("Bitmap",buff.toString());*/
            //reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onPostExecute(String result)
    {

    }
    @SuppressLint("NewApi") public static File stream2file(InputStream in) throws IOException
    { final File tempFile = File.createTempFile("okkk", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);
        return tempFile; }
}
