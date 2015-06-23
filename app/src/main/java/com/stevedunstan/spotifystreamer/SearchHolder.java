package com.stevedunstan.spotifystreamer;

import com.stevedunstan.spotifystreamer.model.SSArtist;

import java.util.ArrayList;

/**
 * CDGB Created by steve on 6/22/15.
 */
public interface SearchHolder {
    public String getSearchString();
    public void setSearchString(String newValue);
    public ArrayList<SSArtist> getArtistList();
}
