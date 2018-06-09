package com.distributedsystems.recommendationsystemclient.Adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.distributedsystems.recommendationsystemclient.Fragments.PoisOnMapFragment;
import com.distributedsystems.recommendationsystemclient.Fragments.ResultsListFragment;

public class ResultsTabAdapter extends FragmentPagerAdapter {
    private Bundle bundle;

    private String[] mTitles = new String[]{
            "Poi Results", "Pois in Map"
    };

    public ResultsTabAdapter(FragmentManager fm, Bundle bundle){
        super(fm);
        this.bundle = bundle;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch(position){
            case 0:
                fragment = new ResultsListFragment();
                break;
            default:
                fragment = new PoisOnMapFragment();
                break;
        }
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }
}
