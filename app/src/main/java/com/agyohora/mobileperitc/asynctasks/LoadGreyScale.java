package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.agyohora.mobileperitc.utils.CommonUtils;

/**
 * Created by Invent on 12-3-18.
 * @see android.os.AsyncTask
 * AsyncTask to create GreyScale graph
 */

public class LoadGreyScale extends AsyncTask<double[][][], Void, Bitmap> {
    private Activity activity;
    private ImageView imageView;
    private ProgressBar progressBar;
    private String eye;

    public LoadGreyScale(Activity activity, ImageView imageView, ProgressBar progressBar, String eye) {
        this.activity = activity;
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.eye = eye;
    }

    @Override
    protected Bitmap doInBackground(double[][][]... params) {
        double[][][] quadrants = params[0];
        View greyScale = CommonUtils.createGreyScale(quadrants, activity, eye);
        return CommonUtils.viewToBitmap(greyScale);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        String name = activity.getResources().getResourceEntryName(imageView.getId());
        CommonUtils.saveImage(bitmap, name, activity.getApplicationContext());
        imageView.setImageBitmap(bitmap);
        progressBar.setVisibility(View.GONE);
    }
}