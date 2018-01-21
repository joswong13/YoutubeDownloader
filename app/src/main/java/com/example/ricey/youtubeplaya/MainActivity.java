package com.example.ricey.youtubeplaya;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends YouTubeBaseActivity {

//    YouTubePlayerView myPlayer;
//    YouTubePlayer.OnInitializedListener yTListener;
//    Button playButton;
    Button searchButton;
    ListView listView;
    EditText searchText;
    private String querySearch;

    FloatingActionButton floatingNext;
    FloatingActionButton floatingPrev;

    private String nextPageToken;
    private String prevPageToken;

    private String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&q=%s&key=%s";
    ArrayList<StoreJsonValues> youtubeArray;
    private static final String TAG = "MainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        searchButton = findViewById(R.id.searchButton);
        listView = findViewById(R.id.youTubeJsonListView);
        searchText = findViewById(R.id.searchBox);
        floatingNext = findViewById(R.id.floatingNextButton);
        floatingPrev = findViewById(R.id.floatingPrevButton);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean done = false;
                if (i == EditorInfo.IME_ACTION_DONE) {

                    InputMethodManager inputKeyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputKeyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Log.d(TAG, "onEditorAction: pressed done");

                    querySearch = searchText.getText().toString();
                    String urlPath = String.format(url,querySearch,YTApiKey.getApiKey());
                    GetYoutubeVideos getJsonData = new GetYoutubeVideos();
                    getJsonData.execute(urlPath);
                    done = true;
                }

                return done;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                querySearch = searchText.getText().toString();
                Log.d(TAG, "onClick: " + querySearch);

                InputMethodManager inputKeyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputKeyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String urlPath = String.format(url,querySearch,YTApiKey.getApiKey());
                GetYoutubeVideos getJsonData = new GetYoutubeVideos();
                getJsonData.execute(urlPath);
            }
        });
        
        floatingNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextPageToken != null)
                {
                    String nextPageUrl = "https://www.googleapis.com/youtube/v3/search?pageToken=%s&part=snippet&maxResults=25&q=%s&key=%s";
                    String urlPath = String.format(nextPageUrl,nextPageToken, querySearch,YTApiKey.getApiKey());
                    GetYoutubeVideos getJsonData = new GetYoutubeVideos();
                    getJsonData.execute(urlPath);

                    Log.d(TAG, "onClick: floating button was clicked");

                }


            }
        });

        floatingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prevPageToken != null)
                {
                    String nextPageUrl = "https://www.googleapis.com/youtube/v3/search?pageToken=%s&part=snippet&maxResults=25&q=%s&key=%s";
                    String urlPath = String.format(nextPageUrl,prevPageToken, querySearch,YTApiKey.getApiKey());
                    GetYoutubeVideos getJsonData = new GetYoutubeVideos();
                    getJsonData.execute(urlPath);

                    Log.d(TAG, "onClick: floating button was clicked");

                }


            }
        });
        
    }


    public class GetYoutubeVideos extends AsyncTask<String, Void, String>
    {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null)
            {
                try {
                    JSONObject youtubeJsonObject = new JSONObject(s);

                    try
                    {
                        nextPageToken = youtubeJsonObject.getString("nextPageToken");
                    } catch (JSONException e)
                    {
                        Log.e(TAG, "onPostExecute: no next page possible");
                    }
                    try
                    {
                        prevPageToken = youtubeJsonObject.getString("prevPageToken");
                    }  catch (JSONException e)
                    {
                        Log.e(TAG, "onPostExecute: no prev page possible");
                    }

                    Log.d(TAG, "onPostExecute: next page token " + nextPageToken);
                    JSONArray youtubeItems = youtubeJsonObject.getJSONArray("items");
                    ParseJson parseJsonUserData = new ParseJson();
                    youtubeArray = parseJsonUserData.parseYoutubeData(youtubeItems);

                } catch (JSONException e) {
                 Log.e(TAG, "doInBackground: second round " + e.getMessage() );
                }



            }

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.listobjectview, youtubeArray);
            listView.setAdapter(feedAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String clicked = youtubeArray.get(i).getVideoID();
                    String videoName = youtubeArray.get(i).getTitle();
                    Log.d(TAG, "onItemClick: " + clicked);

                    Intent intent = new Intent(MainActivity.this, YoutubePlayerActivity.class);
                    intent.putExtra("VideoID" , clicked);
                    intent.putExtra("VideoName", videoName);
                    startActivity(intent);

                }
            });

        }

        @Override
        protected String doInBackground(String... strings)
        {
            String jsonData = downloadJson(strings[0]);

            return jsonData;
        }

        private String downloadJson (String urlPath)
        {
            try
            {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int response = connection.getResponseCode();
                Log.d(TAG, "downloadJson: user response code " + response);

                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader  = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder jsonResult = new StringBuilder();

                String line;

                try {
                    while ((line = reader.readLine()) != null)
                    {
                        jsonResult.append(line).append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                reader.close();

                Log.d(TAG, "downloadJson: finished appending json " + jsonResult.toString());

                return jsonResult.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
//        myPlayer = findViewById(R.id.youtubePlayer);
//        playButton = findViewById(R.id.playButton);
//
//        yTListener = new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                    youTubePlayer.loadVideo("Cck1TMtVedA");
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        };
//
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myPlayer.initialize(YTApiKey.getApiKey(), yTListener);
//            }
//        });


    }

