package com.stevedunstan.spotifystreamer.fragment;

import com.stevedunstan.spotifystreamer.model.SSArtist;

import java.util.ArrayList;

/**
 * The SearchHolder interface allows a loose coupling between activity and fragment.
 */
public interface SearchHolder {
    public String getSearchString();
    public void setSearchString(String newValue);
    public ArrayList<SSArtist> getArtistList();
}
