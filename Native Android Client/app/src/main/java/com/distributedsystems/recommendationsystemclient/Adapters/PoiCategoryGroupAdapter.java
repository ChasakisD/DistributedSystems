package com.distributedsystems.recommendationsystemclient.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.distributedsystems.recommendationsystemclient.Activities.BaseActivity;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.Models.PoiCategory;
import com.distributedsystems.recommendationsystemclient.R;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;
import static com.distributedsystems.recommendationsystemclient.Utils.OpenPoiLocation.showMap;

public class PoiCategoryGroupAdapter extends
        ExpandableRecyclerViewAdapter<
                PoiCategoryGroupAdapter.PoiCategoryHolder,
                PoiCategoryGroupAdapter.PoiHolder> {

    private Context context;
    private Picasso mPicasso;

    public PoiCategoryGroupAdapter(Context context, List<? extends ExpandableGroup> categories) {
        super(categories);

        this.context = context;
        this.mPicasso = Picasso.with(context);
        this.mPicasso.setIndicatorsEnabled(true);
    }

    @Override
    public PoiCategoryHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_list_item, parent, false);

        return new PoiCategoryHolder(itemView);
    }

    @Override
    public PoiHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_list_group_item, parent, false);

        return new PoiHolder(itemView);
    }

    @Override
    public void onBindGroupViewHolder(PoiCategoryHolder holder, int flatPosition, ExpandableGroup group) {
        PoiCategory category = (PoiCategory) group;
        holder.categoryTv.setText(category.getCategory().toValue());

        holder.boxColor.setBackground(ContextCompat.getDrawable(context, R.drawable.upper_rounded_background));
        GradientDrawable drawable = (GradientDrawable) holder.boxColor.getBackground();
        drawable.setColor(Color.parseColor(BaseActivity.categoriesColors.get(category.getCategory())));
    }

    @Override
    public void onBindChildViewHolder(PoiHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        PoiCategory categoryGroup = (PoiCategory) group;

        if(categoryGroup.getItems().get(childIndex) == null) return;

        Poi currentPoi = categoryGroup.getItems().get(childIndex);
        holder.poiNameTv.setText(currentPoi.getName().trim());
        holder.poiCategoryTv.setText(currentPoi.getCategory().toValue());

        if(currentPoi.getPhoto() == null || currentPoi.getPhoto().equals("")) {
            holder.poiPhotoIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.background));
        }
        else {
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(context.getResources().getDimension(R.dimen.poi_photo_radius))
                    .build();
            try{
                mPicasso.load(currentPoi.getPhoto())
                        .fit()
                        .centerCrop()
                        .error(R.drawable.background)
                        .placeholder(R.drawable.background)
                        .transform(transformation)
                        .into(holder.poiPhotoIv);
            } catch (Exception e) {
                holder.poiPhotoIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.background));
            }
        }

        holder.poiLocationIcon.setOnClickListener(view -> showMap(context, currentPoi.getLatitude(), currentPoi.getLongitude()));

        if(childIndex == categoryGroup.getItemCount() - 1){
            if(!(holder.leftView.getBackground() instanceof GradientDrawable)){
                holder.leftView.setBackground(ContextCompat.getDrawable(context, R.drawable.down_rounded_background));
            }

            GradientDrawable gradientDrawable = (GradientDrawable) holder.leftView.getBackground();
            gradientDrawable.setColor(Color.parseColor(BaseActivity.categoriesColors.get(categoryGroup.getCategory())));
        }else{
            holder.leftView.setBackground(new ColorDrawable(Color.parseColor(BaseActivity.categoriesColors.get(categoryGroup.getCategory()))));
        }
    }

    //region Category View Holder

    class PoiCategoryHolder extends GroupViewHolder {
        RelativeLayout categoryHeaderLayout;
        TextView categoryTv;
        View boxColor;
        ImageView isExpandedImageView;

        PoiCategoryHolder(View view){
            super(view);
            categoryTv = view.findViewById(R.id.poi_category_tv);
            categoryHeaderLayout = view.findViewById(R.id.category_header_layout);
            boxColor = view.findViewById(R.id.box_color_category);
            isExpandedImageView = view.findViewById(R.id.is_expanded_iv);
        }

        @Override
        public void expand() {
            animateExpand();
        }

        @Override
        public void collapse() {
            animateCollapse();
        }

        private void animateExpand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            isExpandedImageView.setAnimation(rotate);

            Poi.POICategoryID category = Poi.POICategoryID.fromValue(categoryTv.getText().toString());
            boxColor.setBackground(ContextCompat.getDrawable(context, R.drawable.upper_rounded_background));
            GradientDrawable drawable = (GradientDrawable) boxColor.getBackground();
            drawable.setColor(Color.parseColor(BaseActivity.categoriesColors.get(category)));
        }

        private void animateCollapse() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            isExpandedImageView.setAnimation(rotate);

            Poi.POICategoryID category = Poi.POICategoryID.fromValue(categoryTv.getText().toString());
            boxColor.setBackground(ContextCompat.getDrawable(context, R.drawable.full_rounded_background));
            GradientDrawable drawable = (GradientDrawable) boxColor.getBackground();
            drawable.setColor(Color.parseColor(BaseActivity.categoriesColors.get(category)));

        }
    }

    //endregion

    //region Poi View Holder

    class PoiHolder extends ChildViewHolder{
        TextView poiNameTv;
        TextView poiCategoryTv;
        ImageView poiPhotoIv;
        ImageView poiLocationIcon;
        View leftView;

        PoiHolder(View view){
            super(view);
            poiNameTv = view.findViewById(R.id.poi_name);
            poiCategoryTv = view.findViewById(R.id.poi_category);
            poiPhotoIv = view.findViewById(R.id.poi_photo);
            poiLocationIcon = view.findViewById(R.id.poi_location_icon);
            leftView = view.findViewById(R.id.left_view);
        }
    }

    //endregion
}
