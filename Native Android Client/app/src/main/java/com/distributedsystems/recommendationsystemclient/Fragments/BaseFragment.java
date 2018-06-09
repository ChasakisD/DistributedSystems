package com.distributedsystems.recommendationsystemclient.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment{
    public String[] poiCategoriesAvailable = new String[]{
        "Arts & Entertainment",
        "Bars",
        "Food",
        "Unknown Category"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(getResourceLayout(), container, false);

        /* Bind ButterKnife */
        ButterKnife.bind(this, root);

        return root;
    }

    public abstract int getResourceLayout();
}
