package com.trainor.controlandmeasurement.AdapterClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trainor.controlandmeasurement.Activities.UndermappeActivity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.R;

import java.util.ArrayList;
import java.util.List;

public class UndermappeAdapter extends RecyclerView.Adapter<UndermappeAdapter.UndermappeViewHolder> {
    List<FolderEntity> assignmentList = new ArrayList<>();

    public void setList(List<FolderEntity> _list) {
        this.assignmentList = _list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UndermappeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.undermappe_dialog_adapter_view, parent, false);
        return new UndermappeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UndermappeViewHolder anleggViewHolder, int position) {
        FolderEntity entity = assignmentList.get(position);
        anleggViewHolder.edt_undermappe_text.setText(entity.name);
        anleggViewHolder.ll.setTag(entity);
        anleggViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FolderEntity folderEntity = (FolderEntity) v.getTag();
                UndermappeActivity.getInstance().getSelectedFolder(folderEntity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class UndermappeViewHolder extends RecyclerView.ViewHolder {
        LinearLayout view, ll;
        TextView edt_undermappe_text;

        public UndermappeViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            view = itemView;
            edt_undermappe_text = (TextView) view.findViewById(R.id.edt_undermappe_text);
            ll = (LinearLayout) view.findViewById(R.id.ll);
        }
    }
}
