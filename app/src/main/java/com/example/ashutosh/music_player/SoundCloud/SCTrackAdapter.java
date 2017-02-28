package com.example.ashutosh.music_player.SoundCloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ashutosh.music_player.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ashutosh on 26/2/17.
 */

public class SCTrackAdapter extends BaseAdapter
{
    private Context mContext ;
    private List<Track> mTracks ;

    public SCTrackAdapter(Context context, List<Track> tracks)
    {
        mContext = context ;
        mTracks = tracks ;
    }

    @Override
    public int getCount()
    {
        return mTracks.size() ;
    }

    @Override
    public Object getItem(int position) {
        return mTracks.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = (Track) getItem(position);

        ViewHolder holder ;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tracklistrow, parent, false) ;
            holder = new ViewHolder() ;
            holder.trackImageView = (ImageView) convertView.findViewById(R.id.track_image) ;
            holder.titleTextView = (TextView) convertView.findViewById(R.id.track_title) ;
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag() ;
        }
        holder.titleTextView.setText(track.getTitle());

        Picasso.with(mContext).load(track.getArtworkURL()).into(holder.trackImageView);

        return convertView ;
    }

    static class ViewHolder
    {
        ImageView trackImageView ;
        TextView titleTextView ;
    }
}
