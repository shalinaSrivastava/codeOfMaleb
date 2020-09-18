package com.trainor.controlandmeasurement.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotPassword extends AppCompatActivity {
    @BindView(R.id.tv_help)
    TextView tv_help;
    @BindView(R.id.tv_back)
    TextView tv_back;

    @BindView(R.id.text_heading)
    TextView text_heading;
    @BindView(R.id.edt_text_uname_pwd)
    EditText edt_text_uname_pwd;
    @BindView(R.id.btn_forgot_pwd_uname)
    Button btn_forgot_pwd_uname;
    String status_msg, error_msg;
    ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        getControls();
    }

    public void getControls() {
        connectionDetector = ConnectionDetector.getInstance(this);
        edt_text_uname_pwd.addTextChangedListener(new TextWatcher() {
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
                        String preValue = edt_text_uname_pwd.getText().toString().substring(0, edt_text_uname_pwd.getText().toString().indexOf(" "));
                        if (edt_text_uname_pwd.getText().length() > 1) {
                            String postValue = edt_text_uname_pwd.getText().toString().substring(edt_text_uname_pwd.getText().toString().indexOf(" ") + 1);
                            edt_text_uname_pwd.setText(preValue + postValue);
                            edt_text_uname_pwd.setSelection(preValue.length());
                        } else {
                            edt_text_uname_pwd.setText(preValue);
                        }
                    }
                }
            }
        });
        if (getIntent().getStringExtra("ForgotPassword") != null && getIntent().getStringExtra("ForgotPassword").equals("forgotPasword")) {
            text_heading.setText(getResources().getString(R.string.username));
            btn_forgot_pwd_uname.setText(getResources().getString(R.string.send_new_password));
            edt_text_uname_pwd.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (getIntent().getStringExtra("ForgotUsername") != null && getIntent().getStringExtra("ForgotUsername").equals("forgotUsername")) {
            btn_forgot_pwd_uname.setText(getResources().getString(R.string.get_username));
            text_heading.setText(getResources().getString(R.string.email));
            //edt_text_uname_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if(getIntent().getStringExtra("Username")!= null && !getIntent().getStringExtra("Username").equals(" ")){
            edt_text_uname_pwd.setText(getIntent().getStringExtra("Username"));
        }
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonIntentMethod(HelpActivity.class);
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("ForgotPassword") != null && getIntent().getStringExtra("ForgotPassword").equals("forgotPasword") ||
                        getIntent().getStringExtra("ForgotUsername") != null && getIntent().getStringExtra("ForgotUsername").equals("forgotUsername")) {
                    commonIntentMethod(LoginActivity.class);
                } else {
                    commonIntentMethod(HelpActivity.class);
                }
            }
        });

        btn_forgot_pwd_uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    if (getIntent().getStringExtra("ForgotUsername") != null && getIntent().getStringExtra("ForgotUsername").equals("forgotUsername")) {
                        boolean validEmail = isValidEmail(edt_text_uname_pwd.getText());
                        if (validEmail) {
                            getUsernamePassword("https://maaling.trainor.no/maaling/ajax/global?ie=false&func=getUsername&email=" + edt_text_uname_pwd.getText().toString(), "UsernameRecover");
                        } else {
                            Toast.makeText(ForgotPassword.this, getResources().getString(R.string.enter_valid_email_address), Toast.LENGTH_SHORT).show();
                        }
                    } else if (getIntent().getStringExtra("ForgotPassword") != null && getIntent().getStringExtra("ForgotPassword").equals("forgotPasword")) {
                        String username = edt_text_uname_pwd.getText().toString();
                        if (!username.equals("") || username.length() > 0) {
                            //todo currently removed +edt_text_uname_pwd.getText().toString() from below url, add when it is needed.
                            getUsernamePassword("https://maaling.trainor.no/maaling/ajax/global?ie=false&func=getNewPassword&username=" + edt_text_uname_pwd.getText().toString(), "PasswordRecover");
                        } else {
                            Toast.makeText(ForgotPassword.this, getResources().getString(R.string.enter_valid_username), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    AlertDialogManager.showDialog(ForgotPassword.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
            }
        });
    }

    public void commonIntentMethod(Class activity) {
        Intent intent = new Intent(ForgotPassword.this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        commonIntentMethod(LoginActivity.class);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void getUsernamePassword(String URL, final String type) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("status")) {
                        status_msg = jsonObject.getString("status");
                    }
                    if (jsonObject.has("errormessage")) {
                        error_msg = jsonObject.getString("errormessage");
                    }
                } catch (Exception ex) {
                    Log.d("API Exception", ex.getMessage());
                } finally {
                    if (status_msg.equals("error") && type.equals("UsernameRecover")) {
                        Toast.makeText(ForgotPassword.this, error_msg, Toast.LENGTH_SHORT).show();
                    }
                    if (status_msg.equals("ok") && type.equals("UsernameRecover")) {
                        Toast.makeText(ForgotPassword.this, getResources().getString(R.string.username_sent), Toast.LENGTH_SHORT).show();
                    }
                    if (status_msg.equals("error") && type.equals("PasswordRecover")) {
                        Toast.makeText(ForgotPassword.this, error_msg, Toast.LENGTH_SHORT).show();
                    }
                    if (status_msg.equals("ok") && type.equals("PasswordRecover")) {
                        Toast.makeText(ForgotPassword.this, getResources().getString(R.string.password_sent), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                Toast.makeText(ForgotPassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("content-type", "application/json");
                params.put("accept", "application/json");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue11 = Volley.newRequestQueue(ForgotPassword.this);
        requestQueue11.add(stringRequest);
    }
}
