package com.stevedunstan.spotifystreamer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.stevedunstan.spotifystreamer.R;

public class Network {

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void showNoNetworkToast(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
    }

}
