package com.stevedunstan.spotifystreamer.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class Images {

    public static String getLastImage(List<Image> images) {
        String thumbnail = null;

        // Images are returned by the Spotify API in descending size order.
        if (images != null && images.size() > 0) {
            thumbnail = images.get(images.size()-1).url;
        }

        return thumbnail;
    }

    public static void loadIcon(Context context, ImageView imageView, String url) {
        if (url != null) {
            Picasso.with(context).load(url).placeholder(android.R.drawable.star_big_off).into(imageView);
        }
        else {
            Picasso.with(context).load(android.R.drawable.star_big_off).into(imageView);
        }
    }

}
