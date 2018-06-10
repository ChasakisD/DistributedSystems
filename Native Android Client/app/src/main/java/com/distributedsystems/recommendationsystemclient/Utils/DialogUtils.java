package com.distributedsystems.recommendationsystemclient.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.AlertDialog;

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

        return new AlertDialog.Builder(activity)
                .setView(activity.getLayoutInflater().inflate(R.layout.material_loading_view, null))
                .setCancelable(false)
                .show();
    }
}
