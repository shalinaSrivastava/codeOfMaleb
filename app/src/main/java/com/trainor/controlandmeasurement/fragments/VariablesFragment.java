package com.trainor.controlandmeasurement.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.DecimalDigitsInputFilter;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.HelperClass.XMLParser;
import com.trainor.controlandmeasurement.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class VariablesFragment extends Fragment implements View.OnFocusChangeListener{
    public static VariablesFragment instance;
    public SharedPreferences.Editor editor;
    SharedPreferenceClass spManager;
    private String currentDate;

    @BindView(R.id.spn_facility_type)
    public Spinner spn_facility_type;

    @BindView(R.id.spn_voltage_lvl)
    public Spinner spn_voltage_lvl;

    @BindView(R.id.spn_global_earth)
    public Spinner spn_global_earth;

    @BindView(R.id.spn_disablement)
    public Spinner spn_disablement;

    @BindView(R.id.spn_measures_taken)
    public Spinner spn_measures_taken;

    @BindView(R.id.spn_humidity_lbl)
    public Spinner spn_humidity_lbl;

    @BindView(R.id.spn_soil)
    public Spinner spn_soil;

    @BindView(R.id.spn_electrode_system)
    public Spinner spn_electrode_system;

    @BindView(R.id.edt_control_performed_by)
    EditText edt_control_performed_by;

    @BindView(R.id.edt_transformer_styl)
    EditText edt_transformer_style;

    @BindView(R.id.edt_calculated_pole_grnd_crnt)
    EditText edt_calculated_pole_grnd_crnt;

    @BindView(R.id.iv_date_img)
    ImageView iv_date_img;

    @BindView(R.id.textDate)
    TextView textDate;

    @BindView(R.id.ll_1_pole_current)
    LinearLayout ll_1_pole_current;

    @BindView(R.id.ll_disablement)
    LinearLayout ll_disablement;

    @BindView(R.id.ll_measure_tkn)
    LinearLayout ll_measure_tkn;

    @BindView(R.id.ll_transformer_styl)
    LinearLayout ll_transformer_styl;
    @BindView(R.id.ll_speningsiva)
    LinearLayout ll_speningsiva;

    @BindView(R.id.ll_aadnl_resistence)
    LinearLayout ll_aadnl_resistence;
    @BindView(R.id.spn_aadnl_resistencen)
    public Spinner spn_aadnl_resistencen;

    @BindView(R.id.ll_new_elktrod_trvl_area)
    LinearLayout ll_new_elktrod_trvl_area;
    @BindView(R.id.spn_elktrod_trvl_area)
    public Spinner spn_elktrod_trvl_area;

    @BindView(R.id.ll_disablement_mast)
    LinearLayout ll_disablement_mast;
    @BindView(R.id.spn_disablement_mast)
    public Spinner spn_disablement_mast;

    ArrayAdapter<CharSequence> spenningsniva_adapter, tittak_i_adapter, utkoplingstid_adapter,
            fuktighetsgrad_adapter, jordsmonn_adapter, measure_taken_adapter, elektrodeSystem_adapter, facility_type_adapter,
            additional_resistence_adapter,electrode_trvl_area_adapter,utkoplingstidMast_adapter;

    DatePickerDialog datePickerDialog;

    public VariablesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spManager = new SharedPreferenceClass(getActivity());
        if (editor == null) {
            editor = MainActivity.getInstance().getSharedPreferences("VariablerPref", Context.MODE_PRIVATE).edit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_variables, container, false);
        ButterKnife.bind(VariablesFragment.this, view);
        getControls();
        instance = this;
        return view;
    }

    public synchronized static VariablesFragment getInstance() {
        if (instance == null) {
            instance = new VariablesFragment();
        }
        return instance;
    }

    public void getControls() {
        edt_control_performed_by.setOnFocusChangeListener(this);
        edt_transformer_style.setOnFocusChangeListener(this);
        edt_calculated_pole_grnd_crnt.setOnFocusChangeListener(this);
        callAdapters(spenningsniva_adapter, R.array.spinner_spenningsniva_array, spn_voltage_lvl, "VoltageID");
        callAdapters(tittak_i_adapter, R.array.spinner_global_earth, spn_global_earth, "GlobalEarthID");
        callAdapters(utkoplingstid_adapter, R.array.spinner_utkoplingstid_array, spn_disablement, "Disablement");
        callAdapters(fuktighetsgrad_adapter, R.array.spinner_fuktighetsgrad_array, spn_humidity_lbl, "Moisture");
        callAdapters(jordsmonn_adapter, R.array.spinner_jordsmonn_array, spn_soil, "EarthType");
        callAdapters(measure_taken_adapter, R.array.spinner_meaures_taken_array, spn_measures_taken, "MeasureTaken");
        callAdapters(elektrodeSystem_adapter, R.array.spinner_electrode_sys_array, spn_electrode_system, "ElekrodeSystem");
        //new added 28-12-2020
        callAdapters(facility_type_adapter, R.array.spinner_facility_type_array, spn_facility_type, "FacilityType");
        callAdapters(additional_resistence_adapter, R.array.spinner_addnl_resistence_array, spn_aadnl_resistencen, "AdditionalResistence");
        callAdapters(electrode_trvl_area_adapter, R.array.spinner_meaures_taken_array, spn_elktrod_trvl_area, "TravelArea");
        callAdapters(utkoplingstidMast_adapter, R.array.new_spinner_utkoplingstid_array, spn_disablement_mast, "DisablementMast");

        iv_date_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDatePickerDialog();
                try {
                    if (datePickerDialog == null || !datePickerDialog.isShowing()) {
                        int year = Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(".") + 1));
                        int month = Integer.parseInt(currentDate.substring(currentDate.indexOf(".") + 1, currentDate.lastIndexOf(".")));
                        final int day = Integer.parseInt(currentDate.substring(0, 2));
                        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month + 1;
                                String _day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                                String _month = month < 10 ? "0" + month : month + "";
                                currentDate = _day + "." + _month + "." + year;
                                textDate.setText(currentDate);
                                spManager.saveVariablerValueByKeyName("MeasurementDate", currentDate + "", editor);
                            }
                        }, year, month - 1, day);
                        datePickerDialog.show();
                    }
                } catch (Exception ex) {
                    Log.d("Error", ex.getMessage());
                }
            }
        });
        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDatePickerDialog();
                try {
                    if (datePickerDialog == null || !datePickerDialog.isShowing()) {
                        int year = Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(".") + 1));
                        int month = Integer.parseInt(currentDate.substring(currentDate.indexOf(".") + 1, currentDate.lastIndexOf(".")));
                        final int day = Integer.parseInt(currentDate.substring(0, 2));
                        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month + 1;
                                String _day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";
                                String _month = month < 10 ? "0" + month : month + "";
                                currentDate = _day + "." + _month + "." + year;
                                textDate.setText(currentDate);
                                spManager.saveVariablerValueByKeyName("MeasurementDate", currentDate + "", editor);
                            }
                        }, year, month - 1, day);
                        datePickerDialog.show();
                    }
                } catch (Exception ex) {
                    Log.d("Error", ex.getMessage());
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd.MM.yyyy");
        Date date = calendar.getTime();
        currentDate = dateFormater.format(date);
        setDefaultValue();
        edt_transformer_style.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
        edt_calculated_pole_grnd_crnt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
    }

    public void setDefaultValue() {
        try {
            String measuredBy = spManager.getVariablerValueByKeyName("ControlPerformedBy");
            measuredBy = measuredBy.equals("0") ? spManager.getLoginInfoValueByKeyName("Username") : measuredBy.equals("anyType{}") ? "" : measuredBy;
            String earthFaultCurrent = spManager.getVariablerValueByKeyName("Calculated_1_PoleCurrent");
            spManager.saveVariablerValueByKeyName("ControlPerformedBy", measuredBy, editor);
            edt_control_performed_by.setText(measuredBy);
            edt_control_performed_by.setSelection(measuredBy.length());
            if (!spManager.getVariablerValueByKeyName("MeasurementDate").equals("0")) {
                textDate.setText(spManager.getVariablerValueByKeyName("MeasurementDate"));
            } else {
                textDate.setText(currentDate);
                spManager.saveVariablerValueByKeyName("MeasurementDate", currentDate + "", editor);
            }
            String volID = spManager.getVariablerValueByKeyName("VoltageID");
            int pos = Integer.parseInt(volID.contains(".") ? volID.substring(0, volID.indexOf(".")) : volID);
            spn_voltage_lvl.setSelection(pos);
            String globalearth = spManager.getVariablerValueByKeyName("GlobalEarthID");
            int global_earth_pos = Integer.parseInt(globalearth.contains(".") ? globalearth.substring(0, globalearth.indexOf(".")) : globalearth);
            spn_global_earth.setSelection(global_earth_pos);
            String moisture = spManager.getVariablerValueByKeyName("Moisture");
            int moisture_pos = Integer.parseInt(moisture.contains(".") ? moisture.substring(0, moisture.indexOf(".")) : moisture);
            spn_humidity_lbl.setSelection(moisture_pos);
            String soil = spManager.getVariablerValueByKeyName("EarthType");
            int soil_pos = Integer.parseInt(soil.contains(".") ? soil.substring(0, soil.indexOf(".")) : soil);
            spn_soil.setSelection(soil_pos);
            String measure_taken = spManager.getVariablerValueByKeyName("MeasureTaken");
            int measure_taken_pos = Integer.parseInt(measure_taken.contains(".") ? measure_taken.substring(0, measure_taken.indexOf(".")) : measure_taken);
            spn_measures_taken.setSelection(measure_taken_pos);
            String elektrode = spManager.getVariablerValueByKeyName("ElekrodeSystem");
            int electrodePos = Integer.parseInt(elektrode.contains(".") ? elektrode.substring(0, elektrode.indexOf(".")) : elektrode);
            spn_electrode_system.setSelection(electrodePos);
            String transformerPerformanceVal = spManager.getVariablerValueByKeyName("TransformerStyle");
            edt_transformer_style.setText(transformerPerformanceVal);
            edt_transformer_style.setSelection(transformerPerformanceVal.length());
            String _earthFaultCurrent = earthFaultCurrent.equals("0") ? "" : earthFaultCurrent;
            edt_calculated_pole_grnd_crnt.setText(_earthFaultCurrent);
            edt_calculated_pole_grnd_crnt.setSelection(_earthFaultCurrent.length());
            // new added facilitytype
            String facilityTypeID = spManager.getVariablerValueByKeyName("FacilityType");
            int facility_pos = Integer.parseInt(facilityTypeID.contains(".") ? facilityTypeID.substring(0, facilityTypeID.indexOf(".")) : facilityTypeID);
            spn_facility_type.setSelection(facility_pos);
            String addnlResistenceId = spManager.getVariablerValueByKeyName("AdditionalResistence");
            int addnl_resistence_pos = Integer.parseInt(addnlResistenceId.contains(".") ? addnlResistenceId.substring(0, addnlResistenceId.indexOf(".")) : addnlResistenceId);
            spn_aadnl_resistencen.setSelection(addnl_resistence_pos);
            String travelAreaId = spManager.getVariablerValueByKeyName("TravelArea");
            int travel_pos = Integer.parseInt(travelAreaId.contains(".") ? travelAreaId.substring(0, travelAreaId.indexOf(".")) : travelAreaId);
            spn_elktrod_trvl_area.setSelection(travel_pos);
            String disconnectMastTypeID = spManager.getVariablerValueByKeyName("DisablementMast");
            int mast_pos = Integer.parseInt(disconnectMastTypeID.contains(".") ? disconnectMastTypeID.substring(0, disconnectMastTypeID.indexOf(".")) : disconnectMastTypeID);
            spn_disablement_mast.setSelection(mast_pos);

            String voltageID = spManager.getVariablerValueByKeyName("VoltageID");
            hideUnhide(voltageID);
            String disablementPos = spManager.getVariablerValueByKeyName("Disablement");
            if (disablementPos.equals("0")) {
                spn_disablement.setSelection(0);
            } else {
                //int _pos = XMLParser.getDisconnectName(disablementPos, "disconnectTimes", "Test");

                int _pos = XMLParser.getDisconnectName(disablementPos, "disconnectTimes", "ID");
                if (_pos == 0) {
                    _pos = XMLParser.getDisconnectName(disablementPos, "disconnectTimes", "Name");
                }
                spn_disablement.setSelection(_pos);
            }



        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
    }

    public void callAdapters(ArrayAdapter<CharSequence> adapterName, int textArrayResId,
                             final Spinner spinnerName, final String sharedPrefKeyName) {
        adapterName = ArrayAdapter.createFromResource(getActivity(),
                textArrayResId, R.layout.textview_with_font_change);
        spinnerName.setAdapter(adapterName);
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (sharedPrefKeyName.equals("VoltageID")) {
                    hideUnhide(position + "");
                }
                //new Facility Type 28-12-2020
                if (sharedPrefKeyName.equals("FacilityType")) {
                    spManager.saveVariablerValueByKeyName("FacilityType", position+ "", editor);
                    if(position==1){
                        spn_voltage_lvl.setSelection(2);
                        ll_speningsiva.setVisibility(GONE);
                        ll_aadnl_resistence.setVisibility(View.VISIBLE);
                        ll_measure_tkn.setVisibility(GONE);
                        ll_disablement_mast.setVisibility(View.VISIBLE);
                        ll_disablement.setVisibility(GONE);
                        ll_1_pole_current.setVisibility(View.VISIBLE);
                        ll_new_elktrod_trvl_area.setVisibility(View.VISIBLE);
                        ll_transformer_styl.setVisibility(GONE);
                    }else{
                        ll_speningsiva.setVisibility(View.VISIBLE);
                        ll_aadnl_resistence.setVisibility(GONE);
                        ll_measure_tkn.setVisibility(View.VISIBLE);
                        ll_disablement_mast.setVisibility(GONE);
                        ll_disablement.setVisibility(View.VISIBLE);
                        ll_1_pole_current.setVisibility(View.VISIBLE);
                        ll_new_elktrod_trvl_area.setVisibility(GONE);
                    }

                }
                if(sharedPrefKeyName.equals("AdditionalResistence")){
                    String facilityTypeVoltage="0";
                    if(position==0){
                        facilityTypeVoltage = "0";
                    }else if(position==1){
                        facilityTypeVoltage = "1750";
                    }else if(position==2){
                        facilityTypeVoltage = "4000";
                    }else if(position==3){
                        facilityTypeVoltage = "7000";
                    }
                    spManager.saveVariablerValueByKeyName("AdditionalResistenceVoltage", facilityTypeVoltage, editor);
                }
                if(sharedPrefKeyName.equals("DisablementMast")){
                    if(position<2){
                        ll_new_elktrod_trvl_area.setVisibility(View.VISIBLE);
                    }else{
                        ll_new_elktrod_trvl_area.setVisibility(GONE);
                    }
                }
                MainActivity.from = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edt_control_performed_by:
                if (!hasFocus) {
                    String controlPerformedBy = edt_control_performed_by.getText().toString().equals("") ? "" : edt_control_performed_by.getText().toString();
                    spManager.saveVariablerValueByKeyName("ControlPerformedBy", controlPerformedBy + "", editor);
                }
                break;
            case R.id.edt_transformer_styl:
                if (!hasFocus) {
                    String transformerStyle = edt_transformer_style.getText().toString().equals("") ? "0" : edt_transformer_style.getText().toString();
                    if (!transformerStyle.equals(".")) {
                        spManager.saveVariablerValueByKeyName("TransformerStyle", transformerStyle + "", editor);
                    } else {
                        edt_transformer_style.setText("");
                    }
                }
                break;
            case R.id.edt_calculated_pole_grnd_crnt:
                if (!hasFocus) {
                    String calculated_1_PoleCurrent = edt_calculated_pole_grnd_crnt.getText().toString().equals("") ? "0" : edt_calculated_pole_grnd_crnt.getText().toString();
                    if (!calculated_1_PoleCurrent.equals(".")) {
                        double val = Double.parseDouble(calculated_1_PoleCurrent);
                        if (val > 999) {
                            AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_999), false, new IClickListener() {
                                @Override
                                public void onClick() {
                                    spManager.saveVariablerValueByKeyName("Calculated_1_PoleCurrent", "", editor);
                                    edt_calculated_pole_grnd_crnt.setText("");
                                }
                            });
                        } else {
                            spManager.saveVariablerValueByKeyName("Calculated_1_PoleCurrent", calculated_1_PoleCurrent + "", editor);
                        }
                    } else {
                        edt_calculated_pole_grnd_crnt.setText("");
                        spManager.saveVariablerValueByKeyName("Calculated_1_PoleCurrent", "", editor);
                    }
                }
                break;
        }
    }

    public void hideUnhide(String voltageID) {
        String facilityTypeID = spManager.getVariablerValueByKeyName("FacilityType");
        if(!facilityTypeID.equals("1")){
            if (voltageID.equals("0") || voltageID.equals("3")) {
                ll_transformer_styl.setVisibility(GONE);
                ll_measure_tkn.setVisibility(GONE);
                ll_disablement.setVisibility(View.VISIBLE);
                ll_1_pole_current.setVisibility(View.VISIBLE);
            } else if (voltageID.equals("1")) {
                ll_transformer_styl.setVisibility(View.VISIBLE);
                ll_measure_tkn.setVisibility(GONE);
                ll_disablement.setVisibility(GONE);
                ll_1_pole_current.setVisibility(GONE);
            } else if (voltageID.equals("2")) {
                ll_transformer_styl.setVisibility(GONE);
                ll_disablement.setVisibility(View.VISIBLE);
                ll_measure_tkn.setVisibility(View.VISIBLE);
                ll_1_pole_current.setVisibility(View.VISIBLE);
            }
        }

    }

    public int selectedVoltagePosition() {
        if (spn_voltage_lvl == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("VoltageID"));
            return pos;
        }
        return spn_voltage_lvl.getSelectedItemPosition();
    }

    public int selectedGlobalEarthPosition() {
        if (spn_global_earth == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("GlobalEarthID"));
            return pos;
        }
        return spn_global_earth.getSelectedItemPosition();
    }

    public double selectedDisablementPosition() {
        if (spn_disablement == null) {
            double pos = Double.parseDouble(spManager.getVariablerValueByKeyName("Disablement"));
            return pos;
        }
        int id = spn_disablement.getSelectedItemPosition();
        //double seconds = XMLParser.getDisconnectSecond(id + "", "disconnectTimes");
        return id;
    }

    public int selectedMeasurementPosition() {
        if (spn_measures_taken == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("MeasureTaken"));
            return pos;
        }
        return spn_measures_taken.getSelectedItemPosition();
    }

    public int selectedHumidityPosition() {
        if (spn_humidity_lbl == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("Moisture"));
            return pos;
        }
        return spn_humidity_lbl.getSelectedItemPosition();
    }

    public int selectedSoilPosition() {
        if (spn_soil == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("EarthType"));
            return pos;
        }
        return spn_soil.getSelectedItemPosition();
    }

    public int selectedElektrodePosition() {
        if (spn_electrode_system == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("ElekrodeSystem"));
            return pos;
        }
        return spn_electrode_system.getSelectedItemPosition();
    }

    public int selectedHighVoltageActionTakenPosition() {
        if (spn_measures_taken == null) {
            return 0;
        }
        return spn_measures_taken.getSelectedItemPosition();
    }

    // new facility type 28-12-2020
    public int selectedFacilityTypePosition() {
        if (spn_facility_type == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("FacilityType"));
            return pos;
        }
        return spn_facility_type.getSelectedItemPosition();
    }
    public int selectedAddnlResistencePosition() {
        if (spn_aadnl_resistencen == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("AdditionalResistence"));
            return pos;
        }
        return spn_aadnl_resistencen.getSelectedItemPosition();
    }
    public int selectedTravelAreaPosition() {
        if (spn_elktrod_trvl_area == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("TravelArea"));
            return pos;
        }
        return spn_elktrod_trvl_area.getSelectedItemPosition();
    }
    public int selectedDisablementMastPosition() {
        if (spn_disablement_mast == null) {
            int pos = Integer.parseInt(spManager.getVariablerValueByKeyName("DisablementMast"));
            return pos;
        }
        int id = spn_disablement_mast.getSelectedItemPosition();
        //double seconds = XMLParser.getDisconnectSecond(id + "", "disconnectTimes");
        return id;
    }

}