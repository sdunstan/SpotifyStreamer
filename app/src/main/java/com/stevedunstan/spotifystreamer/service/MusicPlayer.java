package com.stevedunstan.spotifystreamer.service;

import com.stevedunstan.spotifystreamer.model.SSSong;

import java.util.List;

public interface MusicPlayer {
    public static final String BROADCAST_ACTION = "com.stevedunstan.spotifystreamer.service.MusicPlayerBroadcast";
    // Handle: PLAYING, READY, PAUSED, PREPARING
    public static final String EXTENDED_DATA_MUSIC_PLAYER_STATE = "com.stevedunstan.spotifystreamer.service.MusicPlayerBroadcastState";
    String EXTENDED_DATA_SONG_PROGRESS = "SONG-PROGRESS";
    String EXTENDED_DATA_SONG_DURATION = "SONG-DURATION";

    void setPlaylist(List<SSSong> songList);
    void togglePlayPause();


    SSSong setTrack(int track); // sets track and plays if playing (stopping the previously playing track)
    SSSong next(); // convenience for going to next track, plays if playing
    SSSong previous(); // convenience for going to previous track, plays if playing

    SSSong getCurrentSong();
    int getCurrentPosition();
    boolean isPlaying();
    boolean isPreparing();
    boolean hasNext();
    boolean hasPrevious();

    void seek(int position);
}
