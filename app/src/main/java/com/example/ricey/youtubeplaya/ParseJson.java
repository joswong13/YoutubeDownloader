package com.example.ricey.youtubeplaya;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ricey on 1/20/2018.
 */

public class ParseJson {
    private static final String TAG = "ParseJson";

    public ArrayList<StoreJsonValues> parseYoutubeData(JSONArray youtubeData)
    {
        ArrayList<StoreJsonValues> youtubeArrayList = new ArrayList<>();



        try
        {
            for (int i = 0; i < youtubeData.length(); i ++)
            {
                StoreJsonValues youtubeParsesData = new StoreJsonValues();

                JSONObject eachArray = youtubeData.getJSONObject(i);

                JSONObject youtubeVideoID = eachArray.getJSONObject("id");
                youtubeParsesData.setVideoID(youtubeVideoID.getString("videoId"));
                //Log.d(TAG, "parseYoutubeData: " + youtubeParsesData.getVideoID());

                JSONObject snippet = eachArray.getJSONObject("snippet");
                youtubeParsesData.setTitle(snippet.getString("title"));
                //Log.d(TAG, "parseYoutubeData: " + youtubeParsesData.getTitle());

                JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                JSONObject medium = thumbnails.getJSONObject("medium");
                youtubeParsesData.setThumbnailURL(medium.getString("url"));
                //Log.d(TAG, "parseYoutubeData: " + youtubeParsesData.getThumbnailURL());

                youtubeParsesData.setChannel(snippet.getString("channelTitle"));

                String tempDate = snippet.getString("publishedAt");
                youtubeParsesData.setPublished(tempDate.substring(0,9));
                youtubeArrayList.add(youtubeParsesData);
            }


        }catch (JSONException e) {
            e.printStackTrace();
        }


        return youtubeArrayList;

    }

}

// JSON structure
//{
// "kind": "youtube#searchListResponse",
// "etag": "\"g7k5f8kvn67Bsl8L-Bum53neIr4/vtZ1Yk2_Yq24c9z-9ZmpCsoHBHA\"",
// "nextPageToken": "CBkQAA",
// "regionCode": "CA",
// "pageInfo": {
//              "totalResults": 1000000,
//              "resultsPerPage": 25
//              },
// "items": [
//      {
//       "kind": "youtube#searchResult",
//       "etag": "\"g7k5f8kvn67Bsl8L-Bum53neIr4/gez4hi_143VKssHarPEAh2-xfNw\"",
//       "id": {
//              "kind": "youtube#video",
//              "videoId": "Cck1TMtVedA"
//              },
//       "snippet": {
//              "publishedAt": "2014-06-15T03:48:04.000Z",
//              "channelId": "UCnIX_ToUfwE_WQEm5LrO2uQ",
//              "title": "Eagles   Hotel California Lyrics",
//              "description": "",
//              "thumbnails": {
//                          "default": {
//                                      "url": "https://i.ytimg.com/vi/Cck1TMtVedA/default.jpg",
//                                      "width": 120,
//                                      "height": 90
//                                      },
//                          "medium": {
//                                       "url": "https://i.ytimg.com/vi/Cck1TMtVedA/mqdefault.jpg",
//                                       "width": 320,
//                                       "height": 180
//                                       },
//                          "high": {
//                                      "url": "https://i.ytimg.com/vi/Cck1TMtVedA/hqdefault.jpg",
//                                      "width": 480,
//                                      "height": 360
//                                       }
//                              },
//              "channelTitle": "Angel Puente",
//              "liveBroadcastContent": "none"
//                   }
//       },
//