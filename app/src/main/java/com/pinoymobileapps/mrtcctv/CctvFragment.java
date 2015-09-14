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

import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CctvFragment extends Fragment {

    @Bind(R.id.cctv_status)
    TextView mCctvStatus;

    @Bind(R.id.progressbar)
    View mProgressBar;

    @Bind(R.id.image_view)
    ImageView mImageView;

    private int mCctvStatusVisibility;
    private int mProgressBarVisibility;
    private int mCameraId;
    private int mStationId;
    private boolean mIsChangingOrientation = false;
    private Bitmap mImage;

    private ApiService mApiService;
    private Call<ResponseBody> mCaller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        String baseUrl = getResources().getString(R.string.api_base_url);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).build();
        mApiService = retrofit.create(ApiService.class);
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
                showImage(mImage);
            } else {
                mCctvStatus.setVisibility(mCctvStatusVisibility);
                mProgressBar.setVisibility(mProgressBarVisibility);
            }
        } else {
            showLoading();
        }
        stream(mStationId, mCameraId);
    }

    private void stream(final int stationId, final int cameraId) {
        if (isResumed()) {
            //If has internet connection.
            if (isConnected(getActivity())) {
                mCaller = mApiService.stream(stationId, cameraId);
                mCaller.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Response<ResponseBody> response) {
                        if (response.code() == 200 && response.body() != null) {
                            try {
                                mImage = BitmapFactory.decodeStream(response.body().byteStream());
                                //If requested cctv hasn't changed while waiting for a response.
                                if (stationId == mStationId && cameraId == mCameraId) {
                                    if (mImage != null) {
                                        showImage(mImage);
                                    } else {//if response doesn't contain an image.
                                        clearImage();
                                        displayMessage(R.string.cctv_not_available);
                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.code() == 408) {//If connection timeout.
                            clearImage();
                            displayMessage(R.string.connection_timeout);
                        } else {//If request failed.
                            clearImage();
                            displayMessage(R.string.cctv_not_available);
                        }

                        stream(mStationId, mCameraId);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (t instanceof SocketTimeoutException) {
                            clearImage();
                            displayMessage(R.string.connection_timeout);
                        }
                        stream(mStationId, mCameraId);
                    }
                });

            } else {//If no internet connection.
                displayMessage(R.string.connection_not_available);
                waitForAvailableNetwork();
            }
        }
    }

    private boolean isConnected(Context context) {
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

    private void waitForAvailableNetwork() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (!isConnected(getActivity())) {
                    if (mCaller != null) {
                        mCaller.cancel();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                stream(mStationId, mCameraId);
            }
        }.execute();
    }

    @Override
    public void onPause() {
        saveState();
        mIsChangingOrientation = false;
        super.onPause();
    }

    private void saveState() {
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

    private void showImage(Bitmap image) {
        if (image == null) {
            throw new IllegalArgumentException("image cannot be null.");
        } else {
            mCctvStatus.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mImage = image;
            mImageView.setImageBitmap(image);
        }
    }

    public void setCamera(int stationId, int cameraId) {
        if (mStationId != stationId || mCameraId != cameraId) {
            if (mCaller != null) {
                mCaller.cancel();
            }
            mStationId = stationId;
            mCameraId = cameraId;
            showLoading();
        }
    }

    private void showLoading() {
        clearImage();
        displayProgressBar();
    }

    private void clearImage() {
        mImage = null;
        if (mImageView != null) {
            mImageView.setImageBitmap(null);
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
            mImageView.setImageBitmap(null);
        }
    }
}
