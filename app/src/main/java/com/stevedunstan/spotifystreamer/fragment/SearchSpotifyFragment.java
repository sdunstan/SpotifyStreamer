package com.stevedunstan.spotifystreamer.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.model.SSArtist;
import com.stevedunstan.spotifystreamer.widget.SSArtistArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.RetrofitError;


public class SearchSpotifyFragment extends SSFragment {

    private ArrayAdapter<SSArtist> arrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View artistListFragment = inflater.inflate(R.layout.fragment_search_results, container, false);

        setProgressBar((ProgressBar)artistListFragment.findViewById(R.id.progressBar));
        getProgressBar().setVisibility(ProgressBar.INVISIBLE);

        ListView searchResultListView = (ListView) artistListFragment.findViewById(R.id.searchResultListView);
        searchResultListView.setAdapter(getArtistArrayAdapter());

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SSArtist artist = getArtistArrayAdapter().getItem(position);
                ((ArtistSelectionListener)getActivity()).onArtistSelected(artist);
            }
        });

        EditText searchText = (EditText) artistListFragment.findViewById(R.id.artistSearchQueryField);
        searchText.setText(getQuery());

        searchText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        boolean handled = false;
                        CharSequence searchValue = textView.getText();

                        if (actionId == EditorInfo.IME_ACTION_SEARCH && searchValue != null && searchValue.length() > 0) {
                            getArtistArrayAdapter().clear();
                            setQuery(searchValue.toString());
                            handled = true;
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                            QueryArtistsTask queryArtistsTask = new QueryArtistsTask();
                            queryArtistsTask.execute(getQuery());
                        }
                        return handled;
                    }
                }
        );

        return artistListFragment;
    }

    private String getQuery() {
        return ((SearchHolder)getActivity()).getSearchString();
    }

    private void setQuery(String query) {
        ((SearchHolder) getActivity()).setSearchString(query);
    }

    private List<SSArtist> getSearchResults() {
        return ((SearchHolder)getActivity()).getArtistList();
    }

    public ArrayAdapter<SSArtist> getArtistArrayAdapter() {
        if (arrayAdapter == null) {
            arrayAdapter = new SSArtistArrayAdapter(this, getActivity(), getSearchResults());
        }
        return arrayAdapter;
    }

    class QueryArtistsTask extends AsyncTask<String,Void,List<SSArtist>> {

        @Override
        protected List<SSArtist> doInBackground(String... queryStrings) {
            ArrayList<SSArtist> SSArtists = new ArrayList<SSArtist>();
            if (queryStrings != null && queryStrings.length > 0) {
                SpotifyApi api = new SpotifyApi();
                //            api.setAccessToken("");
                SpotifyService spotifyService = api.getService();
                try {
                    setProgressBarVisible(true);
                    ArtistsPager artistsPager = spotifyService.searchArtists(queryStrings[0]);
                    for (kaaes.spotify.webapi.android.models.Artist spotifyArtist : artistsPager.artists.items) {
                        SSArtist ssArtist = new SSArtist.Builder()
                                .withId(spotifyArtist.id)
                                .withName(spotifyArtist.name)
                                .withImageUrl(getArtistThumbnail(spotifyArtist.images))
                                .build();
                        SSArtists.add(ssArtist);
                    }
                }
                catch (RetrofitError exception) {
                    getNetworkUtil().showNoNetworkToast(getActivity());
                }
                finally {
                    setProgressBarVisible(false);
                }
            }

            return SSArtists;
        }

        private String getArtistThumbnail(List<Image> images) {
            String thumbnail = null;

            // Images are returned by the Spotify API in descending size order.
            if (images != null && images.size() > 0) {
                thumbnail = images.get(images.size()-1).url;
            }

            return thumbnail;
        }

        @Override
        protected void onPostExecute(List<SSArtist> artists) {
            getArtistArrayAdapter().clear();
            if (artists.size() == 0) {
                showToastAtTop(R.string.no_results_for_search);
            }
            getArtistArrayAdapter().addAll(artists);
        }


    }

}
