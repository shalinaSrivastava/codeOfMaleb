package com.trainor.controlandmeasurement.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.trainor.controlandmeasurement.AdapterClasses.AnleggAdapter;
import com.trainor.controlandmeasurement.AdapterClasses.CompanyListAdapter;
import com.trainor.controlandmeasurement.AdapterClasses.GetLettersRecyclerViewAdapter;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.WebServiceCall;
import com.trainor.controlandmeasurement.MVVM.Entities.CompaniesEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompanyListActivity extends AppCompatActivity implements Filterable {
    private static CompanyListActivity instance;
    EditText edt_companyName;
    RecyclerView recyclerView;
    ImageView img_back;
    Button btn_search;
    ProgressDialog pDialog;
    TextView txt_heading;
    ConnectionDetector connectionDetector;
    String loginToken, companyId;
    SharedPreferenceClass spManager;
    public SharedPreferences.Editor editor;
    List<CompaniesEntity> companieslist, filteredList;
    CompanyListAdapter companiesListRecyclerViewAdapter;
    CustomFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anlegg);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getControls();
        instance = this;
    }

    public static synchronized CompanyListActivity getInstance() {
        if (instance == null) {
            instance = new CompanyListActivity();
        }
        return instance;
    }

    public void getControls() {
        editor = getSharedPreferences("CompanyPref", Context.MODE_PRIVATE).edit();
        spManager = new SharedPreferenceClass(this);
        connectionDetector = new ConnectionDetector(CompanyListActivity.this);
        companieslist = new ArrayList<>();
        filteredList = new ArrayList<>();
        mFilter = new CustomFilter();
        txt_heading = findViewById(R.id.txt_heading);
        txt_heading.setText(getResources().getString(R.string.select_company));
        img_back = findViewById(R.id.img_back);
        btn_search = findViewById(R.id.btn_search);
        edt_companyName = findViewById(R.id.edt_measuementId);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(CompanyListActivity.this));
        companiesListRecyclerViewAdapter = new CompanyListAdapter(CompanyListActivity.this);
        recyclerView.setAdapter(companiesListRecyclerViewAdapter);
        loginToken = spManager.getLoginInfoValueByKeyName("Token");
        companyId = spManager.getLoginInfoValueByKeyName("CompanyID");
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_companyName.getText().toString().trim().equals("")) {
                    AlertDialogManager.showDialog(CompanyListActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.enter_valid_company_name), false, null);
                } else {
                    getFilter().filter(edt_companyName.getText().toString().trim().toLowerCase());
                }
            }
        });

        if (connectionDetector.isConnectingToInternet()) {
            callCompnyListApi(loginToken, companyId);
        } else {
            AlertDialogManager.showDialog(CompanyListActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, new IClickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            });
        }

       /* edt_companyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getFilter().filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/
    }

    public void callCompnyListApi(String loginToken, String companyId) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showWaitDialog(getResources().getString(R.string.please_wait));
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBdy = "<log:getCompanies><loginToken>" + loginToken + "</loginToken></log:getCompanies>";
                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBdy, URLs.SOAP_ACTION_GET_Companies);
                    companieslist.clear();
                    if (jsonArray != null && jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CompaniesEntity getCompanyInfo = new CompaniesEntity();
                            getCompanyInfo.companyId = jsonObject.getString("companyId");
                            getCompanyInfo.contactPerson = jsonObject.getString("contactPerson");
                            getCompanyInfo.department = jsonObject.getString("department");
                            getCompanyInfo.companyName = jsonObject.getString("name");
                            getCompanyInfo.totalLetters = jsonObject.getString("totalLetters");
                            companieslist.add(getCompanyInfo);
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
                if (companieslist.size() > 0) {
                    companiesListRecyclerViewAdapter.setList(companieslist);
                } else {
                    AlertDialogManager.showDialog(CompanyListActivity.this, getResources().getString(R.string.ok), "", "", "No company found", false, new IClickListener() {
                        @Override
                        public void onClick() {
                            finish();
                        }
                    });
                }
            }
        }.execute();
    }

    public class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(companieslist);
            } else {
                for (final CompaniesEntity filteredEntity : companieslist) {
                    String val = filteredEntity.companyName.toLowerCase();
                    String val2 = constraint.toString().toLowerCase();
                    if (val.contains(val2)) {
                        filteredList.add(filteredEntity);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            companiesListRecyclerViewAdapter.setList(filteredList);
            hideKeyboard();
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public void onBackPressed() {
        hideKeyboard();
        finish();
    }


    public void showWaitDialog(String msg) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(CompanyListActivity.this);
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(edt_companyName.getWindowToken(), 0);
        }
    }

    public void getSelectedCompany(CompaniesEntity entity) {
        editor.putString("CompanyID", entity.companyId);
        editor.commit();
        setResult(0);
        finish();
    }
}
