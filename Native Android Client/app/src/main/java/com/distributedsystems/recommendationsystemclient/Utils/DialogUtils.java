package com.distributedsystems.recommendationsystemclient.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.distributedsystems.recommendationsystemclient.R;

@SuppressLint("InflateParams")
public class DialogUtils {

    public static AlertDialog ShowNetworkErrorDialog(Activity activity){
        if(activity == null) return null;

        return new AlertDialog.Builder(activity)
                .setView(activity.getLayoutInflater().inflate(R.layout.network_error_view, null))
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    dialog.cancel();
                })
                .setCancelable(false)
                .show();
    }

    public static AlertDialog ShowLoadingDialog(Activity activity){
        if(activity == null) return null;

        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(activity.getLayoutInflater().inflate(R.layout.material_loading_view, null))
                .setCancelable(false)
                .show();

        if(alertDialog.getWindow() == null) return null;

        /* Force the layout to wrap content and not fill the screen */
        LinearLayout container = alertDialog.getWindow().findViewById(R.id.loading_linear_layout);
        container.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                alertDialog.getWindow().setLayout(container.getWidth(),
                        WindowManager.LayoutParams.WRAP_CONTENT);
                container.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
            }
        });

        return alertDialog;
    }
}
