package com.stevedunstan.spotifystreamer.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.stevedunstan.spotifystreamer.R;
import com.stevedunstan.spotifystreamer.model.SSArtist;
import com.stevedunstan.spotifystreamer.model.SSSong;
import com.stevedunstan.spotifystreamer.widget.SSSongArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

public class SongListFragment extends SSFragment {

    public static String ARTIST_KEY = "artist";

    private ArrayAdapter<SSSong> topTenAdapter;

    public SongListFragment() {
    }

    public static SongListFragment newInstance(SSArtist artist) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARTIST_KEY, artist);
        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_albums_detail, container, false);

        Bundle arguments = getArguments();
        SSArtist artist = null;
        if (arguments != null) {
            artist = arguments.getParcelable(ARTIST_KEY);
        }
        ListView topTenListView = (ListView) view.findViewById(R.id.topSongsListView);
        topTenListView.setAdapter(getTopTenAdapter());
        topTenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SSSong song = getSongList().get(i);
                ((SongSelectionListener)getActivity()).onSongSelected(song, i);
            }
        });

        setProgressBar((ProgressBar) view.findViewById(R.id.progressBar));

        if (artist != null && getNetworkUtil().isNetworkAvailable(getActivity())) {
            QueryTopTenTask topTenTask = new QueryTopTenTask();
            topTenTask.execute(artist.id);
        }
        else {
            getNetworkUtil().showNoNetworkToast(getActivity());
        }
        return view;
    }

    private List<SSSong> getSongList() {
        return ((SongListHolder)getActivity()).getSongList();
    }

    private void updateAll(List<SSSong> songs) {
        getTopTenAdapter().addAll(songs);
        ((SongListHolder)getActivity()).updateSongList(songs);
    }

    private ArrayAdapter<SSSong> getTopTenAdapter() {
        if (topTenAdapter == null) {
            topTenAdapter = new SSSongArrayAdapter(getActivity(), getSongList());
        }
        return topTenAdapter;
    }



    private class QueryTopTenTask extends AsyncTask<String,Void,List<SSSong>> {

        @Override
        protected List<SSSong> doInBackground(String... artistIds) {

            ArrayList<SSSong> songs = new ArrayList<>();

            if (artistIds != null && artistIds.length > 0) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("country", "US");

                try {
                    setProgressBarVisible(true);
                    Tracks tracks = spotifyService.getArtistTopTrack(artistIds[0], queryMap);

                    int trackNumber = 0;
                    for (Track track : tracks.tracks) {
                        SSSong.SSSongBuilder builder = new SSSong.SSSongBuilder();
                        SSSong song = builder.withAlbumName(track.album.name)
                                .withId(track.id)
                                .withName(track.name)
                                .withArtistName(track.artists)
                                .withAlbumName(track.album.name)
                                .withImageUrls(track.album.images)
                                .withUrl(track.preview_url)
                                .withTrackNumber(trackNumber)
                                .build();
                        songs.add(song);
                        trackNumber++;
                    }
                }
                catch (RetrofitError exception) {
                    getNetworkUtil().showNoNetworkToast(getActivity());
                }
                finally {
                    setProgressBarVisible(false);
                }
            }

            return songs;
        }

        @Override
        protected void onPostExecute(List<SSSong> songs) {
            getTopTenAdapter().clear();
            if (songs.size() > 0) {
                updateAll(songs);
            }
            else {
                showToastAtTop(R.string.no_results_for_artist);
            }
        }
    }

}
