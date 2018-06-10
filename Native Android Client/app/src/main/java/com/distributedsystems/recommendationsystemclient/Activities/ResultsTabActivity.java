package com.distributedsystems.recommendationsystemclient.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.distributedsystems.recommendationsystemclient.Adapters.ResultsTabAdapter;
import com.distributedsystems.recommendationsystemclient.Fragments.SearchUserFragment;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;

import java.util.ArrayList;

import butterknife.BindView;

public class ResultsTabActivity extends BaseActivity {
    @BindView(R.id.pager)
    public ViewPager mPager;

    @BindView(R.id.tabs)
    public TabLayout mTabLayout;

    @BindView(R.id.toolbar_tabs)
    public Toolbar mToolbar;

    private ArrayList<Poi> pois;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.results);

        mPager.setAdapter(new ResultsTabAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mPager);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_results_tab;
    }

    @Override
    public void onBackPressed() {
        SearchUserFragment.notifyPackPressed();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
