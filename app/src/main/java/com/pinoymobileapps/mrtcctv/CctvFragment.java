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
    private static boolean IS_STREAMING = false;

    private int mCctvStatusVisibility;
    private int mProgressBarVisibility;
    private int mCameraId;
    private int mStationId;
    private boolean mIsChangingOrientation = false;
    private Bitmap mImage;

    @Bind(R.id.cctv_status)
    TextView mCctvStatus;

    @Bind(R.id.progressbar)
    View mProgressBar;

    @Bind(R.id.preview)
    ImageView mPreview;

    private class StreamingTask extends AsyncTask<Void, Void, ResponseDetails> {
        int cameraId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cameraId = mCameraId;
            IS_STREAMING = true;
        }

        @Override
        protected ResponseDetails doInBackground(Void... params) {
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
        protected void onPostExecute(ResponseDetails responseDetails) {
            super.onPostExecute(responseDetails);
            IS_STREAMING = false;

            if (responseDetails != null) {
                if (responseDetails.getImage() != null
                        && responseDetails.getStationId() == mStationId
                        && responseDetails.getCameraId() == mCameraId) {

                    mCctvStatus.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mImage = responseDetails.getImage();
                    mPreview.setImageBitmap(mImage);

                } else if (responseDetails.getImage() != null
                        && (responseDetails.getStationId() != mStationId
                        || responseDetails.getCameraId() != mCameraId)) {
                    mImage = null;
                    mPreview.setImageBitmap(mImage);

                } else {
                    mImage = null;
                    mPreview.setImageBitmap(mImage);
                    displayMessage(R.string.no_cctv);
                }
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
            if (mImage != null) {
                mPreview.setImageBitmap(mImage);
            }
            mCctvStatus.setVisibility(mCctvStatusVisibility);
            mProgressBar.setVisibility(mProgressBarVisibility);
        } else {
            clear();
        }

        if (!IS_STREAMING) {
            new StreamingTask().execute();
        }
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
        if (getActivity() != null) {
            String message = getResources().getString(stringResId);
            mCctvStatus.setText(message);
            mCctvStatus.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mPreview.setImageBitmap(null);
        }
    }

    static boolean isConnected(Context context) {
        if (context != null) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr != null) {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.isConnected();
                }
            }

        }

        return false;
    }

    public static ResponseDetails stream(int stationId, int cameraId) throws IOException {
        InputStream is = null;
        Bitmap bmp = null;
        int responseCode;

        try {
            URL url = new URL("http://api.pinoymobileapps.com/mrtcctv/?stationId=" + stationId + "&cameraId=" + cameraId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            responseCode = conn.getResponseCode();
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

        return new ResponseDetails(bmp, stationId, cameraId, responseCode);
    }


}
