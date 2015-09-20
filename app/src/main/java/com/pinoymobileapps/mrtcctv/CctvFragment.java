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
import java.net.UnknownHostException;

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
    private boolean mIsUsingNewApi = true;

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
            restoreViewStates();
            if (mCaller == null) {
                stream(mStationId, mCameraId);
            }
        } else {
            cancelRequest();
            clearImage();
            hideMessage();
            showProgressBar();
            stream(mStationId, mCameraId);
        }
    }

    public void restoreViewStates() {
        if (mImage != null) {
            showImage(mImage);
        }
        if (mCctvStatus != null) {
            mCctvStatus.setVisibility(mCctvStatusVisibility);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(mProgressBarVisibility);
        }
    }

    private void cancelRequest() {
        if (mCaller != null) {
            mCaller.cancel();
            mCaller = null;
        }
    }

    private void stream(final int stationId, final int cameraId) {
        if (isResumed()) {
            if (mIsUsingNewApi) {
                mCaller = mApiService.streamV2(stationId, cameraId);
            } else {
                mCaller = mApiService.streamV1(stationId, cameraId);
            }
            mCaller.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Response<ResponseBody> response) {
                    //If requested cctv hasn't changed while waiting for a response.
                    if (stationId == mStationId && cameraId == mCameraId) {
                        if (response.code() == 200 && response.body() != null) {
                            try {
                                mImage = BitmapFactory.decodeStream(response.body().byteStream());
                                if (mImage != null) {
                                    showImage(mImage);
                                } else {//if response doesn't contain an image.
                                    handleCctvNotAvailable();
                                }
                            } catch (IOException e) {
                                handleCctvNotAvailable();
                            }
                        } else {//If request failed.
                            handleCctvNotAvailable();
                        }
                    }
                    stream(mStationId, mCameraId);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t instanceof SocketTimeoutException) {
                        handleRequestTimeout();
                    } else if (t instanceof UnknownHostException) {
                        handleNetworkNotAvailable();
                    }
                }
            });
        } else {
            cancelRequest();
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
                while (!isConnected(getActivity()) && isResumed()) {
                    cancelRequest();
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
        saveViewStates();
        mIsChangingOrientation = false;
        super.onPause();
    }

    private void saveViewStates() {
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
            hideMessage();
            hideProgressBar();
            mImage = image;
            mImageView.setImageBitmap(image);
        }
    }

    private void handleRequestTimeout() {
        mIsUsingNewApi = !mIsUsingNewApi;
        clearImage();
        showMessage(R.string.connection_timeout);
        stream(mStationId, mCameraId);
    }

    private void handleCctvNotAvailable() {
        mIsUsingNewApi = !mIsUsingNewApi;
        clearImage();
        showMessage(R.string.cctv_not_available);
    }

    private void handleNetworkNotAvailable() {
        clearImage();
        hideProgressBar();
        showMessage(R.string.connection_not_available);
        waitForAvailableNetwork();
    }

    private void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void showMessage(int stringResId) {
        if (getActivity() != null) {
            String message = getResources().getString(stringResId);
            mCctvStatus.setText(message);
            mCctvStatus.setVisibility(View.VISIBLE);
        }
    }

    private void hideMessage() {
        if (mCctvStatus != null) {
            mCctvStatus.setVisibility(View.GONE);
        }
    }

    private void clearImage() {
        mImage = null;
        if (mImageView != null) {
            mImageView.setImageBitmap(null);
        }
    }

    public void setCamera(int stationId, int cameraId) {
        if (mStationId != stationId || mCameraId != cameraId) {
            cancelRequest();
            mStationId = stationId;
            mCameraId = cameraId;
            mIsUsingNewApi = false;
            clearImage();
            hideMessage();
            showProgressBar();
            stream(stationId, cameraId);
        }
    }
}
