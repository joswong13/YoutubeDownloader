package com.example.ricey.youtubeplaya;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerActivity extends YouTubeBaseActivity {

    private static final String TAG = "YTBActivity";
    Button youtubePlay;
    YouTubePlayerView youtubePlayerView;
    YouTubePlayer.OnInitializedListener youtubeListener;
    WebView downloadFromWeb;

    private String videoName;
    private String url;
    private String downloadAPIUrl = "https://www.youtube.com/watch?v=%s";
    //private String downloadAPIUrl = "https://youtubemp3api.com/@api/button/videos/%s";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);

        Intent intentGetExtra = getIntent();

        url = intentGetExtra.getStringExtra("VideoID");
        videoName = intentGetExtra.getStringExtra("VideoName");

        youtubePlay = findViewById(R.id.buttonPlay);
        youtubePlayerView = findViewById(R.id.youtubePlayer);
        downloadFromWeb = findViewById(R.id.webViewDownload);

        checkPermissions();

        downloadFromWeb.setWebViewClient(new WebViewClient());
        downloadFromWeb.loadUrl("https://en.savefrom.net");
        WebSettings webSettings = downloadFromWeb.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        downloadFromWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        downloadFromWeb.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                Log.d(TAG, "onDownloadStart:  clicked download manager");


                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                request.allowScanningByMediaScanner();

                request.setTitle(videoName + ".mp4");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloads");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();

            }
        });

        youtubeListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        youtubePlayerView.initialize(YTApiKey.getApiKey(), youtubeListener);

        youtubePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String downloadURL = String.format(downloadAPIUrl, url);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied", downloadURL);
                clipboard.setPrimaryClip(clip);

            }
        });
    }

    private void checkPermissions() {
        int externalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (externalStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }
}
