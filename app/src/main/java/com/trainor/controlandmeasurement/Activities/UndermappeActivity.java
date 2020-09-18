package com.trainor.controlandmeasurement.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trainor.controlandmeasurement.AdapterClasses.UndermappeAdapter;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.WebServiceCall;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class UndermappeActivity extends AppCompatActivity {
    private static UndermappeActivity instance;
    EditText edt_measuementId;
    TextView header_title;
    RecyclerView recyclerView;
    ImageView img_back;
    Button btn_search;
    ViewModel viewModel;
    ProgressDialog pDialog;
    ConnectionDetector connectionDetector;
    String token, anleggID, from, folderID;
    UndermappeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_undermappe);
        viewModel = ViewModelProviders.of(UndermappeActivity.this).get(ViewModel.class);
        connectionDetector = new ConnectionDetector(UndermappeActivity.this);
        from = getIntent().getExtras().getString("From");
        if (from.equals("Select Undermappe")) {
            token = getIntent().getExtras().getString("LoginToken");
        } else {
            folderID = getIntent().getExtras().getString("FolderID");
        }
        anleggID = getIntent().getExtras().getString("AnleggID");
        header_title = (TextView) findViewById(R.id.header_title);
        img_back = (ImageView) findViewById(R.id.img_back);
        btn_search = (Button) findViewById(R.id.btn_search);
        edt_measuementId = (EditText) findViewById(R.id.edt_measuementId);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        header_title.setText(from);
        recyclerView.setLayoutManager(new LinearLayoutManager(UndermappeActivity.this));
        adapter = new UndermappeAdapter();
        recyclerView.setAdapter(adapter);
        if (from.equals("Select Undermappe")) {
            if (connectionDetector.isConnectingToInternet()) {
                getFolders(token, anleggID);
            } else {
                List<FolderEntity> list = viewModel.getFolders(anleggID);
                if (list != null && list.size() > 0) {
                    adapter.setList(list);
                } else {
                    AlertDialogManager.showDialog(UndermappeActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.no_folder_with_this_plant), false, new IClickListener() {
                        @Override
                        public void onClick() {
                            finish();
                        }
                    });
                }
            }
        } else {
            List<FolderEntity> subFolderList = viewModel.getSubFolderName(anleggID, folderID);
            if (subFolderList != null && subFolderList.size() > 0) {
                adapter.setList(subFolderList);
            } else {
                AlertDialogManager.showDialog(UndermappeActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.no_folder_with_this_plant), false, new IClickListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
            }
        }
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = edt_measuementId.getText().toString();
                if (from.equals("Select Undermappe")) {
                    List<FolderEntity> list = viewModel.searchFolderName(anleggID, val);
                    if (list != null && list.size() > 0) {
                        adapter.setList(list);
                    } else {
                        AlertDialogManager.showDialog(UndermappeActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.no_folder_with_this_plant), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                List<FolderEntity> list = viewModel.getFolders(anleggID);
                                adapter.setList(list);
                            }
                        });
                    }
                } else {
                    List<FolderEntity> _list = viewModel.searchSubFolderName(anleggID, val);
                    if (_list != null && _list.size() > 0) {
                        adapter.setList(_list);
                    } else {
                        AlertDialogManager.showDialog(UndermappeActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.no_folder_with_this_plant), false, new IClickListener() {
                            @Override
                            public void onClick() {
                                List<FolderEntity> subFolderList = viewModel.getSubFolderName(anleggID, folderID);
                                adapter.setList(subFolderList);
                            }
                        });
                    }
                }
            }
        });
        instance = this;
    }

    public static UndermappeActivity getInstance() {
        if (instance == null) {
            instance = new UndermappeActivity();
        }
        return instance;
    }

    public void getFolders(final String loginToken, String assignmentID) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showWaitDialog(getResources().getString(R.string.please_wait));
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBody = "<log:getFolders><loginToken>" + loginToken + "</loginToken><assignmentId>" + assignmentID + "</assignmentId></log:getFolders>";
                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBody, URLs.SOAP_ACTION_GET_FOLDER);
                    if (jsonArray != null && jsonArray.length() != 0) {
                        viewModel.deleteAllFolders();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            FolderEntity getFolderInfo = new FolderEntity();
                            getFolderInfo.assignmentID = jsonObject.getString("assignmentID");
                            getFolderInfo.folderID = jsonObject.getString("folderID");
                            getFolderInfo.name = jsonObject.getString("name");
                            getFolderInfo.parentID = jsonObject.getString("parentID");
                            viewModel.insertFolderDetails(getFolderInfo);
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
                List<FolderEntity> folderList = viewModel.getFolders(anleggID);
                if (folderList != null && folderList.size() > 0) {
                    adapter.setList(folderList);
                } else {
                    AlertDialogManager.showDialog(UndermappeActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.no_folder_with_this_plant), false, new IClickListener() {
                        @Override
                        public void onClick() {
                            finish();
                        }
                    });
                }
            }
        }.execute();
    }

    public void showWaitDialog(String msg) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(UndermappeActivity.this);
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

    public void getSelectedFolder(FolderEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("FolderName", entity.name);
        intent.putExtra("AssignmentID", entity.assignmentID + "");
        intent.putExtra("FolderID", entity.folderID);
        intent.putExtra("ParentID", entity.parentID);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(edt_measuementId.getWindowToken(), 0);
        }
    }
}
