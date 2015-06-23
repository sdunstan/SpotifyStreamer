package com.stevedunstan.spotifystreamer.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // We just did a query for artist data, refresh the list fragment.
    }
}
