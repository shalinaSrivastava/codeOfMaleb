package com.trainor.controlandmeasurement.AdapterClasses;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.R;

import java.util.ArrayList;
import java.util.List;

public class UploadLettersAdapter extends RecyclerView.Adapter<UploadLettersAdapter.ViewHolder> {
    List<LetterEntity> letterList, selectedList;
    Context context;
    ConnectionDetector connectionDetector;

    public UploadLettersAdapter(Context con) {
        this.context = con;
        this.selectedList = new ArrayList<>();
    }

    public void setLetterList(List<LetterEntity> _list) {
        this.letterList = _list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UploadLettersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.upload_letters_adapter, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadLettersAdapter.ViewHolder viewHolder, int position) {
        final LetterEntity _entity = letterList.get(position);
        viewHolder.txt_letter.setText(_entity.measurePointID);
        viewHolder.ll_select.setTag(_entity);
        viewHolder.radio_letter.setTag(_entity);
        viewHolder.ll_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LetterEntity info = (LetterEntity) v.getTag();
                if (selectedList.size() < 5) {
                    if (info.isSelected == null || info.isSelected.equals("") || info.isSelected.equals("false")) {
                        viewHolder.radio_letter.setChecked(true);
                        info.isSelected = "selected";
                        selectedList.add(info);
                    } else {
                        viewHolder.radio_letter.setChecked(false);
                        info.isSelected = "";
                        selectedList.remove(info);
                    }
                } else {
                    if (viewHolder.radio_letter.isChecked() && info.isSelected.equals("selected")) {
                        viewHolder.radio_letter.setChecked(false);
                    } else {
                        AlertDialogManager.showDialog(context, context.getResources().getString(R.string.ok), "", "", context.getResources().getString(R.string.select_5_letter_at_a_time), true, new IClickListener() {
                            @Override
                            public void onClick() {
                                if (viewHolder.radio_letter.isChecked()) {
                                    viewHolder.radio_letter.setChecked(false);
                                }
                            }
                        });
                    }

                    if (selectedList.contains(info)) {
                        viewHolder.radio_letter.setChecked(false);
                        info.isSelected = "";
                        selectedList.remove(info);
                    }
                }
            }
        });

        viewHolder.radio_letter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LetterEntity info = (LetterEntity) v.getTag();
                if (selectedList.size() < 5) {
                    if (info.isSelected == null || info.isSelected.equals("") || info.isSelected.equals("false")) {
                        viewHolder.radio_letter.setChecked(true);
                        info.isSelected = "selected";
                        selectedList.add(info);
                    } else {
                        viewHolder.radio_letter.setChecked(false);
                        info.isSelected = "";
                        selectedList.remove(info);
                    }
                } else {
                    if (viewHolder.radio_letter.isChecked() && info.isSelected.equals("selected")) {
                        viewHolder.radio_letter.setChecked(false);
                    } else {
                        AlertDialogManager.showDialog(context, context.getResources().getString(R.string.ok), "", "", context.getResources().getString(R.string.select_5_letter_at_a_time), true, new IClickListener() {
                            @Override
                            public void onClick() {
                                if (viewHolder.radio_letter.isChecked()) {
                                    viewHolder.radio_letter.setChecked(false);
                                }
                            }
                        });
                    }

                    if (selectedList.contains(info)) {
                        viewHolder.radio_letter.setChecked(false);
                        info.isSelected = "";
                        selectedList.remove(info);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return letterList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_select;
        RadioButton radio_letter;
        TextView txt_letter;

        public ViewHolder(@NonNull RelativeLayout itemView) {
            super(itemView);
            ll_select = itemView.findViewById(R.id.ll_select);
            radio_letter = itemView.findViewById(R.id.radio_letter);
            txt_letter = itemView.findViewById(R.id.txt_letter);
        }
    }

    public List<LetterEntity> getUploadLetterList() {
        return selectedList;
    }
}
