package com.stevedunstan.spotifystreamer.fragment;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.activity.MusicPlayerActivity;
import com.stevedunstan.spotifystreamer.model.SSSong;
import com.stevedunstan.spotifystreamer.service.MusicPlayer;
import com.stevedunstan.spotifystreamer.service.MusicPlayerState;
import com.stevedunstan.spotifystreamer.util.Images;

public class NowPlayingFragment extends DialogFragment {

    private static final String LOG_TAG = NowPlayingFragment.class.getName();
    private ProgressBar progressBar;

    private ImageButton playPauseButton;

    private TextView artistTextView;
    private TextView albumTextView;
    private TextView songTextView;
    private ImageView albumArtImageView;
    private TextView currentPositionTextView;
    private TextView songLengthTextView;
    private SeekBar nowPlayingSeekBar;
    private ValueAnimator seekBarAnimator;

    private MusicPlayerBroadcastReceiver broadcastReceiver;

    public NowPlayingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        broadcastReceiver = new MusicPlayerBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new MusicPlayerBroadcastReceiver(),
                new IntentFilter(MusicPlayer.BROADCAST_ACTION));

        playPauseButton = (ImageButton) view.findViewById(R.id.playPauseButton);
        ImageButton nextButton = (ImageButton) view.findViewById(R.id.nextButton);
        ImageButton previousButton = (ImageButton) view.findViewById(R.id.previousButton);

        artistTextView = (TextView) view.findViewById(R.id.artistTextView);
        albumTextView = (TextView) view.findViewById(R.id.albumTextView);
        songTextView = (TextView) view.findViewById(R.id.songTitleTextView);
        albumArtImageView = (ImageView) view.findViewById(R.id.albumArtImageView);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MusicPlayerActivity)getActivity()).playPauseToggle(view);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MusicPlayerActivity)getActivity()).nextTrack(view);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MusicPlayerActivity)getActivity()).previousTrack(view);
            }
        });

        currentPositionTextView = (TextView) view.findViewById(R.id.currentPositionTextView);
        currentPositionTextView.setFocusable(false);
        songLengthTextView = (TextView) view.findViewById(R.id.songDurationTextView);
        songLengthTextView.setFocusable(false);
        nowPlayingSeekBar = (SeekBar) view.findViewById(R.id.nowPlayingSeekBar);
        nowPlayingSeekBar.setFocusable(false);
        nowPlayingSeekBar.setFocusableInTouchMode(false);

        // SongHolder gets the song from the music player service (the authoritative source).
        // There is a race condition for attaching to the service though so we may have to fall
        // back to the song passed in on the intent.
        SSSong song = ((SongHolder)getActivity()).getSong();
        if (song == null) {
            Log.w(LOG_TAG, "NO SONG IS AVAILABLE YET TO UPDATE THE NOW PLAYING FRAGMENT! Using intent data instead.");
            song = getActivity().getIntent().getParcelableExtra(MusicPlayerActivity.SONG_KEY);
        }
        updateViewsWithSong(song);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        // Unregister broadcast receiver before the fragment is detached from the activity.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroyView();
    }

    private void updateViewsWithSong(SSSong song) {
        if (song == null) return;
        artistTextView.setText(song.artistName);
        albumTextView.setText(song.albumName);
        songTextView.setText(song.name);
        Images.loadIcon(getActivity(), albumArtImageView, song.imageUrl);
    }

    private void resetSeekBar() {
        currentPositionTextView.setText(R.string.seekBarCurrentPosition);
        songLengthTextView.setText(R.string.songDurationTextView);
        nowPlayingSeekBar.setProgress(0);

        if (seekBarAnimator != null) {
            seekBarAnimator.cancel();
            seekBarAnimator = null;
        }
    }

    private void updateSeekBar(int progress, int total) {
        nowPlayingSeekBar.setMax(total);
        nowPlayingSeekBar.setProgress(progress);
        currentPositionTextView.setText(String.format("0:%02d", progress));
        songLengthTextView.setText(String.format("0:%02d", total));

        if (seekBarAnimator != null) {
            seekBarAnimator.cancel();
        }
        seekBarAnimator = ValueAnimator.ofInt(progress, total);
        seekBarAnimator.setInterpolator(new LinearInterpolator());
        seekBarAnimator.setDuration((total - progress) * 1000);

        seekBarAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int progress = (Integer) valueAnimator.getAnimatedValue();
                        nowPlayingSeekBar.setProgress(progress);
                        currentPositionTextView.setText(String.format("0:%02d", progress));
                    }
                }
        );

        seekBarAnimator.start();
    }

    protected void setProgressBarVisible(final boolean show) {
        if (progressBar == null)
            return;

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.GONE);
                }
            });
        }
    }

    public class MusicPlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SSSong song = null;
            if (getActivity() != null) {
                song = ((SongHolder)getActivity()).getSong();
            }
            else {
                return;
            }
            String stateChange = intent.getStringExtra(MusicPlayer.EXTENDED_DATA_MUSIC_PLAYER_STATE);
            if (stateChange == null)
                return;
            MusicPlayerState state = MusicPlayerState.valueOf(stateChange);
            if (song == null)
                return;
            switch (state) {
                case PLAYING:
                    Log.i(LOG_TAG, "Playing. hiding spinner and changing to pause button.");
                    setProgressBarVisible(false);
                    Integer progress = intent.getIntExtra(MusicPlayer.EXTENDED_DATA_SONG_PROGRESS, -1);
                    Integer duration = intent.getIntExtra(MusicPlayer.EXTENDED_DATA_SONG_DURATION, 30);
                    if (progress >= 0) {
                        updateSeekBar(progress, duration);
                    }
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case PAUSED:
                    Log.i(LOG_TAG, "Paused. showing play button.");
                    if (seekBarAnimator != null) {
                        seekBarAnimator.cancel();
                        seekBarAnimator = null;
                    }
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
                case PREPARING:
                    Log.i(LOG_TAG, "Preparing... showing spinner.");
                    updateViewsWithSong(song);
                    resetSeekBar();
                    setProgressBarVisible(true);
                    break;
                case READY:
                    Log.i(LOG_TAG, "Ready (song ended or track changed while READY). showing play button and hiding spinner (just in case.)");
                    setProgressBarVisible(false);
                    updateViewsWithSong(song);
                    resetSeekBar();
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
                default:
                    Log.i(LOG_TAG, "Got unhandled state. Redraw screen. " + state.name());
                    updateViewsWithSong(song);
                    break;
            }
        }
    }
}
