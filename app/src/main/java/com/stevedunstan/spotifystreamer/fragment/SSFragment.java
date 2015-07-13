package com.stevedunstan.spotifystreamer.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stevedunstan.spotifystreamer.util.Network;

/**
 * Base class for Spotify Streamer fragments. Provides access to a progress bar, network utils, etc.
 */
public abstract class SSFragment extends Fragment {
    private Network networkUtil;

    private ProgressBar progressBar;

    public SSFragment() {
        networkUtil = new Network();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    protected Network getNetworkUtil() {
        return networkUtil;
    }

    protected void setProgressBarVisible(final boolean show) {
        if (progressBar == null)
            return;

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.GONE);
                }
            });
        }
    }

    protected void showToastAtTop(final int stringResourceId) {
        Toast toast = Toast.makeText(getActivity(), stringResourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

}
