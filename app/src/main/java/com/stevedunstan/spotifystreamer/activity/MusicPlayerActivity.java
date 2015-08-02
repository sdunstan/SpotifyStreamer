package com.stevedunstan.spotifystreamer.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.fragment.SongHolder;
import com.stevedunstan.spotifystreamer.fragment.SongListHolder;
import com.stevedunstan.spotifystreamer.model.SSSong;
import com.stevedunstan.spotifystreamer.service.MusicPlayer;
import com.stevedunstan.spotifystreamer.service.MusicPlayerService;

import java.util.ArrayList;
import java.util.List;

public abstract class MusicPlayerActivity extends ActionBarActivity implements SongListHolder, SongHolder {

    private static final String LOG_KEY = MusicPlayerActivity.class.getName();
    public static final String SONG_KEY = "song";
    protected static final String SONG_LIST_KEY = "ArtistAlbumsDetail_SONG_LIST_KEY";
    protected MusicPlayer musicPlayer;
    private boolean bound = false;
    private ArrayList<SSSong> songList;
    private SSSong currentSong;

    private Toast navToast;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicPlayerService.MusicServiceBinder binder = (MusicPlayerService.MusicServiceBinder) iBinder;
            musicPlayer = binder.getService();
            bound = true;
            Log.i(LOG_KEY, "musicPlayer bound!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(LOG_KEY, "Removing musicPlayer");
            musicPlayer = null;
            bound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(SONG_LIST_KEY, songList);
        savedInstanceState.putParcelable(SONG_KEY, getSong());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            songList = savedInstanceState.getParcelableArrayList(SONG_LIST_KEY);
            currentSong = savedInstanceState.getParcelable(SONG_KEY);
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

    protected void showToast(int stringResource) {
        if (navToast != null) {
            navToast.cancel();
        }
        navToast = Toast.makeText(this, this.getResources().getString(stringResource), Toast.LENGTH_SHORT);
        navToast.show();
    }

    public void previousTrack(View view) {
        if (musicPlayer.hasPrevious()) {
            musicPlayer.previous();
            currentSong = musicPlayer.getCurrentSong();
        }
        else {
            showToast(R.string.previous_track_not_available);
        }
    }

    public void playPauseToggle(View view) {
        musicPlayer.togglePlayPause();
    }

    public void nextTrack(View view) {
        if (musicPlayer.hasNext()) {
            musicPlayer.next();
            currentSong = musicPlayer.getCurrentSong();
        }
        else {
            showToast(R.string.next_track_not_available);
        }
    }

    @Override
    public void updateSongList(final List<SSSong> songList) {
        getSongList().clear();
        getSongList().addAll(songList);
        if (musicPlayer != null) {
            musicPlayer.setPlaylist(songList);
            currentSong = musicPlayer.getCurrentSong();
        }
    }

    public SSSong getSong() {
        if (musicPlayer != null) {
            currentSong = musicPlayer.getCurrentSong();
        }
        return currentSong;
    }

}
