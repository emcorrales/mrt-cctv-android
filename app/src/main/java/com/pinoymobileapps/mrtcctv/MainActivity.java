package com.pinoymobileapps.mrtcctv;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_STATION = "key_station";

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

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.stations)
    ListView mDrawerList;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private String[] mStations;
    private int mStationId;
    private CctvFragment[] mCameras = new CctvFragment[4];
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupNavigationDrawer();
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        if (savedInstanceState != null) {
            mStationId = savedInstanceState.getInt(KEY_STATION);
        }
        selectStation(mStationId);
    }

    private void setupNavigationDrawer() {
        mStations = getResources().getStringArray(R.array.stations);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.item_drawer, mStations));
        mDrawerList.setItemChecked(0, true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void setupViewPager(ViewPager viewPager) {
        bindViewPagerFragments();
        if (viewPager != null && viewPager.getAdapter() == null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFrag(mCameras[0]);
            adapter.addFrag(mCameras[1]);
            adapter.addFrag(mCameras[2]);
            adapter.addFrag(mCameras[3]);
            viewPager.setAdapter(adapter);
        }
    }

    private void bindViewPagerFragments() {
        mCameras[0] = (CctvFragment) getViewPagerFragment(0);
        if (mCameras[0] == null) {
            mCameras[0] = new CctvFragment();
        }

        mCameras[1] = (CctvFragment) getViewPagerFragment(1);
        if (mCameras[1] == null) {
            mCameras[1] = new CctvFragment();
        }

        mCameras[2] = (CctvFragment) getViewPagerFragment(2);
        if (mCameras[2] == null) {
            mCameras[2] = new CctvFragment();
        }

        mCameras[3] = (CctvFragment) getViewPagerFragment(3);
        if (mCameras[3] == null) {
            mCameras[3] = new CctvFragment();
        }

    }

    private Fragment getViewPagerFragment(int index) {
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager + ":" + index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STATION, mStationId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @OnItemClick(R.id.stations)
    void onClickStation(int stationId) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        selectStation(stationId);
    }

    private void selectStation(final int stationId) {
        mStationId = stationId;
        setTitle(mStations[stationId]);

        switch (mStationId) {
            case NORTH_AVE:
                updateTabs("PLATFORM1", "PLATFORM2", "TICKETING1", "TICKETING2");
                updateCameras();
                break;

            case QUEZON_AVE:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case KAMUNING:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case CUBAO:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case SANTOLAN:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case ORTIGAS:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case SHAW_BLVD:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case BONI:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case GUADALUPE:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case BUENDIA:
                updateTabs("NB PLATFORM", "TICKETING1", "SB PLATFORM", "TICKETING2");
                updateCameras();
                break;

            case AYALA:
                updateTabs("NB PLATFORM", "TICKETING1", "SB PLATFORM", "TICKETING2");
                updateCameras();
                break;

            case MAGALLANES:
                updateTabs("NB PLATFORM", "NB TICKETING", "SB PLATFORM", "SB TICKETING");
                updateCameras();
                break;

            case TAFT:
                updateTabs("PLATFORM1", "PLATFORM2", "TICKETING1", "TICKETING2");
                updateCameras();
                break;

            default:
                break;
        }
    }

    private void updateTabs(String text1, String text2, String text3, String text4) {
        updateTab(0, text1);
        updateTab(1, text2);
        updateTab(2, text3);
        updateTab(3, text4);
    }

    private void updateTab(int index, String text) {
        if (mTabLayout != null) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(index);
            if (tab1 != null) {
                tab1.setText(text);
            }
        }
    }

    private void updateCameras() {
        mCameras[0].setCamera(mStationId, 1);
        mCameras[1].setCamera(mStationId, 2);
        mCameras[2].setCamera(mStationId, 3);
        mCameras[3].setCamera(mStationId, 4);
    }
}
