package com.stevedunstan.spotifystreamer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.stevedunstan.spotifystreamer.fragment.SongListHolder;
import com.stevedunstan.spotifystreamer.model.SSSong;
import com.stevedunstan.spotifystreamer.service.MusicPlayer;
import com.stevedunstan.spotifystreamer.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

public abstract class MusicPlayerActivity extends ActionBarActivity implements SongListHolder {

    public static final String SONG_KEY = "song";
    protected static final String SONG_LIST_KEY = "ArtistAlbumsDetail_SONG_LIST_KEY";
    protected MusicPlayer musicPlayer;
    private boolean bound = false;
    private ArrayList<SSSong> songList;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayerService.MusicServiceBinder binder = (MusicPlayerService.MusicServiceBinder) iBinder;
            musicPlayer = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicPlayer = null;
            bound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(SONG_LIST_KEY, songList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            songList = savedInstanceState.getParcelableArrayList(SONG_LIST_KEY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent musicServiceIntent = new Intent(this, MusicPlayerService.class);
        bindService(musicServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bound) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public List<SSSong> getSongList() {
        if (songList == null) {
            songList = new ArrayList<>();
        }
        return songList;
    }
    public void previousTrack(View view) {
        // TODO: guard against previous off of playlist
        musicPlayer.previous();
    }

    public void playPauseToggle(View view) {
        musicPlayer.togglePlayPause();
    }

    public void nextTrack(View view) {
        // TODO: guard against next off of playlist
        musicPlayer.next();
    }

    @Override
    public void updateSongList(final List<SSSong> songList) {
        getSongList().addAll(songList);
        if (musicPlayer != null) {
            musicPlayer.setPlaylist(songList);
        }
    }

}
