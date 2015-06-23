package com.stevedunstan.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.stevedunstan.spotifystreamer.model.SSArtist;

import java.util.ArrayList;


public class SearchSpotifyActivity extends ActionBarActivity implements SearchHolder {

    public static final String SEARCH_STRING_KEY = "SearchSpotifyActivitySearchString";
    private static final String ARTIST_SEARCH_RESULTS_KEY = "SearchSpotifyActivyArtistList";

    private String mSearchString;

    // Using ArrayList implementation so that we don't have to cast to Serializable when
    // we persist to the Bundele.
    private ArrayList<SSArtist> mArtistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_STRING_KEY);
            mArtistList = (ArrayList<SSArtist>) savedInstanceState.getSerializable(ARTIST_SEARCH_RESULTS_KEY);
        }
        setContentView(R.layout.activity_search_spotify);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SEARCH_STRING_KEY, mSearchString);
        savedInstanceState.putSerializable(ARTIST_SEARCH_RESULTS_KEY, mArtistList);

        super.onSaveInstanceState(savedInstanceState);
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
}
