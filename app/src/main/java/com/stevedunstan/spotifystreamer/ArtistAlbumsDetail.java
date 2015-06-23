package com.stevedunstan.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist_albums_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ArrayList<SSSong> getSongList() {
        if (mSongs == null) {
            mSongs = new ArrayList<SSSong>();
        }
        return mSongs;
    }

}
