package com.distributedsystems.recommendationsystemclient.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;
import com.distributedsystems.recommendationsystemclient.Utils.DialogUtils;
import com.distributedsystems.recommendationsystemclient.Utils.LocationUtils;
import com.distributedsystems.recommendationsystemclient.Utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.CATEGORY;
import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.LATITUDE;
import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.LONGITUDE;
import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.NAME;
import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.PHOTO;
import static com.distributedsystems.recommendationsystemclient.Data.SuggestedPoisContract.SuggestedPoisEntry.POI_ID;

public class SearchUserFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<ArrayList<Poi>> {

    private static final int POIS_LOADER_ID = 100;

    private static boolean onBackPressedFromChild = false;

    @BindView(R.id.masterIp)
    public TextView mMasterIpTextView;

    @BindView(R.id.userId)
    public TextView mUserIdTextView;

    @BindView(R.id.numberOfPois)
    public TextView mRadiusTextView;

    private AlertDialog mLoadingDialog;

    private boolean isSearching;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(getString(R.string.is_searching_key), isSearching);
        outState.putString(getString(R.string.master_ip_key), mMasterIpTextView.getText().toString());
        outState.putString(getString(R.string.user_id_key), mUserIdTextView.getText().toString());
        outState.putString(getString(R.string.radius_key), mRadiusTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null
            && savedInstanceState.containsKey(getString(R.string.is_searching_key))){
            isSearching = savedInstanceState.getBoolean(getString(R.string.is_searching_key));
            mMasterIpTextView.setText(savedInstanceState.getString(getString(R.string.master_ip_key)));
            mUserIdTextView.setText(savedInstanceState.getString(getString(R.string.user_id_key)));
            mRadiusTextView.setText(savedInstanceState.getString(getString(R.string.radius_key)));
        }

        if(isSearching && !onBackPressedFromChild){
            initLoader();
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_search_user;
    }

    @OnClick(R.id.searchUserButton)
    public void searchButtonClick(View view) {
        isSearching = true;
        initLoader();
    }

    private void initLoader(){
        if (getActivity() == null) return;

        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        Loader<ArrayList<Poi>> poisLoader = loaderManager.getLoader(POIS_LOADER_ID);
        if (poisLoader == null) {
            loaderManager.initLoader(POIS_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(POIS_LOADER_ID, null, this);
        }
    }

    public static void notifyPackPressed(){
        onBackPressedFromChild = true;
    }

    //region Loader Callback Implementation

    @NonNull
    @Override
    public Loader<ArrayList<Poi>> onCreateLoader(int id, Bundle args) {
        mLoadingDialog = DialogUtils.ShowLoadingDialog(getActivity());
        return new FetchPoisAsyncTaskLoader(
                getContext(),
                mMasterIpTextView.getText().toString(),
                Integer.parseInt(mUserIdTextView.getText().toString()),
                Integer.parseInt(mRadiusTextView.getText().toString()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Poi>> loader, ArrayList<Poi> data) {
        if(getActivity() == null) return;

        if(onBackPressedFromChild){
            LoaderManager loaderManager = getActivity().getSupportLoaderManager();
            loaderManager.destroyLoader(POIS_LOADER_ID);
            onBackPressedFromChild = false;
            return;
        }

        if(mLoadingDialog != null){
            mLoadingDialog.cancel();
        }

        if(data == null){
            DialogUtils.ShowNetworkErrorDialog(getActivity());
        }else{
            onResultsFetchedCallback.onResultsFetched(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Poi>> loader) {}

    //endregion

    //region Fetch Pois Async Task

    private static class FetchPoisAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Poi>> {
        private String fullIp;
        private int userId;
        private int numberOfPois;

        private ArrayList<Poi> pois;

        FetchPoisAsyncTaskLoader(Context context, String fullIp, int userId, int numberOfPois) {
            super(context);
            this.fullIp = fullIp;
            this.userId = userId;
            this.numberOfPois = numberOfPois;
        }

        @Override
        protected void onStartLoading() {
            if (pois == null) {
                forceLoad();
            } else {
                deliverResult(pois);
            }
        }

        @Nullable
        @Override
        public ArrayList<Poi> loadInBackground() {
            String[] tokens = fullIp.split(":");
            if (tokens.length != 2) return null;

            int port = Integer.parseInt(tokens[1]);

            pois = new NetworkUtils(tokens[0], port, 5)
                    .GetRecommendationPois(userId, numberOfPois, LocationUtils.GetLastKnownLocation(getContext()));

            getContext().getContentResolver()
                    .delete(
                            SuggestedPoisContract.SuggestedPoisEntry.SUGGESTED_POIS_URI,
                            null,
                            null);

            if(pois == null) return null;

            for(Poi p : pois){
                ContentValues contentValues = new ContentValues();

                // Put all details except reviews into the ContentValues
                contentValues.put(POI_ID, p.getId());
                contentValues.put(NAME, p.getName());
                contentValues.put(LATITUDE, p.getLatitude());
                contentValues.put(LONGITUDE, p.getLongitude());
                contentValues.put(CATEGORY, p.getCategory().toValue());
                contentValues.put(PHOTO, p.getPhoto());

                // Insert the content values via a ContentResolver
                getContext().getContentResolver()
                        .insert(SuggestedPoisContract.SuggestedPoisEntry.SUGGESTED_POIS_URI, contentValues);
            }

            return pois;
        }

        @Override
        public void deliverResult(@Nullable ArrayList<Poi> data) {
            pois = data;
            super.deliverResult(data);
        }
    }

    //endregion

    //region Result Callback

    /* Establish communication between this fragment and the wrapper activity
     * In order to inform when the results have been calculated
     * and let activity handle fragment transactions
     */
    OnResultsFetchedListener onResultsFetchedCallback;

    public interface OnResultsFetchedListener {
        void onResultsFetched(ArrayList<Poi> pois);
    }
    // --------------------------------------------------------------------

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            onResultsFetchedCallback = (OnResultsFetchedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnResultsFetchedListener");
        }
    }

    //endregion
}
