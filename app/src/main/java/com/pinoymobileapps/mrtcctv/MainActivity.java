package com.pinoymobileapps.mrtcctv;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_STATION = "key_station";
    private static final String KEY_CAMERA = "key_camera";

    private static final int NORTH_AVE = 0;
    private static final int QUEZON_AVE = 1;
    private static final int KAMUNING = 2;
    private static final int CUBAO = 3;
    private static final int SANTOLAN = 4;
    private static final int ORTIGAS = 5;
    private static final int SHAW_BLVD = 6;
    private static final int BONI = 7;
    private static final int GUADALUPE = 8;
    private static final int BUENDIA = 9;
    private static final int AYALA = 10;
    private static final int MAGALLANES = 11;
    private static final int TAFT = 12;

    private static String NB_PLATFORM;
    private static String NB_PLATFORM1;
    private static String NB_PLATFORM2;

    private static String SB_PLATFORM;
    private static String SB_PLATFORM1;
    private static String SB_PLATFORM2;

    private static String NB_TICKETING;
    private static String NB_TICKETING1;
    private static String NB_TICKETING2;

    private static String SB_TICKETING;
    private static String SB_TICKETING1;
    private static String SB_TICKETING2;

    private static String TICKETING1;
    private static String TICKETING2;

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

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        if (mDrawerLayout != null && mDrawerList != null) {
            setupNavigationDrawer();
        }

        mCctvFragment = (CctvFragment) getSupportFragmentManager().findFragmentById(R.id.cctv);

        if (savedInstanceState != null) {
            int savedStationId = savedInstanceState.getInt(KEY_STATION);
            int savedCameraId = savedInstanceState.getInt(KEY_CAMERA);

            if (mCctvFragment != null) {
                changeCamera(savedStationId, savedCameraId);
            }

        } else {
            initStrings();
            changeCamera(NORTH_AVE, 1);
        }
    }

    private void setupNavigationDrawer() {
        mStations = getResources().getStringArray(R.array.stations);
        if (mDrawerList != null) {
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.item_drawer, mStations));
            mDrawerList.setItemChecked(0, true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        if (mDrawerLayout != null) {
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    }

    private void initStrings() {
        NB_PLATFORM = getResources().getString(R.string.nb_platfrom);
        NB_PLATFORM1 = getResources().getString(R.string.nb_platfrom1);
        NB_PLATFORM2 = getResources().getString(R.string.nb_platfrom2);

        SB_PLATFORM = getResources().getString(R.string.sb_platfrom);
        SB_PLATFORM1 = getResources().getString(R.string.sb_platfrom1);
        SB_PLATFORM2 = getResources().getString(R.string.sb_platfrom2);

        NB_TICKETING = getResources().getString(R.string.nb_ticketing);
        NB_TICKETING1 = getResources().getString(R.string.nb_ticketing1);
        NB_TICKETING2 = getResources().getString(R.string.nb_ticketing2);

        SB_TICKETING = getResources().getString(R.string.sb_ticketing);
        SB_TICKETING1 = getResources().getString(R.string.sb_ticketing1);
        SB_TICKETING2 = getResources().getString(R.string.sb_ticketing2);

        TICKETING1 = getResources().getString(R.string.ticketing1);
        TICKETING2 = getResources().getString(R.string.ticketing2);
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
                && mCctvFragment != null) {
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

        switch (mCurrentStationId) {
            case NORTH_AVE:
                updateTextsOfButtons(SB_PLATFORM1, SB_PLATFORM2, SB_TICKETING1, SB_TICKETING2);
                break;

            case QUEZON_AVE:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case KAMUNING:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case CUBAO:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case SANTOLAN:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case ORTIGAS:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case SHAW_BLVD:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case BONI:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case GUADALUPE:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case BUENDIA:
                updateTextsOfButtons(NB_PLATFORM, TICKETING1, SB_PLATFORM, TICKETING2);
                break;

            case AYALA:
                updateTextsOfButtons(NB_PLATFORM, TICKETING1, SB_PLATFORM, TICKETING2);
                break;

            case MAGALLANES:
                updateTextsOfButtons(NB_PLATFORM, NB_TICKETING, SB_PLATFORM, SB_TICKETING);
                break;

            case TAFT:
                updateTextsOfButtons(NB_PLATFORM1, NB_PLATFORM2, NB_TICKETING1, NB_TICKETING2);
                break;

            default:
                break;
        }
    }

    private void selectButton(Button button) {
        clearButtons();
        if (button != null) {
            button.setTextColor(getResources().getColor(R.color.button_selected_text));
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
            button.setTextColor(getResources().getColor(R.color.button_default_text));
        }
    }

    private void updateTextsOfButtons(String text1, String text2, String text3, String text4) {
        updateButtonText(mButton1, text1);
        updateButtonText(mButton2, text2);
        updateButtonText(mButton3, text3);
        updateButtonText(mButton4, text4);
    }

    private void updateButtonText(Button button, String text) {
        if (button != null) {
            button.setText(text);
        }
    }
}

