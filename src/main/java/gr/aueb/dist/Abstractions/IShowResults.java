package gr.aueb.dist.Abstractions;

import gr.aueb.dist.Models.Poi;

import java.util.List;

public interface IShowResults {
    void GetResults();
    void ShowResults(List<Poi> pois);
}
