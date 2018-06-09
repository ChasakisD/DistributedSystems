package com.distributedsystems.recommendationsystemclient.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public final static Integer[] categoriesColors = new Integer[]{
            0xff9a57af,
            0xff20a8cd,
            0xffffd54f,
            0xff87d063
    };

    public final static Float[] categoriesHues = new Float[]{
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResourceLayout());

        /* Bind ButterKnife */
        ButterKnife.bind(this);
    }

    public abstract int getResourceLayout();
}
