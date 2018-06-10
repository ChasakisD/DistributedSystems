package com.distributedsystems.recommendationsystemclient.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distributedsystems.recommendationsystemclient.Activities.BaseActivity;
import com.distributedsystems.recommendationsystemclient.Adapters.PoiMapAdapter;
import com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;
import com.distributedsystems.recommendationsystemclient.Utils.DialogUtils;
import com.distributedsystems.recommendationsystemclient.Utils.LocationUtils;
import com.distributedsystems.recommendationsystemclient.Utils.PermissionUtils;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;

public class PoisOnMapFragment extends BaseFragment implements OnMapReadyCallback,
        LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.map_nested_scroll_view)
    public NestedScrollView mMapNestedScrollView;

    @BindView(R.id.poi_category_spinner)
    public MaterialSpinner mPoiCategorySpinner;

    public ArrayList<Poi> selectedPois;

    private GoogleMap mGoogleMap;
    private AlertDialog mLoadingDialog;

    private final static int FETCH_DB_LOADER_ID = 200;

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

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
    public void onResume() {
        super.onResume();

        if(getActivity() == null) return;

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<ArrayList<Poi>> poisLoader = loaderManager.getLoader(FETCH_DB_LOADER_ID);
        if (poisLoader == null) {
            loaderManager.initLoader(FETCH_DB_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(FETCH_DB_LOADER_ID, null, this);
        }
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
        if(getContext() == null) return;

        mGoogleMap.clear();

        HashMap<String, String> markerImages = new HashMap<>();

        /* Filter only items for categories */
        selectedPois
                .stream()
                .filter(p -> position == 4
                        || p.getCategory().toValue().equals(poiCategoriesAvailable[position]))
                .forEach(p -> {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                            .title(p.getName())
                            .snippet(p.getCategory().toValue())
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(p))));

                    markerImages.put(marker.getId(), p.getPhoto());
                });

        mGoogleMap.setInfoWindowAdapter(new PoiMapAdapter(getContext(), getLayoutInflater(), markerImages));
    }

    private float getMarkerColor(Poi poi){
        switch(poi.getCategory().toValue()){
            case "Arts & Entertainment": return BaseActivity.categoriesHues[0];
            case "Bars": return BaseActivity.categoriesHues[1];
            case "Food": return BaseActivity.categoriesHues[2];
            default: return BaseActivity.categoriesHues[3];
        }
    }

    //region Fetch Pois from SQLite via CursorLoader

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        mLoadingDialog = DialogUtils.ShowLoadingDialog(getActivity());
        return new CursorLoader(Objects.requireNonNull(getContext()),
                SuggestedPoisContract.SuggestedPoisEntry.SUGGESTED_POIS_URI,
                new String[] {SuggestedPoisContract.SuggestedPoisEntry.POI_ID,
                        SuggestedPoisContract.SuggestedPoisEntry.NAME,
                        SuggestedPoisContract.SuggestedPoisEntry.LATITUDE,
                        SuggestedPoisContract.SuggestedPoisEntry.LONGITUDE,
                        SuggestedPoisContract.SuggestedPoisEntry.CATEGORY,
                        SuggestedPoisContract.SuggestedPoisEntry.PHOTO},
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(mLoadingDialog != null){
            mLoadingDialog.cancel();
        }

        if(data == null) return;

        ArrayList<Poi> newData = new ArrayList<>();
        while (data.moveToNext()) {
            String poiId = data.getString(data
                    .getColumnIndex(SuggestedPoisContract.SuggestedPoisEntry.POI_ID));

            String name = data.getString(data
                    .getColumnIndex(SuggestedPoisContract.SuggestedPoisEntry.NAME));

            String latitude = data.getString(data
                    .getColumnIndex(SuggestedPoisContract.SuggestedPoisEntry.LATITUDE));

            String longitude = data.getString(data
                    .getColumnIndex(SuggestedPoisContract.SuggestedPoisEntry.LONGITUDE));

            String category = data.getString(data
                    .getColumnIndex(SuggestedPoisContract.SuggestedPoisEntry.CATEGORY));

            String photo = data.getString(data
                    .getColumnIndex(SuggestedPoisContract.SuggestedPoisEntry.PHOTO));


            try {
                Poi poi = new Poi(poiId,
                        name,
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        Poi.POICategoryID.fromValue(category),
                        photo);
                newData.add(poi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data.close();
        selectedPois = newData;

        placeMarkersOnMap(mPoiCategorySpinner.getSelectedIndex());
    }

    // ignore
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    //endregion
}