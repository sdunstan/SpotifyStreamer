package com.stevedunstan.spotifystreamer.fragment;

import com.stevedunstan.spotifystreamer.model.SSSong;

public interface SongSelectionListener {

    void onSongSelected(SSSong song, int position);

}
