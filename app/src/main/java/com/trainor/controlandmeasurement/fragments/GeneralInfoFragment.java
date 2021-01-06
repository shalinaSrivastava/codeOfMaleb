package com.trainor.controlandmeasurement.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.trainor.controlandmeasurement.Activities.AnleggActivity;
import com.trainor.controlandmeasurement.Activities.UndermappeActivity;
import com.trainor.controlandmeasurement.BuildConfig;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.WebServiceCall;
import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GeneralInfoFragment extends Fragment implements View.OnFocusChangeListener {
    public static GeneralInfoFragment instance;
    public SharedPreferences.Editor editor;
    public SoapObject response;
    public List<AssignmentEntity> assignmentInfoList;
    SharedPreferenceClass spManager;
    @BindView(R.id.txt_latitude)
    TextView txt_latitude;
    @BindView(R.id.txt_accuracy)
    TextView txt_accuracy;
    @BindView(R.id.txt_longitude)
    TextView txt_longitude;
    @BindView(R.id.txt_accuracy_new)
    TextView txt_accuracy_new;
    @BindView(R.id.gps_button)
    Button gps_button;
    @BindView(R.id.edt_measuring_point_ID)
    public EditText edt_measuring_point_ID;
    @BindView(R.id.edt_loc_indication)
    EditText edt_loc_indication;
    @BindView(R.id.edt_Anlegg)
    EditText edt_Anlegg;
    @BindView(R.id.spn_subfolder)
    EditText spn_subfolder;
    //Spinner spn_subfolder;
    @BindView(R.id.spn_subfolder2)
    EditText spn_subfolder2;
    //Spinner spn_subfolder2;
    @BindView(R.id.ll_undermappe)
    LinearLayout ll_undermappe;
    @BindView(R.id.ll_undermappe2)
    LinearLayout ll_undermappe2;

    public long adminID;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    public boolean mRequestingLocationUpdates, isMPIDUnique;
    public ViewModel viewModel;
    List<FolderEntity> folderList = new LinkedList<>();
    ConnectionDetector connectionDetector;
    ProgressDialog pDialog;
    boolean callApiOnce = true;
    String token = "", anleggID = "", folderID = "";

    public GeneralInfoFragment() {
        // Required empty public constructor
    }

    public synchronized static GeneralInfoFragment getInstance() {
        if (instance == null) {
            instance = new GeneralInfoFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectionDetector = ConnectionDetector.getInstance(getActivity());
        assignmentInfoList = new ArrayList<>();
        if (editor == null) {
            editor = MainActivity.getInstance().getSharedPreferences("GeneralInfoPref", Context.MODE_PRIVATE).edit();
        }
        viewModel = ViewModelProviders.of(MainActivity.getInstance()).get(ViewModel.class);
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_general_info, container, false);
        ButterKnife.bind(GeneralInfoFragment.this, view);
        edt_Anlegg.setFocusable(false);
        spn_subfolder.setFocusable(false);
        spn_subfolder2.setFocusable(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        edt_measuring_point_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_measuring_point_ID.getText().length() > 0) {
                    edt_measuring_point_ID.setBackground(getResources().getDrawable(R.drawable.rounder_corner_edittext));
                } else {
                    edt_measuring_point_ID.setBackground(getResources().getDrawable(R.drawable.edit_text_mandatory));
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edt_measuring_point_ID.getText().length() > 0) {
                    edt_measuring_point_ID.setBackground(getResources().getDrawable(R.drawable.rounder_corner_edittext));
                } else {
                    edt_measuring_point_ID.setBackground(getResources().getDrawable(R.drawable.edit_text_mandatory));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(" ")) {
                    if (edt_measuring_point_ID.getText().length() > 0) {
                        edt_measuring_point_ID.setBackground(getResources().getDrawable(R.drawable.rounder_corner_edittext));
                    } else {
                        edt_measuring_point_ID.setBackground(getResources().getDrawable(R.drawable.edit_text_mandatory));
                    }
                } else {
                    edt_measuring_point_ID.setText("");
                }
            }
        });
        getControls();
        return view;
    }

    public void getControls() {
        spManager = new SharedPreferenceClass(MainActivity.getInstance());
        token = spManager.getLoginInfoValueByKeyName("Token");
        if (edt_loc_indication != null) {
            edt_loc_indication.setOnFocusChangeListener(this);
        }
        if (edt_Anlegg != null) {
            edt_Anlegg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (connectionDetector.isConnectingToInternet()) {
                        getAssignments(token, spManager.getLoginInfoValueByKeyName("CompanyID"));
                    } else {
                        Intent intent = new Intent(getActivity(), AnleggActivity.class);
                        intent.putExtra("LoginToken", token);
                        intent.putExtra("AdminID", adminID + "");
                        intent.putExtra("CompanyID", spManager.getLoginInfoValueByKeyName("CompanyID"));
                        intent.putExtra("From", "Anlegg");
                        startActivityForResult(intent, 122);
                    }*/
                    Intent intent = new Intent(getActivity(), AnleggActivity.class);
                    intent.putExtra("LoginToken", token);
                    intent.putExtra("AdminID", adminID + "");
                    intent.putExtra("CompanyID", spManager.getLoginInfoValueByKeyName("CompanyID"));
                    intent.putExtra("From", "Anlegg");
                    startActivityForResult(intent, 122);
                }
            });
        }
        if (spn_subfolder != null) {
            spn_subfolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UndermappeActivity.class);
                    intent.putExtra("LoginToken", token);
                    intent.putExtra("AnleggID", anleggID);
                    intent.putExtra("From", "Select Undermappe");
                    startActivityForResult(intent, 123);
                }
            });
        }
        if (spn_subfolder2 != null) {
            spn_subfolder2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UndermappeActivity.class);
                    intent.putExtra("FolderID", folderID);
                    intent.putExtra("AnleggID", anleggID);
                    intent.putExtra("From", getResources().getString(R.string.select_undermapee2));
                    startActivityForResult(intent, 124);
                }
            });
        }
        setDefaultValue();
    }

    public void setDefaultValue() {
        mCurrentLocation = null;
        int anleggId = 0;
        String _letterID = spManager.getGeneralInfoValueByKeyName("letterID");
        long letterID = _letterID.equals("") ? 0 : Long.parseLong(_letterID);
        anleggID = spManager.getGeneralInfoValueByKeyName("AnleggID");
        String undermappeID = spManager.getGeneralInfoValueByKeyName("Undermappe2");
        if (!anleggID.equals("")) {
            anleggId = Integer.parseInt(anleggID);
            if (undermappeID.equals("")) {
                spn_subfolder.setText("Velg Undermappe");
                ll_undermappe.setVisibility(View.VISIBLE);
                ll_undermappe2.setVisibility(View.GONE);
            } else {
                List<FolderEntity> list = viewModel.getParentID(anleggID, undermappeID);
                if (list != null && list.size() > 0) {
                    folderList = list;
                    FolderEntity entity = list.get(0);
                    spn_subfolder2.setText(entity.name);
                    String parentID = entity.parentID;
                    list.clear();
                    list = viewModel.getFolder(anleggID, parentID);
                    if (list != null && list.size() > 0) {
                        spn_subfolder.setText(list.get(0).name);
                        ll_undermappe.setVisibility(View.VISIBLE);
                        ll_undermappe2.setVisibility(View.VISIBLE);
                    }
                } else {
                    spn_subfolder.setText("Velg Undermappe");
                    ll_undermappe.setVisibility(View.VISIBLE);
                    ll_undermappe2.setVisibility(View.GONE);
                }
            }
        } else {
            ll_undermappe.setVisibility(View.GONE);
            ll_undermappe2.setVisibility(View.GONE);
        }

        String mpid = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
        if (spManager.getGeneralInfoValueByKeyName("OldMPID").equals("")) {
            spManager.saveGeneralInfoValueByKeyName("OldMPID", mpid, editor);
        }
        edt_measuring_point_ID.setText(mpid);
        String locationDesc = spManager.getGeneralInfoValueByKeyName("LocationIndication").equals("anyType{}") ? "" : spManager.getGeneralInfoValueByKeyName("LocationIndication");
        edt_loc_indication.setText(locationDesc);
        String assignmentName = viewModel.getAssignment(anleggId) == null ? "Velg anlegg" : viewModel.getAssignment(anleggId).assignmentName;
        edt_Anlegg.setText(assignmentName);
        txt_latitude.setText(spManager.getGeneralInfoValueByKeyName("latitude"));
        txt_longitude.setText(spManager.getGeneralInfoValueByKeyName("longitude"));
        txt_accuracy.setText(spManager.getGeneralInfoValueByKeyName("altitude"));
        txt_accuracy_new.setText(spManager.getGeneralInfoValueByKeyName("accuracy"));

        updateLocationUI();
        if (letterID > 0) {
            edt_measuring_point_ID.clearFocus();
            edt_measuring_point_ID.setFocusable(false);
            edt_measuring_point_ID.setOnFocusChangeListener(null);
            edt_measuring_point_ID.setTextColor(getResources().getColor(R.color.tab_grey_color));
        } else {
            edt_measuring_point_ID.clearFocus();
            edt_measuring_point_ID.setFocusable(true);
            edt_measuring_point_ID.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if (!spManager.getLoginInfoValueByKeyName("AdminID").equals("")) {
            adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        }
        txt_latitude.setText(spManager.getGeneralInfoValueByKeyName("latitude"));
        txt_longitude.setText(spManager.getGeneralInfoValueByKeyName("longitude"));
        txt_accuracy.setText(spManager.getGeneralInfoValueByKeyName("altitude"));
        txt_accuracy_new.setText(spManager.getGeneralInfoValueByKeyName("accuracy"));
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());
        mRequestingLocationUpdates = false;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            @SuppressLint("DefaultLocale") String accuracy_new;
            double device_latitude = mCurrentLocation.getLatitude();
            double device_logitude = mCurrentLocation.getLongitude();
            spManager.saveGeneralInfoValueByKeyName("UploadLatitude", device_latitude + "", editor);
            spManager.saveGeneralInfoValueByKeyName("UploadLongitude", device_logitude + "", editor);
            String converted_latitude = convertLatLong(device_latitude, "Latitude");
            String converted_longitude = convertLatLong(device_logitude, "Longitude");
            txt_latitude.setText(converted_latitude);
            txt_longitude.setText(converted_longitude);
            @SuppressLint("DefaultLocale") String _altitude = String.format("%.1f", mCurrentLocation.getAltitude());
            _altitude = _altitude.contains(".") ? _altitude.substring(0, _altitude.lastIndexOf('.')) : _altitude.substring(0, _altitude.lastIndexOf(','));
            txt_accuracy.setText(_altitude);


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                accuracy_new = String.format("%.1f", mCurrentLocation.getVerticalAccuracyMeters());
            }else{
                accuracy_new = String.format("%.1f", mCurrentLocation.getAccuracy());
            }
            txt_accuracy_new.setText(accuracy_new);
            spManager.saveGeneralInfoValueByKeyName("latitude", txt_latitude.getText().toString(), editor);
            spManager.saveGeneralInfoValueByKeyName("longitude", txt_longitude.getText().toString(), editor);
            spManager.saveGeneralInfoValueByKeyName("altitude", txt_accuracy.getText().toString(), editor);
            spManager.saveGeneralInfoValueByKeyName("accuracy", txt_accuracy_new.getText().toString(), editor);
        }

        if (mRequestingLocationUpdates) {
            gps_button.setText(getResources().getString(R.string.stop_gps));
        } else {
            gps_button.setText(getResources().getString(R.string.start_gps));
        }
    }


    @OnClick(R.id.gps_button)
    public void startLocationButtonClick(View view) {
        if (gps_button.getText().equals("Start GPS")) {
            Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            mRequestingLocationUpdates = true;
                            startLocationUpdates();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                openSettings();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else {
            if (gps_button.getText().equals(getResources().getString(R.string.stop_gps))) {
                gps_button.setText(getResources().getString(R.string.start_gps));
                stopLocationUpdates();
            }
        }
    }

    public void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient
                    .removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startLocationUpdates() {
        if (mLocationCallback == null) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mCurrentLocation = locationResult.getLastLocation();
                    updateLocationUI();
                }
            };
        }
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("Error", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                        }
                        updateLocationUI();
                    }
                });
    }

    public String convertLatLong(double value, String type) {
        if (value > 0) {
            String _val = String.format("%5f", value);
            _val = _val.contains(",") ? _val.replace(",", ".") : _val;
            value = Double.parseDouble(_val);
            StringBuilder builder = new StringBuilder();
            String longitudeDegrees = Location.convert(Math.abs(value), Location.FORMAT_SECONDS);
            String[] longitudeSplit = longitudeDegrees.split(":");
            builder.append(longitudeSplit[0]);
            builder.append("°");
            builder.append(" ");
            builder.append(longitudeSplit[1]);
            builder.append("'");
            builder.append(" ");
            String val = longitudeSplit[2];
            if (val.contains(",") || val.contains(".")) {
                val = longitudeSplit[2].contains(",") ? longitudeSplit[2].substring(0, longitudeSplit[2].indexOf(",")) : longitudeSplit[2].substring(0, longitudeSplit[2].indexOf("."));
            }
            builder.append(val);
            builder.append("\"");
            builder.append(" ");
            if (type.equals("Latitude")) {
                if (value < 0) {
                    builder.append("S ");
                } else {
                    builder.append("N ");
                }
            } else {
                if (value < 0) {
                    builder.append("W ");
                } else {
                    builder.append("E ");
                }
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edt_measuring_point_ID:
                if (!hasFocus) {
                    String measurement_pt_id = edt_measuring_point_ID.getText().toString().trim().equals("") ? "" : edt_measuring_point_ID.getText().toString().trim();
                    String mpid = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
                    if (!measurement_pt_id.equals("")) {
                        if (!measurement_pt_id.equals(mpid)) {
                            List<LetterEntity> _list = viewModel.measurePointIDExists(adminID, measurement_pt_id);
                            if (_list != null && _list.size() > 0) {
                                edt_measuring_point_ID.setText(mpid);
                                edt_measuring_point_ID.setSelection(mpid.length());
                                MainActivity.getInstance().setTitle();
                                Toast.makeText(getActivity(), getResources().getString(R.string.measurement_pt_id_already_exists), Toast.LENGTH_SHORT).show();
                            } else {
                                if (connectionDetector.isConnectingToInternet()) {
                                    checkMPIDExists(measurement_pt_id, mpid);
                                } else {
                                    if (!measurement_pt_id.equals(mpid)) {
                                        replaceMpID(mpid, measurement_pt_id);
                                    }
                                    edt_measuring_point_ID.setText(measurement_pt_id);
                                    edt_measuring_point_ID.setSelection(measurement_pt_id.length());
                                    spManager.saveGeneralInfoValueByKeyName("ValidID", "False", editor);
                                    long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
                                    List<LetterEntity> __list = viewModel.letterLocalExists(adminID, _letterID, mpid);
                                    if (_list != null && _list.size() > 0) {
                                        LetterEntity _entity = __list.get(0);
                                        _entity.ID = _list.get(0).ID;
                                        _entity.letterID = _list.get(0).letterID;
                                        _entity.measurePointID = measurement_pt_id;
                                        viewModel.updateLetter(_entity);
                                    }
                                    spManager.saveGeneralInfoValueByKeyName("ActiveMeasurementPointID", measurement_pt_id, editor);
                                    MainActivity.getInstance().setTitle();
                                    HomeFragment.getInstance().setActiveCertificate();
                                }
                            }
                        }
                    } else {
                        spManager.saveGeneralInfoValueByKeyName("MeasurementPointID", "", editor);
                    }
                }
                break;
            case R.id.edt_loc_indication:
                if (!hasFocus) {
                    String locationIndication = edt_loc_indication.getText().toString().trim().equals("") ? "" : edt_loc_indication.getText().toString().trim();
                    spManager.saveGeneralInfoValueByKeyName("LocationIndication", locationIndication + "", editor);
                }
                break;
        }
    }

    public void checkMPIDExists(final String newMPID, final String oldMPID) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                MainActivity.getInstance().showWaitDialog(getResources().getString(R.string.please_wait));
                isMPIDUnique = false;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                SoapObject request = new SoapObject(URLs.NAMESPACE, "getLetters");
                request.addProperty("loginToken", token);
                request.addProperty("measurePointId", newMPID);
                request.addProperty("companyId", spManager.getLoginInfoValueByKeyName("CompanyID"));
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(request);
                envelope.dotNet = false;
                try {
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URLs.URL);
                    androidHttpTransport.call(URLs.NAMESPACE + "getLetters", envelope);
                    response = (SoapObject) envelope.bodyIn;
                } catch (Exception ex) {
                    Log.d("Error", ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (response != null) {
                    for (int i = 0; i < response.getPropertyCount(); i++) {
                        SoapObject soapObj = (SoapObject) response.getProperty(i);
                        for (int j = 0; j < soapObj.getPropertyCount(); j++) {
                            PropertyInfo info = new PropertyInfo();
                            soapObj.getPropertyInfo(j, info);
                            if (info.getName().equals("measurePointId")) {
                                String measurePointID = soapObj.getProperty(j).toString();
                                if (newMPID.equals(measurePointID)) {
                                    isMPIDUnique = true;
                                }
                            }
                        }
                    }

                    if (!isMPIDUnique) {
                        replaceMpID(oldMPID, newMPID);
                        spManager.saveGeneralInfoValueByKeyName("ValidID", "True", editor);
                        spManager.saveGeneralInfoValueByKeyName("ActiveMeasurementPointID", newMPID, editor);
                        HomeFragment.getInstance().setActiveCertificate();
                    } else {
                        edt_measuring_point_ID.setText(oldMPID);
                        edt_measuring_point_ID.setSelection(oldMPID.length());
                        Toast.makeText(getActivity(), getResources().getString(R.string.measurement_pt_id_already_exists), Toast.LENGTH_SHORT).show();
                    }
                }
                MainActivity.getInstance().setTitle();
                MainActivity.getInstance().dismissWaitDialog();
            }
        }.execute();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public void replaceMpID(String oldMPID, String newMPID) {
        long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
        List<LetterEntity> _list = viewModel.letterLocalExists(adminID, _letterID, oldMPID);
        if (_list != null && _list.size() > 0) {
            LetterEntity _entity = _list.get(0);
            _entity.ID = _list.get(0).ID;
            _entity.letterID = _list.get(0).letterID;
            _entity.measurePointID = newMPID;
            viewModel.updateLetter(_entity);
        }
        if (oldMPID.equals("")) {
            spManager.saveGeneralInfoValueByKeyName("OldMPID", newMPID, editor);
            spManager.saveGeneralInfoValueByKeyName("MeasurementPointID", newMPID, editor);
        } else {
            if (!newMPID.equals(oldMPID)) {
                spManager.saveGeneralInfoValueByKeyName("MeasurementPointID", newMPID, editor);
            }
        }

        if (!oldMPID.equals(newMPID)) {
            List<ImageEntity> imageList = viewModel.imageMPIDExists(adminID, oldMPID);
            if (imageList != null && imageList.size() > 0) {
                spManager.saveGeneralInfoValueByKeyName("OldMPID", newMPID, editor);
                final File oldFolder = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + oldMPID);
                final File newFolder = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + newMPID);
                boolean success = oldFolder.renameTo(new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + newMPID));
                for (ImageEntity entity : imageList) {
                    File oldFile = new File(oldFolder.getAbsolutePath() + "/" + entity.fileName);
                    File newFile = new File(newFolder.getAbsolutePath() + "/" + entity.fileName);
                    oldFile.renameTo(newFile);
                }
                oldFolder.delete();
                for (ImageEntity item : imageList) {
                    item.measurePointID = newMPID;
                    item.filePath = newFolder.getAbsolutePath() + "/" + item.fileName;
                    item.description = newMPID;
                    viewModel.updatetImage(item);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 122) {
            if (data != null) {
                callApiOnce = true;
                ll_undermappe.setVisibility(View.VISIBLE);
                String AssignmentName = data.getExtras().getString("AssignmentName");
                anleggID = data.getExtras().getString("AnleggID");
                String CompanyName = data.getExtras().getString("CompanyName");
                spManager.saveGeneralInfoValueByKeyName("AnleggID", anleggID, editor);
                spManager.saveGeneralInfoValueByKeyName("CompanyName", CompanyName, editor);
                edt_Anlegg.setText(AssignmentName);
                ll_undermappe2.setVisibility(View.GONE);
                spn_subfolder.setText("Velg Undermappe");
            }
        } else if (requestCode == 123) {
            if (data != null) {
                String folderName = data.getExtras().getString("FolderName");
                String assignmentID = data.getExtras().getString("AssignmentID");
                folderID = data.getExtras().getString("FolderID");
                String parentID = data.getExtras().getString("ParentID");
                spn_subfolder.setText(folderName);
                List<FolderEntity> subFolderList = viewModel.getSubFolderName(assignmentID, folderID);
                folderList = subFolderList;
                List<String> subFolderNames = new ArrayList<>();
                for (FolderEntity entity : subFolderList) {
                    subFolderNames.add(entity.name);
                }
                if (subFolderNames.size() > 0) {
                    ll_undermappe2.setVisibility(View.VISIBLE);
                    spn_subfolder2.setText("Velg undermappe 2");
                } else {
                    ll_undermappe2.setVisibility(View.GONE);
                }
            }
        } else if (requestCode == 124) {
            if (data != null) {
                ll_undermappe2.setVisibility(View.VISIBLE);
                String folderName = data.getExtras().getString("FolderName");
                String assignmentID = data.getExtras().getString("AssignmentID");
                String _folderID = data.getExtras().getString("FolderID");
                String parentID = data.getExtras().getString("ParentID");
                spn_subfolder2.setText(folderName);
                spManager.saveGeneralInfoValueByKeyName("Undermappe2", _folderID, editor);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mCurrentLocation = null;
        stopLocationUpdates();
    }

    public void showWaitDialog(String msg) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(getActivity());
        }
        pDialog.setMessage(msg);
        pDialog.setCancelable(false);
        if (pDialog != null && !pDialog.isShowing()) {
            pDialog.show();
        }
    }

    public void dismissWaitDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
