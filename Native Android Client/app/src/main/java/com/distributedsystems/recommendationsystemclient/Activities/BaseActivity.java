package com.distributedsystems.recommendationsystemclient.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public final static HashMap<Poi.POICategoryID, String> categoriesColors =
            new HashMap<Poi.POICategoryID, String>(){
        {
            put(Poi.POICategoryID.ARTS_ENTERTAINMENT, "#ff9a57af");
            put(Poi.POICategoryID.BARS, "#ff20a8cd");
            put(Poi.POICategoryID.FOOD, "#ffffd54f");
            put(Poi.POICategoryID.UNKNOWN, "#ff87d063");
        }
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
