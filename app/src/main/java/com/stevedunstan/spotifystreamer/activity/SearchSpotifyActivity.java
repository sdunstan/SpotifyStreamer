package com.stevedunstan.spotifystreamer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.fragment.ArtistSelectionListener;
import com.stevedunstan.spotifystreamer.fragment.SearchHolder;
import com.stevedunstan.spotifystreamer.fragment.SongListFragment;
import com.stevedunstan.spotifystreamer.model.SSArtist;
import com.stevedunstan.spotifystreamer.model.SSSong;

import java.util.ArrayList;


public class SearchSpotifyActivity extends MusicPlayerActivity implements SearchHolder, ArtistSelectionListener {

    public static final String SEARCH_STRING_KEY = "SearchSpotifyActivitySearchString";
    private static final String ARTIST_SEARCH_RESULTS_KEY = "SearchSpotifyActivyArtistList";
    private static final String SONG_LIST_FRAGMENT_TAG = "SongListFragment";

    private String mSearchString;
    private boolean twoPane;

    // Using ArrayList implementation so that we don't have to cast to Serializable when
    // we persist to the Bundele.
    private ArrayList<SSArtist> mArtistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_STRING_KEY);
            mArtistList = savedInstanceState.getParcelableArrayList(ARTIST_SEARCH_RESULTS_KEY);
        }

        setContentView(R.layout.activity_search_spotify);
        if (findViewById(R.id.top_ten_songs_container) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.top_ten_songs_container, new SongListFragment(), SONG_LIST_FRAGMENT_TAG)
                        .commit();
            }
        }
        else {
            twoPane = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(SEARCH_STRING_KEY, mSearchString);
        savedInstanceState.putParcelableArrayList(ARTIST_SEARCH_RESULTS_KEY, mArtistList);
    }

    @Override
    public String getSearchString() {
        return mSearchString;
    }

    @Override
    public void setSearchString(String newValue) {
        mSearchString = newValue;
    }

    @Override
    public ArrayList<SSArtist> getArtistList() {
        if (mArtistList == null) {
            mArtistList = new ArrayList<SSArtist>();
        }
        return mArtistList;
    }

    private ArrayList<SSSong> songList;

    @Override
    public void onArtistSelected(SSArtist artist) {
        if (twoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_ten_songs_container, SongListFragment.newInstance(artist), SONG_LIST_FRAGMENT_TAG)
                    .commit();

        }
        else {
            Intent startArtistDetailIntent = new Intent(this, SongListActivity.class);
            startArtistDetailIntent.putExtra(SongListFragment.ARTIST_KEY, artist);
            startActivity(startArtistDetailIntent);
        }
    }
}
