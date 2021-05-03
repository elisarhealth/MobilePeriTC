package com.agyohora.mobileperitc.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.asynctasks.GetResultData;
import com.agyohora.mobileperitc.asynctasks.UpdateTestRecord;
import com.agyohora.mobileperitc.interfaces.AsyncDbDeleteRecordTask;
import com.agyohora.mobileperitc.interfaces.AsyncDbTaskResult;
import com.agyohora.mobileperitc.model.ResultModel;
import com.agyohora.mobileperitc.utils.CommonUtils;

import java.text.Format;
import java.util.ArrayList;


/**
 * Created by Invent on 28-1-18.
 * ResultDataAdapter is used to bind data to the cardview of Report lists
 */

@SuppressWarnings("unchecked")
public class ResultDataAdapter extends RecyclerView.Adapter<ResultDataAdapter.ViewHolder> implements Filterable {
    private ArrayList<ResultModel> mArrayList;
    private ArrayList<ResultModel> mFilteredList;
    private Activity activity;
    private Format formatter;

    public ResultDataAdapter(ArrayList<ResultModel> arrayList, Activity activity) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        this.activity = activity;
        formatter = CommonUtils.getDateTimeFormat();
    }

    @NonNull
    @Override
    public ResultDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        view.setOnClickListener(mOnClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultDataAdapter.ViewHolder viewHolder, int i) {
        String date = formatter.format(mFilteredList.get(i).getTestDate());
        if (mFilteredList.get(i).getPatSex().equals("Male"))
            viewHolder.sex.setImageResource(R.drawable.male_grey);
        else
            viewHolder.sex.setImageResource(R.drawable.female_grey);
        viewHolder.tv_test_id.setText(mFilteredList.get(i).getId());
        viewHolder.tv_test_perimetry_version.setText(mFilteredList.get(i).getVersion());
        viewHolder.tv_pat_name.setText(mFilteredList.get(i).getPatName());
        viewHolder.tv_pat_mrn.setText(mFilteredList.get(i).getPatMRN());
        viewHolder.tv_pat_mobile.setText(mFilteredList.get(i).getPatMobile());
        viewHolder.tv_pat_test_eye.setText(mFilteredList.get(i).getTestEye());
        viewHolder.tv_test_suggestion.setText(mFilteredList.get(i).getTestSuggestion());
        viewHolder.tv_test_time.setText(date);
    }

    @Override
    public int getItemCount() {
        return this.mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<ResultModel> filteredList = new ArrayList<>();

                    for (ResultModel resultModel : mArrayList) {

                        if (resultModel.getPatName().toLowerCase().contains(charString) || resultModel.getPatMRN().toLowerCase().contains(charString) || resultModel.getPatMobile().contains(charString) || resultModel.getPatMobileUnformatted().contains(charString)) {

                            filteredList.add(resultModel);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<ResultModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_pat_name, tv_test_id, tv_pat_mrn, tv_pat_mobile, tv_pat_test_eye, tv_test_time, tv_test_suggestion, tv_test_perimetry_version;
        ImageView sex;

        ViewHolder(View view) {
            super(view);

            sex = view.findViewById(R.id.PatientSex);
            tv_test_id = view.findViewById(R.id.tv_test_id);
            tv_test_perimetry_version = view.findViewById(R.id.tv_test_perimetry_version);
            tv_pat_name = view.findViewById(R.id.tv_pat_name);
            tv_pat_mrn = view.findViewById(R.id.tv_pat_mrn);
            tv_pat_mobile = view.findViewById(R.id.tv_pat_mobile);
            tv_pat_test_eye = view.findViewById(R.id.tv_pat_test_eye);
            tv_test_time = view.findViewById(R.id.tv_test_time);
            tv_test_suggestion = view.findViewById(R.id.tv_test_suggestion);
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView view = v.findViewById(R.id.tv_test_id);
            TextView version_tv = v.findViewById(R.id.tv_test_perimetry_version);
            TextView tv_test_time = v.findViewById(R.id.tv_test_time);
            final String id = view.getText().toString();
            final String version = version_tv.getText().toString();
            final String time = tv_test_time.getText().toString();
            Log.e("ResultDataAdapter", " version " + version);
            if (version.equals("2")) {
                new GetResultData(new AsyncDbTaskResult() {
                    @Override
                    public void onProcessFinish(Bundle patientTestResults) {
                    }
                }, activity).execute(id, time);
            } else if (version.equals("1")) {
                new UpdateTestRecord(new AsyncDbDeleteRecordTask() {
                    @Override
                    public boolean onProcessFinish(boolean bool) {
                        if (bool) {
                            new GetResultData(new AsyncDbTaskResult() {
                                @Override
                                public void onProcessFinish(Bundle patientTestResults) {
                                }
                            }, activity).execute(id, time);
                        }
                        return bool;
                    }
                }, activity).execute(id, time);
            }

        }
    };

}
