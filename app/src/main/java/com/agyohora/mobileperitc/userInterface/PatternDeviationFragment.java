package com.agyohora.mobileperitc.userInterface;

/**
 * Created by namputo on 06/07/17.
 */

import com.agyohora.mobileperitc.R;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PatternDeviationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_patterndeviation, container, false);

        Bundle b = getArguments();
        ArrayList<Integer> resultArray = b.getIntegerArrayList("PatternDeviation");
        int toSkip1 = 0;
        int toSkip2 = 0;

        if (b.getString("testEye").equals("Right")) {
            //remove the left elements
            rootView.findViewById(R.id.r53l).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r54l).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r9).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r48).setVisibility(View.INVISIBLE);
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
            rootView.findViewById(R.id.r8).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r47).setVisibility(View.INVISIBLE);
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
        Log.d("Lenth",""+resultArray.size());
        //((TextView)findViewById(R.id.resLayout).findViewWithTag("r"+String.valueOf(5))).setText(resultArray.get(2));
        for (int i = 1; i < 55; i++) {
            if (i == toSkip1 || i == toSkip2) {
                rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.argb(255, 255, 255, 255));
                ((TextView) rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i)).setText("");
            } else {
                //Log.d("ReportView"," "+resultArray.()+" "+agerelNorm.size());
                ((TextView) rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i)).setText(String.valueOf(resultArray.get(i - 1)));
                float resulValFloat = (float) resultArray.get(i - 1);
                //setting color

                int resval = resultArray.get(i - 1);
                if (-5.0f < resval && resval < 10.0f) {
                    rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(250, 250, 250));
                } else if (-7 < resval && resval <= -5) {
                    rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(200, 200, 200));
                } else if (-30 < resval && resval <= -7) {
                    rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(100, 100, 100));
                } else {
                    rootView.findViewById(R.id.pattern_resLayout).findViewWithTag("r" + i).setBackgroundColor(Color.rgb(50, 50, 50));
                }
            }
        } /*if(tesType.equals("Suprathreshold")||tesType.equals("Demo")){
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
