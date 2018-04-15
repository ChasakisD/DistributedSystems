package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Models.Poi;

import java.util.List;

public interface IShowResults {
    void GetResults();
    void ShowResults(List<Poi> pois);
}
