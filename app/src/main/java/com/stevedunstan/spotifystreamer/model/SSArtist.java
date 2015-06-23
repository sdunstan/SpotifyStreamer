package com.stevedunstan.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Immutable value object to hold Artist info.
 */
public final class SSArtist implements Parcelable {

    // Adding a new field? Don't forget constructors (both of them) and the builder.
    public final String id;
    public final String name;
    public final String imageUrl;

    private SSArtist(String anId, String aName, String anImageUrl) {
        id = anId;
        name = aName;
        imageUrl = anImageUrl;
    }

    private SSArtist(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    public String toString() {
        return name;
    }

    public int describeContents() { return 0; }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(name);
        out.writeString(imageUrl);
    }

    public static final Parcelable.Creator<SSArtist> CREATOR
            = new Parcelable.Creator<SSArtist>() {
        public SSArtist createFromParcel(Parcel in) {
            return new SSArtist(in);
        }
        public SSArtist[] newArray(int size) {
            return new SSArtist[size];
        }
    };

    public static class Builder {
        String id;
        String name;
        String imageUrl;

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public SSArtist build() {
            return new SSArtist(id, name, imageUrl);
        }
    }
}

