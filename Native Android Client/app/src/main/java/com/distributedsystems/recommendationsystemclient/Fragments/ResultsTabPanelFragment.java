package com.distributedsystems.recommendationsystemclient.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distributedsystems.recommendationsystemclient.R;
import com.distributedsystems.recommendationsystemclient.Adapters.ResultsTabAdapter;

import java.util.Locale;

import butterknife.BindView;

public class ResultsTabPanelFragment extends BaseFragment {

    @BindView(R.id.pager)
    public ViewPager mPager;

    @BindView(R.id.tabs)
    public TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        mPager.setAdapter(new ResultsTabAdapter(getChildFragmentManager(), getArguments()));
        mTabLayout.setupWithViewPager(mPager);

        return root;
    }

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_results_tab;
    }
}
