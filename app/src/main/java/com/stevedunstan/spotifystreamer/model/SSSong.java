package com.stevedunstan.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Immutable Song domain object. Use SSSong.SSSongBuilder to build one.
 */
public final class SSSong implements Parcelable {
    public final String id;
    public final String imageUrl;
    public final String name;
    public final String albumName;

    private SSSong(String id, String imageUrl, String name, String albumName) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.albumName = albumName;
    }

    private SSSong(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        name = in.readString();
        albumName = in.readString();
    }

    public int describeContents() { return 0; }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(imageUrl);
        out.writeString(name);
        out.writeString(albumName);
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

        public SSSongBuilder withId(String anId) {
            id = anId;
            return this;
        }

        public SSSongBuilder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public SSSongBuilder withImageUrls(List<Image> images) {
            withImageUrl(ThumbExtractor.getLastImage(images));
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

        public SSSong build() {
            return new SSSong(id, imageUrl, name, albumName);
        }

    }
}
