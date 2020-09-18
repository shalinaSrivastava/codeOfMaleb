package com.trainor.controlandmeasurement.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trainor.controlandmeasurement.Activities.AnleggActivity;
import com.trainor.controlandmeasurement.Activities.CompanyListActivity;
import com.trainor.controlandmeasurement.Activities.DownloadLetterActivity;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.CompaniesEntity;
import com.trainor.controlandmeasurement.R;

import java.util.ArrayList;
import java.util.List;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.CompanyListViewHolder> {
    List<CompaniesEntity> companiesList = new ArrayList<>();
    Context context;
    SharedPreferenceClass spManager;
    String loginToken;

    public CompanyListAdapter(Context con) {
        this.context = con;
        spManager = new SharedPreferenceClass(con);
        loginToken = spManager.getLoginInfoValueByKeyName("Token");
    }

    public void setList(List<CompaniesEntity> list){
        this.companiesList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompanyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anlegg_dialog_adapter_view, parent, false);
        return new CompanyListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyListViewHolder viewHolder, int position) {
        final CompaniesEntity getCompaniesInfo = companiesList.get(position);
        viewHolder.txt_companyName.setText(getCompaniesInfo.companyName);
        viewHolder.ll.setTag(getCompaniesInfo);
        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) v;
                CompaniesEntity companiesEntity = (CompaniesEntity) linearLayout.getTag();
                CompanyListActivity.getInstance().getSelectedCompany(companiesEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return companiesList.size();
    }




    public class CompanyListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout view, ll;
        TextView txt_companyName;

        public CompanyListViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            view = itemView;
            txt_companyName = view.findViewById(R.id.edt_anlegg_text);
            ll = view.findViewById(R.id.ll);
        }
    }



}
