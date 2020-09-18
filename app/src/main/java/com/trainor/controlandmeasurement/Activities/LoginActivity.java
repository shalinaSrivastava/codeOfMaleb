package com.trainor.controlandmeasurement.Activities;

import android.app.ProgressDialog;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.JSONParser;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.WebServiceCall;
import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LoginEntity;
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
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.btn_login)
    TextView btn_login;
    @BindView(R.id.edt_userName)
    EditText edt_userName;
    @BindView(R.id.edt_password)
    EditText edt_password;
    @BindView(R.id.tv_forgot_pwd)
    TextView tv_forgot_pwd;
    @BindView(R.id.tv_help)
    TextView tv_help;
    @BindView(R.id.tv_forgot_username)
    TextView tv_forgot_username;

    SoapObject response;
    String message, msg;
    ProgressDialog pDialog;
    ConnectionDetector connectionDetector;
    SharedPreferenceClass spManager;
    SharedPreferences.Editor editor;
    public static String adminID = "";
    ViewModel viewModel;
    List<String> assignmentIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        assignmentIDList = new ArrayList<>();
        viewModel = ViewModelProviders.of(LoginActivity.this).get(ViewModel.class);
        spManager = new SharedPreferenceClass(this);
        editor = this.getSharedPreferences("LoginInfoPref", Context.MODE_PRIVATE).edit();
        canLogin();
    }

    public void canLogin() {
        String adminID = spManager.getLoginInfoValueByKeyName("AdminID");
        if (adminID != null && !adminID.equals("")) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.putExtra("From", "Login");
            startActivity(mainIntent);
        } else {
            getControls();
        }
    }

    public void getControls() {
        connectionDetector = ConnectionDetector.getInstance(this);
        edt_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (s.toString().contains(" ")) {
                        String preValue = edt_userName.getText().toString().substring(0, edt_userName.getText().toString().indexOf(" "));
                        if (edt_userName.getText().length() > 1) {
                            String postValue = edt_userName.getText().toString().substring(edt_userName.getText().toString().indexOf(" ") + 1);
                            edt_userName.setText(preValue + postValue);
                            edt_userName.setSelection(preValue.length());
                        } else {
                            edt_userName.setText(preValue);
                        }
                    }
                }
            }
        });
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (s.toString().contains(" ")) {
                        String preValue = edt_password.getText().toString().substring(0, edt_password.getText().toString().indexOf(" "));
                        if (edt_password.getText().length() > 1) {
                            String postValue = edt_password.getText().toString().substring(edt_password.getText().toString().indexOf(" ") + 1);
                            edt_password.setText(preValue + postValue);
                            edt_password.setSelection(preValue.length());
                        } else {
                            edt_password.setText(preValue);
                        }
                    }
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (validateCredentials()) {
                        loginAPI(edt_userName.getText().toString(), edt_password.getText().toString());
                        try {
                            hideKeyboard();
                            showWaitDialog();
                        } catch (Exception ex) {
                            Log.d("Error", ex.getMessage());
                        }
                    }
                } else {
                    AlertDialogManager.showDialog(LoginActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
            }
        });
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        tv_forgot_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                intent.putExtra("ForgotPassword", "forgotPasword");
                intent.putExtra("Username", edt_userName.getText().toString().trim());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        tv_forgot_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                intent.putExtra("ForgotUsername", "forgotUsername");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void loginAPI(final String username, final String password) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... voids) {
                SoapObject soapObject = new SoapObject(URLs.Login_Name_Space, URLs.Login_Method);
                soapObject.addProperty("username", username);
                soapObject.addProperty("password", password);
                soapObject.addProperty("deviceInfo", Build.MODEL);
                SoapSerializationEnvelope env = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                env.dotNet = false;
                env.setOutputSoapObject(soapObject);
                try {
                    HttpTransportSE transport = new HttpTransportSE(URLs.LoginURL);
                    transport.call(URLs.Login_SOAP_Action, env);
                    response = (SoapObject) env.getResponse();
                } catch (SoapFault sof) {
                    msg = sof.faultstring;
                    if ("authenticationexception".equalsIgnoreCase(msg)) {
                        message = "Innlogging feilet, sjekk brukernavn og passord";
                    } else if ("databaseexception".equalsIgnoreCase(msg)) {
                        message = "Serverfeil, prøv igjen senere";
                    } else {
                        message = "Ukjent feil: " + sof.faultcode;
                    }
                } catch (IOException e) {
                    message = "Tilkoblingsfeil. Påse at du er tilkoblet og prøv på nytt." + e.getMessage();
                    dismissWaitDialog();
                } catch (XmlPullParserException e) {
                    message = "Parserfeil. Vennligst prøv på nytt";
                    dismissWaitDialog();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    if (response != null) {
                        LoginEntity loginInfo = new LoginEntity();
                        for (int i = 0; i < response.getPropertyCount(); i++) {
                            PropertyInfo info = new PropertyInfo();
                            response.getPropertyInfo(i, info);
                            if (info.getName().equals("adminID")) {
                                loginInfo.adminID = Long.parseLong(response.getProperty(i).toString());
                            } else if (info.getName().equals("canLogInApp")) {
                                loginInfo.canLogInApp = response.getProperty(i).toString();
                            } else if (info.getName().equals("canWrite")) {
                                loginInfo.canWrite = response.getProperty(i).toString();
                            } else if (info.getName().equals("companyId")) {
                                loginInfo.companyId = response.getProperty(i).toString();
                            } else if (info.getName().equals("firstname")) {
                                loginInfo.firstname = response.getProperty(i).toString();
                            } else if (info.getName().equals("hasAssignmentConstraints")) {
                                loginInfo.hasAssignmentConstraints = response.getProperty(i).toString();
                            } else if (info.getName().equals("lastname")) {
                                loginInfo.lastname = response.getProperty(i).toString();
                            } else if (info.getName().equals("token")) {
                                loginInfo.token = response.getProperty(i).toString();
                            } else if (info.getName().equals("trainorAdmin")) {
                                loginInfo.trainorAdmin = response.getProperty(i).toString();
                            } else if (info.getName().equals("userType")) {
                                loginInfo.userType = response.getProperty(i).toString();
                            } else if (info.getName().equals("username")) {
                                loginInfo.username = response.getProperty(i).toString();
                            }
                        }
                        spManager.saveLoginInfoValueByKeyName("Token", loginInfo.token, editor);
                        if (loginInfo.trainorAdmin.equals("true")) {
                            spManager.saveLoginInfoValueByKeyName("CompanyID", "0", editor);
                        } else {
                            spManager.saveLoginInfoValueByKeyName("CompanyID", loginInfo.companyId, editor);
                        }
                        spManager.saveLoginInfoValueByKeyName("AdminID", loginInfo.adminID + "", editor);
                        //spManager.saveLoginInfoValueByKeyName("Username", loginInfo.username, editor);

                        // changes on 11-09-2020 first name + last name
                        spManager.saveLoginInfoValueByKeyName("Username", loginInfo.firstname+ " "+loginInfo.lastname, editor);
                        spManager.saveLoginInfoValueByKeyName("TabVisible", "false", editor);
                        spManager.saveLoginInfoValueByKeyName("trainorAdmin", loginInfo.trainorAdmin, editor);
                        viewModel.insertLoginDetails(loginInfo);
                        if (message == null || message.equals("")) {

                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            message = "";
                        }
                        if (loginInfo.trainorAdmin.equals("false")) {
                            getAssignments(loginInfo);
                        } else {
                            loginInfo.companyId = "0";
                            getAssignments(loginInfo);
                        }
                    } else {
                        if (message != null && !message.equals("")) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            message = "";
                        }
                        dismissWaitDialog();
                    }
                } catch (Exception ex) {
                    dismissWaitDialog();
                    Log.d("Error", ex.getMessage());
                }
            }
        }.execute();
    }
   /* public void loginAPI(final String username, final String password) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                JSONArray jsonArray = null;
                String SoapBody = "<log:getLoginToken><username>" + username + "</username><password>" + password + "</password><deviceInfo>" + Build.MODEL + "</deviceInfo></log:getLoginToken>";
                String soapBody = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:log=\"http://login.services.ws.measurements.trainor.no/\">\n" +
                        "   <soap:Header/>\n" +
                        "   <soap:Body>\n" +
                        SoapBody +
                        "   </soap:Body>\n" +
                        "</soap:Envelope>";

               // JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBody, URLs.Login_SOAP_Action);
                String response = "";
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(URLs.URL);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8");
                    connection.setRequestProperty("SOAPAction", URLs.Login_SOAP_Action);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    bufferedWriter.write(soapBody);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String line;
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                    }
                    Document document = JSONParser.loadXMLString(response);
                    jsonArray = JSONParser.getFullData(document);
                } catch (Exception ex) {
                    Log.d("Error", ex.getMessage());
                }
                if (jsonArray != null && jsonArray.length() != 0) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {

                    if (response != null) {
                        LoginEntity loginInfo = new LoginEntity();
                        for (int i = 0; i < response.getPropertyCount(); i++) {
                            PropertyInfo info = new PropertyInfo();
                            response.getPropertyInfo(i, info);

                        }
                        spManager.saveLoginInfoValueByKeyName("Token", loginInfo.token, editor);
                        if (loginInfo.trainorAdmin.equals("true")) {
                            spManager.saveLoginInfoValueByKeyName("CompanyID", "0", editor);
                        } else {
                            spManager.saveLoginInfoValueByKeyName("CompanyID", loginInfo.companyId, editor);
                        }
                        spManager.saveLoginInfoValueByKeyName("AdminID", loginInfo.adminID + "", editor);
                        //spManager.saveLoginInfoValueByKeyName("Username", loginInfo.username, editor);

                        // changes on 11-09-2020 first name + last name
                        spManager.saveLoginInfoValueByKeyName("Username", loginInfo.firstname + " " + loginInfo.lastname, editor);
                        spManager.saveLoginInfoValueByKeyName("TabVisible", "false", editor);
                        spManager.saveLoginInfoValueByKeyName("trainorAdmin", loginInfo.trainorAdmin, editor);
                        viewModel.insertLoginDetails(loginInfo);
                        if (message == null || message.equals("")) {

                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            message = "";
                        }
                        if (loginInfo.trainorAdmin.equals("false")) {
                            getAssignments(loginInfo);
                        } else {
                            loginInfo.companyId = "0";
                            getAssignments(loginInfo);
                        }
                    } else {
                        if (message != null && !message.equals("")) {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            message = "";
                        }
                        dismissWaitDialog();
                    }
                } catch (Exception ex) {
                    dismissWaitDialog();
                    Log.d("Error", ex.getMessage());
                }
            }
        }.execute();
    }*/

    public void showWaitDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
        }
        pDialog.setMessage(getResources().getString(R.string.logging_in));
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

    public boolean validateCredentials() {
        if (edt_userName.getText().toString().isEmpty()) {
            AlertDialogManager.showDialog(LoginActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.enter_valid_username), false, null);
            return false;
        }
        if (edt_password.getText().toString().isEmpty()) {
            AlertDialogManager.showDialog(LoginActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.enter_valid_password), false, null);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void getAssignments(LoginEntity loginEntity) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBody = "<log:getAssignments><loginToken>" + loginEntity.token + "</loginToken><companyId>" + loginEntity.companyId + "</companyId></log:getAssignments>";
                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBody, URLs.SOAP_ACTION_GET_ASSIGNMENTS);
                    if (jsonArray != null && jsonArray.length() != 0) {
                        viewModel.deleteAllAssignment();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            AssignmentEntity assignmentInfo = new AssignmentEntity();
                            assignmentInfo.adminID = loginEntity.adminID;
                            assignmentInfo.assignmentID = Integer.parseInt(jsonObject.getString("assignmentID"));
                            assignmentInfo.companyID = Integer.parseInt(jsonObject.getString("companyID"));
                            assignmentInfo.companyName = jsonObject.getString("companyName");
                            assignmentInfo.assignmentName = jsonObject.getString("name");
                            assignmentInfo.status = Integer.parseInt(jsonObject.getString("status"));
                            AssignmentEntity assignmentEntity = viewModel.getAssignment(assignmentInfo.assignmentID);
                            if (assignmentEntity == null || assignmentEntity.assignmentID == 0) {
                                viewModel.insertAssignment(assignmentInfo);
                            }
                            assignmentIDList.add(assignmentInfo.assignmentID + "");
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
                if (loginEntity.canLogInApp.equals("true")) {
                    dismissWaitDialog();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
                dismissWaitDialog();
                /*if (assignmentIDList != null && assignmentIDList.size() > 0) {
                    viewModel.deleteAllFolders();
                    getFolders(loginEntity.token, assignmentIDList.get(0), loginEntity);
                } else {
                    if (loginEntity.canLogInApp.equals("true")) {
                        dismissWaitDialog();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                    dismissWaitDialog();
                }*/
            }
        }.execute();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void getFolders(final String loginToken, String assignmentID, LoginEntity loginEntity) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String soapBody = "<log:getFolders><loginToken>" + loginToken + "</loginToken><assignmentId>" + assignmentID + "</assignmentId></log:getFolders>";
                    JSONArray jsonArray = WebServiceCall.callSoapAPI(soapBody, URLs.SOAP_ACTION_GET_FOLDER);
                    if (jsonArray != null && jsonArray.length() != 0) {
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
                if (assignmentIDList.size() > 0) {
                    assignmentIDList.remove(0);
                }
                if (assignmentIDList.size() > 0) {
                    getFolders(loginEntity.token, assignmentIDList.get(0), loginEntity);
                } else {
                    if (loginEntity.canLogInApp.equals("true")) {
                        dismissWaitDialog();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    }
                    dismissWaitDialog();
                }
            }
        }.execute();
    }
}