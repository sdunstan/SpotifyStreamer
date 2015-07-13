package com.stevedunstan.spotifystreamer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.model.SSSong;
import com.stevedunstan.spotifystreamer.service.MusicPlayer;
import com.stevedunstan.spotifystreamer.service.MusicPlayerState;
import com.stevedunstan.spotifystreamer.util.Images;

/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingFragment extends SSFragment {

    private static final String LOG_TAG = NowPlayingFragment.class.getName();
    private ImageButton playPauseButton;

    public NowPlayingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);

        setProgressBar((ProgressBar)view.findViewById(R.id.progressBar));

        SSSong song = ((SongHolder)getActivity()).getSong();

        TextView artistTextView = (TextView) view.findViewById(R.id.artistTextView);
        artistTextView.setText(song.artistName);

        TextView albumTextView = (TextView) view.findViewById(R.id.albumTextView);
        albumTextView.setText(song.albumName);

        TextView songTextView = (TextView) view.findViewById(R.id.songTitleTextView);
        songTextView.setText(song.name);

        playPauseButton = (ImageButton) view.findViewById(R.id.playPauseButton);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new MusicPlayerBroadcastRecieiver(),
                new IntentFilter(MusicPlayer.BROADCAST_ACTION));

        ImageView albumArtImageView = (ImageView) view.findViewById(R.id.albumArtImageView);
        Images.loadIcon(getActivity(), albumArtImageView, song.imageUrl);

        return view;
    }

    public class MusicPlayerBroadcastRecieiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stateChange = intent.getStringExtra(MusicPlayer.EXTENDED_DATA_MUSIC_PLAYER_STATE);
            if (stateChange == null)
                return;
            MusicPlayerState state = MusicPlayerState.valueOf(stateChange);
            switch (state) {
                case PLAYING:
                    Log.i(LOG_TAG, "Playing. hiding spinner and changing to pause button.");
                    setProgressBarVisible(false);
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                case PAUSED:
                    Log.i(LOG_TAG, "Paused. showing play button.");
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
                case PREPARING:
                    Log.i(LOG_TAG, "Preparing... showing spinner.");
                    setProgressBarVisible(true);
                    break;
                case READY:
                    Log.i(LOG_TAG, "Ready (song ended). showing play button and hiding spinner (just in case.)");
                    setProgressBarVisible(false);
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
                default:
            }
        }
    }
}
