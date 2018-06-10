package com.distributedsystems.recommendationsystemclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.distributedsystems.recommendationsystemclient.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;

public class PoiMapAdapter implements GoogleMap.InfoWindowAdapter {
    private View infoWindowView;
    private LayoutInflater layoutInflater;
    private HashMap<String, String> imagesUri;

    private Marker lastMarker;

    private Picasso picassoSingleton;
    private Transformation transformation;

    public PoiMapAdapter(Context context, LayoutInflater layoutInflater, HashMap<String, String> images) {
        this.layoutInflater = layoutInflater;
        this.imagesUri = images;

        picassoSingleton = Picasso.with(context);
        picassoSingleton.setIndicatorsEnabled(true);

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(context.getResources().getDimension(R.dimen.map_poi_photo_radius))
                .build();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (infoWindowView == null) {
            infoWindowView = layoutInflater.inflate(R.layout.poi_map_info_window, null);
        }

        if (lastMarker != null && lastMarker.getId().equals(marker.getId())) return infoWindowView;

        lastMarker = marker;

        TextView titleTextView = infoWindowView.findViewById(R.id.map_poi_title);
        titleTextView.setText(marker.getTitle());

        TextView snippetTextView = infoWindowView.findViewById(R.id.map_poi_snippet);
        snippetTextView.setText(marker.getSnippet());

        String imageUri = imagesUri.get(marker.getId());
        RoundedImageView poiIconImageView = infoWindowView.findViewById(R.id.map_poi_icon);

        if (imageUri == null) {
            poiIconImageView.setVisibility(View.GONE);
        }
        else {
            picassoSingleton.load(imageUri)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.background)
                    .placeholder(R.drawable.background)
                    .transform(transformation)
                    .into(poiIconImageView, new PicassoPoiIconCallback(marker));
        }

        return infoWindowView;
    }

    private static class PicassoPoiIconCallback implements Callback {
        private Marker marker;

        PicassoPoiIconCallback(Marker marker) {
            this.marker=marker;
        }

        @Override
        public void onError() { }

        @Override
        public void onSuccess() {
            if (marker == null) return;

            if(marker.isInfoWindowShown()){
                marker.hideInfoWindow();
            }
            marker.showInfoWindow();
        }
    }
}
