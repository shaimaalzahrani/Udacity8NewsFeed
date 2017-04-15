package com.example.shaimaalzahrani.udacity8newsfeed;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    ListView listView;
    TextView emptyListTextView;
    ArrayList<News> NewsList = new ArrayList<>();
    NewsList NewsListArrayAdapter;
    boolean isSearchOpened = false;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(
                getString(R.string.news_activity_saved_instance_newslist_key),
                NewsList);
        savedInstanceState.putBoolean(
                getString(R.string.news_activity_saved_instance_news_search_key), isSearchOpened);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.news_list_view_main_activity);
        emptyListTextView = (TextView) findViewById(R.id.empty_news_list_view_main_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(LOG_TAG, "Network connected...");
            AsyncTask NewsAsyncTask = new NewsHttpRequestAsyncTask();
            NewsAsyncTask.execute();
        } else {
            Log.d(LOG_TAG, "Network offline");
            showShortToast(getString(R.string.network_unavailable_message));
        }

        /* At Startup hide the News List */
        listView.setEmptyView(emptyListTextView);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(getString(R.string.news_activity_saved_instance_newslist_key))) {
                NewsList = savedInstanceState
                        .getParcelableArrayList(getString(R.string.news_activity_saved_instance_newslist_key));
                updateNewsList();
            }
        }
    }

    private void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class NewsHttpRequestAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.d(LOG_TAG, "doInBackground: " + params[0]);
            try {
                NewsHttpRequest();
            } catch (IOException e) {
                Log.d(LOG_TAG, "Error on Http Request");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateNewsList();
        }
    }

    private void updateNewsList() {
        NewsListArrayAdapter = new NewsList(getBaseContext(), NewsList);
        listView.setAdapter(NewsListArrayAdapter);
        NewsListArrayAdapter.notifyDataSetChanged();
    }

    private void NewsHttpRequest() throws IOException {
        Log.d(LOG_TAG, "NewsHttpRequest: ");
        InputStream inputStream = null;

        try {
            String encodedQuery = URLEncoder.encode("utf-8");
            URL url = new URL(getResources().getString(R.string.http_query_address) + encodedQuery +
                    getResources().getString(R.string.query_projection_key) +
                    getResources().getString(R.string.projection_lite_key));

            Log.d(LOG_TAG, "url: " + url.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(getResources().getInteger(R.integer.http_read_timeout));
            httpURLConnection.setConnectTimeout(getResources().getInteger(R.integer.http_connect_timeout));
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();

            int response = httpURLConnection.getResponseCode();
            Log.d(LOG_TAG, "Response code is: " + response);

            switch (response) {
                case HttpURLConnection.HTTP_OK:
                    inputStream = httpURLConnection.getInputStream();
                    String stringResponse = readIt(inputStream);
                    parseJson(stringResponse);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    showShortToast(getString(R.string.http_url_not_found_message));
                    break;
                default:
                    showShortToast(getString(R.string.http_generic_error_message));
                    break;
            }

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void parseJson(String stringFromInputStream) {
        try {
            NewsList.clear();
            JSONObject jsonObject = new JSONObject(stringFromInputStream);
            JSONArray jArray = jsonObject.getJSONArray(getString(R.string.json_items_key));

            for(int i = 0; i < jArray.length(); i++) {
                News News = new News();
                JSONObject volumeInfo = jArray.getJSONObject(i)
                        .getJSONObject(getString(R.string.json_volume_info_key));
                String title = volumeInfo.getString(getString(R.string.json_title_key));
                News.setTitle(title);
                String Description = volumeInfo.getString(getString(R.string.json_desc_key));
                News.setDescription(Description);
                NewsList.add(News);
            }

        } catch (JSONException e) {
            Log.d(LOG_TAG, "JSONException");
            e.printStackTrace();
        }
    }

    public String readIt(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(stream));
        String line = responseReader.readLine();

        while (line != null){
            builder.append(line);
            line = responseReader.readLine();
        }

        return builder.toString();
    }
}
