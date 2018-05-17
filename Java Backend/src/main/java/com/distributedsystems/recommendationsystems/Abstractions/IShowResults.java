package com.distributedsystems.recommendationsystems.Abstractions;

import com.distributedsystems.recommendationsystems.Models.Poi;

import java.util.List;

public interface IShowResults {
    void GetResults();
    void ShowResults(List<Poi> pois);
}
