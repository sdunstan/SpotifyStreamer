package com.stevedunstan.spotifystreamer.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.model.SSSong;
import com.stevedunstan.spotifystreamer.util.Images;

import java.util.List;

public class SSSongArrayAdapter extends ArrayAdapter<SSSong> {

    public SSSongArrayAdapter(Context context, List<SSSong> songList) {
        super(context, R.layout.song_list_item, R.id.songTextView, songList);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.song_list_item, parent, false);

            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.songTextView);
            holder.albumTextView = (TextView) convertView.findViewById(R.id.albumTextView);
            holder.icon = (ImageView) convertView.findViewById(R.id.songImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText(this.getItem(position).name);
        holder.albumTextView.setText(this.getItem(position).albumName);
        Images.loadIcon(parent.getContext(), holder.icon, getItem(position).imageUrl);

        return convertView;
    }

    /**
     * ViewHolder pattern from Google I/O 2009 presentation: https://www.youtube.com/watch?v=N6YdwzAvwOA
     */
    static class ViewHolder {
        TextView nameTextView;
        TextView albumTextView;
        ImageView icon;
    }

}
