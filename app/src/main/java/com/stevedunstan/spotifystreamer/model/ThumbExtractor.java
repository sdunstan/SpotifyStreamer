package com.stevedunstan.spotifystreamer.model;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class ThumbExtractor {

    public static String getLastImage(List<Image> images) {
        String thumbnail = null;

        // Images are returned by the Spotify API in descending size order.
        if (images != null && images.size() > 0) {
            thumbnail = images.get(images.size()-1).url;
        }

        return thumbnail;
    }

}
