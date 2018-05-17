package com.distributedsystems.recommendationsystemclient.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import static com.distributedsystems.recommendationsystemclient.Utils.OpenPoiLocation.showMap;

public class ResultsPoisRvAdapter extends RecyclerView.Adapter<ResultsPoisRvAdapter.PoiHolder> {

    private ArrayList<Poi> poisList;
    private Context context;

    public class PoiHolder extends RecyclerView.ViewHolder{
        public TextView poiNameTv;
        public TextView poiCategoryTv;
        public ImageView poiPhotoIv;
        public ImageView poiLocationIcon;

        public PoiHolder(View view){
            super(view);
            poiNameTv = view.findViewById(R.id.poi_name);
            poiCategoryTv = view.findViewById(R.id.poi_category);
            poiPhotoIv = view.findViewById(R.id.poi_photo);
            poiLocationIcon = view.findViewById(R.id.poi_location_icon);
        }
    }

    public ResultsPoisRvAdapter(Context context, ArrayList<Poi> poisList){
        if(poisList == null) this.poisList = new ArrayList<>();
        else this.poisList = poisList;

        this.context = context;
    }

    @NonNull
    @Override
    public PoiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_list_group_item, parent, false);

        return new PoiHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PoiHolder holder, int position) {
        if(poisList.get(position) == null) return;

        Poi currentPoi = poisList.get(position);
        holder.poiNameTv.setText(currentPoi.getName().trim());
        holder.poiCategoryTv.setText(currentPoi.getCategory().toValue());

        if(currentPoi.getPhoto() == null || currentPoi.getPhoto().equals("")) {
            holder.poiPhotoIv.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.background, context.getTheme()));
        }
        else {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(ContextCompat.getColor(context, R.color.primary_dark))
                    .borderWidthDp(1)
                    .cornerRadiusDp(context.getResources().getDimension(R.dimen.poi_photo_radius))
                    .build();
            try{
                Picasso.with(context)
                        .load(currentPoi.getPhoto())
                        .fit()
                        .centerCrop()
                        .error(R.drawable.background)
                        .placeholder(R.drawable.background)
                        .transform(transformation)
                        .into(holder.poiPhotoIv);
            } catch (Exception e) {
                // in any case an error has been made
                holder.poiPhotoIv.setImageDrawable(context.getResources()
                        .getDrawable(R.drawable.background, context.getTheme()));
            }
        }

        holder.poiLocationIcon.setOnClickListener(view -> {
            showMap(context, currentPoi.getLatitude(), currentPoi.getLongitude());
        });
    }

    @Override
    public int getItemCount() {
        return poisList == null ? 0 : poisList.size();
    }

}

