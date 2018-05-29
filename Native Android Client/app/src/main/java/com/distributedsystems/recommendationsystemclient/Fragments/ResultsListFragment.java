package com.distributedsystems.recommendationsystemclient.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

    private ResultsGroupAdapter adapter;

    @Override
    public int getResourceLayout() {
        return R.layout.fragment_results_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle argumentsBundle = getArguments();
        if(argumentsBundle == null
                || !argumentsBundle.containsKey(getString(R.string.results_key))) {
            Toast.makeText(getContext(), "An error occurred while opening pois results", Toast.LENGTH_LONG).show();
            return;
        }

        // lets create the HashMap used to populate rv
        HashMap<String, ArrayList<Poi>> data = new HashMap<>();
        String[] categoriesAvailable = new String[4];
        categoriesAvailable[0] = "Arts & Entertainment";
        categoriesAvailable[1] = "Bars";
        categoriesAvailable[2] = "Food";
        categoriesAvailable[3] = "Unknown Category";

        ArrayList<Poi> artsAndEnterntaimentCategory = new ArrayList<>();
        ArrayList<Poi> barstCategory = new ArrayList<>();
        ArrayList<Poi> foodCategory = new ArrayList<>();
        ArrayList<Poi> unknownCategory = new ArrayList<>();

        ArrayList<Poi> allPois = (ArrayList<Poi>)argumentsBundle.get(getString(R.string.results_key));
        allPois.forEach((p) -> {
            if(p.getCategory() == null
                    || p.getCategory().equals("")) {
                unknownCategory.add(p);
            }
            else {
                switch(p.getCategory().toValue()){
                    case "Arts & Entertainment":
                        artsAndEnterntaimentCategory.add(p);
                        break;
                    case "Bars":
                        barstCategory.add(p);
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

        if(artsAndEnterntaimentCategory.size() != 0) data.put(categoriesAvailable[0], artsAndEnterntaimentCategory);
        if(barstCategory.size() != 0) data.put(categoriesAvailable[1], barstCategory);
        if(foodCategory.size() != 0) data.put(categoriesAvailable[2], foodCategory);
        if(unknownCategory.size() != 0) data.put(categoriesAvailable[3], unknownCategory);

        adapter = new ResultsGroupAdapter(getContext(), data);
        categoriesRv.setAdapter(adapter);
    }
}
