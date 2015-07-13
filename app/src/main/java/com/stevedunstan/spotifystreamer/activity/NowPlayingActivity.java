package com.stevedunstan.spotifystreamer.activity;

import android.os.Bundle;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.fragment.SongHolder;
import com.stevedunstan.spotifystreamer.model.SSSong;

public class NowPlayingActivity extends MusicPlayerActivity implements SongHolder{

    private static final String SONG_KEY = "NowPlayingActivity_SONG_KEY";

    private SSSong song;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(SONG_KEY, song);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            song = savedInstanceState.getParcelable(SONG_KEY);
        }
        else {
            song = getIntent().getParcelableExtra(MusicPlayerActivity.SONG_KEY);
        }
        setContentView(R.layout.activity_now_playing);
    }


    @Override
    public SSSong getSong() {
        return song;
    }

}
