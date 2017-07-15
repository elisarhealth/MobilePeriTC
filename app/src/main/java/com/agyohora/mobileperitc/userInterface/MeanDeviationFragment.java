package com.agyohora.mobileperitc.userInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.agyohora.mobileperitc.R;

import java.util.ArrayList;

/**
 * Created by namputo on 06/07/17.
 */

public class MeanDeviationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle b = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_meandeviation, container, false);


        int toSkip1 = 0;
        int toSkip2 = 0;
        ArrayList<Integer> resultArray = b.getIntegerArrayList("AbsValues");
        ArrayList<Integer> agerelNorm = b.getIntegerArrayList("AgeRelNorm");
        if (b.getString("testEye").equals("Right")) {
            //remove the left elements
            rootView.findViewById(R.id.r53l).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r54l).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r9).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r48).setVisibility(View.VISIBLE);
            toSkip1 = 9;
            toSkip2 = 48;
            rootView.findViewById(R.id.r53l).setTag(null);
            rootView.findViewById(R.id.r54l).setTag(null);
            rootView.findViewById(R.id.r53r).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r54r).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r53r).setTag("r53");
            rootView.findViewById(R.id.r54r).setTag("r54");
            //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53r")).setText(resultArray.get(52));
            //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54r")).setText(resultArray.get(53));
        } else if (b.getString("testEye").equals("Left")) {
            //remove the right element
            rootView.findViewById(R.id.r53r).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r54r).setVisibility(View.INVISIBLE);
            toSkip1 = 22;
            toSkip2 = 35;
            rootView.findViewById(R.id.r53r).setTag(null);
            rootView.findViewById(R.id.r54r).setTag(null);
            rootView.findViewById(R.id.r53l).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r54l).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r53l).setTag("r53");
            rootView.findViewById(R.id.r54l).setTag("r54");
            //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53l")).setText(resultArray.get(52));
            //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54l")).setText(resultArray.get(53));
        }
        Log.d("Length",""+resultArray.size());
        Log.d("Length of age",""+agerelNorm.size());
        //((TextView)findViewById(R.id.resLayout).findViewWithTag("r"+String.valueOf(5))).setText(resultArray.get(2));
        for (int i = 1; i < 55; i++) {
            if(i==toSkip1||i==toSkip2){
                rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.argb(255,255,255,255));
                ((TextView) rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i)).setText("");
            }else {

                //Log.d("ReportView"," "+resultArray.()+" "+agerelNorm.size());
                ((TextView) rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i)).setText(String.valueOf(resultArray.get(i - 1) - agerelNorm.get(i - 1) - 2));
                float resulValFloat = (float) resultArray.get(i - 1);
                //setting color
                int resval = resultArray.get(i - 1) - agerelNorm.get(i - 1) - 2;
                if (-5.0f < resval && resval < 10.0f) {
                    rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(220, 220, 220));
                } else if (-9 < resval && resval <= -5) {
                    rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(200, 200, 200));
                } else if (-30 < resval && resval <= -9) {
                    rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(100, 100, 100));
                } else {
                    rootView.findViewById(R.id.mean_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(50, 50, 50));
                }
            }

        }

            /*if(tesType.equals("Suprathreshold")||tesType.equals("Demo")){
                switch (resultArray.get(i-1)){
                    case ".":
                        ( findViewById(R.id.resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.GRAY);
                        break;
                    case "+":
                        ( findViewById(R.id.resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.LTGRAY);
                        break;
                    case "-":
                        ( findViewById(R.id.resLayout).findViewWithTag("r" + i)).setBackgroundColor(Color.DKGRAY);
                }
            }*/




        return rootView;
    }
}
