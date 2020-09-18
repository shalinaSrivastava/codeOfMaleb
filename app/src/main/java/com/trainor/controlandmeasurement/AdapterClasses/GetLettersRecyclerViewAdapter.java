package com.trainor.controlandmeasurement.AdapterClasses;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetLettersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<LetterEntity> getLettersList;
    public List<Long> listToBeDownloaded;
    Context context;
    String convertedMeasurementDate = "";
    public ViewModel viewModel;
    SharedPreferenceClass spManager;

    public GetLettersRecyclerViewAdapter(RecyclerView recyclerView, Context con, List<LetterEntity> _list) {
        this.context = con;
        this.getLettersList = _list;
        listToBeDownloaded = new ArrayList<>();
    }

    public void setList(List<LetterEntity> list) {
        this.getLettersList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_downloaded_letters, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder _holder, int position) {
        MyViewHolder holder = (MyViewHolder) _holder;
        final LetterEntity getLettersInfo = getLettersList.get(position);
        holder.company_name.setText(getLettersInfo.companyName);
        holder.measured_by.setText(getLettersInfo.measuredBy);
        holder.measurement_pt_id.setText(getLettersInfo.measurePointID);
        holder.treatment.setText("");
        if (getLettersInfo.locationDescription.equals("anyType{}")) {
            holder.location_des.setText("");
        } else {
            holder.location_des.setText(getLettersInfo.locationDescription);
        }
        try {
            SimpleDateFormat formatDate, formatedDate;
            formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            formatedDate = new SimpleDateFormat("dd.MM.yyyy");
            Date measurementDate = formatDate.parse(getLettersInfo.measurementDate);
            convertedMeasurementDate = formatedDate.format(measurementDate);
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
        holder.measuement_date.setText(convertedMeasurementDate);
        holder.activity_get_download_letters.setTag(getLettersInfo);
        holder.activity_get_download_letters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel = ViewModelProviders.of(MainActivity.getInstance()).get(ViewModel.class);
                final LetterEntity letterinfo = (LetterEntity) view.getTag();
                spManager = new SharedPreferenceClass(context);
                String tag = viewModel.getTag(Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID")), letterinfo.measurePointID, letterinfo.letterID);
                if (tag != null && !tag.equals("uploaded") && !listToBeDownloaded.contains(letterinfo.letterID)) {
                    AlertDialogManager.showDialog(context, context.getResources().getString(R.string.ok), "Cancel", "", context.getResources().getString(R.string.redownload_letter), true, new IClickListener() {
                        @Override
                        public void onClick() {
                            letterinfo.isSelected = "true";
                            holder.check_img.setVisibility(View.VISIBLE);
                            holder.activity_get_download_letters.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                            listToBeDownloaded.add(letterinfo.letterID);
                        }
                    });
                } else {
                    if (!listToBeDownloaded.contains(letterinfo.letterID)) {
                        letterinfo.isSelected = "true";
                        holder.check_img.setVisibility(View.VISIBLE);
                        holder.activity_get_download_letters.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                        listToBeDownloaded.add(letterinfo.letterID);
                    } else {
                        letterinfo.isSelected = "false";
                        holder.check_img.setVisibility(View.GONE);
                        holder.activity_get_download_letters.setBackgroundColor(context.getResources().getColor(R.color.login_page_back));
                        listToBeDownloaded.remove(letterinfo.letterID);
                    }
                }
            }
        });
        if (!listToBeDownloaded.contains(getLettersInfo.letterID)) {
            getLettersInfo.isSelected = "false";
            holder.check_img.setVisibility(View.GONE);
            holder.activity_get_download_letters.setBackgroundColor(context.getResources().getColor(R.color.login_page_back));
        } else {
            getLettersInfo.isSelected = "true";
            holder.check_img.setVisibility(View.VISIBLE);
            holder.activity_get_download_letters.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return getLettersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout activity_get_download_letters;
        public TextView company_name, measured_by, measurement_pt_id, treatment, location_des, measuement_date;
        ImageView check_img;

        public MyViewHolder(LinearLayout view) {
            super(view);
            activity_get_download_letters = view;
            company_name = (TextView) activity_get_download_letters.findViewById(R.id.tv_company_name);
            measured_by = (TextView) activity_get_download_letters.findViewById(R.id.tv_measured_by);
            measurement_pt_id = (TextView) activity_get_download_letters.findViewById(R.id.tv_measurement_pt_id);
            treatment = (TextView) activity_get_download_letters.findViewById(R.id.tv_treatment);
            location_des = (TextView) activity_get_download_letters.findViewById(R.id.tv_location_des);
            measuement_date = (TextView) activity_get_download_letters.findViewById(R.id.tv_measuement_date);
            check_img = (ImageView) activity_get_download_letters.findViewById(R.id.check_img);
        }
    }

    public List<Long> getLettersToBeDownloaded() {
        return listToBeDownloaded;
    }
}