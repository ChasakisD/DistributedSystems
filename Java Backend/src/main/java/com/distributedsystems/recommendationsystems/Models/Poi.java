package com.distributedsystems.recommendationsystems.Models;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;

public class Poi implements Serializable {
    @SerializedName("POI")
    private String id;

    @SerializedName("POI_name")
    private String name;

    @SerializedName("latidude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("POI_category_id")
    private POICategoryID category;

    @SerializedName("photos")
    private String photo;

    private int distance;

    public enum POICategoryID {
        @SerializedName("Arts & Entertainment")
        ARTS_ENTERTAINMENT,

        @SerializedName("Bars")
        BARS,

        @SerializedName("Food")
        FOOD;

        public String toValue() {
            switch (this) {
                case ARTS_ENTERTAINMENT:
                    return "Arts & Entertainment";
                case BARS:
                    return "Bars";
                case FOOD:
                    return "Food";
            }
            return null;
        }

        public static POICategoryID forValue(String value) throws IOException {
            if (value.equals("Arts & Entertainment")) return ARTS_ENTERTAINMENT;
            if (value.equals("Bars")) return BARS;
            if (value.equals("Food")) return FOOD;
            throw new IOException("Cannot deserialize POICategoryID");
        }
    }

    public Poi() {
    }

    public Poi(String id){
        this.id = id;
    }

    public Poi(String id, String name, double latitude, double longitude, POICategoryID category) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public POICategoryID getCategory() {
        return category;
    }

    public void setCategory(POICategoryID category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "POI Id: " + getId();
    }
}