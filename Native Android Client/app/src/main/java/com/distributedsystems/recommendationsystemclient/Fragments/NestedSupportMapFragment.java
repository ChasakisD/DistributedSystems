package com.distributedsystems.recommendationsystemclient.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Since we have the MapFragment inside a NestedScrollView
 * the scroll event will be blocked. So, we must add the fragment inside
 * a Scrolling Frame Layout to wrap it up and enable its scrolling.
 * Also we must disallow the scroll of the nested scroll view when the map
 * is scrolling. This is why the OnTouchListener Interface is created and the
 * onTouch is invoked whenever the user scrolls the map fragment.
 */
public class NestedSupportMapFragment extends SupportMapFragment {
    private OnTouchListener mListener;

    public interface OnTouchListener {
        void onTouch();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance) {
        View root = super.onCreateView(layoutInflater, viewGroup, savedInstance);

        if(getContext() == null) return root;

        ScrollingWrapper frameLayout = new ScrollingWrapper(getActivity());
        frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));

        ViewGroup viewGroupLayout = (ViewGroup) root;
        if(viewGroupLayout == null) return null;

        viewGroupLayout.addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return root;
    }

    public void setListener(OnTouchListener listener) {
        mListener = listener;
    }

    private class ScrollingWrapper extends FrameLayout {
        public ScrollingWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    mListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }

}
