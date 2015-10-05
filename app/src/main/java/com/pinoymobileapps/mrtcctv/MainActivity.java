package com.pinoymobileapps.mrtcctv;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_STATION = "key_station";
    private static final String KEY_CAMERA = "key_camera";

    @Nullable
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Nullable
    @Bind(R.id.stations)
    ListView mDrawerList;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Nullable
    @Bind(R.id.nb_platform)
    Button mNbPlatform;

    @Nullable
    @Bind(R.id.nb_ticketing)
    Button mNbTicketing;

    @Nullable
    @Bind(R.id.sb_platform)
    Button mSbPlatform;

    @Nullable
    @Bind(R.id.sb_ticketing)
    Button mSbTicketing;

    private CctvFragment mCctvFragment;
    private String[] mStations;
    private int mCurrentStationId = 0;
    private int mCurrentCameraId = 1;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int stationId = 0;
        int cameraId = 1;

        if (savedInstanceState != null) {
            stationId = savedInstanceState.getInt(KEY_STATION);
            cameraId = savedInstanceState.getInt(KEY_CAMERA);
        }
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        if (mDrawerLayout != null && mDrawerList != null) {
            setupNavigationDrawer(stationId);
        }
        mCctvFragment = (CctvFragment) getSupportFragmentManager().findFragmentById(R.id.cctv);
        if (mCctvFragment != null) {
            changeCamera(stationId, cameraId);
        }
    }

    private void setupNavigationDrawer(int selectedIndex) {
        mStations = getResources().getStringArray(R.array.stations);
        if (mDrawerList != null) {
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.item_drawer, mStations));
            mDrawerList.setItemChecked(selectedIndex, true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STATION, mCurrentStationId);
        outState.putInt(KEY_CAMERA, mCurrentCameraId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus
                && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && mCctvFragment != null
                && mCctvFragment.getView() != null
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCctvFragment.getView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Nullable
    @OnItemClick(R.id.stations)
    void onClickStation(int nextStationId) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        changeCamera(nextStationId, mCurrentCameraId);
    }

    @Nullable
    @OnClick({R.id.nb_platform, R.id.nb_ticketing, R.id.sb_platform, R.id.sb_ticketing})
    void onClickButton(Button button) {
        switch (button.getId()) {
            case R.id.nb_platform:
                changeCamera(mCurrentStationId, 1);
                break;

            case R.id.nb_ticketing:
                changeCamera(mCurrentStationId, 2);
                break;

            case R.id.sb_platform:
                changeCamera(mCurrentStationId, 3);
                break;

            case R.id.sb_ticketing:
                changeCamera(mCurrentStationId, 4);
                break;

            default:
                break;
        }
    }

    private void changeCamera(final int stationId, final int cameraId) {
        changeStation(stationId);
        mCurrentCameraId = cameraId;
        Button button = mapButton(mCurrentCameraId);
        selectButton(button);
        mCctvFragment.setCamera(stationId, mCurrentCameraId);
    }

    private Button mapButton(int cameraId) {
        switch (cameraId) {
            case 1:
                return mNbPlatform;

            case 2:
                return mNbTicketing;

            case 3:
                return mSbPlatform;

            case 4:
                return mSbTicketing;

            default:
                break;
        }

        return null;
    }

    private void changeStation(final int stationId) {
        mCurrentStationId = stationId;
        if (mToolbar != null) {
            setTitle(mStations[stationId]);
        }
    }

    private void selectButton(Button button) {
        clearButtons();
        if (button != null) {
            button.setTextColor(ContextCompat.getColor(this, R.color.button_selected_text));
        }
    }

    private void clearButtons() {
        clearButton(mNbPlatform);
        clearButton(mNbTicketing);
        clearButton(mSbPlatform);
        clearButton(mSbTicketing);
    }

    private void clearButton(Button button) {
        if (button != null) {
            button.setTextColor(ContextCompat.getColor(this, R.color.button_default_text));
        }
    }
}

