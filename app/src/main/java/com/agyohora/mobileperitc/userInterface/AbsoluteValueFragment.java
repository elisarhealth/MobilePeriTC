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

import static android.support.design.R.id.info;
//import static com.agyohora.mobileperitc.R.id.info;

public class AbsoluteValueFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_absolutevalues, container, false);

        ArrayList<Integer> resultArray = b.getIntegerArrayList("AbsValues");
        if (b.getString("testEye").equals("Right")) {
            //remove the left elements
            rootView.findViewById(R.id.r53l).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.r54l).setVisibility(View.INVISIBLE);
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
            rootView.findViewById(R.id.r53r).setTag(null);
            rootView.findViewById(R.id.r54r).setTag(null);
            rootView.findViewById(R.id.r53l).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r54l).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.r53l).setTag("r53");
            rootView.findViewById(R.id.r54l).setTag("r54");
            //((TextView) findViewById(R.id.resLayout).findViewWithTag("r53l")).setText(resultArray.get(52));
            //((TextView) findViewById(R.id.resLayout).findViewWithTag("r54l")).setText(resultArray.get(53));
        }
        //((TextView)findViewById(R.id.resLayout).findViewWithTag("r"+String.valueOf(5))).setText(resultArray.get(2));
        for (int i = 1; i < 55; i++) {
            //Log.d("ReportView"," "+resultArray.size());
            ((TextView) rootView.findViewById(R.id.absvals_resLayout).findViewWithTag("r" + i)).setText(String.valueOf(resultArray.get(i - 1)));
            //setting color
            int colVal = resultArray.get(i-1)*6;
            (rootView.findViewById(R.id.absvals_resLayout).findViewWithTag("r"+i)).setBackgroundColor(Color.rgb(colVal,colVal,colVal));
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
        }







        return rootView;
    }
}
