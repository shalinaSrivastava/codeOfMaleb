package com.trainor.controlandmeasurement.Activities;

import android.app.Activity;
import android.app.ProgressDialog;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.AdapterClasses.AnleggAdapter;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.WebServiceCall;
import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.List;

public class AnleggActivity extends AppCompatActivity {
    public static volatile AnleggActivity instance;
    EditText edt_measuementId;
    RecyclerView recyclerView;
    ImageView img_back;
    Button btn_search;
    ViewModel viewModel;
    ProgressDialog pDialog;
    ConnectionDetector connectionDetector;
    String token, companyID;
    long adminID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_anlegg);
        connectionDetector = new ConnectionDetector(AnleggActivity.this);
        token = getIntent().getExtras().getString("LoginToken");
        String _adminID = getIntent().getExtras().getString("AdminID");
        companyID = getIntent().getExtras().getString("CompanyID");
        adminID = Long.parseLong(_adminID);
        img_back = (ImageView) findViewById(R.id.img_back);
        btn_search = (Button) findViewById(R.id.btn_search);
        edt_measuementId = (EditText) findViewById(R.id.edt_measuementId);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(AnleggActivity.this));
        final AnleggAdapter adapter = new AnleggAdapter();
        viewModel = ViewModelProviders.of(AnleggActivity.this).get(ViewModel.class);
        recyclerView.setAdapter(adapter);
        edt_measuementId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    viewModel.getAssignment().observe(AnleggActivity.this, new Observer<List<AssignmentEntity>>() {
                        @Override
                        public void onChanged(@Nullable List<AssignmentEntity> assignmentEntities) {
                            adapter.setList(assignmentEntities);
                        }
                    });
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String mpid = edt_measuementId.getText().toString().trim();
                if (mpid.equals("")) {
                    Toast.makeText(AnleggActivity.this, getResources().getString(R.string.enter_search_text), Toast.LENGTH_SHORT).show();
                } else {
                    List<AssignmentEntity> list = viewModel.getAssignmentByName(edt_measuementId.getText().toString().trim());
                    if (list == null || list.size() == 0) {
                        Toast.makeText(AnleggActivity.this, getResources().getString(R.string.anlegg_search), Toast.LENGTH_SHORT).show();
                    }
                    adapter.setList(list);
                }
            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });
        if (connectionDetector.isConnectingToInternet()) {
            getAssignments(token, companyID, adapter);
        } else {
            viewModel.getAssignment().observe(AnleggActivity.this, new Observer<List<AssignmentEntity>>() {
                @Override
                public void onChanged(@Nullable List<AssignmentEntity> assignmentEntities) {
                    adapter.setList(assignmentEntities);
                }
            });
        }
        instance = this;
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        finish();
    }

    public synchronized static AnleggActivity getInstance() {
        if (instance == null) {
            instance = new AnleggActivity();
        }
        return instance;
    }

    public void selectedAnlegg(AssignmentEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("AssignmentName", entity.assignmentName);
        intent.putExtra("AnleggID", entity.assignmentID + "");
        intent.putExtra("CompanyName", entity.companyName);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(edt_measuementId.getWindowToken(), 0);
        }
    }

    public void getAssignments(final String loginToken, final String companyID, AnleggAdapter adapter) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showWaitDialog(getResources().getString(R.string.please_wait));
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBdy = "<log:getAssignments><loginToken>" + loginToken + "</loginToken><companyId>" + companyID + "</companyId></log:getAssignments>";
                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBdy, URLs.SOAP_ACTION_GET_ASSIGNMENTS);
                    if (jsonArray != null && jsonArray.length() != 0) {
                        viewModel.deleteAllAssignment();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            AssignmentEntity assignmentInfo = new AssignmentEntity();
                            assignmentInfo.adminID = adminID;
                            assignmentInfo.assignmentID = Integer.parseInt(jsonObject.getString("assignmentID"));
                            assignmentInfo.companyID = Integer.parseInt(jsonObject.getString("companyID"));
                            assignmentInfo.companyName = jsonObject.getString("companyName");
                            assignmentInfo.assignmentName = jsonObject.getString("name");
                            assignmentInfo.status = Integer.parseInt(jsonObject.getString("status"));
                            AssignmentEntity assignmentEntity = viewModel.getAssignment(assignmentInfo.assignmentID);
                            if (assignmentEntity == null || assignmentEntity.assignmentID == 0) {
                                viewModel.insertAssignment(assignmentInfo);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Log.d("Error", ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dismissWaitDialog();
                viewModel.getAssignment().observe(AnleggActivity.this, new Observer<List<AssignmentEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<AssignmentEntity> assignmentEntities) {
                        adapter.setList(assignmentEntities);
                    }
                });
            }
        }.execute();
    }

    public void showWaitDialog(String msg) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(AnleggActivity.this);
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
