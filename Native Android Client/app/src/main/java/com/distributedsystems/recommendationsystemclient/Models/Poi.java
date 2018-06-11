package com.distributedsystems.recommendationsystemclient.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Poi implements Serializable, Parcelable{
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
        FOOD,

        @SerializedName("Unknown")
        UNKNOWN;

        public String toValue() {
            switch (this) {
                case ARTS_ENTERTAINMENT:
                    return "Arts & Entertainment";
                case BARS:
                    return "Bars";
                case FOOD:
                    return "Food";
                default:
                    return "";
            }
        }

        public static POICategoryID fromValue(String value) {
            if (value.equals("Arts & Entertainment")) return ARTS_ENTERTAINMENT;
            if (value.equals("Bars")) return BARS;
            if (value.equals("Food")) return FOOD;
            else return UNKNOWN;
        }
    }

    public Poi() {
    }

    public Poi(String id){
        this.id = id;
    }

    public Poi(String id, String name, double latitude, double longitude, POICategoryID category, String photo) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.photo = photo;
    }

    protected Poi(Parcel in) {
        id = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        photo = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(category.toValue());
        dest.writeString(photo);
    }

    public static final Creator<Poi> CREATOR = new Creator<Poi>() {
        @Override
        public Poi createFromParcel(Parcel in) {
            return new Poi(in);
        }

        @Override
        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };
}
