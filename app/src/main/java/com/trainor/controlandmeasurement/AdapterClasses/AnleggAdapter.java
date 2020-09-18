package com.trainor.controlandmeasurement.AdapterClasses;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trainor.controlandmeasurement.Activities.AnleggActivity;
import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.R;

import java.util.ArrayList;
import java.util.List;

public class AnleggAdapter extends RecyclerView.Adapter<AnleggAdapter.AnleggViewHolder> {
    List<AssignmentEntity> assignmentList = new ArrayList<>();

    public void setList(List<AssignmentEntity> _list) {
        this.assignmentList = _list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnleggViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anlegg_dialog_adapter_view, parent, false);
        return new AnleggViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnleggViewHolder anleggViewHolder, int position) {
        AssignmentEntity entity = assignmentList.get(position);
        anleggViewHolder.edt_anlegg_text.setText(entity.assignmentName);
        anleggViewHolder.ll.setTag(entity);
        anleggViewHolder.ll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AssignmentEntity assignmentInfo = (AssignmentEntity) v.getTag();
                AnleggActivity.getInstance().selectedAnlegg(assignmentInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class AnleggViewHolder extends RecyclerView.ViewHolder {
        LinearLayout view, ll;
        TextView edt_anlegg_text;

        public AnleggViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            view = itemView;
            edt_anlegg_text = view.findViewById(R.id.edt_anlegg_text);
            ll = view.findViewById(R.id.ll);
        }
    }
}
