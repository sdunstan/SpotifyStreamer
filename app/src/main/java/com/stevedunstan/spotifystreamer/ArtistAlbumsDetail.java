package com.stevedunstan.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.stevedunstan.spotifystreamer.model.SSArtist;
import com.stevedunstan.spotifystreamer.model.SSSong;

import java.util.ArrayList;


public class ArtistAlbumsDetail extends ActionBarActivity implements SongListHolder {

    private static final String SONG_LIST_KEY = "ArtistAlbumsDetail_SONG_LIST_KEY";

    private ArrayList<SSSong> mSongs;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(SONG_LIST_KEY, mSongs);
        super.onSaveInstanceState(savedInstanceState);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSongs = savedInstanceState.getParcelableArrayList(SONG_LIST_KEY);
        }
        setContentView(R.layout.activity_artist_albums_detail);
        SSArtist artist = getIntent().getParcelableExtra("artist");
        if (artist != null && artist.name != null && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(artist.name);
        }
    }

    @Override
    public ArrayList<SSSong> getSongList() {
        if (mSongs == null) {
            mSongs = new ArrayList<SSSong>();
        }
        return mSongs;
    }

}
