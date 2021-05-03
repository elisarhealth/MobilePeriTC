package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.ui.ScreeningReportActivity;
import com.agyohora.mobileperitc.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Invent on 22-3-18.
 *
 * @see android.os.AsyncTask
 * AsyncTask to create screening report images
 */

public class LoadScreeningGraphs extends AsyncTask<ArrayList<String>, Void, Bitmap> {

    private Activity activity;


    public LoadScreeningGraphs(Activity activity) {
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
        View defectivePointGraph = CommonUtils.getScreeningDefectivePointGraph(activity, pattern, eye, coordinates, pointValues);

        return CommonUtils.viewToBitmap(defectivePointGraph);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        try {
            ImageView colorGraph = activity.findViewById(R.id.graph);
            if (colorGraph != null)
                colorGraph.setImageBitmap(bitmap);
            String name = activity.getResources().getResourceEntryName(colorGraph.getId());
            CommonUtils.saveImage(bitmap, name, activity.getApplicationContext());
            ScreeningReportActivity.loadingFlag = false;
            activity.invalidateOptionsMenu();
        } catch (Resources.NotFoundException e) {
            Log.e("LoadScreeningGraphs", "Resources NotFoundException");
        } catch (Exception e) {
            Log.e("LoadScreeningGraphs", e.getMessage());
        }
    }
}
