package com.distributedsystems.recommendationsystemclient.Adapters;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.distributedsystems.recommendationsystemclient.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ResultsGroupAdapter extends RecyclerView.Adapter<ResultsGroupAdapter.CategoryHolder>{
    private final static List<Integer> boxColors = Arrays.asList(
            0xfff06292,
            0xff4db6ac,
            0xffffd54f,
            0xffaed581
    );

    private final static List<Integer> boxColorsOpacity = Arrays.asList(
            0xaaf06292,
            0xaa4db6ac,
            0xaaffd54f,
            0xaaaed581
    );

    private final int COUNTDOWN_RUNNING_TIME = 300;

    private ArrayList<String> poiCategories;
    private HashMap<String, ArrayList<Poi>> data;
    private int expandedItemIndex = -1;
    private Context context;

    public class CategoryHolder extends RecyclerView.ViewHolder{
        public RelativeLayout categoryHeaderLayout;
        public LinearLayout categoryItemExpandedLayout;
        public TextView categoryTv;
        public RecyclerView poisOfCategory;
        public View boxColor;
        public ImageView isExpandedImageView;

        public CategoryHolder(View view){
            super(view);
            categoryTv = view.findViewById(R.id.poi_category_tv);
            categoryHeaderLayout = view.findViewById(R.id.category_header_layout);
            categoryItemExpandedLayout = view.findViewById(R.id.expanded_category);
            poisOfCategory = view.findViewById(R.id.pois_of_category_list);
            boxColor = view.findViewById(R.id.box_color_category);
            isExpandedImageView = view.findViewById(R.id.is_expanded_iv);
        }
    }

    public ResultsGroupAdapter(Context context,
                               HashMap<String, ArrayList<Poi>> data){
        if(data == null) this.poiCategories = new ArrayList<>();
        else {
            this.poiCategories = new ArrayList<>(data.keySet());
        }

        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.results_list_item, parent, false);

        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        String category = poiCategories.get(position).trim();
        holder.categoryTv.setText(category);

        int colorIndex = position % boxColors.size();
        holder.boxColor.setBackgroundColor(boxColors.get(colorIndex));

        holder.categoryHeaderLayout.setOnClickListener(view -> {
            if(expandedItemIndex == holder.getAdapterPosition()) expandedItemIndex = -1;
            else expandedItemIndex = holder.getAdapterPosition();
            notifyDataSetChanged();
        });

        if(expandedItemIndex == position){
            holder.isExpandedImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down));
            holder.categoryItemExpandedLayout.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.expand_item);
            holder.categoryItemExpandedLayout
                    .startAnimation(animation);
        }
        else {
            holder.isExpandedImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up));
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.collapse_item);
            holder.categoryItemExpandedLayout
                    .startAnimation(animation);
            CountDownTimer countDownTimerStatic = new CountDownTimer(COUNTDOWN_RUNNING_TIME, 16) {
                @Override
                public void onTick(long millisUntilFinished) {}
                @Override
                public void onFinish() {
                    holder.categoryItemExpandedLayout.setVisibility(View.GONE);
                }
            };
            countDownTimerStatic.start();
        }

        if(data == null || !data.containsKey(category)) return;

        holder.poisOfCategory.setAdapter(new ResultsPoisRvAdapter(context, data.get(category), boxColors.get(colorIndex)));
        holder.poisOfCategory.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public int getItemCount() {
        return poiCategories == null ? 0 : poiCategories.size();
    }
}

