package com.stevedunstan.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.fragment.SongListFragment;
import com.stevedunstan.spotifystreamer.fragment.SongSelectionListener;
import com.stevedunstan.spotifystreamer.model.SSArtist;
import com.stevedunstan.spotifystreamer.model.SSSong;


public class SongListActivity extends MusicPlayerActivity implements SongSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_albums_detail);
        SSArtist artist = getIntent().getParcelableExtra(SongListFragment.ARTIST_KEY);
        if (artist != null) {
            getSupportActionBar().setSubtitle(artist.name);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.top_ten_songs_container, SongListFragment.newInstance(artist))
                    .commit();
        }
    }

    @Override
    public void onSongSelected(SSSong song, int position) {
        musicPlayer.setTrack(position);
        Intent intent = new Intent(this, NowPlayingActivity.class);
        intent.putExtra(SONG_KEY, musicPlayer.getCurrentSong());
        startActivity(intent);
    }
}
