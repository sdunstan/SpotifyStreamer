package com.stevedunstan.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * CDGB Created by steve on 6/8/15.
 */
public class SpotifyContentProvider extends ContentProvider {
    private final UriMatcher uriMatcher;

    public SpotifyContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.stevedunstan.spotifystreamer", "artists/*", 0);
    }

    @Override
    public boolean onCreate() {
        // TODO: create an instance variable of our db
        return true;
    }

    // content://com.stevedunstan.spotifystreamer/artists/{filter}
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        String type = "";

        switch(match) {
            case 0:

                break;
            default:
                break;
        }

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
