package com.stevedunstan.spotifystreamer;

import android.app.Fragment;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stevedunstan.spotifystreamer.util.NetworkUtil;

public abstract class SSFragment extends Fragment {
    private NetworkUtil networkUtil;

    private ProgressBar progressBar;

    public SSFragment() {
        networkUtil = new NetworkUtil();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    protected NetworkUtil getNetworkUtil() {
        return networkUtil;
    }

    protected void setProgressBarVisible(final boolean show) {
        if (progressBar == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE);
            }
        });
    }

    protected void loadIcon(ImageView imageView, String url) {
        if (url != null) {
            Picasso.with(getActivity()).load(url).placeholder(android.R.drawable.star_big_off).into(imageView);
        }
        else {
            Picasso.with(getActivity()).load(android.R.drawable.star_big_off).into(imageView);
        }
    }

    protected void showToastAtTop(final int stringResourceId) {
        Toast toast = Toast.makeText(getActivity(), stringResourceId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

}
