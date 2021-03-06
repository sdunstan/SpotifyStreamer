package com.stevedunstan.spotifystreamer.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.stevedunstan.spotifystreamer.model.SSSong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicPlayerService extends Service implements MusicPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String LOG_TAG = "MusicPlayerService";
    private static final short NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;
    private MusicPlayerState state = MusicPlayerState.DESTROYED;
    private int currentTrack = -1;
    private List<SSSong> songList;
    private final IBinder binder = new MusicServiceBinder();

    // **** Begin public interface ****
    @Override
    public synchronized void setPlaylist(List<SSSong> songList) {
        Log.i(LOG_TAG, "Setting song list  size is " + (songList != null ? songList.size() : -1));
        // TODO: memory management? Make a copy so the original can be GC'd?
        if (state != MusicPlayerState.READY) {
            destroyPlayer();
            initPlayer();
        }

        this.songList = new ArrayList<>();
        this.songList.addAll(songList);
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
    public synchronized SSSong setTrack(int track) {
        Log.i(LOG_TAG, "Setting current track to " + track);

        boolean continuePlaying = (state == MusicPlayerState.PLAYING || state == MusicPlayerState.PREPARING);
        SSSong nextSong = null;
        if (songList != null && songList.size() > 0) {
            if (track > (songList.size()-1) || track < 0) {
                throw new IllegalStateException("Can't go to track because the track number is not on this playlist.");
            }
            currentTrack = track;
            nextSong = songList.get(currentTrack);
            if (isPreparing()) {
                Log.i(LOG_TAG, "Preparing, resetting player.");
                destroyPlayer();
                initPlayer();
            }
            else {
                mediaPlayer.reset();
            }
            state = MusicPlayerState.READY;
            Log.i(LOG_TAG, "Track is in range: " + nextSong.name);
            if (continuePlaying) {
                play();
            }
            broadcastStateChange();
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
        Log.i(LOG_TAG, "Getting current song");
        SSSong currentSong = null;
        if (songList != null && currentTrack >= 0 && currentTrack < songList.size()) {
            currentSong = songList.get(currentTrack);
            Log.i(LOG_TAG, "Found current song: " + currentSong.name);
        }
        else {
            Log.i(LOG_TAG, String.format("Song list is null (%b) or current track out of range (%d). No current song.", songList == null, currentTrack));
        }
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

    @Override
    public boolean hasNext() {
        return songList != null && songList.size() > 1 && (currentTrack < (songList.size()-1));
    }

    @Override
    public boolean hasPrevious() {
        return songList != null && songList.size() > 1 && currentTrack > 0;
    }

    @Override
    public void poke() {
        broadcastStateChange();
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
        if (state == MusicPlayerState.DESTROYED) {
            initPlayer();
        }
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

    private void notifyPlay(String song) {
        Log.i(LOG_TAG, "Creating music player service in foreground.");
        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("Spotify Streamer")
                        .setContentText("Playing " + song)
                        .build();

        this.startForeground(NOTIFICATION_ID, notification);
    }

    private void releaseNotification() {
        stopForeground(true);
    }

    private void play() {
        state = MusicPlayerState.INTERMEDIATE;
        try {
            SSSong song = songList.get(currentTrack);
            mediaPlayer.setDataSource(song.url);
            mediaPlayer.prepareAsync();
            state = MusicPlayerState.PREPARING;
            notifyPlay(song.name);
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
        releaseNotification();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        state = MusicPlayerState.DESTROYED;
    }

    private void broadcastStateChange() {
        Intent intent = new Intent(BROADCAST_ACTION)
                .putExtra(EXTENDED_DATA_MUSIC_PLAYER_STATE, state.name());

        if (state == MusicPlayerState.PLAYING) {
            int duration = mediaPlayer.getDuration();
            duration = (duration <= 0) ? 30 : duration / 1000;

            int progress = mediaPlayer.getCurrentPosition();
            progress /= 1000;

            intent.putExtra(EXTENDED_DATA_SONG_DURATION, duration)
                    .putExtra(EXTENDED_DATA_SONG_PROGRESS, progress);

        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
        releaseNotification();
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
