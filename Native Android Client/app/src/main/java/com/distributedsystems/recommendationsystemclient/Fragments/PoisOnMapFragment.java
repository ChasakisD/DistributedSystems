package com.distributedsystems.recommendationsystemclient.Fragments;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distributedsystems.recommendationsystemclient.Activities.BaseActivity;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;
import com.distributedsystems.recommendationsystemclient.Utils.LocationUtils;
import com.distributedsystems.recommendationsystemclient.Utils.PermissionUtils;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import butterknife.BindView;

public class PoisOnMapFragment extends BaseFragment implements OnMapReadyCallback{

    @BindView(R.id.map_nested_scroll_view)
    public NestedScrollView mMapNestedScrollView;

    @BindView(R.id.poi_category_spinner)
    public MaterialSpinner mPoiCategorySpinner;

    private ArrayList<Poi> selectedPois;

    private GoogleMap mGoogleMap;

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        if(getArguments() == null || !getArguments().containsKey(getString(R.string.results_key)))
            return root;

        selectedPois = (ArrayList<Poi>) getArguments().get(getString(R.string.results_key));

        mPoiCategorySpinner.setItems((String[]) ArrayUtils.appendToArray(poiCategoriesAvailable, "All categories"));
        mPoiCategorySpinner.setOnItemSelectedListener((view, position, id, item) -> placeMarkersOnMap(position));

        if(getActivity() == null) return root;

        NestedSupportMapFragment mSupportMapFragment = (NestedSupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.pois_map_view);
        if(mSupportMapFragment == null) return root;

        mSupportMapFragment.getMapAsync(this);
        mSupportMapFragment.setListener(() -> mMapNestedScrollView.requestDisallowInterceptTouchEvent(true));

        return root;
    }

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_map_pois;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if(getContext() == null) return;

        Location userLocation = LocationUtils.GetLastKnownLocation(getContext());
        if(userLocation == null) return;

        placeMarkersOnMap(mPoiCategorySpinner.getSelectedIndex());

        mGoogleMap.moveCamera(CameraUpdateFactory
                .newLatLng(new LatLng(
                        userLocation.getLatitude(), userLocation.getLongitude())));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f), 2000, null);

        if(!PermissionUtils.ArePermissionsGranted(getContext())) return;

        /* PermissionUtils just checked for permissions, but compiler does not know it */
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setTrafficEnabled(true);
    }

    private void placeMarkersOnMap(int position){
        if(mGoogleMap == null) return;
        if(selectedPois == null) return;

        mGoogleMap.clear();

        /* Filter only items for categories */
        selectedPois
            .stream()
            .filter(p -> position == 4
                    || p.getCategory().toValue().equals(poiCategoriesAvailable[position]))
            .forEach(p ->
                mGoogleMap.addMarker(new MarkerOptions()
                        .title(p.getName())
                        .snippet(p.getCategory().toValue())
                        .position(new LatLng(p.getLatitude(), p.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(p)))));
    }

    private float getMarkerColor(Poi poi){
        switch(poi.getCategory().toValue()){
            case "Arts & Entertainment": return BaseActivity.categoriesHues[0];
            case "Bars": return BaseActivity.categoriesHues[1];
            case "Food": return BaseActivity.categoriesHues[2];
            default: return BaseActivity.categoriesHues[3];
        }
    }
}
