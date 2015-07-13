package com.stevedunstan.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.stevedunstan.spotifystreamer.util.Images;

import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Immutable Song domain object. Use SSSong.SSSongBuilder to build one.
 */
public final class SSSong implements Parcelable {
    public final String id;
    public final String imageUrl;
    public final String name;
    public final String artistName;
    public final String albumName;
    public final String url;
    public final int topTenTrackNumber;

    private SSSong(String id, String imageUrl, String name, String albumName, String artistName, String url, int topTenTrackNumber) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.albumName = albumName;
        this.artistName = artistName;
        this.url = url;
        this.topTenTrackNumber = topTenTrackNumber;
    }

    private SSSong(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        albumName = in.readString();
        artistName = in.readString();
        url = in.readString();
        topTenTrackNumber = in.readInt();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(imageUrl);
        out.writeString(name);
        out.writeString(albumName);
        out.writeString(artistName);
        out.writeString(url);
        out.writeInt(topTenTrackNumber);
    }

    public static final Parcelable.Creator<SSSong> CREATOR = new Parcelable.Creator<SSSong>() {
        public SSSong createFromParcel(Parcel in) {
            return new SSSong(in);
        }
        public SSSong[] newArray(int size) {
            return new SSSong[size];
        }
    };

    public static class SSSongBuilder {
        private String id;
        private String imageUrl;
        private String name;
        private String albumName;
        private String artistName;
        private String url;
        private int trackNumber;

        public SSSongBuilder withId(String anId) {
            id = anId;
            return this;
        }

        public SSSongBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public SSSongBuilder withImageUrls(List<Image> images) {
            withImageUrl(Images.getLastImage(images));
            return this;
        }

        public SSSongBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SSSongBuilder withAlbumName(String albumName) {
            this.albumName = albumName;
            return this;
        }

        public SSSongBuilder withArtistName(String name) {
            this.artistName = name;
            return this;
        }

        public SSSongBuilder withArtistName(List<ArtistSimple> artists) {
            this.artistName = (artists.size() > 0 ? artists.get(0).name : "Unknown Artist");
            return this;
        }

        public SSSongBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public SSSongBuilder withTrackNumber(int trackNumber) {
            this.trackNumber = trackNumber;
            return this;
        }

        public SSSong build() {
            return new SSSong(id, imageUrl, name, albumName, artistName, url, trackNumber);
        }

    }
}
