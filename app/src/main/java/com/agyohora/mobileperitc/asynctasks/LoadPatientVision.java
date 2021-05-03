package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.agyohora.mobileperitc.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Invent on 22-3-18.
 * @see android.os.AsyncTask
 * AsyncTask to create patient's copy graph(overlayed taj-mahal)
 */

public class LoadPatientVision extends AsyncTask<ArrayList<String>, Void, Bitmap> {
    private Activity activity;
    private ImageView imageView;
    private ProgressBar progressBar;
    private String  strategy;

    public LoadPatientVision(Activity activity, ImageView imageView, ProgressBar progressBar, String eye, String strategy) {
        this.activity = activity;
        this.imageView = imageView;
        this.progressBar = progressBar;
        this.strategy = strategy;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @SafeVarargs
    @Override
    protected final Bitmap doInBackground(ArrayList<String>... params) {
        ArrayList<String> values = params[0];
        ArrayList<String> coordinates = params[1];
        ArrayList<String> pointValues = params[2];
        String pattern = values.get(0);
        String eye = values.get(1);
        Log.d("From", "LoadPatientVision");
        View greyScale = CommonUtils.getDefectivePointGreyGraph(activity, pattern, eye, coordinates, pointValues, strategy);
        return CommonUtils.viewToBitmap(greyScale);
    }

    @Override
    protected void onPostExecute(Bitmap grey) {
        String name = activity.getResources().getResourceEntryName(imageView.getId());
        Log.d("SavedName", " " + name);
        CommonUtils.saveImage(grey, "Original", activity.getApplicationContext());
        imageView.setImageBitmap(grey);
        progressBar.setVisibility(View.GONE);
    }
}
