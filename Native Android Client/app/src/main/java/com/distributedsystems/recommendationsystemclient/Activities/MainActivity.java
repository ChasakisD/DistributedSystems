package com.distributedsystems.recommendationsystemclient.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.distributedsystems.recommendationsystemclient.Fragments.ResultsTabPanelFragment;
import com.distributedsystems.recommendationsystemclient.Fragments.SearchUserFragment;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;

public class MainActivity extends BaseActivity
                        implements SearchUserFragment.OnResultsFetchedListener{

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;

    @BindView(R.id.collapsing_toolbar)
    public CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mNavigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.recommendations:
                    selectedFragment = new SearchUserFragment();
                    break;
                default:
                    selectedFragment = new ResultsTabPanelFragment();
                    break;
            }

            try {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();

                item.setChecked(true);
                setTitle(item.getTitle());

                mDrawerLayout.closeDrawers();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return true;
        });

        // highlight item
        checkItem(savedInstanceState);

        // set the title of the Action Bar according to the selected item on the drawer
        switch (getCheckedItem()) {
            case 0:
                setTitle(R.string.recommendations);
                break;
            default:
                setTitle(R.string.second_item);
        }


        mNavigationView.setCheckedItem(R.id.recommendations);

        // by default open up Search User fragment
        if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SearchUserFragment.class.newInstance())
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(getString(R.string.drawer_item_key), getCheckedItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        checkItem(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Highlight the corresponding item
    private void checkItem(Bundle savedInstanceState){
        if(savedInstanceState != null) {
            int indexOfCheckedItem = savedInstanceState.getInt(getString(R.string.drawer_item_key), -1);
            if(indexOfCheckedItem != -1) {
                switch (indexOfCheckedItem) {
                    case 0: mNavigationView.setCheckedItem(R.id.recommendations);
                        break;
                    default: mNavigationView.setCheckedItem(R.id.secondItem);
                        break;
                }
            }
        }
        else {
            // default checked item to check is the Popular one;
            mNavigationView.setCheckedItem(R.id.recommendations);
        }
    }

    private int getCheckedItem() {
        Menu menu = mNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.isChecked()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onResultsFetched(ArrayList<Poi> pois) {
        Menu menu = mNavigationView.getMenu();
        menu.getItem(1).setChecked(true);
        mNavigationView.setCheckedItem(R.id.secondItem);
        setTitle(R.string.second_item);

        FragmentManager fragmentManager = getSupportFragmentManager();
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.results_key), pois);

            ResultsTabPanelFragment fragment = ResultsTabPanelFragment.class.newInstance();
            fragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
