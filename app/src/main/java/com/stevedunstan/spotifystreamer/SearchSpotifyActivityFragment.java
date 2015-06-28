package com.stevedunstan.spotifystreamer;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevedunstan.spotifystreamer.model.SSArtist;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchSpotifyActivityFragment extends SSFragment {

    private ArrayAdapter<SSArtist> arrayAdapter;
    private LayoutInflater mInflater;

    public SearchSpotifyActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View artistListFragment = inflater.inflate(R.layout.fragment_search_results, container, false);

        setProgressBar((ProgressBar)artistListFragment.findViewById(R.id.progressBar));
        getProgressBar().setVisibility(ProgressBar.INVISIBLE);

        ListView searchResultListView = (ListView) artistListFragment.findViewById(R.id.searchResultListView);
        searchResultListView.setAdapter(getArtistArrayAdapter());

        searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SSArtist artist = getArtistArrayAdapter().getItem(position);
                Intent startArtistAlbumIntent = new Intent(getActivity(), ArtistAlbumsDetail.class);
                startArtistAlbumIntent.putExtra("artist", artist);
                startActivity(startArtistAlbumIntent);
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
            arrayAdapter = new ArrayAdapter<SSArtist>(getActivity(), R.layout.search_list_item, R.id.artistName, getSearchResults()) {


                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder;
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.search_list_item, parent, false);

                        holder = new ViewHolder();
                        holder.text = (TextView) convertView.findViewById(R.id.artistName);
                        holder.icon = (ImageView) convertView.findViewById(R.id.artistImage);
                        convertView.setTag(holder);
                    }
                    else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    holder.text.setText(this.getItem(position).name);
                    loadIcon(holder.icon, getItem(position).imageUrl);

                    return convertView;
                }
                public void loadIcon(ImageView imageView, String url) {
                    if (url != null) {
                        Picasso.with(getActivity()).load(url).placeholder(android.R.drawable.star_big_off).into(imageView);
                    }
                    else {
                        Picasso.with(getActivity()).load(android.R.drawable.star_big_off).into(imageView);
                    }
                }
            };
        }
        return arrayAdapter;
    }

    /**
     * ViewHolder pattern from Google I/O 2009 presentation: https://www.youtube.com/watch?v=N6YdwzAvwOA
      */
    static class ViewHolder {
        TextView text;
        ImageView icon;
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
