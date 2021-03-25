package com.trainor.controlandmeasurement.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.DecimalDigitsInputFilter;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.LAYOUT_DIRECTION_INHERIT;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;
import static android.view.View.TEXT_DIRECTION_FIRST_STRONG_LTR;

public class MeasuredValueFragment extends Fragment implements View.OnFocusChangeListener {
    SharedPreferenceClass spManager;
    SharedPreferences.Editor editor;
    @BindView(R.id.edt_avstand_m_ec)
    EditText edt_avstand_m_ec;
    @BindView(R.id.edt_direction_forward)
    EditText edt_direction_forward;
    @BindView(R.id.edt_direction_backward)
    EditText edt_direction_backward;

    @BindView(R.id.edt_gearth_index1)
    EditText edt_gearth_index1;
    @BindView(R.id.edt_gearth_index2)
    EditText edt_gearth_index2;
    @BindView(R.id.edt_gearth_index3)
    EditText edt_gearth_index3;
    @BindView(R.id.edt_gearth_index4)
    EditText edt_gearth_index4;
    @BindView(R.id.edt_gearth_index5)
    EditText edt_gearth_index5;
    @BindView(R.id.edt_gearth_index6)
    EditText edt_gearth_index6;
    @BindView(R.id.edt_gearth_index7)
    EditText edt_gearth_index7;
    @BindView(R.id.edt_gearth_index8)
    EditText edt_gearth_index8;
    @BindView(R.id.edt_gearth_index9)
    EditText edt_gearth_index9;
    @BindView(R.id.edt_gearth_index_5)
    EditText edt_gearth_index_5;
    @BindView(R.id.edt_gearth_index_6)
    EditText edt_gearth_index_6;

    @BindView(R.id.edt_learth_index1)
    EditText edt_learth_index1;
    @BindView(R.id.edt_learth_index2)
    EditText edt_learth_index2;
    @BindView(R.id.edt_learth_index3)
    EditText edt_learth_index3;
    @BindView(R.id.edt_learth_index4)
    EditText edt_learth_index4;
    @BindView(R.id.edt_learth_index5)
    EditText edt_learth_index5;
    @BindView(R.id.edt_learth_index6)
    EditText edt_learth_index6;
    @BindView(R.id.edt_learth_index7)
    EditText edt_learth_index7;
    @BindView(R.id.edt_learth_index8)
    EditText edt_learth_index8;
    @BindView(R.id.edt_learth_index9)
    EditText edt_learth_index9;
    @BindView(R.id.edt_learth_index_5)
    EditText edt_learth_index_5;
    @BindView(R.id.edt_learth_index_6)
    EditText edt_learth_index_6;
    @BindView(R.id.measure_local_electrode)
    TextView measure_local_electrode;
    @BindView(R.id.show_all)
    TextView show_all;
    @BindView(R.id.show_global_earth)
    TextView show_global_earth;
    @BindView(R.id.show_local_earth)
    TextView show_local_earth;
    @BindView(R.id.ll_local_electrode_values)
    LinearLayout ll_local_electrode_values;

    @BindView(R.id.p2)
    TextView p2;
    @BindView(R.id.p3)
    TextView p3;
    @BindView(R.id.p4)
    TextView p4;
    @BindView(R.id.p5)
    TextView p5;
    @BindView(R.id.p6)
    TextView p6;
    @BindView(R.id.p7)
    TextView p7;
    @BindView(R.id.p8)
    TextView p8;
    @BindView(R.id.p9)
    TextView p9;
    @BindView(R.id.p10)
    TextView p10;
    @BindView(R.id.p5_minus)
    TextView p5_minus;
    @BindView(R.id.p6_minus)
    TextView p6_minus;

    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.globalwebview)
    WebView globalwebview;
    @BindView(R.id.ll_global_jord_values)
    LinearLayout ll_global_jord_values;
    @BindView(R.id.ll_button_view)
    LinearLayout ll_button_view;
    @BindView(R.id.lineView)
    View lineView;
    String graphType = "", global_arr = "", local_arr = "";

    public MeasuredValueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spManager = new SharedPreferenceClass(MainActivity.getInstance());
        if (editor == null) {
            editor = MainActivity.getInstance().getSharedPreferences("MeasuredValuesPref", Context.MODE_PRIVATE).edit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measured_values, container, false);
        ButterKnife.bind(MeasuredValueFragment.this, view);
        getControls();
        return view;
    }



    public void getControls() {
        edt_avstand_m_ec.setOnFocusChangeListener(this);
        edt_direction_forward.setOnFocusChangeListener(this);
        edt_direction_backward.setOnFocusChangeListener(this);

        edt_gearth_index1.setOnFocusChangeListener(this);
        edt_gearth_index2.setOnFocusChangeListener(this);
        edt_gearth_index3.setOnFocusChangeListener(this);
        edt_gearth_index4.setOnFocusChangeListener(this);
        edt_gearth_index5.setOnFocusChangeListener(this);
        edt_gearth_index6.setOnFocusChangeListener(this);
        edt_gearth_index7.setOnFocusChangeListener(this);
        edt_gearth_index8.setOnFocusChangeListener(this);
        edt_gearth_index9.setOnFocusChangeListener(this);
        edt_gearth_index_5.setOnFocusChangeListener(this);
        edt_gearth_index_6.setOnFocusChangeListener(this);

        edt_learth_index1.setOnFocusChangeListener(this);
        edt_learth_index2.setOnFocusChangeListener(this);
        edt_learth_index3.setOnFocusChangeListener(this);
        edt_learth_index4.setOnFocusChangeListener(this);
        edt_learth_index5.setOnFocusChangeListener(this);
        edt_learth_index6.setOnFocusChangeListener(this);
        edt_learth_index7.setOnFocusChangeListener(this);
        edt_learth_index8.setOnFocusChangeListener(this);
        edt_learth_index9.setOnFocusChangeListener(this);
        edt_learth_index_5.setOnFocusChangeListener(this);
        edt_learth_index_6.setOnFocusChangeListener(this);

        measure_local_electrode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measure_local_electrode.setVisibility(View.GONE);
                ll_local_electrode_values.setVisibility(View.VISIBLE);
            }
        });
        edt_avstand_m_ec.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4)});
        edt_direction_forward.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        edt_direction_backward.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        edt_gearth_index1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index3.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index4.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index5.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index6.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index7.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index8.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index9.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index_5.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_gearth_index_6.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});

        edt_learth_index1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index3.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index4.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index5.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index6.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index7.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index8.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index9.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index_5.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        edt_learth_index_6.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 3)});
        setDefaultValues();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edt_avstand_m_ec:
                if (!hasFocus) {
                    String val_edt_avstand_m = edt_avstand_m_ec.getText().toString().equals("") ? "0" : edt_avstand_m_ec.getText().toString();
                    spManager.saveGraphValueByKeyName("Avstand_m_EC", val_edt_avstand_m, editor);
                    calcDistance();
                }
                break;
            case R.id.edt_direction_forward:
                if (!hasFocus) {
                    String val_dir_forward = edt_direction_forward.getText().toString().equals("") ? "" : edt_direction_forward.getText().toString();
                    double val = Double.parseDouble(val_dir_forward.equals("") ? "0" : val_dir_forward);
                    if (val > 360) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_360), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("DirectionForward", "", editor);
                                edt_direction_forward.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("DirectionForward", val_dir_forward, editor);
                    }
                }
                break;
            case R.id.edt_direction_backward:
                if (!hasFocus) {
                    String val_dir_backward = edt_direction_backward.getText().toString().equals("") ? "" : edt_direction_backward.getText().toString();
                    double val = Double.parseDouble(val_dir_backward.equals("") ? "0" : val_dir_backward);
                    if (val > 360) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_360), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("DirectionBackward", "", editor);
                                edt_direction_backward.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("DirectionBackward", val_dir_backward, editor);
                    }
                }
                break;
            case R.id.edt_gearth_index1:
                if (!hasFocus) {
                    String val_edt_gearth_index1 = edt_gearth_index1.getText().toString().equals("") ? "" : edt_gearth_index1.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index1.equals("") ? "0" : val_edt_gearth_index1);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex1", "", editor);
                                edt_gearth_index1.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex1", val_edt_gearth_index1, editor);
                        /*String showText = val_edt_gearth_index1.length() > 3 ? (val_edt_gearth_index1.substring(0, 4).contains(".") ? val_edt_gearth_index1.substring(0, 4) : val_edt_gearth_index1.substring(0, 3)) : val_edt_gearth_index1;
                        edt_gearth_index1.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex1");
                    edt_gearth_index1.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index1);
                }
                break;
            case R.id.edt_gearth_index2:
                if (!hasFocus) {
                    String val_edt_gearth_index2 = edt_gearth_index2.getText().toString().equals("") ? "" : edt_gearth_index2.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index2.equals("") ? "0" : val_edt_gearth_index2);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex2", "", editor);
                                edt_gearth_index2.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex2", val_edt_gearth_index2, editor);
                        /*String showText = val_edt_gearth_index2.length() > 3 ? (val_edt_gearth_index2.substring(0, 4).contains(".") ? val_edt_gearth_index2.substring(0, 4) : val_edt_gearth_index2.substring(0, 3)) : val_edt_gearth_index2;
                        edt_gearth_index2.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex2");
                    edt_gearth_index2.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index2);
                }
                break;
            case R.id.edt_gearth_index3:
                if (!hasFocus) {
                    String val_edt_gearth_index3 = edt_gearth_index3.getText().toString().equals("") ? "" : edt_gearth_index3.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index3.equals("") ? "0" : val_edt_gearth_index3);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex3", "", editor);
                                edt_gearth_index3.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex3", val_edt_gearth_index3, editor);
                        /*String showText = val_edt_gearth_index3.length() > 3 ? (val_edt_gearth_index3.substring(0, 4).contains(".") ? val_edt_gearth_index3.substring(0, 4) : val_edt_gearth_index3.substring(0, 3)) : val_edt_gearth_index3;
                        edt_gearth_index3.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex3");
                    edt_gearth_index3.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index3);
                }
                break;
            case R.id.edt_gearth_index4:
                if (!hasFocus) {
                    String val_edt_gearth_index4 = edt_gearth_index4.getText().toString().equals("") ? "" : edt_gearth_index4.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index4.equals("") ? "0" : val_edt_gearth_index4);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex4", "", editor);
                                edt_gearth_index4.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex4", val_edt_gearth_index4, editor);
                        /*String showText = val_edt_gearth_index4.length() > 3 ? (val_edt_gearth_index4.substring(0, 4).contains(".") ? val_edt_gearth_index4.substring(0, 4) : val_edt_gearth_index4.substring(0, 3)) : val_edt_gearth_index4;
                        edt_gearth_index4.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex4");
                    edt_gearth_index4.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index4);
                }
                break;
            case R.id.edt_gearth_index5:
                if (!hasFocus) {
                    String val_edt_gearth_index5 = edt_gearth_index5.getText().toString().equals("") ? "" : edt_gearth_index5.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index5.equals("") ? "0" : val_edt_gearth_index5);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex5", "", editor);
                                edt_gearth_index5.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex5", val_edt_gearth_index5, editor);
                        /*String showText = val_edt_gearth_index5.length() > 3 ? (val_edt_gearth_index5.substring(0, 4).contains(".") ? val_edt_gearth_index5.substring(0, 4) : val_edt_gearth_index5.substring(0, 3)) : val_edt_gearth_index5;
                        edt_gearth_index5.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex5");
                    edt_gearth_index5.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index5);
                }
                break;
            case R.id.edt_gearth_index6:
                if (!hasFocus) {
                    String val_edt_gearth_index6 = edt_gearth_index6.getText().toString().equals("") ? "" : edt_gearth_index6.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index6.equals("") ? "0" : val_edt_gearth_index6);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex6", "", editor);
                                edt_gearth_index6.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex6", val_edt_gearth_index6, editor);
                        /*String showText = val_edt_gearth_index6.length() > 3 ? (val_edt_gearth_index6.substring(0, 4).contains(".") ? val_edt_gearth_index6.substring(0, 4) : val_edt_gearth_index6.substring(0, 3)) : val_edt_gearth_index6;
                        edt_gearth_index6.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex6");
                    edt_gearth_index6.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index6);
                }
                break;
            case R.id.edt_gearth_index7:
                if (!hasFocus) {
                    String val_edt_gearth_index7 = edt_gearth_index7.getText().toString().equals("") ? "" : edt_gearth_index7.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index7.equals("") ? "0" : val_edt_gearth_index7);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex7", "", editor);
                                edt_gearth_index7.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex7", val_edt_gearth_index7, editor);
                       /* String showText = val_edt_gearth_index7.length() > 3 ? (val_edt_gearth_index7.substring(0, 4).contains(".") ? val_edt_gearth_index7.substring(0, 4) : val_edt_gearth_index7.substring(0, 3)) : val_edt_gearth_index7;
                        edt_gearth_index7.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex7");
                    edt_gearth_index7.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index7);
                }
                break;
            case R.id.edt_gearth_index8:
                if (!hasFocus) {
                    String val_edt_gearth_index8 = edt_gearth_index8.getText().toString().equals("") ? "" : edt_gearth_index8.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index8.equals("") ? "0" : val_edt_gearth_index8);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex8", "", editor);
                                edt_gearth_index8.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex8", val_edt_gearth_index8, editor);
                        /*String showText = val_edt_gearth_index8.length() > 3 ? (val_edt_gearth_index8.substring(0, 4).contains(".") ? val_edt_gearth_index8.substring(0, 4) : val_edt_gearth_index8.substring(0, 3)) : val_edt_gearth_index8;
                        edt_gearth_index8.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex8");
                    edt_gearth_index8.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index8);
                }
                break;
            case R.id.edt_gearth_index9:
                if (!hasFocus) {
                    String val_edt_gearth_index9 = edt_gearth_index9.getText().toString().equals("") ? "" : edt_gearth_index9.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index9.equals("") ? "0" : val_edt_gearth_index9);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex9", "", editor);
                                edt_gearth_index9.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex9", val_edt_gearth_index9, editor);
                       /* String showText = val_edt_gearth_index9.length() > 3 ? (val_edt_gearth_index9.substring(0, 4).contains(".") ? val_edt_gearth_index9.substring(0, 4) : val_edt_gearth_index9.substring(0, 3)) : val_edt_gearth_index9;
                        edt_gearth_index9.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex9");
                    edt_gearth_index9.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index9);
                }
                break;
            case R.id.edt_gearth_index_5:
                if (!hasFocus) {
                    String val_edt_gearth_index_5 = edt_gearth_index_5.getText().toString().equals("") ? "" : edt_gearth_index_5.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index_5.equals("") ? "0" : val_edt_gearth_index_5);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex_5", "", editor);
                                edt_gearth_index_5.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex_5", val_edt_gearth_index_5, editor);
                        /*String showText = val_edt_gearth_index_5.length() > 3 ? (val_edt_gearth_index_5.substring(0, 4).contains(".") ? val_edt_gearth_index_5.substring(0, 4) : val_edt_gearth_index_5.substring(0, 3)) : val_edt_gearth_index_5;
                        edt_gearth_index_5.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex_5");
                    edt_gearth_index_5.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index_5);
                }
                break;
            case R.id.edt_gearth_index_6:
                if (!hasFocus) {
                    String val_edt_gearth_index_6 = edt_gearth_index_6.getText().toString().equals("") ? "" : edt_gearth_index_6.getText().toString();
                    double val = Double.parseDouble(val_edt_gearth_index_6.equals("") ? "0" : val_edt_gearth_index_6);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("GlobalEarthIndex_6", "", editor);
                                edt_gearth_index_6.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("GlobalEarthIndex_6", val_edt_gearth_index_6, editor);
                        /*String showText = val_edt_gearth_index_6.length() > 3 ? (val_edt_gearth_index_6.substring(0, 4).contains(".") ? val_edt_gearth_index_6.substring(0, 4) : val_edt_gearth_index_6.substring(0, 3)) : val_edt_gearth_index_6;
                        edt_gearth_index_6.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("GlobalEarthIndex_6");
                    edt_gearth_index_6.setText(showText);
                    ckeckElectrodeForGraphInput(edt_gearth_index_6);
                }
                break;
            case R.id.edt_learth_index1:
                if (!hasFocus) {
                    String val_edt_learth_index1 = edt_learth_index1.getText().toString().equals("") ? "" : edt_learth_index1.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index1.equals("") ? "0" : val_edt_learth_index1);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex1", "", editor);
                                edt_learth_index1.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex1", val_edt_learth_index1, editor);
                        /*String showText = val_edt_learth_index1.length() > 3 ? (val_edt_learth_index1.substring(0, 4).contains(".") ? val_edt_learth_index1.substring(0, 4) : val_edt_learth_index1.substring(0, 3)) : val_edt_learth_index1;
                        edt_learth_index1.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex1");
                    edt_learth_index1.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index1);
                }
                break;
            case R.id.edt_learth_index2:
                if (!hasFocus) {
                    String val_edt_learth_index2 = edt_learth_index2.getText().toString().equals("") ? "" : edt_learth_index2.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index2.equals("") ? "0" : val_edt_learth_index2);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex2", "", editor);
                                edt_learth_index2.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex2", val_edt_learth_index2, editor);
                       /* String showText = val_edt_learth_index2.length() > 3 ? (val_edt_learth_index2.substring(0, 4).contains(".") ? val_edt_learth_index2.substring(0, 4) : val_edt_learth_index2.substring(0, 3)) : val_edt_learth_index2;
                        edt_learth_index2.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex2");
                    edt_learth_index2.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index2);
                }
                break;
            case R.id.edt_learth_index3:
                if (!hasFocus) {
                    String val_edt_learth_index3 = edt_learth_index3.getText().toString().equals("") ? "" : edt_learth_index3.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index3.equals("") ? "0" : val_edt_learth_index3);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex3", "", editor);
                                edt_learth_index3.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex3", val_edt_learth_index3, editor);
                        /*String showText = val_edt_learth_index3.length() > 3 ? (val_edt_learth_index3.substring(0, 4).contains(".") ? val_edt_learth_index3.substring(0, 4) : val_edt_learth_index3.substring(0, 3)) : val_edt_learth_index3;
                        edt_learth_index3.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex3");
                    edt_learth_index3.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index3);
                }
                break;
            case R.id.edt_learth_index4:
                if (!hasFocus) {
                    String val_edt_learth_index4 = edt_learth_index4.getText().toString().equals("") ? "" : edt_learth_index4.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index4.equals("") ? "0" : val_edt_learth_index4);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex4", "", editor);
                                edt_learth_index4.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex4", val_edt_learth_index4, editor);
                        /*String showText = val_edt_learth_index4.length() > 3 ? (val_edt_learth_index4.substring(0, 4).contains(".") ? val_edt_learth_index4.substring(0, 4) : val_edt_learth_index4.substring(0, 3)) : val_edt_learth_index4;
                        edt_learth_index4.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex4");
                    edt_learth_index4.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index4);
                }
                break;
            case R.id.edt_learth_index5:
                if (!hasFocus) {
                    String val_edt_learth_index5 = edt_learth_index5.getText().toString().equals("") ? "" : edt_learth_index5.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index5.equals("") ? "0" : val_edt_learth_index5);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex5", "", editor);
                                edt_learth_index5.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex5", val_edt_learth_index5, editor);
                        /*String showText = val_edt_learth_index5.length() > 3 ? (val_edt_learth_index5.substring(0, 4).contains(".") ? val_edt_learth_index5.substring(0, 4) : val_edt_learth_index5.substring(0, 3)) : val_edt_learth_index5;
                        edt_learth_index5.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex5");
                    edt_learth_index5.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index5);
                }
                break;
            case R.id.edt_learth_index6:
                if (!hasFocus) {
                    String val_edt_learth_index6 = edt_learth_index6.getText().toString().equals("") ? "" : edt_learth_index6.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index6.equals("") ? "0" : val_edt_learth_index6);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex6", "", editor);
                                edt_learth_index6.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex6", val_edt_learth_index6, editor);
                        /*String showText = val_edt_learth_index6.length() > 3 ? (val_edt_learth_index6.substring(0, 4).contains(".") ? val_edt_learth_index6.substring(0, 4) : val_edt_learth_index6.substring(0, 3)) : val_edt_learth_index6;
                        edt_learth_index6.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex6");
                    edt_learth_index6.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index6);
                }
                break;
            case R.id.edt_learth_index7:
                if (!hasFocus) {
                    String val_edt_learth_index7 = edt_learth_index7.getText().toString().equals("") ? "" : edt_learth_index7.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index7.equals("") ? "0" : val_edt_learth_index7);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex7", "", editor);
                                edt_learth_index7.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex7", val_edt_learth_index7, editor);
                        /*String showText = val_edt_learth_index7.length() > 3 ? (val_edt_learth_index7.substring(0, 4).contains(".") ? val_edt_learth_index7.substring(0, 4) : val_edt_learth_index7.substring(0, 3)) : val_edt_learth_index7;
                        edt_learth_index7.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex7");
                    edt_learth_index7.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index7);
                }
                break;
            case R.id.edt_learth_index8:
                if (!hasFocus) {
                    String val_edt_learth_index8 = edt_learth_index8.getText().toString().equals("") ? "" : edt_learth_index8.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index8.equals("") ? "0" : val_edt_learth_index8);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex8", "", editor);
                                edt_learth_index8.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex8", val_edt_learth_index8, editor);
                        /*String showText = val_edt_learth_index8.length() > 3 ? (val_edt_learth_index8.substring(0, 4).contains(".") ? val_edt_learth_index8.substring(0, 4) : val_edt_learth_index8.substring(0, 3)) : val_edt_learth_index8;
                        edt_learth_index8.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex8");
                    edt_learth_index8.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index8);
                }
                break;
            case R.id.edt_learth_index9:
                if (!hasFocus) {
                    String val_edt_learth_index9 = edt_learth_index9.getText().toString().equals("") ? "" : edt_learth_index9.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index9.equals("") ? "0" : val_edt_learth_index9);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex9", "", editor);
                                edt_learth_index9.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex9", val_edt_learth_index9, editor);
                       /* String showText = val_edt_learth_index9.length() > 3 ? (val_edt_learth_index9.substring(0, 4).contains(".") ? val_edt_learth_index9.substring(0, 4) : val_edt_learth_index9.substring(0, 3)) : val_edt_learth_index9;
                        edt_learth_index9.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex9");
                    edt_learth_index9.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index9);
                }
                break;
            case R.id.edt_learth_index_5:
                if (!hasFocus) {
                    String val_edt_learth_index_5 = edt_learth_index_5.getText().toString().equals("") ? "" : edt_learth_index_5.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index_5.equals("") ? "0" : val_edt_learth_index_5);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex_5", "", editor);
                                edt_learth_index_5.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex_5", val_edt_learth_index_5, editor);
                        //String showText = val_edt_learth_index_5.length() > 3 ? (val_edt_learth_index_5.substring(0, 4).contains(".") ? val_edt_learth_index_5.substring(0, 4) : val_edt_learth_index_5.substring(0, 3)) : val_edt_learth_index_5;
                        //edt_learth_index_5.setText(showText);
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex_5");
                    edt_learth_index_5.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index_5);
                }
                break;
            case R.id.edt_learth_index_6:
                if (!hasFocus) {
                    String val_edt_learth_index_6 = edt_learth_index_6.getText().toString().equals("") ? "" : edt_learth_index_6.getText().toString();
                    double val = Double.parseDouble(val_edt_learth_index_6.equals("") ? "0" : val_edt_learth_index_6);
                    if (val > 20000) {
                        AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", "", getResources().getString(R.string.o_to_20000), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                spManager.saveGraphValueByKeyName("LocalEarthIndex_6", "", editor);
                                edt_learth_index_6.setText("");
                            }
                        });
                    } else {
                        spManager.saveGraphValueByKeyName("LocalEarthIndex_6", val_edt_learth_index_6, editor);
                       /* String showText = val_edt_learth_index_6.length() > 3 ? (val_edt_learth_index_6.substring(0, 4).contains(".") ? val_edt_learth_index_6.substring(0, 4) : val_edt_learth_index_6.substring(0, 3)) : val_edt_learth_index_6;
                        edt_learth_index_6.setText(showText);*/
                        renderGraph();
                    }
                } else {
                    String showText = spManager.getGraphValueByKeyName("LocalEarthIndex_6");
                    edt_learth_index_6.setText(showText);
                    ckeckElectrodeForGraphInput(edt_learth_index_6);
                }
                break;
        }
    }

    public void setDefaultValues() {
        if (spManager == null) {
            spManager = new SharedPreferenceClass(MainActivity.getInstance());
        }
        String sp_val_Avstand_m_ec = spManager.getGraphValueByKeyName("Avstand_m_EC");
        if (!sp_val_Avstand_m_ec.equals("") || !sp_val_Avstand_m_ec.equals("0")) {
            edt_avstand_m_ec.setText(sp_val_Avstand_m_ec);
            edt_avstand_m_ec.setSelection(sp_val_Avstand_m_ec.length());
        } else {
            edt_avstand_m_ec.setText("");
        }
        edt_direction_forward.setText(spManager.getGraphValueByKeyName("DirectionForward"));
        edt_direction_backward.setText(spManager.getGraphValueByKeyName("DirectionBackward"));
        edt_gearth_index1.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex1"));
        edt_gearth_index2.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex2"));
        edt_gearth_index3.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex3"));
        edt_gearth_index4.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex4"));
        edt_gearth_index5.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex5"));
        edt_gearth_index6.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex6"));
        edt_gearth_index7.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex7"));
        edt_gearth_index8.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex8"));
        edt_gearth_index9.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex9"));
        edt_gearth_index_5.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex_5"));
        edt_gearth_index_6.setText(spManager.getGraphValueByKeyName("GlobalEarthIndex_6"));
        /*String val_edt_gearth_index1 = spManager.getGraphValueByKeyName("GlobalEarthIndex1");
        val_edt_gearth_index1 = val_edt_gearth_index1.length() > 3 ? (val_edt_gearth_index1.substring(0, 4).contains(".") ? val_edt_gearth_index1.substring(0, 4) : val_edt_gearth_index1.substring(0, 3)) : val_edt_gearth_index1;
        edt_gearth_index1.setText(val_edt_gearth_index1);
        String val_edt_gearth_index2 = spManager.getGraphValueByKeyName("GlobalEarthIndex2");
        val_edt_gearth_index2 = val_edt_gearth_index2.length() > 3 ? (val_edt_gearth_index2.substring(0, 4).contains(".") ? val_edt_gearth_index2.substring(0, 4) : val_edt_gearth_index2.substring(0, 3)) : val_edt_gearth_index2;
        edt_gearth_index2.setText(val_edt_gearth_index2);
        String val_edt_gearth_index3 = spManager.getGraphValueByKeyName("GlobalEarthIndex3");
        val_edt_gearth_index3 = val_edt_gearth_index3.length() > 3 ? (val_edt_gearth_index3.substring(0, 4).contains(".") ? val_edt_gearth_index3.substring(0, 4) : val_edt_gearth_index3.substring(0, 3)) : val_edt_gearth_index3;
        edt_gearth_index3.setText(val_edt_gearth_index3);
        String val_edt_gearth_index4 = spManager.getGraphValueByKeyName("GlobalEarthIndex4");
        val_edt_gearth_index4 = val_edt_gearth_index4.length() > 3 ? (val_edt_gearth_index4.substring(0, 4).contains(".") ? val_edt_gearth_index4.substring(0, 4) : val_edt_gearth_index4.substring(0, 3)) : val_edt_gearth_index4;
        edt_gearth_index4.setText(val_edt_gearth_index4);
        String val_edt_gearth_index5 = spManager.getGraphValueByKeyName("GlobalEarthIndex5");
        val_edt_gearth_index5 = val_edt_gearth_index5.length() > 3 ? (val_edt_gearth_index5.substring(0, 4).contains(".") ? val_edt_gearth_index5.substring(0, 4) : val_edt_gearth_index5.substring(0, 3)) : val_edt_gearth_index5;
        edt_gearth_index5.setText(val_edt_gearth_index5);
        String val_edt_gearth_index6 = spManager.getGraphValueByKeyName("GlobalEarthIndex6");
        val_edt_gearth_index6 = val_edt_gearth_index6.length() > 3 ? (val_edt_gearth_index6.substring(0, 4).contains(".") ? val_edt_gearth_index6.substring(0, 4) : val_edt_gearth_index6.substring(0, 3)) : val_edt_gearth_index6;
        edt_gearth_index6.setText(val_edt_gearth_index6);
        String val_edt_gearth_index7 = spManager.getGraphValueByKeyName("GlobalEarthIndex7");
        val_edt_gearth_index7 = val_edt_gearth_index7.length() > 3 ? (val_edt_gearth_index7.substring(0, 4).contains(".") ? val_edt_gearth_index7.substring(0, 4) : val_edt_gearth_index7.substring(0, 3)) : val_edt_gearth_index7;
        edt_gearth_index7.setText(val_edt_gearth_index7);
        String val_edt_gearth_index8 = spManager.getGraphValueByKeyName("GlobalEarthIndex8");
        val_edt_gearth_index8 = val_edt_gearth_index8.length() > 3 ? (val_edt_gearth_index8.substring(0, 4).contains(".") ? val_edt_gearth_index8.substring(0, 4) : val_edt_gearth_index8.substring(0, 3)) : val_edt_gearth_index8;
        edt_gearth_index8.setText(val_edt_gearth_index8);
        String val_edt_gearth_index9 = spManager.getGraphValueByKeyName("GlobalEarthIndex9");
        val_edt_gearth_index9 = val_edt_gearth_index9.length() > 3 ? (val_edt_gearth_index9.substring(0, 4).contains(".") ? val_edt_gearth_index9.substring(0, 4) : val_edt_gearth_index9.substring(0, 3)) : val_edt_gearth_index9;
        edt_gearth_index9.setText(val_edt_gearth_index9);
        String val_edt_gearth_index_5 = spManager.getGraphValueByKeyName("GlobalEarthIndex_5");
        val_edt_gearth_index_5 = val_edt_gearth_index_5.length() > 3 ? (val_edt_gearth_index_5.substring(0, 4).contains(".") ? val_edt_gearth_index_5.substring(0, 4) : val_edt_gearth_index_5.substring(0, 3)) : val_edt_gearth_index_5;
        edt_gearth_index_5.setText(val_edt_gearth_index_5);
        String val_edt_gearth_index_6 = spManager.getGraphValueByKeyName("GlobalEarthIndex_6");
        val_edt_gearth_index_6 = val_edt_gearth_index_6.length() > 3 ? (val_edt_gearth_index_6.substring(0, 4).contains(".") ? val_edt_gearth_index_6.substring(0, 4) : val_edt_gearth_index_6.substring(0, 3)) : val_edt_gearth_index_6;
        edt_gearth_index_6.setText(val_edt_gearth_index_6);*/

        edt_learth_index1.setText(spManager.getGraphValueByKeyName("LocalEarthIndex1"));
        edt_learth_index2.setText(spManager.getGraphValueByKeyName("LocalEarthIndex2"));
        edt_learth_index3.setText(spManager.getGraphValueByKeyName("LocalEarthIndex3"));
        edt_learth_index4.setText(spManager.getGraphValueByKeyName("LocalEarthIndex4"));
        edt_learth_index5.setText(spManager.getGraphValueByKeyName("LocalEarthIndex5"));
        edt_learth_index6.setText(spManager.getGraphValueByKeyName("LocalEarthIndex6"));
        edt_learth_index7.setText(spManager.getGraphValueByKeyName("LocalEarthIndex7"));
        edt_learth_index8.setText(spManager.getGraphValueByKeyName("LocalEarthIndex8"));
        edt_learth_index9.setText(spManager.getGraphValueByKeyName("LocalEarthIndex9"));
        edt_learth_index_5.setText(spManager.getGraphValueByKeyName("LocalEarthIndex_5"));
        edt_learth_index_6.setText(spManager.getGraphValueByKeyName("LocalEarthIndex_6"));

        /*String val_edt_learth_index1 = spManager.getGraphValueByKeyName("LocalEarthIndex1");
        val_edt_learth_index1 = val_edt_learth_index1.length() > 3 ? (val_edt_learth_index1.substring(0, 4).contains(".") ? val_edt_learth_index1.substring(0, 4) : val_edt_learth_index1.substring(0, 3)) : val_edt_learth_index1;
        edt_learth_index1.setText(val_edt_learth_index1);
        String val_edt_learth_index2 = spManager.getGraphValueByKeyName("LocalEarthIndex2");
        val_edt_learth_index2 = val_edt_learth_index2.length() > 3 ? (val_edt_learth_index2.substring(0, 4).contains(".") ? val_edt_learth_index2.substring(0, 4) : val_edt_learth_index2.substring(0, 3)) : val_edt_learth_index2;
        edt_learth_index2.setText(val_edt_learth_index2);
        String val_edt_learth_index3 = spManager.getGraphValueByKeyName("LocalEarthIndex3");
        val_edt_learth_index3 = val_edt_learth_index3.length() > 3 ? (val_edt_learth_index3.substring(0, 4).contains(".") ? val_edt_learth_index3.substring(0, 4) : val_edt_learth_index3.substring(0, 3)) : val_edt_learth_index3;
        edt_learth_index3.setText(val_edt_learth_index3);
        String val_edt_learth_index4 = spManager.getGraphValueByKeyName("LocalEarthIndex4");
        val_edt_learth_index4 = val_edt_learth_index4.length() > 3 ? (val_edt_learth_index4.substring(0, 4).contains(".") ? val_edt_learth_index4.substring(0, 4) : val_edt_learth_index4.substring(0, 3)) : val_edt_learth_index4;
        edt_learth_index4.setText(val_edt_learth_index4);
        String val_edt_learth_index5 = spManager.getGraphValueByKeyName("LocalEarthIndex5");
        val_edt_learth_index5 = val_edt_learth_index5.length() > 3 ? (val_edt_learth_index5.substring(0, 4).contains(".") ? val_edt_learth_index5.substring(0, 4) : val_edt_learth_index5.substring(0, 3)) : val_edt_learth_index5;
        edt_learth_index5.setText(val_edt_learth_index5);
        String val_edt_learth_index6 = spManager.getGraphValueByKeyName("LocalEarthIndex6");
        val_edt_learth_index6 = val_edt_learth_index6.length() > 3 ? (val_edt_learth_index6.substring(0, 4).contains(".") ? val_edt_learth_index6.substring(0, 4) : val_edt_learth_index6.substring(0, 3)) : val_edt_learth_index6;
        edt_learth_index6.setText(val_edt_learth_index6);
        String val_edt_learth_index7 = spManager.getGraphValueByKeyName("LocalEarthIndex7");
        val_edt_learth_index7 = val_edt_learth_index7.length() > 3 ? (val_edt_learth_index7.substring(0, 4).contains(".") ? val_edt_learth_index7.substring(0, 4) : val_edt_learth_index7.substring(0, 3)) : val_edt_learth_index7;
        edt_learth_index7.setText(val_edt_learth_index7);
        String val_edt_learth_index8 = spManager.getGraphValueByKeyName("LocalEarthIndex8");
        val_edt_learth_index8 = val_edt_learth_index8.length() > 3 ? (val_edt_learth_index8.substring(0, 4).contains(".") ? val_edt_learth_index8.substring(0, 4) : val_edt_learth_index8.substring(0, 3)) : val_edt_learth_index8;
        edt_learth_index8.setText(val_edt_learth_index8);
        String val_edt_learth_index9 = spManager.getGraphValueByKeyName("LocalEarthIndex9");
        val_edt_learth_index9 = val_edt_learth_index9.length() > 3 ? (val_edt_learth_index9.substring(0, 4).contains(".") ? val_edt_learth_index9.substring(0, 4) : val_edt_learth_index9.substring(0, 3)) : val_edt_learth_index9;
        edt_learth_index9.setText(val_edt_learth_index9);
        String val_edt_learth_index_5 = spManager.getGraphValueByKeyName("LocalEarthIndex_5");
        val_edt_learth_index_5 = val_edt_learth_index_5.length() > 3 ? (val_edt_learth_index_5.substring(0, 4).contains(".") ? val_edt_learth_index_5.substring(0, 4) : val_edt_learth_index_5.substring(0, 3)) : val_edt_learth_index_5;
        edt_learth_index_5.setText(val_edt_learth_index_5);
        String val_edt_learth_index_6 = spManager.getGraphValueByKeyName("LocalEarthIndex_6");
        val_edt_learth_index_6 = val_edt_learth_index_6.length() > 3 ? (val_edt_learth_index_6.substring(0, 4).contains(".") ? val_edt_learth_index_6.substring(0, 4) : val_edt_learth_index_6.substring(0, 3)) : val_edt_learth_index_6;
        edt_learth_index_6.setText(val_edt_learth_index_6);*/

        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        MainActivity mainActivity = MainActivity.getInstance();
        int elektrode = 0;
        if (variablesFragment != null && mainActivity.variablePageClicked) {
            elektrode = variablesFragment.selectedElektrodePosition();
        } else {
            String electrode = spManager.getVariablerValueByKeyName("ElekrodeSystem");
            electrode = electrode.contains(".") ? electrode.substring(0, electrode.indexOf(".")) : electrode;
            elektrode = Integer.parseInt(electrode);
        }
        graphType = elektrode == 1 ? "Global" : elektrode == 2 ? "Local" : "defaultElectrode";
        hideUnhideGraph(graphType);
    }

    public void hideUnhideGraph(String elektrodeType) {
        if (elektrodeType.equals("Local")) {
            ll_button_view.setVisibility(View.GONE);
            globalwebview.setVisibility(View.GONE);
            ll_global_jord_values.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
            ll_local_electrode_values.setVisibility(View.VISIBLE);
            webview.setVisibility(View.VISIBLE);
            drawLocalGraph("file:///android_asset/localgraph.html");
        } else if (elektrodeType.equals("defaultElectrode")) {
            webview.setVisibility(View.GONE);
            ll_local_electrode_values.setVisibility(View.GONE);
            globalwebview.setVisibility(View.VISIBLE);
            ll_global_jord_values.setVisibility(View.VISIBLE);
            drawGlobalGraph("file:///android_asset/globalgraph.html");
        } else {
            ll_button_view.setVisibility(View.GONE);
            globalwebview.setVisibility(View.VISIBLE);
            ll_global_jord_values.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
            webview.setVisibility(View.VISIBLE);
            ll_local_electrode_values.setVisibility(View.VISIBLE);
            drawLocalGraph("file:///android_asset/localgraph.html");
            drawGlobalGraph("file:///android_asset/globalgraph.html");
        }
        calcDistance();
        renderGraph();
    }

    public void calcDistance() {
        float[] n = new float[10];
        int totalDistance = Integer.parseInt(edt_avstand_m_ec.getText().toString().equals("") ? "0" : edt_avstand_m_ec.getText().toString());
        float _totalDistance = (float) totalDistance;
        if (_totalDistance > 0) {
            for (int i = 0; i < 10; i++) {
                float temp = _totalDistance / 10 * (i + 1);
                temp = roundToHalf(temp + "");
                n[i] = temp;
            }
            String _p2 = n[0] >= 1000 ? (String.valueOf(n[0]).contains(".") ? (n[0] + "").substring(0, (n[0] + "").lastIndexOf(".")) : n[0] + "") : n[0] + "";
            String _p3 = n[1] >= 1000 ? (String.valueOf(n[1]).contains(".") ? (n[1] + "").substring(0, (n[1] + "").lastIndexOf(".")) : n[1] + "") : n[1] + "";
            String _p4 = n[2] >= 1000 ? (String.valueOf(n[2]).contains(".") ? (n[2] + "").substring(0, (n[2] + "").lastIndexOf(".")) : n[2] + "") : n[2] + "";
            String _p5 = n[3] >= 1000 ? (String.valueOf(n[3]).contains(".") ? (n[3] + "").substring(0, (n[3] + "").lastIndexOf(".")) : n[3] + "") : n[3] + "";
            String _p6 = n[4] >= 1000 ? (String.valueOf(n[4]).contains(".") ? (n[4] + "").substring(0, (n[4] + "").lastIndexOf(".")) : n[4] + "") : n[4] + "";
            String _p7 = n[5] >= 1000 ? (String.valueOf(n[5]).contains(".") ? (n[5] + "").substring(0, (n[5] + "").lastIndexOf(".")) : n[5] + "") : n[5] + "";
            String _p8 = n[6] >= 1000 ? (String.valueOf(n[6]).contains(".") ? (n[6] + "").substring(0, (n[6] + "").lastIndexOf(".")) : n[6] + "") : n[6] + "";
            String _p9 = n[7] >= 1000 ? (String.valueOf(n[7]).contains(".") ? (n[7] + "").substring(0, (n[7] + "").lastIndexOf(".")) : n[7] + "") : n[7] + "";
            String _p10 = n[8] >= 1000 ? (String.valueOf(n[8]).contains(".") ? (n[8] + "").substring(0, (n[8] + "").lastIndexOf(".")) : n[8] + "") : n[8] + "";
            p2.setText(_p2);
            p3.setText(_p3);
            p4.setText(_p4);
            p5.setText(_p5);
            p6.setText(_p6);
            p7.setText(_p7);
            p8.setText(_p8);
            p9.setText(_p9);
            p10.setText(_p10);
            p5_minus.setText("-" + _p6);
            p6_minus.setText("-" + _p7);
        } else {
            p2.setText("");
            p3.setText("");
            p4.setText("");
            p5.setText("");
            p6.setText("");
            p7.setText("");
            p8.setText("");
            p9.setText("");
            p10.setText("");
            p5_minus.setText("");
            p6_minus.setText("");
        }
    }

    public float roundToHalf(String value) {
        float converted = Float.parseFloat(value);
        int _converted = (int) converted;
        float decimal = (converted - Integer.parseInt(_converted + "", 10));
        decimal = Math.round(decimal * 10);
        if (decimal == 5)
            return (float) (Integer.parseInt(_converted + "", 10) + 0.5);
        if ((decimal < 3) || (decimal > 7))
            return Math.round(converted);
        else
            return (float) (Integer.parseInt(_converted + "", 10) + 0.5);
    }

    public void drawLocalGraph(String file_path) {
        WebSettings webSetting = webview.getSettings();
        webSetting.setDatabaseEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowFileAccessFromFileURLs(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setDisplayZoomControls(true);
        webview.setWebViewClient(new WebViewClient());
        webview.addJavascriptInterface(new JsHandler(), "JsHandler");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head>");
            sb.append("<Title>Vennligst vent...</Title>");
            sb.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
            sb.append("<meta http-equiv='cache-control' content='max-age=0' />");
            sb.append("<meta http-equiv='cache-control' content='no-cache' />");
            sb.append("<meta http-equiv='expires' content='0' />");
            sb.append("<meta http-equiv='expires' content='Tue, 01 Jan 1980 1:00:00 GMT' />");
            sb.append("<meta http-equiv='pragma' content='no-cache' />");
            sb.append("<meta charset='UTF-8'>");
            sb.append("<meta name='Generator' content='Cocoa HTML Writer'>");
            sb.append("<meta name='CocoaVersion' content='1504.81'>");
            sb.append("<meta name='viewport' content='width=device-width,height=device-height, initial-scale=1.0' />");
            sb.append("<style type='text/css'>" +
                    " html,body,iframe {" +
                    "height: 100%;" +
                    "width: 100%;" +
                    "margin: 0;" +
                    "padding: 0;" +
                    "min-height :100%;" +
                    "}" +
                    "</style>");
            sb.append("</head>");
            sb.append("<body>");
            sb.append(String.format(" <iframe id='myFrame' frameborder='0' src='%s'>", file_path));
            sb.append("</iframe></body></html>");
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                }

                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
            webview.loadDataWithBaseURL("", sb.toString(), "text/html", "utf-8", "");
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
    }

    public void drawGlobalGraph(String file_path) {
        WebSettings webSetting = globalwebview.getSettings();
        webSetting.setDatabaseEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowFileAccessFromFileURLs(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setDisplayZoomControls(true);
        globalwebview.setWebViewClient(new WebViewClient());
        globalwebview.addJavascriptInterface(new JsHandler(), "JsHandler");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><head>");
            sb.append("<Title>Vennligst vent...</Title>");
            sb.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
            sb.append("<meta http-equiv='cache-control' content='max-age=0' />");
            sb.append("<meta http-equiv='cache-control' content='no-cache' />");
            sb.append("<meta http-equiv='expires' content='0' />");
            sb.append("<meta http-equiv='expires' content='Tue, 01 Jan 1980 1:00:00 GMT' />");
            sb.append("<meta http-equiv='pragma' content='no-cache' />");
            sb.append("<meta charset='UTF-8'>");
            sb.append("<meta name='Generator' content='Cocoa HTML Writer'>");
            sb.append("<meta name='CocoaVersion' content='1504.81'>");
            sb.append("<meta name='viewport' content='width=device-width,height=device-height, initial-scale=1.0' />");
            sb.append("<style type='text/css'>" +
                    " html,body,iframe {" +
                    "height: 100%;" +
                    "width: 100%;" +
                    "margin: 0;" +
                    "padding: 0;" +
                    "min-height :100%;" +
                    "}" +
                    "</style>");
            sb.append("</head>");
            sb.append("<body>");
            sb.append(String.format("<iframe id='myFrame' frameborder='0' src='%s'>", file_path));
            sb.append("</iframe></body></html>");
            globalwebview.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                }

                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }
            });
            globalwebview.loadDataWithBaseURL("", sb.toString(), "text/html", "utf-8", "");
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
    }

    public class JsHandler {
        @JavascriptInterface
        public String graphInputValues() {
            return global_arr;
        }

        @JavascriptInterface
        public String localGraphInputValues() {
            return local_arr;
        }
    }

    public void renderGraph() {
        /*String index1 = edt_gearth_index1.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index1.getText().toString()) == 0 ? "empty" : edt_gearth_index1.getText().toString();
        String index2 = edt_gearth_index2.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index2.getText().toString()) == 0 ? "empty" : edt_gearth_index2.getText().toString();
        String index3 = edt_gearth_index3.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index3.getText().toString()) == 0 ? "empty" : edt_gearth_index3.getText().toString();
        String index4 = edt_gearth_index4.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index4.getText().toString()) == 0 ? "empty" : edt_gearth_index4.getText().toString();
        String index5 = edt_gearth_index5.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index5.getText().toString()) == 0 ? "empty" : edt_gearth_index5.getText().toString();
        String index6 = edt_gearth_index6.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index6.getText().toString()) == 0 ? "empty" : edt_gearth_index6.getText().toString();
        String index7 = edt_gearth_index7.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index7.getText().toString()) == 0 ? "empty" : edt_gearth_index7.getText().toString();
        String index8 = edt_gearth_index8.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index8.getText().toString()) == 0 ? "empty" : edt_gearth_index8.getText().toString();
        String index9 = edt_gearth_index9.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index9.getText().toString()) == 0 ? "empty" : edt_gearth_index9.getText().toString();
        String index10 = edt_gearth_index_5.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index_5.getText().toString()) == 0 ? "empty" : edt_gearth_index_5.getText().toString();
        String index11 = edt_gearth_index_6.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_gearth_index_6.getText().toString()) == 0 ? "empty" : edt_gearth_index_6.getText().toString();*/

        String ind_1 = spManager.getGraphValueByKeyName("GlobalEarthIndex1");
        String index1 = ind_1.equals("") ? "empty" : Double.parseDouble(ind_1) == 0 ? "empty" : ind_1;
        String ind_2 = spManager.getGraphValueByKeyName("GlobalEarthIndex2");
        String index2 = ind_2.equals("") ? "empty" : Double.parseDouble(ind_2) == 0 ? "empty" : ind_2;
        String ind_3 = spManager.getGraphValueByKeyName("GlobalEarthIndex3");
        String index3 = ind_3.equals("") ? "empty" : Double.parseDouble(ind_3) == 0 ? "empty" : ind_3;
        String ind_4 = spManager.getGraphValueByKeyName("GlobalEarthIndex4");
        String index4 = ind_4.equals("") ? "empty" : Double.parseDouble(ind_4) == 0 ? "empty" : ind_4;
        String ind_5 = spManager.getGraphValueByKeyName("GlobalEarthIndex5");
        String index5 = ind_5.equals("") ? "empty" : Double.parseDouble(ind_5) == 0 ? "empty" : ind_5;
        String ind_6 = spManager.getGraphValueByKeyName("GlobalEarthIndex6");
        String index6 = ind_6.equals("") ? "empty" : Double.parseDouble(ind_6) == 0 ? "empty" : ind_6;
        String ind_7 = spManager.getGraphValueByKeyName("GlobalEarthIndex7");
        String index7 = ind_7.equals("") ? "empty" : Double.parseDouble(ind_7) == 0 ? "empty" : ind_7;
        String ind_8 = spManager.getGraphValueByKeyName("GlobalEarthIndex8");
        String index8 = ind_8.equals("") ? "empty" : Double.parseDouble(ind_8) == 0 ? "empty" : ind_8;
        String ind_9 = spManager.getGraphValueByKeyName("GlobalEarthIndex9");
        String index9 = ind_9.equals("") ? "empty" : Double.parseDouble(ind_9) == 0 ? "empty" : ind_9;
        String ind_10 = spManager.getGraphValueByKeyName("GlobalEarthIndex_5");
        String index10 = ind_10.equals("") ? "empty" : Double.parseDouble(ind_10) == 0 ? "empty" : ind_10;
        String ind_11 = spManager.getGraphValueByKeyName("GlobalEarthIndex_6");
        String index11 = ind_11.equals("") ? "empty" : Double.parseDouble(ind_11) == 0 ? "empty" : ind_11;

        /*String index_l_1 = edt_learth_index1.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index1.getText().toString()) == 0 ? "empty" : edt_learth_index1.getText().toString();
        String index_l_2 = edt_learth_index2.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index2.getText().toString()) == 0 ? "empty" : edt_learth_index2.getText().toString();
        String index_l_3 = edt_learth_index3.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index3.getText().toString()) == 0 ? "empty" : edt_learth_index3.getText().toString();
        String index_l_4 = edt_learth_index4.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index4.getText().toString()) == 0 ? "empty" : edt_learth_index4.getText().toString();
        String index_l_5 = edt_learth_index5.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index5.getText().toString()) == 0 ? "empty" : edt_learth_index5.getText().toString();
        String index_l_6 = edt_learth_index6.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index6.getText().toString()) == 0 ? "empty" : edt_learth_index6.getText().toString();
        String index_l_7 = edt_learth_index7.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index7.getText().toString()) == 0 ? "empty" : edt_learth_index7.getText().toString();
        String index_l_8 = edt_learth_index8.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index8.getText().toString()) == 0 ? "empty" : edt_learth_index8.getText().toString();
        String index_l_9 = edt_learth_index9.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index9.getText().toString()) == 0 ? "empty" : edt_learth_index9.getText().toString();
        String index_l_10 = edt_learth_index_5.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index_5.getText().toString()) == 0 ? "empty" : edt_learth_index_5.getText().toString();
        String index_l_11 = edt_learth_index_6.getText().toString().equals("") ? "empty" : Double.parseDouble(edt_learth_index_6.getText().toString()) == 0 ? "empty" : edt_learth_index_6.getText().toString();*/

        String ind_l_1 = spManager.getGraphValueByKeyName("LocalEarthIndex1");
        String index_l_1 = ind_l_1.equals("") ? "empty" : Double.parseDouble(ind_l_1) == 0 ? "empty" : ind_l_1;
        String ind_l_2 = spManager.getGraphValueByKeyName("LocalEarthIndex2");
        String index_l_2 = ind_l_2.equals("") ? "empty" : Double.parseDouble(ind_l_2) == 0 ? "empty" : ind_l_2;
        String ind_l_3 = spManager.getGraphValueByKeyName("LocalEarthIndex3");
        String index_l_3 = ind_l_3.equals("") ? "empty" : Double.parseDouble(ind_l_3) == 0 ? "empty" : ind_l_3;
        String ind_l_4 = spManager.getGraphValueByKeyName("LocalEarthIndex4");
        String index_l_4 = ind_l_4.equals("") ? "empty" : Double.parseDouble(ind_l_4) == 0 ? "empty" : ind_l_4;
        String ind_l_5 = spManager.getGraphValueByKeyName("LocalEarthIndex5");
        String index_l_5 = ind_l_5.equals("") ? "empty" : Double.parseDouble(ind_l_5) == 0 ? "empty" : ind_l_5;
        String ind_l_6 = spManager.getGraphValueByKeyName("LocalEarthIndex6");
        String index_l_6 = ind_l_6.equals("") ? "empty" : Double.parseDouble(ind_l_6) == 0 ? "empty" : ind_l_6;
        String ind_l_7 = spManager.getGraphValueByKeyName("LocalEarthIndex7");
        String index_l_7 = ind_l_7.equals("") ? "empty" : Double.parseDouble(ind_l_7) == 0 ? "empty" : ind_l_7;
        String ind_l_8 = spManager.getGraphValueByKeyName("LocalEarthIndex8");
        String index_l_8 = ind_l_8.equals("") ? "empty" : Double.parseDouble(ind_l_8) == 0 ? "empty" : ind_l_8;
        String ind_l_9 = spManager.getGraphValueByKeyName("LocalEarthIndex9");
        String index_l_9 = ind_l_9.equals("") ? "empty" : Double.parseDouble(ind_l_9) == 0 ? "empty" : ind_l_9;
        String ind_l_10 = spManager.getGraphValueByKeyName("LocalEarthIndex_5");
        String index_l_10 = ind_l_10.equals("") ? "empty" : Double.parseDouble(ind_l_10) == 0 ? "empty" : ind_l_10;
        String ind_l_11 = spManager.getGraphValueByKeyName("LocalEarthIndex_6");
        String index_l_11 = ind_l_11.equals("") ? "empty" : Double.parseDouble(ind_l_11) == 0 ? "empty" : ind_l_11;

        edt_gearth_index1.setSelection(edt_gearth_index1.getText().toString().length());
        edt_gearth_index2.setSelection(edt_gearth_index2.getText().toString().length());
        edt_gearth_index3.setSelection(edt_gearth_index3.getText().toString().length());
        edt_gearth_index4.setSelection(edt_gearth_index4.getText().toString().length());
        edt_gearth_index5.setSelection(edt_gearth_index5.getText().toString().length());
        edt_gearth_index6.setSelection(edt_gearth_index6.getText().toString().length());
        edt_gearth_index7.setSelection(edt_gearth_index7.getText().toString().length());
        edt_gearth_index8.setSelection(edt_gearth_index8.getText().toString().length());
        edt_gearth_index9.setSelection(edt_gearth_index9.getText().toString().length());
        edt_gearth_index_5.setSelection(edt_gearth_index_5.getText().toString().length());
        edt_gearth_index_6.setSelection(edt_gearth_index_6.getText().toString().length());

        edt_learth_index1.setSelection(edt_learth_index1.getText().toString().length());
        edt_learth_index2.setSelection(edt_learth_index2.getText().toString().length());
        edt_learth_index3.setSelection(edt_learth_index3.getText().toString().length());
        edt_learth_index4.setSelection(edt_learth_index4.getText().toString().length());
        edt_learth_index5.setSelection(edt_learth_index5.getText().toString().length());
        edt_learth_index6.setSelection(edt_learth_index6.getText().toString().length());
        edt_learth_index7.setSelection(edt_learth_index7.getText().toString().length());
        edt_learth_index8.setSelection(edt_learth_index8.getText().toString().length());
        edt_learth_index9.setSelection(edt_learth_index9.getText().toString().length());
        edt_learth_index_5.setSelection(edt_learth_index_5.getText().toString().length());
        edt_learth_index_6.setSelection(edt_learth_index_6.getText().toString().length());

        global_arr = "";
        global_arr = index1;
        global_arr += "," + index2;
        global_arr += "," + index3;
        global_arr += "," + index4;
        global_arr += "," + index5;
        global_arr += "," + index6;
        global_arr += "," + index7;
        global_arr += "," + index8;
        global_arr += "," + index9;
        global_arr += "," + index10;
        global_arr += "," + index11;

        local_arr = "";
        local_arr = index_l_1;
        local_arr += "," + index_l_2;
        local_arr += "," + index_l_3;
        local_arr += "," + index_l_4;
        local_arr += "," + index_l_5;
        local_arr += "," + index_l_6;
        local_arr += "," + index_l_7;
        local_arr += "," + index_l_8;
        local_arr += "," + index_l_9;
        local_arr += "," + index_l_10;
        local_arr += "," + index_l_11;

        if (graphType.equals("Local")) {
            drawLocalGraph("file:///android_asset/localgraph.html");
        } else {
            drawLocalGraph("file:///android_asset/localgraph.html");
            drawGlobalGraph("file:///android_asset/globalgraph.html");
        }
    }

    public void ckeckElectrodeForGraphInput(EditText editText) {
        if (graphType.equals("defaultElectrode")) {
            editText.setEnabled(false);
            Toast.makeText(getActivity(), getResources().getString(R.string.no_electrode_selected), Toast.LENGTH_SHORT).show();
        }
    }
}