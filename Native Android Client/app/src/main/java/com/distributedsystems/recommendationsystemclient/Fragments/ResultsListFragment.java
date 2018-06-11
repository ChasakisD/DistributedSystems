package com.distributedsystems.recommendationsystemclient.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.distributedsystems.recommendationsystemclient.Adapters.PoiCategoryGroupAdapter;
import com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.Models.PoiCategory;
import com.distributedsystems.recommendationsystemclient.R;
import com.distributedsystems.recommendationsystemclient.Utils.DialogUtils;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;

public class ResultsListFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final static int FETCH_DB_LOADER2_ID = 201;

    @BindView(R.id.results_list)
    public RecyclerView categoriesRv;

    private AlertDialog mLoadingDialog;
    private PoiCategoryGroupAdapter mPoiAdapter;

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_results_list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();

        if(getActivity() == null) return;

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<ArrayList<Poi>> poisLoader = loaderManager.getLoader(FETCH_DB_LOADER2_ID);
        if (poisLoader == null) {
            loaderManager.initLoader(FETCH_DB_LOADER2_ID, null, this);
        } else {
            loaderManager.restartLoader(FETCH_DB_LOADER2_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPoiAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mPoiAdapter != null) {
            mPoiAdapter.onRestoreInstanceState(savedInstanceState);
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

        ArrayList<Poi> pois = new ArrayList<>();
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

            pois.add(new Poi(poiId, name,
                    Double.parseDouble(latitude), Double.parseDouble(longitude),
                    Poi.POICategoryID.fromValue(category), photo));
        }

        data.close();

        ArrayList<PoiCategory> categoriesGroup = new ArrayList<>();

        ArrayList<Poi> artsAndEntertainmentCategory = new ArrayList<>();
        ArrayList<Poi> barsCategory = new ArrayList<>();
        ArrayList<Poi> foodCategory = new ArrayList<>();
        ArrayList<Poi> unknownCategory = new ArrayList<>();

        for(Poi p : pois){
            if(p == null) return;
            if(p.getCategory() == null
                    || p.getCategory().toValue().equals("")) {
                unknownCategory.add(p);
            }
            else {
                switch(p.getCategory().toValue()){
                    case "Arts & Entertainment":
                        artsAndEntertainmentCategory.add(p);
                        break;
                    case "Bars":
                        barsCategory.add(p);
                        break;
                    case "Food":
                        foodCategory.add(p);
                        break;
                    default:
                        unknownCategory.add(p);
                        break;
                }
            }
        }

        if(artsAndEntertainmentCategory.size() != 0)
            categoriesGroup.add(new PoiCategory(Poi.POICategoryID.ARTS_ENTERTAINMENT, poiCategoriesAvailable[0], artsAndEntertainmentCategory));
        if(barsCategory.size() != 0)
            categoriesGroup.add(new PoiCategory(Poi.POICategoryID.BARS, poiCategoriesAvailable[1], barsCategory));
        if(foodCategory.size() != 0)
            categoriesGroup.add(new PoiCategory(Poi.POICategoryID.FOOD, poiCategoriesAvailable[2], foodCategory));
        if(unknownCategory.size() != 0)
            categoriesGroup.add(new PoiCategory(Poi.POICategoryID.UNKNOWN, poiCategoriesAvailable[3], unknownCategory));

        mPoiAdapter = new PoiCategoryGroupAdapter(getContext(), categoriesGroup);
        categoriesRv.setAdapter(mPoiAdapter);
        categoriesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        if (!(categoriesRv.getItemAnimator() instanceof DefaultItemAnimator)) return;
        ((DefaultItemAnimator) categoriesRv.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    // ignore
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) { }

    //endregion
}