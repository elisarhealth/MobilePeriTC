package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.agyohora.mobileperitc.utils.CommonUtils;

import java.util.ArrayList;

public class PD_Plot_Ten_Dash_Two  extends AsyncTask<ArrayList<String>, Void, Bitmap> {
    private Activity activity;
    private ImageView imageView;

    public PD_Plot_Ten_Dash_Two(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    @SafeVarargs
    @Override
    protected final Bitmap doInBackground(ArrayList<String>... params) {
        ArrayList<String> values = params[0];
        ArrayList<String> result = params[1];
        ArrayList<String> result_values = params[2];
        ArrayList<String> result_deviation = params[3];
        String eye = values.get(0);
        String pattern = values.get(1);

        View graphTwo = CommonUtils.getPD_Plot(activity, eye, result, result_values,result_deviation);
        return CommonUtils.viewToBitmap(graphTwo);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        String name = activity.getResources().getResourceEntryName(imageView.getId());
        CommonUtils.saveImage(bitmap, name, activity.getApplicationContext());
        imageView.setImageBitmap(bitmap);
    }
}