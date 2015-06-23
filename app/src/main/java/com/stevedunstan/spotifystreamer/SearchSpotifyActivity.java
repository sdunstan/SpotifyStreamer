package com.stevedunstan.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
    protected void onDestroy() {
        super.onDestroy();
        Log.i("SearchSpogityActivity", "DESTROYING ACTIVITY");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SEARCH_STRING_KEY, mSearchString);
        savedInstanceState.putSerializable(ARTIST_SEARCH_RESULTS_KEY, mArtistList);

        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spotify_streamer, menu);
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

    public void mapMe(MenuItem item) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(Uri.parse("geo:0,0?q=85284"));
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
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
