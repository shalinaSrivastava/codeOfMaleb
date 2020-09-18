package com.trainor.controlandmeasurement.fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trainor.controlandmeasurement.Activities.DownloadLetterActivity;
import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.Activities.UploadLetters;
import com.trainor.controlandmeasurement.AdapterClasses.DownloadedLetterAdapter;
import com.trainor.controlandmeasurement.AdapterClasses.HistoryLetterAdapter;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.Activities.LoginActivity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    public static HomeFragment instance;
    @BindView(R.id.btn_logout)
    TextView btn_logout;
    @BindView(R.id.btn_newletter)
    TextView btn_newletter;
    @BindView(R.id.btn_download)
    TextView btn_download;
    @BindView(R.id.tv_active_certificate_name)
    TextView tv_active_certificate_name;
    @BindView(R.id.tv_homepage_des)
    TextView tv_homepage_des;
    @BindView(R.id.txt_upload_all)
    TextView txt_upload_all;
    @BindView(R.id.btn_upload)
    TextView btn_upload;
    @BindView(R.id.tv_downloaded_certificate)
    TextView tv_downloaded_certificate;
    @BindView(R.id.tv_history)
    TextView tv_history;
    @BindView(R.id.lineview1)
    View lineview1;
    @BindView(R.id.lineView2)
    View lineView2;
    @BindView(R.id.recycler_view_history)
    RecyclerView recycler_view_history;
    @BindView(R.id.recycler_view_downloaded_certificate)
    RecyclerView recycler_view_downloaded_certificate;

    public SharedPreferenceClass spManager;
    SharedPreferences.Editor editor;
    public ViewModel viewModel;
    DownloadedLetterAdapter adapter;
    HistoryLetterAdapter historyLetterAdapter;
    long adminID;
    ConnectionDetector connectionDetector;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(HomeFragment.this, view);
        instance = this;
        return view;
    }

    public synchronized static HomeFragment getInstance() {
        if (instance == null) {
            instance = new HomeFragment();
        }
        return instance;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getControls();
    }

    public void getControls() {
        connectionDetector = ConnectionDetector.getInstance(getActivity());
        viewModel = ViewModelProviders.of(MainActivity.getInstance()).get(ViewModel.class);
        spManager = new SharedPreferenceClass(getActivity());
        if (editor == null) {
            editor = getActivity().getSharedPreferences("LoginInfoPref", Context.MODE_PRIVATE).edit();
        }
        if (!spManager.getLoginInfoValueByKeyName("AdminID").equals("")) {
            adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        }
        btn_upload.setVisibility(View.GONE);
        tv_active_certificate_name.setVisibility(View.GONE);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogManager.showDialog(getActivity(), getString(R.string.logout), getString(R.string.cancel), getString(R.string.logout), getString(R.string.log_out_popup_msg), true, new IClickListener() {
                    @Override
                    public void onClick() {
                        String adminID = spManager.getLoginInfoValueByKeyName("AdminID");
                        if (!adminID.equals("")) {
                            MainActivity mainActivity = MainActivity.getInstance();
                            long _adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
                            final String measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
                            String anleggID = spManager.getGeneralInfoValueByKeyName("AnleggID");
                            String measuredBy = spManager.getVariablerValueByKeyName("ControlPerformedBy");
                            long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
                            final List<LetterEntity> _list = viewModel.letterLocalExists(_adminID, _letterID, measurePointID);
                            if (!measurePointID.equals("") && !anleggID.equals("")) {
                                if (_list == null || _list.size() == 0) {
                                    LetterEntity _entity = mainActivity.getLetterEntity();
                                    _entity.measurePointID = measurePointID;
                                    _entity.assignmentID = anleggID;
                                    _entity.measuredBy = measuredBy;
                                    viewModel.insertLetter(_entity);
                                } else {
                                    LetterEntity entity = _list.get(0);
                                    if (entity.Tag.equals("Saved")) {
                                        long _id = entity.ID;
                                        entity = mainActivity.getLetterEntity();
                                        entity.measurePointID = measurePointID;
                                        entity.assignmentID = anleggID;
                                        entity.measuredBy = measuredBy;
                                        entity.ID = _id;
                                        entity.letterID = _letterID;
                                        viewModel.updateLetter(entity);
                                    }
                                }
                            } else {
                                File directory = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + measurePointID);
                                if (directory.exists()) {
                                    File[] files = directory.listFiles();
                                    if (files != null && files.length > 0) {
                                        for (int i = 0; i < files.length; i++) {
                                            File _file = files[i];
                                            if (_file.exists()) {
                                                _file.delete();
                                            }
                                        }
                                        directory.delete();
                                    }
                                    viewModel.deleteImagesMPID(_adminID, measurePointID);
                                }
                            }
                            viewModel.deleteLoginEntity();
                            spManager.removePref("LoginInfoPref");
                            spManager.removePref("GeneralInfoPref");
                            spManager.removePref("VariablerPref");
                            spManager.removePref("CalculationPref");
                            spManager.removePref("MeasuredValuesPref");
                            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    }
                });
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent downloadLettersIntent = new Intent(getContext(), DownloadLetterActivity.class);
                startActivityForResult(downloadLettersIntent, 234);
            }
        });
        adapter = new DownloadedLetterAdapter(getActivity(), viewModel);
        recycler_view_downloaded_certificate.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view_downloaded_certificate.setAdapter(adapter);

        historyLetterAdapter = new HistoryLetterAdapter(getActivity(), viewModel);
        recycler_view_history.setAdapter(historyLetterAdapter);
        recycler_view_history.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getDownloadedLetters(adminID).observe(getActivity(), new Observer<List<LetterEntity>>() {
            @Override
            public void onChanged(@Nullable List<LetterEntity> letterEntities) {
                if (letterEntities.size() > 0) {
                    if (spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID") != null && !spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID").equals("")) {
                        tv_homepage_des.setText(getString(R.string.active_certificate));
                    } else {
                        tv_homepage_des.setText(getString(R.string.when_letters_downloaded));
                    }
                    tv_downloaded_certificate.setVisibility(View.VISIBLE);
                    lineview1.setVisibility(View.VISIBLE);
                    recycler_view_downloaded_certificate.setVisibility(View.VISIBLE);
                    adapter.setList(letterEntities);
                } else {
                    if (spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID") != null && !spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID").equals("")) {
                        tv_homepage_des.setText(getString(R.string.active_certificate));
                    } else {
                        tv_homepage_des.setText(getString(R.string.no_letters_downloaded));
                    }
                    tv_downloaded_certificate.setVisibility(View.GONE);
                    lineview1.setVisibility(View.GONE);
                    recycler_view_downloaded_certificate.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getHistoryLetters(adminID).observe(getActivity(), new Observer<List<LetterEntity>>() {
            @Override
            public void onChanged(@Nullable List<LetterEntity> letterEntities) {
                if (letterEntities.size() > 0) {
                    tv_history.setVisibility(View.VISIBLE);
                    lineView2.setVisibility(View.VISIBLE);
                    historyLetterAdapter.setList(letterEntities);
                } else {
                    tv_history.setVisibility(View.GONE);
                    lineView2.setVisibility(View.GONE);
                }
            }
        });

        btn_newletter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spManager.saveLoginInfoValueByKeyName("TabVisible", "true", editor);
                MainActivity.from = "NewLetter";
                removeLetterDataRefrences();
                MainActivity.getInstance().tv_title.setText("Nytt målebrev");
                MainActivity.getInstance().variablePageClicked = false;
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    MainActivity.getInstance().uploadLetter();
                } else {
                    AlertDialogManager.showDialog(getActivity(), getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
            }
        });

        txt_upload_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadLettersIntent = new Intent(getContext(), UploadLetters.class);
                startActivity(uploadLettersIntent);
            }
        });
        setActiveCertificate();
    }

    public void removeLetterDataRefrences() {
        MainActivity mainActivity = MainActivity.getInstance();
        long adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        final String measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
        String anleggID = spManager.getGeneralInfoValueByKeyName("AnleggID");
        long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
        final List<LetterEntity> _list = viewModel.letterLocalExists(adminID, _letterID, measurePointID);
        if (!measurePointID.equals("") && !anleggID.equals("")) {
            if (_list == null || _list.size() == 0) {
                LetterEntity _entity = mainActivity.getLetterEntity();
                if (!_entity.voltage.equals("-2") && !_entity.electrode.equals("-2")) {
                    _entity.measurePointID = measurePointID;
                    _entity.assignmentID = anleggID;
                    _entity.measuredBy = spManager.getVariablerValueByKeyName("ControlPerformedBy");
                    viewModel.insertLetter(_entity);
                }
            } else {
                LetterEntity entity = _list.get(0);
                if (entity.Tag.equals("Saved")) {
                    long _id = entity.ID;
                    entity = mainActivity.getLetterEntity();
                    entity.measurePointID = measurePointID;
                    entity.assignmentID = anleggID;
                    entity.measuredBy = spManager.getVariablerValueByKeyName("ControlPerformedBy");
                    entity.ID = _id;
                    entity.letterID = _letterID;
                    viewModel.updateLetter(entity);
                }
            }
        } else {
            File directory = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + measurePointID);
            if (directory.exists()) {
                File[] files = directory.listFiles();
                if (files != null && files.length > 0) {
                    for (int i = 0; i < files.length; i++) {
                        File _file = files[i];
                        if (_file.exists()) {
                            _file.delete();
                        }
                    }
                    directory.delete();
                }
                viewModel.deleteImagesMPID(adminID, measurePointID);
            }
        }
        spManager.removePref("GeneralInfoPref");
        spManager.removePref("VariablerPref");
        spManager.removePref("CalculationPref");
        spManager.removePref("MeasuredValuesPref");
        setActiveCertificate();
        MainActivity.getInstance().setTitle();
        spManager.saveLoginInfoValueByKeyName("TabVisible", "true", editor);
        TabLayout tabLayout = mainActivity.tabLayout;
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            tab.setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
            tab.setClickable(true);
        }
        mainActivity.viewPager.setCurrentItem(1, true);
    }

    public void setActiveCertificate() {
        if (spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID") != null && !spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID").equals("")) {
            btn_upload.setVisibility(View.VISIBLE);
            tv_active_certificate_name.setVisibility(View.VISIBLE);
            tv_homepage_des.setText(getString(R.string.active_certificate));
        } else {
            btn_upload.setVisibility(View.GONE);
            tv_active_certificate_name.setVisibility(View.GONE);
            tv_homepage_des.setText(getString(R.string.when_letters_downloaded));
        }
        tv_active_certificate_name.setText(spManager.getGeneralInfoValueByKeyName("ActiveMeasurementPointID"));
        List<LetterEntity> list = viewModel.getSavedLetter(adminID);
        if (list != null && list.size() > 1) {
            txt_upload_all.setVisibility(View.VISIBLE);
        } else {
            txt_upload_all.setVisibility(View.GONE);
        }
    }
}
