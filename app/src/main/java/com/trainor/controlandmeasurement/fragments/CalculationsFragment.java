package com.trainor.controlandmeasurement.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.Calc.Utils;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.DecimalDigitsInputFilter;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.XMLParser;
import com.trainor.controlandmeasurement.R;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalculationsFragment extends Fragment implements View.OnFocusChangeListener {
    int numberOfDistances = 10;
    int estimatedResistanceElectrodeAccuracy = 1000;
    SharedPreferenceClass spManager;
    SharedPreferences.Editor editor;

    @BindView(R.id.edt_measured_referance_local_electrode)
    EditText edt_measured_referance_local_electrode;
    @BindView(R.id.edt_measured_referance_throughout)
    EditText edt_measured_referance_throughout;
    @BindView(R.id.edt_basic_calulation)
    EditText edt_basic_calulation;
    @BindView(R.id.edt_measure_with_pliers_resistance)
    EditText edt_measure_with_pliers_resistance;
    @BindView(R.id.edt_measure_with_pliers_voltage)
    EditText edt_measure_with_pliers_voltage;
    @BindView(R.id.edt_consistant_local_without_electrode)
    EditText edt_consistant_local_without_electrode;
    @BindView(R.id.edt_comments)
    EditText edt_comments;
    @BindView(R.id.ll_bero_mask)
    LinearLayout ll_bero_mask;
    @BindView(R.id.ll_beregnet_berøringsspenning)
    LinearLayout ll_beregnet_berøringsspenning;
    @BindView(R.id.ll_malt_ref_gjenomgnd)
    LinearLayout ll_malt_ref_gjenomgnd;
    @BindView(R.id.ll_max_allowed)
    LinearLayout ll_max_allowed;
    @BindView(R.id.ll_mask_tiltatt_overgangestand)
    LinearLayout ll_mask_tiltatt_overgangestand;
    @BindView(R.id.ll_calculated)
    LinearLayout ll_calculated;
    @BindView(R.id.ll_half_rounded)
    LinearLayout ll_half_rounded;
    @BindView(R.id.iv_status)
    ImageView iv_status;
    @BindView(R.id.txt_referanseavstand)
    TextView txt_referanseavstand;
    @BindView(R.id.txt_motstand_elektrode)
    TextView txt_motstand_elektrode;
    @BindView(R.id.txt_maks_tillatt)
    TextView txt_maks_tillatt;
    @BindView(R.id.txt_beregnet_beroringspenning)
    TextView txt_beregnet_beroringspenning;
    @BindView(R.id.txt_beroringspening_maks)
    TextView txt_beroringspening_maks;
    @BindView(R.id.txt_maks_tillatt_overgangsmotstand)
    TextView txt_maks_tillatt_overgangsmotstand;
    @BindView(R.id.txt_beregnet_jordpotensialheving_ue)
    TextView txt_beregnet_jordpotensialheving_ue;
    @BindView(R.id.txt_status_string)
    TextView txt_status_string;

    @BindView(R.id.ll5)
    LinearLayout ll5;
    @BindView(R.id.ll11)
    LinearLayout ll11;

    @BindView(R.id.ll2)
    LinearLayout ll2;

    List<String> climbFactorNames;
    HashMap<String, Double> climbFactorHashMap;
    double refdistMinU, refdistMaxU;
    String graphSavedValues;

    public CalculationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spManager = new SharedPreferenceClass(MainActivity.getInstance());
        if (editor == null) {
            editor = MainActivity.getInstance().getSharedPreferences("CalculationPref", Context.MODE_PRIVATE).edit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculations, container, false);
        ButterKnife.bind(CalculationsFragment.this, view);
        getControls();
        return view;
    }

    public void getControls() {
        edt_measured_referance_local_electrode.setOnFocusChangeListener(this);
        edt_measured_referance_throughout.setOnFocusChangeListener(this);
        edt_basic_calulation.setOnFocusChangeListener(this);
        edt_measure_with_pliers_resistance.setOnFocusChangeListener(this);
        edt_measure_with_pliers_voltage.setOnFocusChangeListener(this);
        edt_consistant_local_without_electrode.setOnFocusChangeListener(this);
        edt_comments.setOnFocusChangeListener(this);
        edt_measured_referance_local_electrode.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 3)});
        edt_measured_referance_throughout.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 3)});
        edt_basic_calulation.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 3)});
        //edt_measure_with_pliers_resistance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        //edt_measure_with_pliers_voltage.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        edt_consistant_local_without_electrode.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 3)});
        setDefaultValue();
    }

    public void setDefaultValue() {
        String sp_val_ref_local_electrode = spManager.getCalculationValueByKeyName("RefrenceLocalElectrode");
        String sp_val_ref_throughout = spManager.getCalculationValueByKeyName("RefrenceThroughout");
        String sp_val_basic_installation = spManager.getCalculationValueByKeyName("BasicInstallation");
        String sp_val_piler_resistence = spManager.getCalculationValueByKeyName("clampAmp");
        String sp_val_piler_voltage = spManager.getCalculationValueByKeyName("clampMeasurement");
        String sp_val_local_without_electrode = spManager.getCalculationValueByKeyName("noLocalElectrode");
        String sp_val_comment = spManager.getCalculationValueByKeyName("Comments").equals("anyType{}") ? "" : spManager.getCalculationValueByKeyName("Comments");
        //String sp_val_comment = spManager.getCalculationValueByKeyName("Comments");
        edt_measured_referance_local_electrode.setText(sp_val_ref_local_electrode);
        edt_measured_referance_local_electrode.setSelection(sp_val_ref_local_electrode.length());
        edt_measured_referance_throughout.setText(sp_val_ref_throughout);
        edt_measured_referance_throughout.setSelection(sp_val_ref_throughout.length());
        edt_basic_calulation.setText(sp_val_basic_installation);
        edt_measure_with_pliers_resistance.setText(sp_val_piler_voltage);
        edt_measure_with_pliers_voltage.setText(sp_val_piler_resistence);
        edt_consistant_local_without_electrode.setText(sp_val_local_without_electrode);
        edt_comments.setText(sp_val_comment);
        climbFactorHashMap = new HashMap<>();
        refdistMinU = XMLParser.getValue("referenceDistances", 0, 0);
        refdistMaxU = XMLParser.getValue("referenceDistances", 1, 0);
        climbFactorNames = XMLParser.getListValues("climbFactors", "name");
        String electrodeID = "";
        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        MainActivity mainActivity = MainActivity.getInstance();
        if (variablesFragment != null && mainActivity.variablePageClicked) {
            electrodeID = variablesFragment.selectedElektrodePosition() + "";
        } else {
            electrodeID = spManager.getVariablerValueByKeyName("ElekrodeSystem");
        }
        hideUnhide();
        if (electrodeID.equals("2")) {
            ll11.setVisibility(View.GONE);
            ll5.setVisibility(View.GONE);
            for (int i = 1; i < 10; i++) {
                if (i == 1) {
                    graphSavedValues = spManager.getGraphValueByKeyName("LocalEarthIndex" + i).equals("") ? "0" : spManager.getGraphValueByKeyName("LocalEarthIndex" + i);
                } else {
                    graphSavedValues += "," + (spManager.getGraphValueByKeyName("LocalEarthIndex" + i).equals("") ? "0" : spManager.getGraphValueByKeyName("LocalEarthIndex" + i));
                }
            }
        } else {
            ll11.setVisibility(View.VISIBLE);
            ll5.setVisibility(View.VISIBLE);
            for (int i = 1; i < 10; i++) {
                if (i == 1) {
                    graphSavedValues = spManager.getGraphValueByKeyName("GlobalEarthIndex" + i).equals("") ? "0" : spManager.getGraphValueByKeyName("GlobalEarthIndex" + i);
                } else {
                    graphSavedValues += "," + (spManager.getGraphValueByKeyName("GlobalEarthIndex" + i).equals("") ? "0" : spManager.getGraphValueByKeyName("GlobalEarthIndex" + i));
                }
            }
        }

        if (graphSavedValues != null && !graphSavedValues.equals("")) {
            double totalDistance = Double.parseDouble(spManager.getGraphValueByKeyName("Avstand_m_EC").equals("") ? "0" : spManager.getGraphValueByKeyName("Avstand_m_EC"));
            calculateClimbFactors(graphSavedValues.split(","));
            double avgClimbFactor = getAvgClimbFactors();
            double avg = Utils.roundTo(this.getAvgClimbFactors(), 0.01D);
            //String averageClimbFactor = Double.isNaN(avgClimbFactor) ? "" : String.valueOf(Utils.roundTo(avgClimbFactor, 0.001));
            double referenceDistance = getReferenceDistancePercentage(totalDistance, avg);
            String refDistVal = referenceDistance == 0 ? "" : String.valueOf(referenceDistance);
            double[] arr = getDoubleArray(graphSavedValues.split(","));
            double estimatedResistanceElectrode = Utils.roundTo(getEstimatedResistanceElectrode(arr, totalDistance, referenceDistance), 0.01);
            String estResistanceElectrode = Double.isNaN(avgClimbFactor) || totalDistance < 1 ? "" : String.valueOf(estimatedResistanceElectrode);
            if (!refDistVal.equals("")) {
                txt_referanseavstand.setText(refDistVal + " m"); //Beregnet referanseavstand
            }
            if (!estResistanceElectrode.equals("")) {
                txt_motstand_elektrode.setText(estResistanceElectrode + " ohm");  //Beregnet motstand elektrode
            }
           /* if(checkforCalculation()){
                calculate();
            }*/
            calculate();
        }
    }

    public boolean checkforCalculation() {
        String electrodeID = "";
        String voltageID = "";
        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        MainActivity mainActivity = MainActivity.getInstance();
        if (variablesFragment != null && mainActivity.variablePageClicked) {
            electrodeID = variablesFragment.selectedElektrodePosition() + "";
        } else {
            electrodeID = spManager.getVariablerValueByKeyName("ElekrodeSystem");
        }

        if (variablesFragment != null && mainActivity.variablePageClicked) {
            voltageID = variablesFragment.selectedVoltagePosition() + "";
        } else {
            voltageID = spManager.getVariablerValueByKeyName("VoltageID");
        }
        if (!electrodeID.equals("0") && !voltageID.equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edt_measured_referance_local_electrode:
                if (!hasFocus) {
                    String val_ref_local_electrode = edt_measured_referance_local_electrode.getText().toString().equals("") ? "" : edt_measured_referance_local_electrode.getText().toString();

                    if (!val_ref_local_electrode.equals(".")) {
                        double val = Double.parseDouble(val_ref_local_electrode.equals("") ? "0" : val_ref_local_electrode);
                        if (val > 20000) {
                            AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                                @Override
                                public void onClick() {
                                    spManager.saveCalculationValueByKeyName("RefrenceLocalElectrode", "", editor);
                                    edt_measured_referance_local_electrode.setText("");
                                }
                            });
                        } else {
                            spManager.saveCalculationValueByKeyName("RefrenceLocalElectrode", val_ref_local_electrode, editor);
                        }
                        calculate();
                    } else {
                        edt_measured_referance_local_electrode.setText("");
                    }

                }
                break;
            case R.id.edt_measured_referance_throughout:
                if (!hasFocus) {
                    String val_ref_throughout = edt_measured_referance_throughout.getText().toString().equals("") ? "" : edt_measured_referance_throughout.getText().toString();

                    if (!val_ref_throughout.equals(".")) {
                        double val = Double.parseDouble(val_ref_throughout.equals("") ? "0" : val_ref_throughout);
                        if (val > 20000) {
                            AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                                @Override
                                public void onClick() {
                                    spManager.saveCalculationValueByKeyName("RefrenceThroughout", "", editor);
                                    edt_measured_referance_throughout.setText("");
                                }
                            });
                        } else {
                            spManager.saveCalculationValueByKeyName("RefrenceThroughout", val_ref_throughout, editor);
                        }
                        calculate();
                    } else {
                        edt_measured_referance_throughout.setText("");
                    }

                }
                break;
            case R.id.edt_basic_calulation:
                if (!hasFocus) {
                    String val_basic_calculation = edt_basic_calulation.getText().toString().equals("") ? "" : edt_basic_calulation.getText().toString();
                    if (!val_basic_calculation.equals(".")) {
                        double val = Double.parseDouble(val_basic_calculation.equals("") ? "0" : val_basic_calculation);
                        if (val > 20000) {
                            AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                                @Override
                                public void onClick() {
                                    spManager.saveCalculationValueByKeyName("BasicInstallation", "", editor);
                                    edt_basic_calulation.setText("");
                                }
                            });
                        } else {
                            spManager.saveCalculationValueByKeyName("BasicInstallation", val_basic_calculation, editor);
                        }
                    } else {
                        edt_basic_calulation.setText("");
                    }
                }
                break;
            case R.id.edt_measure_with_pliers_resistance:
                if (!hasFocus) {
                    // new 20-05-2020
                    String val_pilar_resistence = edt_measure_with_pliers_resistance.getText().toString().equals("") ? "" : edt_measure_with_pliers_resistance.getText().toString();
                    spManager.saveCalculationValueByKeyName("clampMeasurement", val_pilar_resistence, editor);

                }
                break;
            case R.id.edt_measure_with_pliers_voltage:
                // new 20-05-2020
                if (!hasFocus) {
                    String val_PilerVoltage = edt_measure_with_pliers_voltage.getText().toString().equals("") ? "" : edt_measure_with_pliers_voltage.getText().toString();
                    spManager.saveCalculationValueByKeyName("clampAmp", val_PilerVoltage, editor);
                }
                break;
            case R.id.edt_consistant_local_without_electrode:
                if (!hasFocus) {
                    String val_local_without_electrode = edt_consistant_local_without_electrode.getText().toString().equals("") ? "" : edt_consistant_local_without_electrode.getText().toString();
                    if (!val_local_without_electrode.equals(".")) {
                        double val = Double.parseDouble(val_local_without_electrode.equals("") ? "0" : val_local_without_electrode);
                        if (val > 20000) {
                            AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                                @Override
                                public void onClick() {
                                    spManager.saveCalculationValueByKeyName("noLocalElectrode", "", editor);
                                    edt_consistant_local_without_electrode.setText("");
                                }
                            });
                        } else {
                            spManager.saveCalculationValueByKeyName("noLocalElectrode", val_local_without_electrode, editor);
                        }
                    } else {
                        edt_consistant_local_without_electrode.setText("");
                    }
                }
                break;
            case R.id.edt_comments:
                if (!hasFocus) {
                    String val_comments = edt_comments.getText().toString().equals("") ? "" : edt_comments.getText().toString();
                    spManager.saveCalculationValueByKeyName("Comments", val_comments, editor);
                }
                break;
        }
    }

    public void hideUnhide() {
        String electrodeID = "";
        String voltageID = "";
        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        MainActivity mainActivity = MainActivity.getInstance();
        if (variablesFragment != null && mainActivity.variablePageClicked) {
            voltageID = variablesFragment.selectedVoltagePosition() + "";
            electrodeID = variablesFragment.selectedElektrodePosition() + "";
        } else {
            electrodeID = spManager.getVariablerValueByKeyName("ElekrodeSystem");
            voltageID = spManager.getVariablerValueByKeyName("VoltageID");
        }

        if (voltageID.equals("1")) {
            ll_bero_mask.setVisibility(View.VISIBLE);
            ll_beregnet_berøringsspenning.setVisibility(View.VISIBLE);
            /*ll2.setVisibility(View.GONE);
            ll5.setVisibility(View.GONE);
            ll11.setVisibility(View.GONE);*/
        } else {
            ll_bero_mask.setVisibility(View.GONE);
            ll_beregnet_berøringsspenning.setVisibility(View.GONE);
        }
        if (voltageID.equals("0") && electrodeID.equals("0") || voltageID.equals("2") && electrodeID.equals("0") ||
                voltageID.equals("3") && electrodeID.equals("0") || voltageID.equals("2") && electrodeID.equals("1") ||
                voltageID.equals("3") && electrodeID.equals("1") || voltageID.equals("0") && electrodeID.equals("1")) {
            //ll_malt_ref_gjenomgnd.setVisibility(View.VISIBLE);
            ll_mask_tiltatt_overgangestand.setVisibility(View.GONE);
            ll_max_allowed.setVisibility(View.VISIBLE);
            ll_calculated.setVisibility(View.VISIBLE);
        } else if (voltageID.equals("0") && electrodeID.equals("2") || voltageID.equals("2") && electrodeID.equals("2") ||
                voltageID.equals("3") && electrodeID.equals("2")) {
            //ll_malt_ref_gjenomgnd.setVisibility(View.GONE);
            ll_mask_tiltatt_overgangestand.setVisibility(View.GONE);
            ll_max_allowed.setVisibility(View.VISIBLE);
            ll_calculated.setVisibility(View.VISIBLE);
        } else if (voltageID.equals("1") && electrodeID.equals("0") ||
                voltageID.equals("1") && electrodeID.equals("2")) {
            //ll_malt_ref_gjenomgnd.setVisibility(View.GONE);
            ll_mask_tiltatt_overgangestand.setVisibility(View.VISIBLE);
            ll_max_allowed.setVisibility(View.GONE);
            ll_calculated.setVisibility(View.GONE);
        } else if (voltageID.equals("1") && electrodeID.equals("1")) {
            //ll_malt_ref_gjenomgnd.setVisibility(View.VISIBLE);
            ll_mask_tiltatt_overgangestand.setVisibility(View.VISIBLE);
            ll_max_allowed.setVisibility(View.GONE);
            ll_calculated.setVisibility(View.GONE);
        }
        /*} else if (voltageID.equals("1") && electrodeID.equals("0") || voltageID.equals("1") && electrodeID.equals("1") ||
                voltageID.equals("1") && electrodeID.equals("2")) {
            ll_malt_ref_gjenomgnd.setVisibility(View.VISIBLE);
            ll_mask_tiltatt_overgangestand.setVisibility(View.VISIBLE);
            ll_max_allowed.setVisibility(View.GONE);
            ll_calculated.setVisibility(View.GONE);
        }*/
    }

    public void calculate() {
        int elektrode = 0;
        int globalEarth = 0;
        int voltage = 0;
        double transformerPerformance = 0;
        double earthFaultCurrent = 0;
        boolean highVoltageActionTaken = false;
        String statusString = "";
        // double refdistancel = Double.parseDouble(edt_measured_referance_local_electrode.getText().toString().equals("") ? "0" : edt_measured_referance_local_electrode.getText().toString());
        double refdistancet = Double.parseDouble(edt_measured_referance_throughout.getText().toString().equals("") ? "0" : edt_measured_referance_throughout.getText().toString());
        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        MainActivity mainActivity = MainActivity.getInstance();
        if (variablesFragment != null && mainActivity.variablePageClicked) {
            globalEarth = variablesFragment.selectedGlobalEarthPosition();
            elektrode = variablesFragment.selectedElektrodePosition();
            voltage = variablesFragment.selectedVoltagePosition();
            highVoltageActionTaken = variablesFragment.selectedHighVoltageActionTakenPosition() != 0;
            transformerPerformance = variablesFragment.edt_transformer_style == null ? Double.parseDouble(spManager.getVariablerValueByKeyName("TransformerStyle")) : Double.parseDouble(variablesFragment.edt_transformer_style.getText().toString().equals("") ? "0" : variablesFragment.edt_transformer_style.getText().toString());
            earthFaultCurrent = variablesFragment.edt_calculated_pole_grnd_crnt == null ? Double.parseDouble(spManager.getVariablerValueByKeyName("Calculated_1_PoleCurrent")) : Double.parseDouble(variablesFragment.edt_calculated_pole_grnd_crnt.getText().toString().equals("") ? "0" : variablesFragment.edt_calculated_pole_grnd_crnt.getText().toString());
        } else {
            String electrode = spManager.getVariablerValueByKeyName("ElekrodeSystem");
            String globalearth = spManager.getVariablerValueByKeyName("GlobalEarthID");
            String volID = spManager.getVariablerValueByKeyName("VoltageID");
            elektrode = Integer.parseInt(electrode.contains(".") ? electrode.substring(0, electrode.indexOf(".")) : electrode);
            globalEarth = Integer.parseInt(globalearth.contains(".") ? globalearth.substring(0, globalearth.indexOf(".")) : globalearth);
            voltage = Integer.parseInt(volID.contains(".") ? volID.substring(0, volID.indexOf(".")) : volID);
            highVoltageActionTaken = Integer.parseInt(spManager.getVariablerValueByKeyName("MeasureTaken")) == 1;
            transformerPerformance = Double.parseDouble(spManager.getVariablerValueByKeyName("TransformerStyle"));
            earthFaultCurrent = Double.parseDouble(spManager.getVariablerValueByKeyName("Calculated_1_PoleCurrent"));
        }
        boolean isGlobalEarth = globalEarth != 0;
        if ((elektrode == 1 && refdistancet == 0.0) || (elektrode != 1 && refdistancet == 0.0)) {
            statusString = "Mangler referanseverdi " + (elektrode == 1 ? "gjennomgående" : "lokal elektrode");
            ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_incomplete_data));
            iv_status.setBackground(getResources().getDrawable(R.drawable.ic_incomplete_icon));
            if (isGlobalEarth) {
                statusString = "Godkjent - Global jord";
                ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_valid));
                iv_status.setBackground(getResources().getDrawable(R.drawable.ic_invalid));
            }
            txt_status_string.setText(statusString);
            return;
        }

        if (voltage != 1) {
            try {
                double est = calculateEstimatedGroundPotentialRise(elektrode);
                double estEarthPotentialRise = Utils.roundTo(est, 0.1);      // Beregnet jordpotensialeheving UE
                txt_beregnet_jordpotensialheving_ue.setText(estEarthPotentialRise + " V");
                double maxVoltage = findTouchVoltage() * findMultiplier(voltage, highVoltageActionTaken);   // Maks Tillatt jordpotensialeheving UE
                txt_maks_tillatt.setText(maxVoltage + " V");
                if (maxVoltage < estEarthPotentialRise) {
                    statusString = "Tilfredsstiller IKKE kravene til berøringsspenning";
                    txt_status_string.setText(statusString);
                    ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_invalid));
                    iv_status.setBackground(getResources().getDrawable(R.drawable.ic_check_circle));
                } else {
                    statusString = "Tilfredstiller kravene til berøringsspenning";
                    if (isGlobalEarth) {
                        statusString = "Godkjent - Global jord";
                    }
                    txt_status_string.setText(statusString);
                    ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_valid));
                    iv_status.setBackground(getResources().getDrawable(R.drawable.ic_invalid));
                }
                if (earthFaultCurrent == 0.0) {
                    statusString = "Ufullstendig data";
                    txt_status_string.setText(statusString);
                    ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_incomplete_data));
                    iv_status.setBackground(getResources().getDrawable(R.drawable.ic_incomplete_icon));
                }
            } catch (Exception ex) {
                Log.d("Error", ex.getMessage());
                return;
            }
        } else if (transformerPerformance > 0.0) {
            try {
                double maxResistance = calculateMaximumResistance(transformerPerformance);
                //String val = elektrode == 2 ? (edt_measured_referance_local_electrode.getText().toString().equals("") ? "0" : edt_measured_referance_local_electrode.getText().toString()) : edt_measured_referance_throughout.getText().toString().equals("") ? "0" : edt_measured_referance_throughout.getText().toString();
                String val = "";
               /* if(edt_measured_referance_local_electrode.getText().toString().equals("")){
                    val = "0";
                }else{
                    val = edt_measured_referance_local_electrode.getText().toString();
                }*/
                if (edt_measured_referance_throughout.getText().toString().equals("")) {
                    val = "0";
                } else {
                    val = edt_measured_referance_throughout.getText().toString();
                }
                double referenceValueLocal = Double.parseDouble(val);
                double _calculatedTouchVoltage = calculatedTouchVoltage(referenceValueLocal, transformerPerformance);
                double max_resistance = Utils.roundTo(maxResistance, 0.01); //Maks tillatt overgangsmotstand
                txt_maks_tillatt_overgangsmotstand.setText(max_resistance + " ohm");
                double cal_touch_voltage = Utils.roundTo(_calculatedTouchVoltage, 0.001);
                txt_beregnet_beroringspenning.setText(cal_touch_voltage + " V");
                double max_touch_voltage = XMLParser.getTouchLowVoltage();
                txt_beroringspening_maks.setText(max_touch_voltage + " V");
                if (maxResistance > refdistancet) {
                    statusString = "Tilfredstiller kravene til berøringsspenning";
                    txt_status_string.setText(statusString);
                    if (isGlobalEarth) {
                        statusString = "Godkjent - Global jord";
                        txt_status_string.setText(statusString);
                    }
                    ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_valid));
                    iv_status.setBackground(getResources().getDrawable(R.drawable.ic_invalid));
                } else {
                    statusString = "Tilfredsstiller IKKE kravene til berøringsspenning";
                    txt_status_string.setText(statusString);
                    ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_invalid));
                }
            } catch (Exception ex) {
                Log.d("Error", ex.getMessage());
                return;
            }
        }

        if (isGlobalEarth) {
            statusString = "Godkjent - Global jord";
            txt_status_string.setText(statusString);
            ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_valid));
        } else {
            if (statusString.equals("")) {
                statusString = "Mangler referanseverdi " + (elektrode == 1 ? "gjennomgående" : "lokal elektrode");
                txt_status_string.setText(statusString);
                ll_half_rounded.setBackground(getResources().getDrawable(R.drawable.half_rounded_corner_incomplete_data));
                iv_status.setBackground(getResources().getDrawable(R.drawable.ic_incomplete_icon));
            }
        }
    }

    public double calculateEstimatedGroundPotentialRise(int elekrode) {
        double referenceValueThrough = Double.parseDouble(edt_measured_referance_throughout.getText().toString().equals("") ? "0" : edt_measured_referance_throughout.getText().toString());
        //double referenceValueLocal = Double.parseDouble(edt_measured_referance_local_electrode.getText().toString().equals("") ? "0" : edt_measured_referance_local_electrode.getText().toString());
        double earthFaultCurrent = Double.parseDouble(spManager.getVariablerValueByKeyName("Calculated_1_PoleCurrent"));
        double base = elekrode == 2 ? referenceValueThrough : referenceValueThrough;
        return earthFaultCurrent <= 0.0D ? 0.0D : base * earthFaultCurrent;
    }

    public int findTouchVoltage() {
        double disconnect = Double.parseDouble(spManager.getVariablerValueByKeyName("Disablement"));
        String disablement = disconnect == 0 ? "0.05" : disconnect + "";
        int val = XMLParser.getDisconnectVoltage(disablement, "disconnectTimes", "ID");
        if (val == 0) {
            val = XMLParser.getDisconnectVoltage(disablement, "disconnectTimes", "Seconds");
        }
        return val;
    }

    public int findMultiplier(int voltage, boolean highVoltageActionTaken) {
        switch (voltage) {
            case 2:
                if (highVoltageActionTaken) {
                    return 4;
                }
                return 2;
            case 3:
                return 1;
            default:
                return 0;
        }
    }

    public double calculateMaximumResistance(double transformerPerformance) {
        double touchLowVoltage = XMLParser.getTouchLowVoltage();
        double val = touchLowVoltage / (transformerPerformance * 0.002D);
        return val;
    }

    public double calculatedTouchVoltage(double referenceValueLocal, double transformerPerformance) {
        return referenceValueLocal * transformerPerformance * 0.002D;
    }

    public void calculateClimbFactors(String[] input) {
        for (int i = 0; i < climbFactorNames.size(); ++i) {
            climbFactorHashMap.put(climbFactorNames.get(i), this.calculateClimbFactorValue(climbFactorNames.get(i), input));
        }
    }

    private double calculateClimbFactorValue(String factor, String[] input) {
        String[] r = factor.replace("P", "").split("-");
        double r1 = Double.parseDouble(input[Integer.parseInt(r[0]) - 2]);
        double r2 = Double.parseDouble(input[Integer.parseInt(r[1]) - 2]);
        double r3 = Double.parseDouble(input[Integer.parseInt(r[2]) - 2]);
        if (r1 * r2 * r3 == 0.0D) {
            return 0.0D;
        } else {
            try {
                if ((r2 - r1) == 0) {
                    return 0.0D;
                } else {
                    return (r3 - r2) / (r2 - r1);
                }
            } catch (Exception ex) {
                return 0.0D;
            }
        }
    }

    public double getAvgClimbFactors() {
        int n = 0;
        double total = 0.0D;
        for (int i = 0; i < climbFactorNames.size(); ++i) {
            double value = climbFactorHashMap.get(climbFactorNames.get(i));
            if (validateClimbFactorValue(value)) {
                ++n;
                total += value;
            }
        }
        return total / (double) n;
    }

    public boolean validateClimbFactorValue(double u) {
        return u < refdistMaxU && u > refdistMinU;
    }

    public double getReferenceDistancePercentage(double totalDistance, double avgClimbFactor) {
        if (totalDistance == 0) {
            return 0.0D;
        } else {
            double avg = Utils.roundTo(avgClimbFactor, 0.01D);
            double refDistanceP = XMLParser.getValue("checkRefDistance", 0, avg);
            double returnVal = totalDistance * refDistanceP;
            return Utils.roundTo(returnVal, 0.5D);
        }
    }

    public double[] getDoubleArray(String[] input) {
        double[] arr = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            arr[i] = Double.parseDouble(input[i]);
        }
        return arr;
    }

    public double getEstimatedResistanceElectrode(double[] input, double totalDistance, double referenceDistance) {
        input = validateInput(input);
        double increment = totalDistance / (double) numberOfDistances;
        int firstPoint = (int) (referenceDistance / increment) - 1;
        if (firstPoint < 0) {
            firstPoint = 0;
        }
        int accuracy = this.estimatedResistanceElectrodeAccuracy;
        double[] dist = new double[accuracy];
        double[] val = new double[accuracy];
        double minValue = input[firstPoint];
        double maxValue = input[firstPoint + 1];
        double incVal = maxValue - minValue;
        int index;
        for (index = 0; index < accuracy; ++index) {
            dist[index] = increment * (double) (firstPoint + 1) + increment * (1.0D / (double) accuracy) * (double) index;
            if (index == 0) {
                val[index] = minValue;
            } else {
                val[index] = val[index - 1] + incVal * (1.0D / (double) accuracy);
            }
        }
        index = 0;
        for (int i = 0; i < accuracy; ++i) {
            if (dist[i] > referenceDistance) {
                index = i - 1;
                if (i == 0) {
                    index = 0;
                }
                break;
            }
        }
        return val[index];
    }

    private double[] validateInput(double[] input) {
        if (input.length != climbFactorNames.size()) {
            double[] d = new double[climbFactorNames.size()];
            for (int i = 0; i < d.length; ++i) {
                if (i >= input.length) {
                    d[i] = 0.0D;
                } else {
                    d[i] = input[i];
                }
            }
            return d;
        } else {
            return input;
        }
    }
}
