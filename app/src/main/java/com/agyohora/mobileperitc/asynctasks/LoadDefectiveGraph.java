package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Invent on 22-3-18.
 * @see android.os.AsyncTask
 * AsyncTask to create Defective Graph (graph one)
 *
 */

public class LoadDefectiveGraph extends AsyncTask<ArrayList<String>, Void, Bitmap> {

    private Activity activity;


    public LoadDefectiveGraph(Activity activity) {
        this.activity = activity;
    }

    @SafeVarargs
    @Override
    protected final Bitmap doInBackground(ArrayList<String>... params) {
        ArrayList<String> values = params[0];
        ArrayList<String> coordinates = params[1];
        ArrayList<String> pointValues = params[2];
        String pattern = values.get(0);
        String eye = values.get(1);
        View defectivePointGraph = CommonUtils.getDefectivePointColoredGraph(activity, pattern, eye, coordinates, pointValues);

        return CommonUtils.viewToBitmap(defectivePointGraph);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView colorGraph = activity.findViewById(R.id.graph);
        colorGraph.setImageBitmap(bitmap);
        String name = activity.getResources().getResourceEntryName(colorGraph.getId());
        //CommonUtils.saveImage(CommonUtils.getResizedBitmap(bitmap), "Resized", activity.getApplicationContext());
        CommonUtils.saveImage(bitmap, name, activity.getApplicationContext());
    }
}