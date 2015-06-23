package com.stevedunstan.spotifystreamer.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.model.SSArtist;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SpotifyService extends IntentService {

    public static final String SEARCH_STRING = "search_string";

    public SpotifyService() {
        super("SpotifyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String searchString = intent.getStringExtra(SEARCH_STRING);
        Log.d(this.getClass().getName(), "Handling intent! Search is " + searchString);

        ArrayList<SSArtist> SSArtists = new ArrayList<SSArtist>();

        ObjectOutputStream oos = null;
        try {
            FileOutputStream artistsFileStream = openFileOutput(getString(R.string.ARTISTS_FILE), Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(artistsFileStream);
            oos.writeObject(SSArtists);
        } catch (IOException e) {
            Log.e(this.getClass().getName(), "Could not serialize artists.", e);
        }
        finally {
            try {
                oos.close();
            } catch (Throwable e) {
                Log.e(this.getClass().getName(), "Could not close artists file", e);
            }
        }

        Intent localIntent = new Intent(getString(R.string.SPOTIFY_SERVICE_BROADCAST_ACTION));
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
