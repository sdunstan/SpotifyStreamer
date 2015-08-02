package com.stevedunstan.spotifystreamer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.fragment.NowPlayingFragment;

public class NowPlayingActivity extends MusicPlayerActivity {
    private NowPlayingFragment nowPlayingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_albums_detail);

        if (savedInstanceState == null) {
            nowPlayingFragment = new NowPlayingFragment();
            nowPlayingFragment.setShowsDialog(false);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .add(R.id.top_ten_songs_container, nowPlayingFragment)
                    .commit();
        }
    }

}
