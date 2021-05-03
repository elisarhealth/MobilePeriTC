package com.agyohora.mobileperitc.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.model.ClickerHistoryModel;
import com.agyohora.mobileperitc.utils.CommonUtils;

import java.text.Format;
import java.util.ArrayList;

public class ClickerHistoryAdapter extends RecyclerView.Adapter<ClickerHistoryAdapter.ViewHolder> {
    private ArrayList<ClickerHistoryModel> mArrayList;
    private ArrayList<ClickerHistoryModel> mFilteredList;
    private Activity activity;
    private Format formatter;

    public ClickerHistoryAdapter(ArrayList<ClickerHistoryModel> arrayList, Activity activity) {
        mArrayList = arrayList;
        mFilteredList = arrayList;
        this.activity = activity;
        formatter = CommonUtils.getDateTimeFormat();
    }

    @NonNull
    @Override
    public ClickerHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_clicker_history, viewGroup, false);
        return new ClickerHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tv_id.setText(mFilteredList.get(i).getId());
        viewHolder.tv_nature.setText(mFilteredList.get(i).getNature());
        viewHolder.tv_service_no.setText(mFilteredList.get(i).getService_no());
        viewHolder.tv_serial_no.setText(mFilteredList.get(i).getSerial_number());
        viewHolder.tv_count.setText(mFilteredList.get(i).getCount());
        viewHolder.tv_created_date.setText(mFilteredList.get(i).getCreateDate());

    }


    @Override
    public int getItemCount() {
        return this.mFilteredList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_id, tv_nature, tv_service_no, tv_serial_no, tv_count, tv_created_date;

        ViewHolder(View view) {
            super(view);
            tv_id = view.findViewById(R.id.tv_id);
            tv_nature = view.findViewById(R.id.tv_nature);
            tv_service_no = view.findViewById(R.id.tv_service_no);
            tv_serial_no = view.findViewById(R.id.tv_serial_no);
            tv_count = view.findViewById(R.id.tv_count);
            tv_created_date = view.findViewById(R.id.tv_created_date);
        }
    }


}