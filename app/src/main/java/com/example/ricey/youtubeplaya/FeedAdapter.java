package com.example.ricey.youtubeplaya;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Ricey on 1/8/2018.
 */

public class FeedAdapter extends ArrayAdapter {

    private static final String TAG = "FeedAdapter";

    private final int layoutResource;

    private final LayoutInflater layoutInflater;

    private List<StoreJsonValues> videos;


    public FeedAdapter(@NonNull Context context, int resource, List<StoreJsonValues> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.videos = applications;
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        StoreJsonValues currentVid = videos.get(position);

        viewHolder.youtubeTitle.setText(currentVid.getTitle());
        viewHolder.youtubeChannel.setText(currentVid.getChannel());
        viewHolder.publishedDate.setText(currentVid.getPublished());
        Picasso.with(this.getContext()).load(currentVid.getThumbnailURL()).into(viewHolder.thumbnail);




        return convertView;
    }

    private class ViewHolder
    {
        final TextView youtubeTitle;
        final TextView youtubeChannel;
        final ImageView thumbnail;
        final TextView publishedDate;

        ViewHolder(View v)
        {
            this.youtubeTitle = v.findViewById(R.id.youtubeTitle);
            this.youtubeChannel = v.findViewById(R.id.youtubeChannel);
            this.thumbnail = v.findViewById(R.id.youtubeThumbnail);
            this.publishedDate = v.findViewById(R.id.publishedDate);

        }
    }

}






