package com.distributedsystems.recommendationsystemclient.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.distributedsystems.recommendationsystemclient.Fragments.DummyFragment;
import com.distributedsystems.recommendationsystemclient.Fragments.ResultsListFragment;

public class ResultsTabAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{
            "Poi Results", "Pois in Map"
    };

    public ResultsTabAdapter(FragmentManager fm){
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new ResultsListFragment();
            default: return new DummyFragment();
        }
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }
}
