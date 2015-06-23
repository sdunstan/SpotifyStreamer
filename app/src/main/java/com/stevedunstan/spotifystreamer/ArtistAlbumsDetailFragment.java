package com.stevedunstan.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.stevedunstan.spotifystreamer.model.SSArtist;
import com.stevedunstan.spotifystreamer.model.SSSong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class ArtistAlbumsDetailFragment extends Fragment {

    private ArrayAdapter<SSSong> topTenAdapter;
    private LayoutInflater mInflater;

    public ArtistAlbumsDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_artist_albums_detail, container, false);
        SSArtist artist = getActivity().getIntent().getParcelableExtra("artist");
        ListView topTenListView = (ListView) view.findViewById(R.id.topSongsListView);
        topTenListView.setAdapter(getTopTenAdapter());

        QueryTopTenTask topTenTask = new QueryTopTenTask();
        topTenTask.execute(artist.id);
        return view;
    }

    private List<SSSong> getSongList() {
        return ((SongListHolder)getActivity()).getSongList();
    }

    private ArrayAdapter<SSSong> getTopTenAdapter() {
        if (topTenAdapter == null) {
            topTenAdapter = new ArrayAdapter<SSSong>(getActivity(), R.layout.song_list_item, R.id.songTextView, getSongList()) {

                // TODO: this is now boilerplate code. Figure out a way to reduce.
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder;
                    if (convertView == null) {
                        convertView = mInflater.inflate(R.layout.song_list_item, parent, false);

                        holder = new ViewHolder();
                        holder.nameTextView = (TextView) convertView.findViewById(R.id.songTextView);
                        holder.albumTextView = (TextView) convertView.findViewById(R.id.albumTextView);
                        holder.icon = (ImageView) convertView.findViewById(R.id.songImage);
                        convertView.setTag(holder);
                    }
                    else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    holder.nameTextView.setText(this.getItem(position).name);
                    holder.albumTextView.setText(this.getItem(position).albumName);
                    loadIcon(holder.icon, getItem(position).imageUrl);

                    return convertView;
                }

                // TODO: pull up
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
        return topTenAdapter;
    }

    /**
     * ViewHolder pattern from Google I/O 2009 presentation: https://www.youtube.com/watch?v=N6YdwzAvwOA
     */
    static class ViewHolder {
        TextView nameTextView;
        TextView albumTextView;
        ImageView icon;
    }

    private class QueryTopTenTask extends AsyncTask<String,Void,List<SSSong>> {

        @Override
        protected List<SSSong> doInBackground(String... artistIds) {
            ArrayList<SSSong> songs = new ArrayList<>();

            if (artistIds != null && artistIds.length > 0) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotifyService = api.getService();
                Map<String, Object> queryMap = new HashMap<String, Object>();
                queryMap.put("country", "US");
                Tracks tracks = spotifyService.getArtistTopTrack(artistIds[0], queryMap);

                for (Track track : tracks.tracks) {
                    SSSong.SSSongBuilder builder = new SSSong.SSSongBuilder();
                    SSSong song = builder.withAlbumName(track.album.name)
                            .withId(track.id)
                            .withName(track.name)
                            .withImageUrls(track.album.images)
                            .build();
                    songs.add(song);
                }
            }

            return songs;
        }

        @Override
        protected void onPostExecute(List<SSSong> songs) {
            getTopTenAdapter().clear();
            getTopTenAdapter().addAll(songs);
        }
    }

}