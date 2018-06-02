package com.distributedsystems.recommendationsystemclient.Models;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

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
            return "";
        }

        public static POICategoryID forValue(String value) throws IOException {
            if (value.equals("Arts & Entertainment")) return ARTS_ENTERTAINMENT;
            if (value.equals("Bars")) return BARS;
            if (value.equals("Food")) return FOOD;
            throw new IOException("Cannot deserialize POICategoryID");
        }
    }

    // todo
    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
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

    public POICategoryID getCategory() {
        return category;
    }

    public void setCategory(POICategoryID category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "POI Id: " + getId();
    }
}
