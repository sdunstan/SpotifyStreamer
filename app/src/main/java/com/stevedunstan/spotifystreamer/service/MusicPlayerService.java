package com.stevedunstan.spotifystreamer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.stevedunstan.spotifystreamer.model.SSSong;

import java.io.IOException;
import java.util.List;


public class MusicPlayerService extends Service implements MusicPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String LOG_TAG = "MusicPlayerService";

    private MediaPlayer mediaPlayer;
    private MusicPlayerState state = MusicPlayerState.DESTROYED;
    private int currentTrack = -1;
    private List<SSSong> songList;
    private final IBinder binder = new MusicServiceBinder();

    // **** Begin public interface ****
    @Override
    public synchronized void setPlaylist(List<SSSong> songList) {
        // TODO: memory management? Make a copy so the original can be GC'd?
        if (state != MusicPlayerState.READY) {
            destroyPlayer();
            initPlayer();
        }

        this.songList = songList;
        this.currentTrack = (this.songList != null && this.songList.size() > 0) ? 0 : -1;
    }

    @Override
    public void seek(int position) {

    }

    @Override
    public void togglePlayPause() {
        if (state == MusicPlayerState.PLAYING) {
            mediaPlayer.pause();
            state = MusicPlayerState.PAUSED;
            broadcastStateChange();
        }
        else if (state == MusicPlayerState.PAUSED) {
            mediaPlayer.start();
            state = MusicPlayerState.PLAYING;
            broadcastStateChange();
        }
        else if (state == MusicPlayerState.READY) {
            if (currentTrack < 0 || currentTrack >= songList.size())
                currentTrack = 0;
            play();
        }
    }

    @Override
    public SSSong setTrack(int track) {
        boolean continuePlaying = (state == MusicPlayerState.PLAYING);
        SSSong nextSong = null;
        if (songList != null && songList.size() > 0) {
            if (track >= (songList.size()-1) || track < 0) {
                throw new IllegalStateException("Can't go to track because the track number is not on this playlist.");
            }
            currentTrack = track;
            nextSong = songList.get(currentTrack);
            mediaPlayer.reset();
            state = MusicPlayerState.READY;
            if (continuePlaying) {
                play();
            }
            return nextSong;
        }
        else {
            throw new IllegalStateException("Can't go to track because there is no playlist set.");
        }
    }

    @Override
    public synchronized SSSong next() {
        return setTrack(currentTrack+1);
    }

    @Override
    public synchronized SSSong previous() {
        return setTrack(currentTrack-1);
    }

    @Override
    public synchronized SSSong getCurrentSong() {
        SSSong currentSong = null;
        if (songList != null && currentTrack > 0 && currentTrack < songList.size())
            currentSong = songList.get(currentTrack);
        return currentSong;
    }

    @Override
    public int getCurrentPosition() {
        int currentPosition = 0;
        try {
            currentPosition = mediaPlayer.getCurrentPosition();
        }
        catch(IllegalStateException e) {
        }
        return currentPosition;
    }

    @Override
    public boolean isPlaying() {
        return state == MusicPlayerState.PLAYING;
    }

    @Override
    public boolean isPreparing() {
        return state == MusicPlayerState.INTERMEDIATE || state == MusicPlayerState.PREPARING;
    }
    // **** End public interface ****

    // **** Service lifecycle ****
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Got start command...");
        initPlayer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Destroy...");
        destroyPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "Got bind command...");
        initPlayer();
        return binder;
    }

    private void play() {
        state = MusicPlayerState.INTERMEDIATE;
        try {
            mediaPlayer.setDataSource(songList.get(currentTrack).url);
            mediaPlayer.prepareAsync();
            state = MusicPlayerState.PREPARING;
            broadcastStateChange();
        } catch (IOException e) {
            state = MusicPlayerState.READY;
            // TODO: broadcast exception so UI can display a message?
            Log.e(LOG_TAG, "Could not load url for track " + currentTrack, e);
        }
        catch (IllegalStateException se) {
            state = MusicPlayerState.READY;
        }
    }

    private synchronized void initPlayer() {
        state = MusicPlayerState.READY;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    private synchronized void destroyPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        state = MusicPlayerState.DESTROYED;
    }

    private void broadcastStateChange() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(BROADCAST_ACTION).putExtra(EXTENDED_DATA_MUSIC_PLAYER_STATE, state.name()));
    }

    // **** Media Player Callbacks ***

    @Override
    public void onPrepared(MediaPlayer aMediaPlayer) {
        Log.v(LOG_TAG, "Prepared! now staring track");
        synchronized (this) {
            aMediaPlayer.start();
            state = MusicPlayerState.PLAYING;
            broadcastStateChange();
        }
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.v(LOG_TAG, "ON COMPLETION CALLBACK. Resetting media player");
        mediaPlayer.reset();
        state = MusicPlayerState.READY;
        broadcastStateChange();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    // **** Binder ****

    public class MusicServiceBinder extends Binder {
        public MusicPlayer getService() {
            return MusicPlayerService.this;
        }
    }
}
