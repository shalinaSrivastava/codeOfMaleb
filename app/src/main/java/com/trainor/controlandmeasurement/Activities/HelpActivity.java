package com.trainor.controlandmeasurement.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpActivity extends AppCompatActivity {
    @BindView(R.id.ll_support_phone)
    LinearLayout ll_support_phone;
    @BindView(R.id.ll_support_mail)
    LinearLayout ll_support_mail;
    @BindView(R.id.tv_back)
    TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        getControls();
    }

    public void getControls() {
        ll_support_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "support@trainor.no", ""));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Measurement Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body Text");
                    startActivity(Intent.createChooser(emailIntent, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ll_support_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isTelephonyEnabled()) {
                        makeCall();
                    } else {
                        AlertDialogManager.showDialog(HelpActivity.this, "",getResources().getString(R.string.cancel), "", getResources().getString(R.string.device_not_support_call_fun),true,null);
                    }
                } catch (Exception ex) {
                    AlertDialogManager.showDialog(HelpActivity.this,"",getResources().getString(R.string.ok), getResources().getString(R.string.call_permission_error), ex.getMessage(), true, null);
                }
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonIntentMethod(LoginActivity.class);
            }
        });
    }
    public void makeCall() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:+4733378910"));
        startActivity(callIntent);
    }

    public void commonIntentMethod(Class activity) {
        Intent intent = new Intent(HelpActivity.this, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isTelephonyEnabled() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        commonIntentMethod(LoginActivity.class);
    }
}
