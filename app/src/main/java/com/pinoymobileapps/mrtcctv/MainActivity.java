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
    @Bind(R.id.button1)
    Button mButton1;

    @Nullable
    @Bind(R.id.button2)
    Button mButton2;

    @Nullable
    @Bind(R.id.button3)
    Button mButton3;

    @Nullable
    @Bind(R.id.button4)
    Button mButton4;

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
    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4})
    void onClickButton(Button button) {
        switch (button.getId()) {
            case R.id.button1:
                changeCamera(mCurrentStationId, 1);
                break;

            case R.id.button2:
                changeCamera(mCurrentStationId, 2);
                break;

            case R.id.button3:
                changeCamera(mCurrentStationId, 3);
                break;

            case R.id.button4:
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
                return mButton1;

            case 2:
                return mButton2;

            case 3:
                return mButton3;

            case 4:
                return mButton4;

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
        clearButton(mButton1);
        clearButton(mButton2);
        clearButton(mButton3);
        clearButton(mButton4);
    }

    private void clearButton(Button button) {
        if (button != null) {
            button.setTextColor(ContextCompat.getColor(this, R.color.button_default_text));
        }
    }
}

