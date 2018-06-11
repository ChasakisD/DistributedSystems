package com.distributedsystems.recommendationsystemclient.Models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class PoiCategory extends ExpandableGroup<Poi> {
    private Poi.POICategoryID category;

    public PoiCategory(Poi.POICategoryID category, String title, List<Poi> items) {
        super(title, items);
        this.category = category;
    }

    public Poi.POICategoryID getCategory() {
        return category;
    }

    public void setCategory(Poi.POICategoryID category) {
        this.category = category;
    }
}
