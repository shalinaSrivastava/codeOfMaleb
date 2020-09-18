package com.trainor.controlandmeasurement.Activities;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.AdapterClasses.GetLettersRecyclerViewAdapter;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.WebServiceCall;
import com.trainor.controlandmeasurement.HelperClass.XMLParser;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.CDATASection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadLetterActivity extends AppCompatActivity implements View.OnClickListener {
    public static DownloadLetterActivity instance;

    @BindView(R.id.btn_all_letters)
    TextView btn_all_letters;
    @BindView(R.id.btn_search)
    TextView btn_search;
    @BindView(R.id.ll_download)
    LinearLayout ll_download;
    @BindView(R.id.edt_measuementId)
    EditText edt_measuementId;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.recyclerView_downloaded_letters)
    RecyclerView recyclerView_downloaded_letters;

    ProgressDialog pDialog;
    GetLettersRecyclerViewAdapter getLettersRecyclerViewAdapter;
    SharedPreferenceClass spManager;
    String measurePointId;
    ConnectionDetector connectionDetector;
    public static int currentFileIndex = 0;
    public static int totalURLIndex = 0;
    long adminID;
    String loginToken, companyId;
    public List<LetterEntity> getLettersInfoList, _list;
    public List<LetterEntity> listToBeDownloaded;
    public List<String> notDownloadedLettersList;
    ViewModel viewModel;
    NetworkChangeReceiver networkChangeReceiver;
    boolean uploadBtnClicked;
    LinearLayoutManager linearLayoutManager;
    Boolean isScrolling = true;
    int currentItems, totalItems, scrollOutItems;
    String tempCompanyId = "", currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_letter);
        ButterKnife.bind(this);
        getControls();
        instance = this;
    }

    public synchronized static DownloadLetterActivity getInstance() {
        if (instance == null) {
            instance = new DownloadLetterActivity();
        }
        return instance;
    }

    public void getControls() {
        viewModel = ViewModelProviders.of(DownloadLetterActivity.this).get(ViewModel.class);
        listToBeDownloaded = new ArrayList<>();
        getLettersInfoList = new ArrayList<>();
        _list = new ArrayList<>();
        notDownloadedLettersList = new ArrayList<>();
        connectionDetector = ConnectionDetector.getInstance(this);
        spManager = new SharedPreferenceClass(this);
        if (!spManager.getLoginInfoValueByKeyName("AdminID").equals("")) {
            adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        }
        loginToken = spManager.getLoginInfoValueByKeyName("Token");
        companyId = spManager.getLoginInfoValueByKeyName("CompanyID");
        linearLayoutManager = new LinearLayoutManager(DownloadLetterActivity.this);
        linearLayoutManager.setAutoMeasureEnabled(false);
        recyclerView_downloaded_letters.setLayoutManager(linearLayoutManager);
        btn_all_letters.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        ll_download.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        /*try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = inputFormat.parse("2018-04-10T04:00:00.000Z");
            String formattedDate = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = calendar.getTime();
        currentDate = dateFormater.format(date);
        System.out.println("current date = "+currentDate);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (networkChangeReceiver != null) {
                unregisterReceiver(networkChangeReceiver);
            }
        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());
        }
    }

    public void showWaitDialog(String message) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
        }
        pDialog.setMessage(message);
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(edt_measuementId.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all_letters:
                /*if (connectionDetector.isConnectingToInternet()) {
                    edt_measuementId.setEnabled(false);
                    getLettersApi(loginToken, "", companyId);
                } else {
                    AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }*/
                if (connectionDetector.isConnectingToInternet()) {
                    edt_measuementId.setEnabled(false);
                    String trainor_admin_status = spManager.getLoginInfoValueByKeyName("trainorAdmin");
                    if (trainor_admin_status.equals("true")) {
                        Intent intent = new Intent(DownloadLetterActivity.this, CompanyListActivity.class);
                        intent.putExtra("From", "GetAllLetter");
                        startActivityForResult(intent, 127);
                    } else {
                        getLettersApi(loginToken, "", companyId);
                    }
                } else {
                    AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
                break;
            case R.id.btn_search:
                hideKeyboard();
                measurePointId = edt_measuementId.getText().toString().trim();
                if (connectionDetector.isConnectingToInternet()) {
                    if (measurePointId == null || measurePointId.equals("")) {
                        Toast.makeText(DownloadLetterActivity.this, getString(R.string.balnk_measurement_pt), Toast.LENGTH_SHORT).show();
                    } else {
                        if (spManager.getLoginInfoValueByKeyName("trainorAdmin").equals("true")) {
                            if (tempCompanyId.equals("")) {
                                tempCompanyId = "0";
                            }
                            getLettersApi(loginToken, measurePointId, tempCompanyId);
                        } else {
                            getLettersApi(loginToken, measurePointId, companyId);
                        }
                        //getLettersApi(loginToken, measurePointId, companyId);
                    }
                } else {
                    AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
                break;
            case R.id.ll_download:
                if (connectionDetector.isConnectingToInternet()) {
                    uploadBtnClicked = true;
                    if (getLettersInfoList.size() > 0) {
                        List<Long> letterIDList = getLettersRecyclerViewAdapter.getLettersToBeDownloaded();
                        listToBeDownloaded = getSelectedLetterList(getLettersRecyclerViewAdapter.getLettersToBeDownloaded());
                        if (listToBeDownloaded.size() > 0 && (letterIDList != null && letterIDList.size() > 0)) {
                            currentFileIndex = 1;
                            totalURLIndex = listToBeDownloaded.size();
                            notDownloadedLettersList.clear();
                            getLetterData(listToBeDownloaded.get(0).letterID, listToBeDownloaded.get(0).companyName, listToBeDownloaded.get(0).measurePointID);
                        } else {
                            uploadBtnClicked = false;
                            Toast.makeText(DownloadLetterActivity.this, getResources().getString(R.string.no_certificate_selected), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        uploadBtnClicked = false;
                        Toast.makeText(DownloadLetterActivity.this, getResources().getString(R.string.no_certificate_downloaded), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
                break;
            case R.id.iv_back:
                goToMainActivity();
                break;
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo wifi = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            final android.net.NetworkInfo mobile = connMgr
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (!wifi.isConnected() && !mobile.isConnected()) {
                if (uploadBtnClicked) {
                    AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, new IClickListener() {
                        @Override
                        public void onClick() {
                            goToMainActivity();
                        }
                    });
                }
            }
        }
    }

    public void goToMainActivity() {
        setResult(RESULT_OK);
        dismissWaitDialog();
        finish();
    }

    public void getLettersApi(String loginToken, String measurePointId, String companyId) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showWaitDialog(getResources().getString(R.string.getting_letters));
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBody = "<log:getLetters><loginToken>" + loginToken + "</loginToken><measurePointId>" + measurePointId + "</measurePointId><companyId>" + companyId + "</companyId></log:getLetters>";


                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBody, URLs.SOAP_ACTION_Get_Letters);
                    List<LetterEntity> list = new ArrayList<>();
                    if (jsonArray != null && jsonArray.length() != 0) {
                        getLettersInfoList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LetterEntity getLettersInfo = new LetterEntity();
                            getLettersInfo.assignmentID = jsonObject.getString("assignmentId");
                            getLettersInfo.companyName = jsonObject.getString("companyName");
                            getLettersInfo.letterID = Long.parseLong(jsonObject.getString("letterID"));
                            getLettersInfo.locationDescription = jsonObject.getString("locationDescription");
                            getLettersInfo.measurePointID = jsonObject.getString("measurePointId");
                            getLettersInfo.measuredBy = jsonObject.getString("measuredBy");
                            if (!jsonObject.has("measurementDate")) {
                                getLettersInfo.measurementDate = "";
                            } else {
                                getLettersInfo.measurementDate = jsonObject.getString("measurementDate");
                            }
                            getLettersInfo.isSelected = "false";
                            list.add(getLettersInfo);
                        }
                    }
                    getLettersInfoList = list;
                } catch (Exception ex) {
                    Log.d("Error", ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                edt_measuementId.setEnabled(true);
                dismissWaitDialog();
                if (getLettersInfoList.size() > 0) {
                    if (getLettersInfoList.size() >= 20) {
                        _list = getSubList(20);
                    } else {
                        _list = getLettersInfoList;
                    }
                    getLettersRecyclerViewAdapter = new GetLettersRecyclerViewAdapter(recyclerView_downloaded_letters, DownloadLetterActivity.this, _list);
                    recyclerView_downloaded_letters.setAdapter(getLettersRecyclerViewAdapter);
                    recyclerView_downloaded_letters.addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            currentItems = linearLayoutManager.getChildCount();
                            totalItems = linearLayoutManager.getItemCount();
                            scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                            if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                isScrolling = false;
                                getData();
                            }
                        }
                    });
                   /* recyclerView_downloaded_letters.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                           *//* currentItems = linearLayoutManager.getChildCount();
                            totalItems = linearLayoutManager.getItemCount();
                            scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                            if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                                isScrolling = false;
                                getData();
                            }*//*
                            getData();
                        }
                    });*/
                } else {
                   // getLettersRecyclerViewAdapter = new GetLettersRecyclerViewAdapter(recyclerView_downloaded_letters, DownloadLetterActivity.this, _list);
                    //recyclerView_downloaded_letters.setAdapter(getLettersRecyclerViewAdapter);
                    AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.no_letter_found) + " " + measurePointId, false, null);
                }
            }
        }.execute();
    }

    public void getData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int size = _list.size();
                int sizes = getLettersInfoList.size();
                if ((size + 20) <= sizes) {
                    _list.clear();
                    _list = getSubList(size + 20);
                    getLettersRecyclerViewAdapter.setList(_list);
                } else {
                    if (size != sizes) {
                        _list.clear();
                        _list = getSubList(sizes);
                        getLettersRecyclerViewAdapter.setList(_list);
                    }
                }
            }
        });
    }

    public void getLetterData(final long letterID, final String companyName, String measurePointId) {
        showWaitDialog(getResources().getString(R.string.downloading) + " " + currentFileIndex + " " + getResources().getString(R.string.of) + " " + totalURLIndex + " " + getResources().getString(R.string.letters));
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBody = "<log:getLetter><loginToken>" + loginToken + "</loginToken><letterID>" + letterID + "</letterID><revision>-1</revision><includeImages>false</includeImages></log:getLetter>";
                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBody, URLs.SOAP_ACTION_Get_Letters);
                    if (jsonArray != null && jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            LetterEntity getLettersInfo = new LetterEntity();
                            getLettersInfo.altitude = jsonObject.getString("altitude");
                            getLettersInfo.approvedBy = jsonObject.getString("approvedBy");
                            getLettersInfo.assignmentID = jsonObject.getString("assignmentID");
                            getLettersInfo.baseDataVersion = jsonObject.getString("baseDataVersion");

                            // chnages on 18-08-2020
                            //getLettersInfo.clampAmp = jsonObject.getString("clampAmp");
                            if (jsonObject.has("clampAmp")) {
                                getLettersInfo.clampAmp = jsonObject.getString("clampAmp");
                            } else {
                                getLettersInfo.clampAmp = "";
                            }
                            getLettersInfo.clampMeasurement = jsonObject.getString("clampMeasurement");
                            getLettersInfo.comments = jsonObject.getString("comments");
                            getLettersInfo.directionForward = jsonObject.getString("compassDirection");
                            getLettersInfo.deleted = jsonObject.getString("deleted");

                            int disconnectedId =  XMLParser.getDisconnectID( Double.valueOf(jsonObject.getString("disconnectTime")),"disconnectTimes");
                            getLettersInfo.disconnectTime = disconnectedId+"";
                            //getLettersInfo.disconnectTime = jsonObject.getString("disconnectTime");
                            getLettersInfo.distance = jsonObject.getString("distance");
                            getLettersInfo.earthFaultCurrent = jsonObject.getString("earthFaultCurrent");
                            getLettersInfo.earthType = jsonObject.getString("earthType");
                            getLettersInfo.electrode = jsonObject.getString("electrode");
                            getLettersInfo.electrodeType = jsonObject.getString("electrodeType");
                            getLettersInfo.fefTable = jsonObject.getString("fefTable");

                            // chnages on 19-05-2020
                            //getLettersInfo.folderId = jsonObject.getString("folderId");
                            if (jsonObject.has("folderId")) {
                                getLettersInfo.folderId = jsonObject.getString("folderId");
                            } else {
                                getLettersInfo.folderId = "";
                            }
                            getLettersInfo.globalEarth = jsonObject.getString("globalEarth");
                            getLettersInfo.highVoltageActionTaken = jsonObject.getString("highVoltageActionTaken");
                            getLettersInfo.input = jsonObject.getString("input");
                            getLettersInfo.latitude = jsonObject.getString("latitude");
                            getLettersInfo.letterID = Long.parseLong(jsonObject.getString("letterID"));
                            getLettersInfo.locationDescription = jsonObject.getString("locationDescription");
                            getLettersInfo.longitude = jsonObject.getString("longitude");
                            getLettersInfo.measurePointID = jsonObject.getString("measurePointID");
                            getLettersInfo.measuredBy = jsonObject.getString("measuredBy");

                            // chnages on 19-05-2020
                            //getLettersInfo.basicInstallation = jsonObject.getString("measuredReference");
                            if (jsonObject.has("measuredReference")) {
                                getLettersInfo.basicInstallation = jsonObject.getString("measuredReference");
                            } else {
                                getLettersInfo.basicInstallation = "";
                            }

                            //getLettersInfo.measurementDate = jsonObject.getString("measurementDate");
                            if (!jsonObject.has("measurementDate")) {
                                //getLettersInfo.measurementDate = "2016-01-01T00:00:00+02:00";
                                getLettersInfo.measurementDate = currentDate;
                            } else {
                                getLettersInfo.measurementDate = jsonObject.getString("measurementDate");
                            }
                            getLettersInfo.moisture = jsonObject.getString("moisture");
                            getLettersInfo.noLocalElectrode = jsonObject.getString("noLocalElectrode");
                            getLettersInfo.published = jsonObject.getString("published");
                            getLettersInfo.refL = jsonObject.getString("refL");
                            getLettersInfo.refT = jsonObject.getString("refT");
                            getLettersInfo.registered = jsonObject.getString("registered");
                            getLettersInfo.registeredBy = jsonObject.getString("registeredBy");
                            getLettersInfo.registeredByName = jsonObject.getString("registeredByName");

                            // changed revision count to +1 15-09-2020
                            int revision_count = Integer.parseInt(jsonObject.getString("revision")) + 1;
                            getLettersInfo.revision = revision_count+"";
                            //getLettersInfo.revision = jsonObject.getString("revision");

                            getLettersInfo.satisfy = jsonObject.getString("satisfy");
                            getLettersInfo.season = jsonObject.getString("season");
                            getLettersInfo.trainorApproved = jsonObject.getString("trainorApproved");
                            if (jsonObject.has("trainorComments")) {
                                getLettersInfo.trainorComments = jsonObject.getString("trainorComments");
                            } else {
                                getLettersInfo.trainorComments = "";
                            }
                            getLettersInfo.transformerPerformance = jsonObject.getString("transformerPerformance");
                            if (jsonObject.has("updated")) {
                                getLettersInfo.updated = jsonObject.getString("updated");
                            } else {
                                getLettersInfo.updated = "";
                            }
                            getLettersInfo.updatedBy = jsonObject.getString("updatedBy");
                            getLettersInfo.updatedByName = jsonObject.getString("updatedByName");
                            getLettersInfo.voltage = jsonObject.getString("voltage");

                            // chnages on 19-05-2020
                            //getLettersInfo.directionBackward = jsonObject.getString("compassDirectionBackwards");
                            if (jsonObject.has("compassDirectionBackwards")) {
                                getLettersInfo.directionBackward = jsonObject.getString("compassDirectionBackwards");
                            } else {
                                getLettersInfo.directionBackward = "";
                            }

                            // chnages on 19-05-2020
                           // getLettersInfo.localElectrodeInput = jsonObject.getString("localElectrodeInput");
                            if (jsonObject.has("localElectrodeInput")) {
                                getLettersInfo.localElectrodeInput = jsonObject.getString("localElectrodeInput");
                            } else {
                                getLettersInfo.localElectrodeInput = "";
                            }

                            getLettersInfo.adminID = adminID;
                            getLettersInfo.companyName = companyName;
                            getLettersInfo.Tag = "downloaded";
                            getLettersInfo.timestamp = System.currentTimeMillis();
                            List<LetterEntity> letterList = viewModel.letterLocalExists(adminID, letterID, measurePointId);
                            if (letterList != null && letterList.size() > 0) {
                                getLettersInfo.ID = letterList.get(0).ID;
                                viewModel.updateLetter(getLettersInfo);
                            } else {
                                viewModel.insertLetter(getLettersInfo);
                            }
                        }
                    } else {
                        notDownloadedLettersList.add(measurePointId);
                    }
                } catch (Exception ex) {
                    notDownloadedLettersList.add(measurePointId);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (listToBeDownloaded.size() > 0) {
                    listToBeDownloaded.remove(listToBeDownloaded.get(0));
                }
                if (listToBeDownloaded.size() > 0) {
                    currentFileIndex += 1;
                    getLetterData(listToBeDownloaded.get(0).letterID, listToBeDownloaded.get(0).companyName, listToBeDownloaded.get(0).measurePointID);
                } else {
                    if (notDownloadedLettersList.size() > 0) {
                        String listString = "";
                        for (String s : notDownloadedLettersList) {
                            if (listString.equals("")) {
                                listString = s;
                            } else {
                                listString += ", " + s;
                            }
                        }
                        AlertDialogManager.showDialog(DownloadLetterActivity.this, getResources().getString(R.string.ok), "", getResources().getString(R.string.download_unsuccessful), getResources().getString(R.string.following_letters_cud_not_downloaded) + ": \n " + listString, false, new IClickListener() {
                            @Override
                            public void onClick() {
                                goToMainActivity();
                            }
                        });
                    } else {
                        if (connectionDetector.isConnectingToInternet()) {
                            Toast.makeText(DownloadLetterActivity.this, getResources().getString(R.string.download_completed), Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        }
                    }
                }
            }
        }.execute();
    }

    public List<LetterEntity> getSubList(int index) {
        List<LetterEntity> list = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            list.add(getLettersInfoList.get(i));
        }
        return list;
    }

    public List<LetterEntity> getSelectedLetterList(List<Long> letterIDList) {
        List<LetterEntity> list = new ArrayList<>();
        for (LetterEntity item : getLettersInfoList) {
            if (letterIDList.contains(item.letterID)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        edt_measuementId.setEnabled(true);
        if (requestCode == 127 && resultCode == 0) {
            String _companyID = spManager.getCompanyValueByKeyName("CompanyID");
            spManager.removePref("CompanyPref");
            if (!_companyID.equals("")) {
                companyId = _companyID;
                tempCompanyId = _companyID;
                getLettersApi(loginToken, "", _companyID);
            }
        }
    }
}