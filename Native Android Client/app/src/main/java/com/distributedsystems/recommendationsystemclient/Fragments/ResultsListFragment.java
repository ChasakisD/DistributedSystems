package com.distributedsystems.recommendationsystemclient.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.distributedsystems.recommendationsystemclient.Adapters.ResultsGroupAdapter;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;

public class ResultsListFragment extends BaseFragment{

    @BindView(R.id.results_list)
    RecyclerView categoriesRv;

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_results_list;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        Bundle argumentsBundle = getArguments();
        if(argumentsBundle == null
                || !argumentsBundle.containsKey(getString(R.string.results_key))) {
            Toast.makeText(
                    getContext(),
                    "An error occurred while opening pois results",
                    Toast.LENGTH_LONG)
                .show();
            return root;
        }

        // lets create the HashMap used to populate rv
        HashMap<String, ArrayList<Poi>> data = new HashMap<>();

        ArrayList<Poi> artsAndEntertainmentCategory = new ArrayList<>();
        ArrayList<Poi> barsCategory = new ArrayList<>();
        ArrayList<Poi> foodCategory = new ArrayList<>();
        ArrayList<Poi> unknownCategory = new ArrayList<>();

        ArrayList<Poi> allPois = (ArrayList<Poi>) argumentsBundle.get(getString(R.string.results_key));
        if(allPois == null) return root;

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

        if(artsAndEntertainmentCategory.size() != 0) data.put(poiCategoriesAvailable[0], artsAndEntertainmentCategory);
        if(barsCategory.size() != 0) data.put(poiCategoriesAvailable[1], barsCategory);
        if(foodCategory.size() != 0) data.put(poiCategoriesAvailable[2], foodCategory);
        if(unknownCategory.size() != 0) data.put(poiCategoriesAvailable[3], unknownCategory);

        categoriesRv.setAdapter(new ResultsGroupAdapter(getContext(), data));
        categoriesRv.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }
}
