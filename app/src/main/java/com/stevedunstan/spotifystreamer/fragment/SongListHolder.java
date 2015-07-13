package com.stevedunstan.spotifystreamer.fragment;

import com.stevedunstan.spotifystreamer.model.SSSong;

import java.util.List;

/**
 * The SongListHolder interface provides a loose coupling between fragments providing a song list
 * and activities that contain the fragment.
 */
public interface SongListHolder {
    List<SSSong> getSongList();
    void updateSongList(final List<SSSong> songList);
}
