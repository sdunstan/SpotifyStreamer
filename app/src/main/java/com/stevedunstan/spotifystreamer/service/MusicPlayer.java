package com.stevedunstan.spotifystreamer.service;

import com.stevedunstan.spotifystreamer.model.SSSong;

import java.util.List;

public interface MusicPlayer {
    public static final String BROADCAST_ACTION = "com.stevedunstan.spotifystreamer.service.MusicPlayerBroadcast";
    // Handle: PLAYING, READY, PAUSED, PREPARING
    public static final String EXTENDED_DATA_MUSIC_PLAYER_STATE = "com.stevedunstan.spotifystreamer.service.MusicPlayerBroadcastState";

    void setPlaylist(List<SSSong> songList);
    void togglePlayPause();


    SSSong setTrack(int track); // sets track and plays if playing (stopping the previously playing track)
    SSSong next(); // convenience for going to next track, plays if playing
    SSSong previous(); // convenience for going to previous track, plays if playing

    SSSong getCurrentSong();
    int getCurrentPosition();
    boolean isPlaying();
    boolean isPreparing();

    void seek(int position);
}
