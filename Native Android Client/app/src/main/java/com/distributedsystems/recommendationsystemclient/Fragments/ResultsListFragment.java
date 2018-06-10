package com.distributedsystems.recommendationsystemclient.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.distributedsystems.recommendationsystemclient.Adapters.ResultsGroupAdapter;
import com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class ResultsListFragment extends BaseFragment{

    private final static int FETCH_DB_LOADER2_ID = 201;

    @BindView(R.id.results_list)
    RecyclerView categoriesRv;

    public ArrayList<Poi> allPois;

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_results_list;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<ArrayList<Poi>> poisLoader = loaderManager.getLoader(FETCH_DB_LOADER2_ID);
        if (poisLoader == null) {
            loaderManager.initLoader(FETCH_DB_LOADER2_ID, null, FavoriteMoviesLoader);
        } else {
            loaderManager.restartLoader(FETCH_DB_LOADER2_ID, null, FavoriteMoviesLoader);
        }

        return root;
    }

    public LoaderManager.LoaderCallbacks<Cursor> FavoriteMoviesLoader
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            return new CursorLoader(getContext(),
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
            if(data == null) {
                return;
            }

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


                Poi poi = null;

                try {
                    poi = new Poi(poiId,
                            name,
                            Double.parseDouble(latitude),
                            Double.parseDouble(longitude),
                            Poi.POICategoryID.fromValue(category),
                            photo);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(poi == null) continue;
                newData.add(poi);
            }
            data.close();
            allPois = newData;

            // lets create the HashMap used to populate rv
            HashMap<String, ArrayList<Poi>> allPoisHashmap = new HashMap<>();

            ArrayList<Poi> artsAndEntertainmentCategory = new ArrayList<>();
            ArrayList<Poi> barsCategory = new ArrayList<>();
            ArrayList<Poi> foodCategory = new ArrayList<>();
            ArrayList<Poi> unknownCategory = new ArrayList<>();

            allPois.forEach(p -> {
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
            });

            if(artsAndEntertainmentCategory.size() != 0) allPoisHashmap.put(poiCategoriesAvailable[0], artsAndEntertainmentCategory);
            if(barsCategory.size() != 0) allPoisHashmap.put(poiCategoriesAvailable[1], barsCategory);
            if(foodCategory.size() != 0) allPoisHashmap.put(poiCategoriesAvailable[2], foodCategory);
            if(unknownCategory.size() != 0) allPoisHashmap.put(poiCategoriesAvailable[3], unknownCategory);

            categoriesRv.setAdapter(new ResultsGroupAdapter(getContext(), allPoisHashmap));
            categoriesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        // ignore
        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    };
}