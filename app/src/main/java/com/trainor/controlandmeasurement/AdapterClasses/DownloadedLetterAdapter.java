package com.trainor.controlandmeasurement.AdapterClasses;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.XMLParser;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;
import com.trainor.controlandmeasurement.fragments.GeneralInfoFragment;
import com.trainor.controlandmeasurement.fragments.HomeFragment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DownloadedLetterAdapter extends RecyclerView.Adapter<DownloadedLetterAdapter.MyViewHolder> {
    private List<LetterEntity> getDownloadedLettersList = new ArrayList<>();
    Context context;
    SharedPreferences.Editor generalInfoEditor, variableEditor, measuredValEditor, calculationEditor;
    SharedPreferenceClass spManager;
    SharedPreferences.Editor editor;
    Date measurementDate;
    String ConvertedDate, convertedMeasurementDate;
    ViewModel viewModel;

    public DownloadedLetterAdapter(Context con, ViewModel _viewModel) {
        this.context = con;
        this.viewModel = _viewModel;
        if (editor == null) {
            editor = context.getSharedPreferences("LoginInfoPref", Context.MODE_PRIVATE).edit();
        }
    }

    public void setList(List<LetterEntity> list) {
        this.getDownloadedLettersList = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_downloaded_letters, parent, false);
        spManager = new SharedPreferenceClass(context);
        if (generalInfoEditor == null) {
            generalInfoEditor = context.getSharedPreferences("GeneralInfoPref", Context.MODE_PRIVATE).edit();
        }
        if (variableEditor == null) {
            variableEditor = MainActivity.getInstance().getSharedPreferences("VariablerPref", Context.MODE_PRIVATE).edit();
        }
        if (measuredValEditor == null) {
            measuredValEditor = MainActivity.getInstance().getSharedPreferences("MeasuredValuesPref", Context.MODE_PRIVATE).edit();
        }
        if (calculationEditor == null) {
            calculationEditor = MainActivity.getInstance().getSharedPreferences("CalculationPref", Context.MODE_PRIVATE).edit();
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final LetterEntity getLettersInfo = getDownloadedLettersList.get(position);

        holder.company_name.setText(getLettersInfo.companyName);
        holder.measured_by.setText(getLettersInfo.measuredBy);
        holder.measurement_pt_id.setText(getLettersInfo.measurePointID);
        if (getLettersInfo.Tag != null && getLettersInfo.Tag.equals("Saved")) {
            holder.treatment.setText(context.getResources().getString(R.string.treated));
        } else {
            holder.treatment.setText(context.getResources().getString(R.string.non_treated));
        }
        if (getLettersInfo.locationDescription.equals("anyType{}")) {
            holder.location_des.setText("");
        } else {
            holder.location_des.setText(getLettersInfo.locationDescription);
        }
        if (getLettersInfo.measurementDate.length() > 11) {
            try {
                SimpleDateFormat formatDate, formatedDate;
                formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                formatedDate = new SimpleDateFormat("dd.MM.yyyy");
                measurementDate = formatDate.parse(getLettersInfo.measurementDate);
                convertedMeasurementDate = formatedDate.format(measurementDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            String[] convertedDateArr = getLettersInfo.measurementDate.split("-");
            convertedMeasurementDate = convertedDateArr[2] + "." + convertedDateArr[1] + "." + convertedDateArr[0];
        }

        holder.measuement_date.setText(convertedMeasurementDate);
        holder.ll_open.setTag(getLettersInfo);
        holder.ll_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LetterEntity _entityInfo = (LetterEntity) view.getTag();
                MainActivity mainActivity = MainActivity.getInstance();
                final String measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
                long letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
                /*if (!_entityInfo.measurePointID.equals(measurePointID) || _entityInfo.letterID != letterID) {
                    HomeFragment.getInstance().removeLetterDataRefrences();
                    setDefaultValues(_entityInfo);
                    HomeFragment.getInstance().setActiveCertificate();
                    mainActivity.setTitle();
                    GeneralInfoFragment.getInstance().setDefaultValue();
                }*/
                HomeFragment.getInstance().removeLetterDataRefrences();
                setDefaultValues(_entityInfo);
                HomeFragment.getInstance().setActiveCertificate();
                mainActivity.setTitle();
                GeneralInfoFragment.getInstance().setDefaultValue();
                mainActivity.variablePageClicked = false;
                mainActivity.viewPager.setCurrentItem(1, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getDownloadedLettersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_open;
        public TextView company_name, measured_by, measurement_pt_id, treatment, location_des, measuement_date;

        public MyViewHolder(LinearLayout view) {
            super(view);
            ll_open = view;
            company_name = view.findViewById(R.id.tv_company_name);
            measured_by = view.findViewById(R.id.tv_measured_by);
            measurement_pt_id = view.findViewById(R.id.tv_measurement_pt_id);
            treatment = view.findViewById(R.id.tv_treatment);
            location_des = view.findViewById(R.id.tv_location_des);
            measuement_date = view.findViewById(R.id.tv_measuement_date);
        }
    }

    public void setDefaultValues(LetterEntity _entity) {
        String latitude = "", longitude = "";
        spManager.saveGeneralInfoValueByKeyName("MeasurementPointID", _entity.measurePointID + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("OldMPID", _entity.measurePointID + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("ActiveMeasurementPointID", _entity.measurePointID + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("CompanyName", _entity.companyName + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("AnleggID", _entity.assignmentID + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("altitude", _entity.altitude + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("letterID", _entity.letterID + "", generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("Revision", _entity.revision + "", generalInfoEditor);

        if (Double.parseDouble(_entity.latitude) == 0.0) {
            latitude = _entity.latitude;
        } else {
            latitude = GeneralInfoFragment.getInstance().convertLatLong(Double.parseDouble(_entity.latitude), "Latitude");
        }
        if (Double.parseDouble(_entity.longitude) == 0.0) {
            longitude = _entity.longitude;
        } else {
            longitude = GeneralInfoFragment.getInstance().convertLatLong(Double.parseDouble(_entity.longitude), "Longitude");
        }
        spManager.saveGeneralInfoValueByKeyName("LocationIndication", _entity.locationDescription, generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("latitude", latitude, generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("longitude", longitude, generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("UploadLatitude", _entity.latitude, generalInfoEditor);
        spManager.saveGeneralInfoValueByKeyName("UploadLongitude", _entity.longitude, generalInfoEditor);
        spManager.saveVariablerValueByKeyName("Calculated_1_PoleCurrent", _entity.earthFaultCurrent, variableEditor);
        spManager.saveVariablerValueByKeyName("ControlPerformedBy", _entity.measuredBy, variableEditor);
        if (_entity.voltage.equals("-2")) {
            spManager.saveVariablerValueByKeyName("VoltageID", "0", variableEditor);
        } else {
            spManager.saveVariablerValueByKeyName("VoltageID", _entity.voltage, variableEditor);
        }
        if (_entity.electrode.equals("-2") || _entity.electrode.equals("anyType{}")) {
            spManager.saveVariablerValueByKeyName("ElekrodeSystem", "0", variableEditor);
        } else {
            spManager.saveVariablerValueByKeyName("ElekrodeSystem", _entity.electrode, variableEditor);
        }
        if (_entity.globalEarth.equals("true")) {
            spManager.saveVariablerValueByKeyName("GlobalEarthID", "1", variableEditor);
        } else if (_entity.globalEarth.equals("false")) {
            spManager.saveVariablerValueByKeyName("GlobalEarthID", "0", variableEditor);
        }

        if (_entity.moisture.equals("-2")) {
            spManager.saveVariablerValueByKeyName("Moisture", "0", variableEditor);
        } else {
            spManager.saveVariablerValueByKeyName("Moisture", _entity.moisture, variableEditor);
        }
        if (_entity.earthType.equals("-2")) {
            spManager.saveVariablerValueByKeyName("EarthType", "0", variableEditor);
        } else {
            spManager.saveVariablerValueByKeyName("EarthType", _entity.earthType, variableEditor);
        }
        if (_entity.measurementDate.length() > 11) {
            try {
                SimpleDateFormat formatDate, formatedDate;
                formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                formatedDate = new SimpleDateFormat("dd.MM.yyyy");
                measurementDate = formatDate.parse(_entity.measurementDate);
                ConvertedDate = formatedDate.format(measurementDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            String[] convertedDateArr = _entity.measurementDate.split("-");
            ConvertedDate = convertedDateArr[2] + "." + convertedDateArr[1] + "." + convertedDateArr[0];
        }

        spManager.saveVariablerValueByKeyName("ControlPerformedBy", _entity.measuredBy, variableEditor);
        spManager.saveVariablerValueByKeyName("MeasurementDate", ConvertedDate, variableEditor);
        spManager.saveVariablerValueByKeyName("TransformerStyle", _entity.transformerPerformance, variableEditor);

        spManager.saveGraphValueByKeyName("Avstand_m_EC", _entity.distance, measuredValEditor);
        _entity.directionForward = _entity.directionForward == null ? "" : _entity.directionForward;
        _entity.directionBackward = _entity.directionBackward == null ? "" : _entity.directionBackward;
        spManager.saveGraphValueByKeyName("DirectionForward", _entity.directionForward, measuredValEditor);
        spManager.saveGraphValueByKeyName("DirectionBackward", _entity.directionBackward, measuredValEditor);
        String graphInputdata = _entity.input;
        String localGraphInput = _entity.localElectrodeInput;
        String[] localInput = localGraphInput.split(",");
        String[] items = graphInputdata.split(",");
        if (items.length == 0) {
            graphInputdata = graphInputdata.replaceAll("", "0");
            items = graphInputdata.split(",");
        }
        if (localInput.length == 0) {
            localGraphInput = localGraphInput.replaceAll("", "0");
            localInput = localGraphInput.split(",");
        }
        int count = 0;
        int index = 1;
        for (String item : items) {
            if (item.equals("0")) {
                item = "";
            }
            if (count <= 1) {
                if (count == 0) {
                    spManager.saveGraphValueByKeyName("GlobalEarthIndex_6", item, measuredValEditor);
                } else if (count == 1) {
                    spManager.saveGraphValueByKeyName("GlobalEarthIndex_5", item, measuredValEditor);
                }
            } else if (count > 1) {
                spManager.saveGraphValueByKeyName("GlobalEarthIndex" + index + "", item, measuredValEditor);
                index = index + 1;
            }
            count = count + 1;
        }
        count = 0;
        index = 1;
        for (String item : localInput) {
            if (item.equals("0")) {
                item = "";
            }
            if (count <= 1) {
                if (count == 0) {
                    spManager.saveGraphValueByKeyName("LocalEarthIndex_6", item, measuredValEditor);
                } else if (count == 1) {
                    spManager.saveGraphValueByKeyName("LocalEarthIndex_5", item, measuredValEditor);
                }
            } else if (count > 1) {
                spManager.saveGraphValueByKeyName("LocalEarthIndex" + index + "", item, measuredValEditor);
                index = index + 1;
            }
            count = count + 1;
        }
        _entity.refL = _entity.refL == null ? "" : _entity.refL;
        spManager.saveCalculationValueByKeyName("RefrenceLocalElectrode", _entity.refL, calculationEditor);

        _entity.refT = _entity.refT == null ? "" : _entity.refT;
        spManager.saveCalculationValueByKeyName("RefrenceThroughout", _entity.refT, calculationEditor);

        _entity.basicInstallation = _entity.basicInstallation == null ? "" : _entity.basicInstallation;
        spManager.saveCalculationValueByKeyName("BasicInstallation", _entity.basicInstallation, calculationEditor);

        _entity.clampAmp = _entity.clampAmp == null ? "" : _entity.clampAmp;
        spManager.saveCalculationValueByKeyName("clampAmp", _entity.clampAmp, calculationEditor);

        _entity.clampMeasurement = _entity.clampMeasurement == null ? "" : _entity.clampMeasurement;
        spManager.saveCalculationValueByKeyName("clampMeasurement", _entity.clampMeasurement, calculationEditor);

        _entity.noLocalElectrode = _entity.noLocalElectrode == null ? "" : _entity.noLocalElectrode;
        spManager.saveCalculationValueByKeyName("noLocalElectrode", _entity.noLocalElectrode, calculationEditor);

        //_entity.trainorComments = _entity.trainorComments == null ? "" : _entity.trainorComments;
        _entity.comments = _entity.comments == null ? "" : _entity.comments;
        spManager.saveCalculationValueByKeyName("Comments", _entity.comments, calculationEditor);

        if (_entity.disconnectTime.contains("-2.0") || _entity.disconnectTime.equals("anyType{}")) {
            spManager.saveVariablerValueByKeyName("Disablement", "0", variableEditor);
        } else {
            //int disconnectedId =  XMLParser.getDisconnectID( Double.parseDouble(_entity.disconnectTime + ""),"disconnectTimes");
            //spManager.saveVariablerValueByKeyName("Disablement", disconnectedId+ "", variableEditor);

            spManager.saveVariablerValueByKeyName("Disablement", _entity.disconnectTime + "", variableEditor);
        }

        _entity.highVoltageActionTaken = _entity.highVoltageActionTaken == null ? "false" : _entity.highVoltageActionTaken;
        if (_entity.highVoltageActionTaken.equals("false")) {
            spManager.saveVariablerValueByKeyName("MeasureTaken", "0", variableEditor);
        } else {
            spManager.saveVariablerValueByKeyName("MeasureTaken", "1", variableEditor);
        }
        spManager.saveGeneralInfoValueByKeyName("Undermappe2", _entity.folderId, generalInfoEditor);
        long adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        File file = new File(android.os.Environment.getExternalStorageDirectory(), ("Measurement/" + adminID + "/." + _entity.measurePointID));
        if (file.listFiles() != null) {
            List<File> listFile = Arrays.asList(file.listFiles());
            for (int i = 0; i < listFile.size(); i++) {
                ImageEntity imageInfo = new ImageEntity();
                imageInfo.adminID = adminID;
                imageInfo.letterID = _entity.letterID;
                imageInfo.measurePointID = _entity.measurePointID;
                imageInfo.fileName = listFile.get(i).getName();
                imageInfo.filePath = listFile.get(i).getAbsolutePath();
                imageInfo.description = _entity.measurePointID;
                imageInfo.Tag = "saved";
                List<ImageEntity> _list = viewModel.imageExists(adminID, imageInfo.fileName);
                if (_list == null || _list.size() == 0) {
                    viewModel.insertImageData(imageInfo);
                } else {
                    ImageEntity imageEntity = _list.get(0);
                    if (imageEntity.Tag.equals("saved")) {
                        imageInfo.imageID = imageEntity.imageID;
                        viewModel.updatetImage(imageInfo);
                    }
                }
            }
        }
        if (context != null) {
            Toast.makeText(context, _entity.measurePointID + " " + context.getResources().getString(R.string.has_been_loaded), Toast.LENGTH_SHORT).show();
        }
    }
}