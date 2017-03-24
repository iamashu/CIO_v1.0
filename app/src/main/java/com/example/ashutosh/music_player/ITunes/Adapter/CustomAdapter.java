package com.example.ashutosh.music_player.ITunes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashutosh.music_player.ITunes.Model.Pojo;
import com.example.ashutosh.music_player.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ashutosh on 25/3/17.
 */

public class CustomAdapter extends ArrayAdapter<Pojo> {

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CustomAdapter(Context context, int resource, List<Pojo> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater layoutInflater;
            layoutInflater = LayoutInflater.from(getContext());
            v = layoutInflater.inflate(R.layout.item, null);
        }
        Pojo item = getItem(position);
        if (item != null) {
            TextView artist = (TextView) v.findViewById(R.id.artistTextView);
            TextView track = (TextView) v.findViewById(R.id.trackTextView);
            ImageView imageView = (ImageView) v.findViewById(R.id.track_image) ;

            if (artist != null) {
                artist.setText(item.getArtistName());
            }

            if (track != null) {
                track.setText(item.getTrackName());
            }

            if(imageView != null) {
                Picasso.with(getContext()).load(item.getImageView()).into(imageView);
            }

        }


        return v;
    }
}
