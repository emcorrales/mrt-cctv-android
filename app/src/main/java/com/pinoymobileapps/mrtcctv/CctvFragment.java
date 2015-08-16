package com.pinoymobileapps.mrtcctv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CctvFragment extends Fragment {

    private static String TAG = CctvFragment.class.getSimpleName();

    private int mCctvStatusVisibility;
    private int mProgressBarVisibility;
    private int mCameraId;
    private int mStationId;
    private boolean mIsChangingOrientation = false;

    @Bind(R.id.cctv_status)
    TextView mCctvStatus;

    @Bind(R.id.progressbar)
    View mProgressBar;

    @Bind(R.id.preview)
    ImageView mPreview;

    private class StreamingTask extends AsyncTask<Void, Void, Bitmap> {
        int cameraId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cameraId = mCameraId;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (isConnected(getActivity())) {
                try {
                    return stream(mStationId, mCameraId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                displayMessage(R.string.no_connection);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                mCctvStatus.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mPreview.setImageBitmap(bitmap);
            } else {
                displayMessage(R.string.no_cctv);
            }

            if (isResumed()) {
                new StreamingTask().execute();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cctv, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mIsChangingOrientation) {
            mCctvStatus.setVisibility(mCctvStatusVisibility);
            mProgressBar.setVisibility(mProgressBarVisibility);

        } else {
            clear();
        }
        new StreamingTask().execute();
    }

    @Override
    public void onPause() {
        saveCustomState();
        mIsChangingOrientation = false;
        super.onPause();
    }

    private void saveCustomState() {
        if (mCctvStatus != null) {
            mCctvStatusVisibility = mCctvStatus.getVisibility();
        }

        if (mProgressBar != null) {
            mProgressBarVisibility = mProgressBar.getVisibility();
        }
    }

    @Override
    public void onDetach() {
        mIsChangingOrientation = true;
        super.onDetach();
    }

    public void setCamera(int stationId, int cameraId) {
        if (mStationId != stationId || mCameraId != cameraId) {
            mStationId = stationId;
            mCameraId = cameraId;
            clear();
        }
    }

    private void clear() {
        displayProgressBar();

        if (mPreview != null) {
            mPreview.setImageBitmap(null);
        }
    }

    private void displayProgressBar() {
        if (mCctvStatus != null) {
            mCctvStatus.setVisibility(View.INVISIBLE);
        }

        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void displayMessage(int stringResId) {
        String message = getResources().getString(stringResId);
        mCctvStatus.setText(message);
        mCctvStatus.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mPreview.setImageBitmap(null);
    }

    static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static Bitmap stream(int stationId, int cameraId) throws IOException {
        InputStream is = null;
        Bitmap bmp = null;

        try {
            URL url = new URL("http://api.pinoymobileapps.com/mrtcctv/?stationId=" + stationId + "&cameraId=" + cameraId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
                bmp = BitmapFactory.decodeStream(bufferedInputStream);
            }

        } finally {
            if (is != null) {
                is.close();
            }
        }

        return bmp;
    }


}
