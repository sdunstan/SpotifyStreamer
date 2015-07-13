package com.stevedunstan.spotifystreamer.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.fragment.SearchSpotifyFragment;
import com.stevedunstan.spotifystreamer.model.SSArtist;

import java.util.List;

public class SSArtistArrayAdapter extends ArrayAdapter<SSArtist> {

    private SearchSpotifyFragment fragment;

    public SSArtistArrayAdapter(SearchSpotifyFragment fragment, Context context, List<SSArtist> artistList) {
        super(context, R.layout.search_list_item, R.id.artistName, artistList);
        this.fragment = fragment;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_list_item, parent, false);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.artistName);
            holder.icon = (ImageView) convertView.findViewById(R.id.artistImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(this.getItem(position).name);
        loadIcon(holder.icon, getItem(position).imageUrl);

        return convertView;
    }

    public void loadIcon(ImageView imageView, String url) {
        if (url != null) {
            Picasso.with(fragment.getActivity()).load(url).placeholder(android.R.drawable.star_big_off).into(imageView);
        } else {
            Picasso.with(fragment.getActivity()).load(android.R.drawable.star_big_off).into(imageView);
        }
    }

    /**
     * ViewHolder pattern from Google I/O 2009 presentation: https://www.youtube.com/watch?v=N6YdwzAvwOA
     */
    class ViewHolder {
        TextView text;
        ImageView icon;
    }

}
