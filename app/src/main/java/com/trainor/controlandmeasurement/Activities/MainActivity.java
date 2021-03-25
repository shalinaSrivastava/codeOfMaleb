package com.trainor.controlandmeasurement.Activities;

import android.app.ProgressDialog;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.HelperClass.XMLParser;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;
import com.trainor.controlandmeasurement.fragments.CalculationsFragment;
import com.trainor.controlandmeasurement.fragments.GeneralInfoFragment;
import com.trainor.controlandmeasurement.fragments.PicturesFragment;
import com.trainor.controlandmeasurement.fragments.HomeFragment;
import com.trainor.controlandmeasurement.fragments.MeasuredValueFragment;
import com.trainor.controlandmeasurement.fragments.VariablesFragment;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static MainActivity instance;
    public TabLayout tabLayout;
    public String measurementPointID, anleggID, faultString = "";
    public ViewPager viewPager;
    SharedPreferenceClass spManager;
    @BindView(R.id.ll_menu)
    LinearLayout ll_menu;
    @BindView(R.id.ll_menu_options)
    LinearLayout ll_menu_options;
    @BindView(R.id.iv_open_menu)
    ImageView iv_open_menu;
    @BindView(R.id.iv_hide_menu)
    ImageView iv_hide_menu;
    @BindView(R.id.tv_menu_text)
    TextView tv_menu_text;
    @BindView(R.id.rl_menu_options)
    RelativeLayout rl_menu_options;
    @BindView(R.id.ll_save)
    LinearLayout ll_save;
    @BindView(R.id.ll_upload)
    LinearLayout ll_upload;
    @BindView(R.id.ll_discard)
    LinearLayout ll_discard;
    @BindView(R.id.tv_title)
    public TextView tv_title;

    public ViewModel viewModel;
    public long _letterIDFromServer, adminID;
    List<ImageEntity> uploadImagesList = new ArrayList<>();
    boolean hasImage = false, isUploadBtnClicked;
    SoapObject imageResponse, response;
    ProgressDialog dialog;
    public ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    List<Fragment> mFragmentList = new ArrayList<>();
    SharedPreferences.Editor editor, loginEditor;
    ConnectionDetector connectionDetector;
    boolean isMPIDUnique;
    public static String from = "", fromCamera = "";
    public int notUploadedImageCount = 0;
    public boolean variablePageClicked = false, configChanged = false;
    long imageID = 0;
    String isNewLetter = "true";
    String imageIdsUpload = "0", firstImageId = "0";
    ArrayList<Long> imageIds;
    Long letteridToCheckNewLetter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fromCamera = bundle.get("From").toString();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ButterKnife.bind(this);
        getControls();
        instance = this;
    }

    public synchronized static MainActivity getInstance() {
        if (instance == null) {
            instance = new MainActivity();
        }
        return instance;
    }

    public void getControls() {
/*
        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*/

        if (editor == null) {
            editor = getSharedPreferences("GeneralInfoPref", Context.MODE_PRIVATE).edit();
        }
        if (loginEditor == null) {
            loginEditor = getSharedPreferences("LoginInfoPref", Context.MODE_PRIVATE).edit();
        }
        viewModel = ViewModelProviders.of(MainActivity.this).get(ViewModel.class);
        spManager = new SharedPreferenceClass(this);
        connectionDetector = ConnectionDetector.getInstance(this);
        viewPager = findViewById(R.id.viewpager);
        adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        tabLayout.setHorizontalScrollBarEnabled(false);
        for (int i = 0; i < tabLayout.getTabCount() - 1; i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            if (!spManager.getLoginInfoValueByKeyName("TabVisible").equals("true")) {
                if (i != 0) {
                    tab.setBackgroundColor(getResources().getColor(R.color.tab_grey_color));
                    tab.setClickable(false);
                }
            }
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, 3, 0);
            tab.requestLayout();
        }

        View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tabLayout.getTabCount() - 1);
        if (!spManager.getLoginInfoValueByKeyName("TabVisible").equals("true")) {
            tab.setBackgroundColor(getResources().getColor(R.color.tab_grey_color));
            tab.setClickable(false);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (i == 0) {
                    rl_menu_options.setVisibility(View.GONE);
                } else {
                    showMenuOptions();
                }
            }

            @Override
            public void onPageSelected(int i) {
                Fragment frag = mFragmentList.get(i);
                try {
                    if (frag instanceof HomeFragment) {
                        HomeFragment homeFragment = (HomeFragment) frag;
                        if (homeFragment != null) {
                            homeFragment.setActiveCertificate();
                            homeFragment.getControls();
                        }
                    }
                    if (frag instanceof MeasuredValueFragment) {
                        MeasuredValueFragment measuredValueFragment = (MeasuredValueFragment) frag;
                        if (measuredValueFragment != null) {
                            measuredValueFragment.setDefaultValues();
                        }
                    }
                    if (frag instanceof GeneralInfoFragment) {
                        GeneralInfoFragment generalInfoFragment = (GeneralInfoFragment) frag;
                        if (generalInfoFragment != null) {
                            generalInfoFragment.getControls();
                        }
                    }
                    if (frag instanceof VariablesFragment) {
                        VariablesFragment variablesFragment = (VariablesFragment) frag;
                        if (variablesFragment != null) {
                            variablesFragment.getControls();
                        }
                    }
                    if (frag instanceof PicturesFragment) {
                        PicturesFragment picturesFragment = (PicturesFragment) frag;
                        if (picturesFragment != null) {
                            picturesFragment.getControls();
                        }
                    }
                    if (frag instanceof CalculationsFragment) {
                        CalculationsFragment calculationFragment = (CalculationsFragment) frag;
                        if (calculationFragment != null) {
                            calculationFragment.getControls();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < tabLayout.getTabCount(); j++) {
                    View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(j);
                    if (spManager.getLoginInfoValueByKeyName("TabVisible").equals("true")) {
                        if (j == i) {
                            tab.setBackgroundColor(getResources().getColor(R.color.color_selected));
                        } else {
                            tab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                }
                if (i == 2) {
                    variablePageClicked = true;
                }
                if (!from.equals("NewLetter")) {
                    setSpinnerValues();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (!spManager.getLoginInfoValueByKeyName("TabVisible").equals("true")) {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        ll_save.setOnClickListener(this);
        ll_upload.setOnClickListener(this);
        ll_discard.setOnClickListener(this);
        setTitle();
        if (fromCamera.equals("Camera")) {
            fromCamera = "";
            viewPager.setCurrentItem(5);
        }

        imageIds = new ArrayList<Long>();

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new HomeFragment(), "Hjem");
        adapter.addFragment(new GeneralInfoFragment(), "Generell Info");
        adapter.addFragment(new VariablesFragment(), "Variabler");
        adapter.addFragment(new MeasuredValueFragment(), "Måleverdier");
        adapter.addFragment(new CalculationsFragment(), "Beregninger");
        adapter.addFragment(new PicturesFragment(), "Bilder");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_save:
                measurementPointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
                anleggID = spManager.getGeneralInfoValueByKeyName("AnleggID");
                if (measurementPointID.equals("")) {
                    showToast(getResources().getString(R.string.balnk_measurement_pt));
                    return;
                } else if (anleggID.equals("")) {
                    showToast(getResources().getString(R.string.invalid_anlegg));
                    return;
                }
                LetterEntity _entity = getLetterEntity();
                if (_entity.voltage.equals("-2")) {
                    showToast(getResources().getString(R.string.invalid_voltage));
                    return;
                } else if (_entity.electrode.equals("-2")) {
                    showToast(getResources().getString(R.string.invalid_elektrode));
                    return;
                }
                long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
                _entity.letterID = _letterID;
                //List<LetterEntity> _list = viewModel.letterLocalExists(_entity.adminID, _entity.letterID, oldMPID);
                List<LetterEntity> _list = viewModel.letterLocalExists(_entity.adminID, _entity.letterID, measurementPointID);
                if (_list == null || _list.size() == 0) {
                    viewModel.insertLetter(_entity);
                } else {
                    _entity.ID = _list.get(0).ID;
                    _entity.letterID = _list.get(0).letterID;
                    _entity.measurePointID = measurementPointID;
                    viewModel.updateLetter(_entity);
                }
                spManager.saveGeneralInfoValueByKeyName("ActiveMeasurementPointID", measurementPointID, editor);
                HomeFragment.getInstance().setActiveCertificate();
                showToast(getResources().getString(R.string.saved));
                break;
            case R.id.ll_upload:
                if (connectionDetector.isConnectingToInternet()) {
                    uploadLetter();
                } else {
                    AlertDialogManager.showDialog(MainActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                }
                break;
            case R.id.ll_discard:
                final String _measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
                String anleggID = spManager.getGeneralInfoValueByKeyName("AnleggID");
                if (!_measurePointID.equals("") && !anleggID.equals("")) {
                    AlertDialogManager.showDialog(MainActivity.this, getResources().getString(R.string.discard), getResources().getString(R.string.cancel), getResources().getString(R.string.discard), getResources().getString(R.string.want_to_delete_letter), true, new IClickListener() {
                        @Override
                        public void onClick() {
                            long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
                            viewModel.deleteLetterBasedOnMeasurePointID(adminID, _letterID, _measurePointID);
                            deleteLetter(_measurePointID);
                            spManager.saveLoginInfoValueByKeyName("TabVisible", "false", loginEditor);
                            MainActivity mainActivity = MainActivity.getInstance();
                            TabLayout tabLayout = mainActivity.tabLayout;
                            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                                if (i != 0) {
                                    tab.setBackgroundColor(getResources().getColor(R.color.tab_grey_color));
                                    tab.setClickable(false);
                                }
                            }
                            mainActivity.viewPager.setCurrentItem(0, true);
                            HomeFragment.getInstance().getControls();
                            showToast(getResources().getString(R.string.letter_deleted_sucessfully));
                        }
                    });
                } else {
                    reset();
                }
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void showMenuOptions() {
        ll_menu.setVisibility(View.VISIBLE);
        rl_menu_options.setVisibility(View.VISIBLE);
        final Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        final Animation slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_to_right);
        ll_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_menu_options.clearAnimation();
                if (iv_open_menu.isShown()) {
                    iv_open_menu.setVisibility(View.GONE);
                    iv_hide_menu.setVisibility(View.VISIBLE);
                    tv_menu_text.setText(getString(R.string.hide));
                    ll_menu_options.setVisibility(View.VISIBLE);
                    ll_menu_options.startAnimation(slideLeft);
                } else {
                    iv_open_menu.setVisibility(View.VISIBLE);
                    iv_hide_menu.setVisibility(View.GONE);
                    tv_menu_text.setText(getString(R.string.menu));
                    ll_menu_options.startAnimation(slideRight);
                    ll_menu_options.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String tokenFrmPref = spManager.getLoginInfoValueByKeyName("Token");
        if (tokenFrmPref != null && !tokenFrmPref.equals("")) {
            finishAffinity();
        }
    }

    public void uploadLetter(final String loginToken, final String letterJSON, final boolean forceSave, final boolean hasImages, final LetterEntity _entity) {
        letteridToCheckNewLetter = _entity.letterID;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showWaitDialog(getResources().getString(R.string.uploading_letters));
                faultString = "";
                _letterIDFromServer = 0;
                notUploadedImageCount = 0;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                SoapObject request = new SoapObject(URLs.NAMESPACE, URLs.METHOD_NAME_SAVE_Letter);
                request.addProperty("loginToken", loginToken);
                request.addProperty("letterGson", letterJSON);
                request.addProperty("forceSave", forceSave);
                request.addProperty("hasImages", hasImages);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(request);
                envelope.dotNet = false;
                try {
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URLs.URL);
                    androidHttpTransport.call(URLs.NAMESPACE + URLs.METHOD_NAME_SAVE_Letter, envelope);
                    if (envelope.bodyIn instanceof SoapFault) {
                        faultString = ((SoapFault) envelope.bodyIn).faultstring;
                    } else {
                        SoapObject response = (SoapObject) envelope.bodyIn;
                        for (int i = 0; i < response.getPropertyCount(); i++) {
                            SoapObject soapObj = (SoapObject) response.getProperty(i);
                            for (int j = 0; j < soapObj.getPropertyCount(); j++) {
                                PropertyInfo info = new PropertyInfo();
                                soapObj.getPropertyInfo(j, info);
                                if (info.getName().equals("letterId")) {
                                    _letterIDFromServer = Long.parseLong(soapObj.getProperty(j).toString());
                                    _entity.Tag = "uploaded";
                                    _entity.validID = "True";
                                    _entity.letterID = _letterIDFromServer;
                                    _entity.companyName = spManager.getGeneralInfoValueByKeyName("CompanyName");
                                    _entity.adminID = adminID;
                                    spManager.saveGeneralInfoValueByKeyName("letterID", _letterIDFromServer + "", editor);
                                    viewModel.updateLetter(_entity);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    dismissWaitDialog();
                    isUploadBtnClicked = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                GeneralInfoFragment.getInstance().edt_measuring_point_ID.clearFocus();
                GeneralInfoFragment.getInstance().edt_measuring_point_ID.setFocusable(false);
                GeneralInfoFragment.getInstance().edt_measuring_point_ID.setOnFocusChangeListener(null);
                if (!faultString.equals("")) {
                    isUploadBtnClicked = false;
                    dismissWaitDialog();
                    showToast(faultString);
                } else {
                    if (hasImages && _letterIDFromServer != 0) {
                        //imgPosition = 0;
                        if (uploadImagesList.size() > 0) {
                            if (uploadImagesList.size() == 1) {
                                uploadImage(_letterIDFromServer, uploadImagesList.get(0), "true");
                            } else {
                                uploadImage(_letterIDFromServer, uploadImagesList.get(0), "false");
                            }
                        } else {
                            dismissWaitDialog();
                        }
                    } else if (_letterIDFromServer != 0) {
                        isUploadBtnClicked = false;
                        dismissWaitDialog();
                        showToast(getResources().getString(R.string.letter_uploaded_sucessfully));
                        reset();
                    }
                }
            }
        }.execute();
    }

    public void uploadImage(final long letterID, final ImageEntity imageEntity, final String isLast) {
        imageResponse = null;
        faultString = "";
        File imagefile = new File(imageEntity.filePath);
        if (imagefile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            String imageData = Base64.encodeToString(b, Base64.DEFAULT);
            final String letterImageGson = "{\"description\":\"" + imageEntity.description + "\",\"filename\":\"" + imageEntity.fileName + "\",\"imageData\":\"" + imageData + "\"}";
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    SoapObject request = new SoapObject(URLs.NAMESPACE, URLs.METHOD_NAME_UPLOAD_IMAGE);
                    request.addProperty("loginToken", spManager.getLoginInfoValueByKeyName("Token"));
                    request.addProperty("letterId", letterID);
                    request.addProperty("letterImageGson", letterImageGson);
                    request.addProperty("isLast", isLast);
                    if (letteridToCheckNewLetter == 0) {
                        isNewLetter = "true";
                    } else {
                        isNewLetter = "false";
                    }
                    request.addProperty("isNewLetter", isNewLetter);
                    if (firstImageId.equals("0")) {
                        request.addProperty("imageIds", "");
                    } else {
                        for (Long imageId : imageIds) {
                            request.addProperty("imageIds", imageId);
                        }
                    }

                    //request.addProperty("imageIds", imageIdsUpload);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;
                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(URLs.URL);
                        androidHttpTransport.call(URLs.NAMESPACE + URLs.METHOD_NAME_UPLOAD_IMAGE, envelope);
                        if (envelope.bodyIn instanceof SoapFault) {
                            faultString = "";
                            faultString = ((SoapFault) envelope.bodyIn).faultstring;
                        } else {
                            imageResponse = (SoapObject) envelope.bodyIn;
                        }
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                        notUploadedImageCount += 1;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    imageEntity.letterID = _letterIDFromServer;
                    viewModel.updatetImage(imageEntity);
                    if (!faultString.equals("")) {
                        showToast(faultString);
                        notUploadedImageCount += 1;
                    } else {
                        if (imageResponse != null) {
                            for (int i = 0; i < imageResponse.getPropertyCount(); i++) {
                                SoapObject soapObj = (SoapObject) imageResponse.getProperty(i);
                                for (int j = 0; j < soapObj.getPropertyCount(); j++) {
                                    PropertyInfo info = new PropertyInfo();
                                    soapObj.getPropertyInfo(j, info);
                                    if (info.getName().equals("imageId")) {
                                        imageID = Long.parseLong(soapObj.getProperty(j).toString());
                                        System.out.println("Image id = " + imageID);
                                        firstImageId = "Not First";
                                        imageIds.add(imageID);
                                       /* if(imageid.equals("0")){
                                            imageid = imageID+"";
                                        }else{
                                            //imageid = ","+imageID+"";
                                            imageIds.add(imageID);
                                        }*/
                                        //String a = imageid.substring(0, imageid.lastIndexOf(","));
                                        System.out.println("Array to be upload = " + imageIdsUpload);
                                        //imageIdsUpload = "["+imageid+"]";
                                        System.out.println("Array to be upload = " + imageIdsUpload);
                                        imageEntity.Tag = "uploaded";
                                        viewModel.updatetImage(imageEntity);
                                    }
                                }
                            }
                        }
                    }
                    uploadImagesList.remove(uploadImagesList.get(0));
                    if (uploadImagesList.size() > 0) {
                        if (uploadImagesList.size() == 1) {
                            uploadImage(letterID, uploadImagesList.get(0), "true");
                        } else {
                            uploadImage(letterID, uploadImagesList.get(0), "false");
                        }
                    } else {
                        firstImageId = "0";
                        isUploadBtnClicked = false;
                        dismissWaitDialog();
                        if (notUploadedImageCount != 0) {
                            if (notUploadedImageCount == 1) {
                                AlertDialogManager.showDialog(MainActivity.this, getResources().getString(R.string.ok), "", "", getResources().getString(R.string.image_could_not_uploaded), false, null);
                            } else {
                                AlertDialogManager.showDialog(MainActivity.this, getResources().getString(R.string.ok), "", "", notUploadedImageCount + " " + getResources().getString(R.string.image_could_not_uploaded), false, null);
                            }
                        } else {
                            showToast(getResources().getString(R.string.letter_uploaded_sucessfully));
                        }
                        reset();
                    }
                    //dismissWaitDialog();
                }
            }.execute();
        } else {
            imagefile.delete();
            viewModel.deleteImage(imageEntity);
        }
    }

    public LetterEntity getLetterEntity() {
        double disablementPosition = 0, earthPosition = 0, electrodePosition = 0, globalEarthPosition = 0, measureTakenPosition = 0, moisturePosition = 0, voltagePosition = 0,
        facilityTypePos = 0, addnlResistencePos=0, elktrodeTrvlAreaPos=0, disconnectMastPos=0;
        LetterEntity entity = new LetterEntity();
        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        GeneralInfoFragment generalInfoFragment = GeneralInfoFragment.getInstance();
        entity.adminID = adminID;
        String _altitude = spManager.getGeneralInfoValueByKeyName("altitude").equals("") ? "0.0" : spManager.getGeneralInfoValueByKeyName("altitude");
        entity.altitude = String.valueOf(Double.parseDouble(_altitude));
        entity.approvedBy = "";
        entity.assignmentID = anleggID;
        entity.baseDataVersion = "";
        entity.clampAmp = spManager.getCalculationValueByKeyName("clampAmp").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("clampAmp");
        entity.clampMeasurement = spManager.getCalculationValueByKeyName("clampMeasurement").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("clampMeasurement");
        entity.comments = spManager.getCalculationValueByKeyName("Comments");
        //added on 03-02-2021
        entity.estimatedTouchVoltage = spManager.getCalculationValueByKeyName("EstimatedTouchVoltage").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("EstimatedTouchVoltage");
        //end
        entity.compassDirection = "0";
        entity.deleted = "true";
        if (variablePageClicked) {
            disablementPosition = variablesFragment.selectedDisablementPosition();
            earthPosition = variablesFragment.selectedSoilPosition();
            electrodePosition = variablesFragment.selectedElektrodePosition();
            globalEarthPosition = variablesFragment.selectedGlobalEarthPosition();
            measureTakenPosition = variablesFragment.selectedMeasurementPosition();
            moisturePosition = variablesFragment.selectedHumidityPosition();
            voltagePosition = variablesFragment.selectedVoltagePosition();

            // new added 27-01-2021
            facilityTypePos = variablesFragment.selectedFacilityTypePosition();
            addnlResistencePos = variablesFragment.selectedAddnlResistencePosition();
            elktrodeTrvlAreaPos = variablesFragment.selectedTravelAreaPosition();
            disconnectMastPos = variablesFragment.selectedDisablementMastPosition();
        } else {
            disablementPosition = Double.parseDouble(spManager.getVariablerValueByKeyName("Disablement"));
            earthPosition = Double.parseDouble(spManager.getVariablerValueByKeyName("EarthType"));
            electrodePosition = Double.parseDouble(spManager.getVariablerValueByKeyName("ElekrodeSystem"));
            globalEarthPosition = Double.parseDouble(spManager.getVariablerValueByKeyName("GlobalEarthID"));
            measureTakenPosition = Double.parseDouble(spManager.getVariablerValueByKeyName("MeasureTaken"));
            moisturePosition = Double.parseDouble(spManager.getVariablerValueByKeyName("Moisture"));
            voltagePosition = Double.parseDouble(spManager.getVariablerValueByKeyName("VoltageID"));

            // new added 27-01-2021
            facilityTypePos = Double.parseDouble(spManager.getVariablerValueByKeyName("FacilityType"));
            addnlResistencePos = Double.parseDouble(spManager.getVariablerValueByKeyName("AdditionalResistence"));
            elktrodeTrvlAreaPos = Double.parseDouble(spManager.getVariablerValueByKeyName("TravelArea"));
            disconnectMastPos = Double.parseDouble(spManager.getVariablerValueByKeyName("DisablementMast"));
        }
        String undermappe_2 = spManager.getGeneralInfoValueByKeyName("Undermappe2");
        entity.folderId = undermappe_2.equals("") ? "-2" : undermappe_2;
        if (disablementPosition == 0) {
            entity.disconnectTime = "-2.0";
        } else {
            /*double disconnectSecond = XMLParser.getDisconnectSecond(disablementPosition + "", "disconnectTimes");
            entity.disconnectTime = disconnectSecond + "";*/
            entity.disconnectTime = disablementPosition + "";
        }
        if (earthPosition == 0) {
            entity.earthType = "-2";
        } else {
            String _earthType = earthPosition + "";
            if (_earthType.contains(".")) {
                _earthType = _earthType.substring(0, _earthType.lastIndexOf("."));
            }
            entity.earthType = _earthType;
        }
        if (electrodePosition == 0) {
            entity.electrode = "-2";
        } else {
            String _electrodePosition = electrodePosition + "";
            if (_electrodePosition.contains(".")) {
                _electrodePosition = _electrodePosition.substring(0, _electrodePosition.lastIndexOf("."));
            }
            entity.electrode = _electrodePosition;
        }
        if (globalEarthPosition == 0) {
            entity.globalEarth = "false";
        } else {
            entity.globalEarth = "true";
        }
        if (measureTakenPosition == 0) {
            entity.highVoltageActionTaken = "false";
        } else {
            entity.highVoltageActionTaken = "true";
        }
        if (moisturePosition == 0) {
            entity.moisture = "-2";
        } else {
            String _moisturePosition = moisturePosition + "";
            if (_moisturePosition.contains(".")) {
                _moisturePosition = _moisturePosition.substring(0, _moisturePosition.lastIndexOf("."));
            }
            entity.moisture = _moisturePosition;
        }
        if (voltagePosition == 0) {
            entity.voltage = "-2";
        } else {
            String _voltagePosition = voltagePosition + "";
            if (_voltagePosition.contains(".")) {
                _voltagePosition = _voltagePosition.substring(0, _voltagePosition.lastIndexOf("."));
            }
            entity.voltage = _voltagePosition;
        }
        entity.distance = spManager.getGraphValueByKeyName("Avstand_m_EC").equals("") ? "0" : spManager.getGraphValueByKeyName("Avstand_m_EC");
        entity.earthFaultCurrent = spManager.getVariablerValueByKeyName("Calculated_1_PoleCurrent").equals("0") ? "0.0" : spManager.getVariablerValueByKeyName("Calculated_1_PoleCurrent");
        //entity.electrodeType = "-2";
        entity.electrodeType = "";
        entity.fefTable = "-1";
        entity.images = "";
        entity.input = "";
        String _input = spManager.getGraphValueByKeyName("GlobalEarthIndex_6");
        _input += "," + spManager.getGraphValueByKeyName("GlobalEarthIndex_5");
        for (int i = 1; i < 10; i++) {
            _input += "," + spManager.getGraphValueByKeyName("GlobalEarthIndex" + i);
        }
        entity.input = "" + _input + "";

        //local graph
        entity.localElectrodeInput = "";
        String _localEkectrode = spManager.getGraphValueByKeyName("LocalEarthIndex_6");
        _localEkectrode += "," + spManager.getGraphValueByKeyName("LocalEarthIndex_5");
        for (int i = 1; i < 10; i++) {
            _localEkectrode += "," + spManager.getGraphValueByKeyName("LocalEarthIndex" + i);
        }
        entity.localElectrodeInput = "" + _localEkectrode + "";

        entity.latitude = spManager.getGeneralInfoValueByKeyName("UploadLatitude").equals("") ? "0.0" : spManager.getGeneralInfoValueByKeyName("UploadLatitude");
        entity.locationDescription = spManager.getGeneralInfoValueByKeyName("LocationIndication");
        entity.longitude = spManager.getGeneralInfoValueByKeyName("UploadLongitude").equals("") ? "0.0" : spManager.getGeneralInfoValueByKeyName("UploadLongitude");
        entity.measurePointID = measurementPointID;
        String measuredBy = spManager.getVariablerValueByKeyName("ControlPerformedBy");
        measuredBy = measuredBy.equals("0") ? spManager.getLoginInfoValueByKeyName("Username") : measuredBy;
        entity.measuredBy = measuredBy.equals("0") ? spManager.getLoginInfoValueByKeyName("Username") : measuredBy;
        String measurementDate = spManager.getVariablerValueByKeyName("MeasurementDate");
        String _date = measurementDate.replace(".", "-");
        String[] _dateArr = _date.split("-");
        entity.measurementDate = _dateArr[2] + "-" + _dateArr[1] + "-" + _dateArr[0];
        entity.noLocalElectrode = spManager.getCalculationValueByKeyName("noLocalElectrode").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("noLocalElectrode");
        // change publish 15-09-2020
        String trainor_admin_status = spManager.getLoginInfoValueByKeyName("trainorAdmin");
        if (trainor_admin_status.equals("true")) {
            entity.published = "false";
        } else {
            entity.published = "true";
        }
        //entity.published = "true";
        entity.refL = spManager.getCalculationValueByKeyName("RefrenceLocalElectrode").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("RefrenceLocalElectrode");
        entity.refT = spManager.getCalculationValueByKeyName("RefrenceThroughout").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("RefrenceThroughout");
        entity.registeredBy = "0";
        entity.registeredByName = "";
        long _letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
        String revisionCount = spManager.getGeneralInfoValueByKeyName("Revision");
        //String revisionCount = viewModel.getRevisionCount(Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID")), entity.measurePointID, _letterID);
        if (_letterID == 0) {
            entity.revision = "0";
        } else {
            entity.revision = revisionCount;
        }
        //entity.revision = "1";
        //entity.satisfy = "false";
        if(spManager.getCalculationValueByKeyName("Status").equals("Approved")){
            entity.satisfy = "true";
        }else if(spManager.getCalculationValueByKeyName("Status").equals("NotApproved")){
            entity.satisfy = "false";
        }else{
            entity.satisfy = "false";
        }
        entity.season = "0";
        entity.trainorApproved = "false";
        entity.transformerPerformance = spManager.getVariablerValueByKeyName("TransformerStyle").equals("0") ? "0.0" : spManager.getVariablerValueByKeyName("TransformerStyle");
        entity.updatedBy = "0";
        entity.updatedByName = spManager.getLoginInfoValueByKeyName("Username");
        entity.companyName = spManager.getGeneralInfoValueByKeyName("CompanyName");
        entity.Tag = "Saved";
        entity.isSelected = "false";
        entity.validID = spManager.getGeneralInfoValueByKeyName("ValidID").equals("") ? "True" : spManager.getGeneralInfoValueByKeyName("ValidID");
        entity.timestamp = System.currentTimeMillis();
        entity.directionForward = spManager.getGraphValueByKeyName("DirectionForward").equals("") ? "0" : spManager.getGraphValueByKeyName("DirectionForward");
        entity.directionBackward = spManager.getGraphValueByKeyName("DirectionBackward").equals("") ? "0" : spManager.getGraphValueByKeyName("DirectionBackward");
        entity.lokalElektrodeVal = spManager.getCalculationValueByKeyName("RefrenceLocalElectrode");
        entity.globalElektrodeVal = spManager.getCalculationValueByKeyName("RefrenceThroughout");
        String _basicInstallation = spManager.getCalculationValueByKeyName("BasicInstallation").equals("") ? "0.0" : spManager.getCalculationValueByKeyName("BasicInstallation");
        entity.basicInstallation = String.valueOf(Double.parseDouble(_basicInstallation));
        entity.trainorComments = spManager.getCalculationValueByKeyName("Comments");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (entity.letterID == 0) {
            try {
                Date date = new Date();
                entity.registered = sdf.format(date);
                entity.updated = sdf.format(date);
            } catch (Exception ex) {
                Log.d("Error", ex.getMessage());
            }
        } else {
            entity.registered = sdf.format(entity.registered);
            try {
                if (entity.updated == null || entity.updated.equals("")) {
                    Date date = new Date();
                    entity.updated = sdf.format(date);
                }
            } catch (Exception ex) {
                Log.d("Error", ex.getMessage());
            }
        }

        // new added 27-01-2021
        if (disconnectMastPos == 0) {
            entity.disconnectTimeMast = "-2.0";
        } else {
            entity.disconnectTimeMast = disconnectMastPos + "";
        }
        if (elktrodeTrvlAreaPos == 0) {
            entity.electrodeInTraveledArea = "false";
        } else {
            entity.electrodeInTraveledArea = "true";
        }
        if (addnlResistencePos == 0) {
            entity.additionalResistance = "-2.0";
        } else {
            entity.additionalResistance = addnlResistencePos + "";
        }
        if (facilityTypePos == 0) {
            entity.facilityType = "-2.0";
        } else {
            entity.facilityType = facilityTypePos + "";
        }

        return entity;
    }

    public void showWaitDialog(String loaderMsg) {
        if (dialog == null) {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setCancelable(false);
        }
        if (!dialog.isShowing()) {
            dialog.setMessage(loaderMsg);
            dialog.show();
        }
    }

    public void dismissWaitDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setTitle() {
        if (!spManager.getGeneralInfoValueByKeyName("MeasurementPointID").equals("")) {
            tv_title.setText(spManager.getGeneralInfoValueByKeyName("MeasurementPointID"));
        } else {
            tv_title.setText("Målebrev er ikke valgt");
        }
    }

    public void deleteLetter(String measurePointID) {
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
        spManager.removePref("GeneralInfoPref");
        spManager.removePref("VariablerPref");
        spManager.removePref("CalculationPref");
        spManager.removePref("MeasuredValuesPref");
        GeneralInfoFragment.getInstance().edt_measuring_point_ID.setText("");
        MainActivity.getInstance().setTitle();
        HomeFragment.getInstance().setActiveCertificate();
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void checkMPIDExists(final String newMPID, final String oldMPID, final LetterEntity entity) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showWaitDialog(getResources().getString(R.string.please_wait));
                response = null;
                isMPIDUnique = false;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                SoapObject request = new SoapObject("http://letter.services.ws.measurements.trainor.no/", "getLetters");
                request.addProperty("loginToken", spManager.getLoginInfoValueByKeyName("Token"));
                request.addProperty("measurePointId", newMPID);
                request.addProperty("companyId", spManager.getLoginInfoValueByKeyName("CompanyID"));
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(request);
                envelope.dotNet = false;
                try {
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URLs.URL);
                    androidHttpTransport.call("http://letter.services.ws.measurements.trainor.no/getLetters", envelope);
                    response = (SoapObject) envelope.bodyIn;
                } catch (Exception e) {
                    Log.d("Error", e.getMessage());
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
                        GeneralInfoFragment.getInstance().replaceMpID(oldMPID, newMPID);
                        dismissWaitDialog();
                        isUploadBtnClicked = true;
                        String _test = "testAndroid";
                        String seconds = XMLParser.getDisconnectSeconds(Double.valueOf(entity.disconnectTime), "disconnectTimes");
                        String secondsMast = XMLParser.getDisconnectMastSeconds(Double.parseDouble(entity.disconnectTimeMast), "newDisconnectTimes");
                        String facilityTye="", addnlResistence="";
                        if(entity.facilityType.equals("-2.0")){
                            facilityTye = "station";
                        }else{
                            facilityTye = "mast";
                        }
                        if(entity.additionalResistance.contains("-2.0")){
                            addnlResistence="ud1";
                        }else if(entity.additionalResistance.contains("1")){
                            addnlResistence="ud2";
                        }else if(entity.additionalResistance.contains("2")){
                            addnlResistence="ud3";
                        }else if(entity.additionalResistance.contains("3")){
                            addnlResistence="ud4";
                        }
                        String apiversion = "1";
                        String estimatedTouchVoltage;
                        String letterJSON;
                        if(entity.estimatedTouchVoltage.equals("0.0")){
                            letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampAmp + ",\"clampMeasurement\":" + entity.clampMeasurement + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + facilityTye + "\",\"additionalResistance\":\"" + addnlResistence + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + secondsMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":null}";
                        }else{
                            letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampAmp + ",\"clampMeasurement\":" + entity.clampMeasurement + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + facilityTye + "\",\"additionalResistance\":\"" + addnlResistence + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + secondsMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+entity.estimatedTouchVoltage+"\"}";
                        }

                       /* Log.d("disconnected sec", seconds + "");
                        String letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampMeasurement + ",\"clampMeasurement\":" + entity.clampAmp + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + entity.facilityType + "\",\"additionalResistance\":\"" + entity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + entity.disconnectTimeMast + "}";
                        */uploadLetter(spManager.getLoginInfoValueByKeyName("Token"), letterJSON, true, hasImage, entity);
                    } else {
                        GeneralInfoFragment.getInstance().edt_measuring_point_ID.setText(oldMPID);
                        GeneralInfoFragment.getInstance().edt_measuring_point_ID.setSelection(oldMPID.length());
                        showToast(getResources().getString(R.string.measurement_pt_id_already_exists));
                        dismissWaitDialog();
                    }
                }
            }
        }.execute();
    }

    public void reset() {
        spManager.removePref("GeneralInfoPref");
        spManager.removePref("VariablerPref");
        spManager.removePref("CalculationPref");
        spManager.removePref("MeasuredValuesPref");
        spManager.saveLoginInfoValueByKeyName("TabVisible", "false", loginEditor);
        GeneralInfoFragment.getInstance().edt_measuring_point_ID.setText("");
        MainActivity mainActivity = MainActivity.getInstance();
        TabLayout tabLayout = mainActivity.tabLayout;
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            if (i != 0) {
                tab.setBackgroundColor(getResources().getColor(R.color.tab_grey_color));
                tab.setClickable(false);
            }
        }
        MainActivity.getInstance().setTitle();
        HomeFragment.getInstance().setActiveCertificate();
        mainActivity.viewPager.setCurrentItem(0, true);
    }

    public void setSpinnerValues() {
        VariablesFragment variablesFragment = VariablesFragment.getInstance();
        if (variablesFragment != null && spManager != null && variablesFragment.editor != null && variablePageClicked) {
            spManager.saveVariablerValueByKeyName("VoltageID", variablesFragment.selectedVoltagePosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("GlobalEarthID", variablesFragment.selectedGlobalEarthPosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("Disablement", variablesFragment.selectedDisablementPosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("Moisture", variablesFragment.selectedHumidityPosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("EarthType", variablesFragment.selectedSoilPosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("MeasureTaken", variablesFragment.selectedMeasurementPosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("ElekrodeSystem", variablesFragment.selectedElektrodePosition() + "", variablesFragment.editor);
       // new added facilityType 28-12-2020
            spManager.saveVariablerValueByKeyName("FacilityType", variablesFragment.selectedFacilityTypePosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("AdditionalResistence", variablesFragment.selectedAddnlResistencePosition() + "", variablesFragment.editor);
            //spManager.saveVariablerValueByKeyName("TravelArea", variablesFragment.selectedTravelAreaPosition() + "", variablesFragment.editor);
            spManager.saveVariablerValueByKeyName("DisablementMast", variablesFragment.selectedDisablementMastPosition() + "", variablesFragment.editor);
            if(variablesFragment.selectedDisablementMastPosition()>2){
                spManager.saveVariablerValueByKeyName("TravelArea", "0", variablesFragment.editor);
            }else{
                spManager.saveVariablerValueByKeyName("TravelArea", variablesFragment.selectedTravelAreaPosition() + "", variablesFragment.editor);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void uploadLetter() {
        measurementPointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
        long letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
        uploadImagesList = viewModel.getImagesToUpload(adminID, letterID, measurementPointID);
        hasImage = uploadImagesList != null && uploadImagesList.size() != 0;
        String OldMPID = spManager.getGeneralInfoValueByKeyName("OldMPID");
        String measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
        LetterEntity entity = viewModel.getSavedLetter(adminID, letterID, measurePointID) == null ? null : viewModel.getSavedLetter(adminID, letterID, measurePointID).size() == 0 ? null : viewModel.getSavedLetter(adminID, letterID, measurePointID).get(0);
        if (entity != null && !isUploadBtnClicked) {
            if (connectionDetector.isConnectingToInternet()) {
                if (letterID == 0) {
                    checkMPIDExists(measurePointID, OldMPID, entity);
                } else {
                    isUploadBtnClicked = true;
                    String _test = "testAndroid";
                    String seconds = XMLParser.getDisconnectSeconds(Double.parseDouble(entity.disconnectTime), "disconnectTimes");
                    String secondsMast = XMLParser.getDisconnectMastSeconds(Double.parseDouble(entity.disconnectTimeMast), "newDisconnectTimes");
                    String facilityTye="", addnlResistence="";
                    if(entity.facilityType.equals("-2.0")){
                        facilityTye = "station";
                    }else{
                        facilityTye = "mast";
                    }
                    if(entity.additionalResistance.contains("-2.0")){
                        addnlResistence="ud1";
                    }else if(entity.additionalResistance.contains("1")){
                        addnlResistence="ud2";
                    }else if(entity.additionalResistance.contains("2")){
                        addnlResistence="ud3";
                    }else if(entity.additionalResistance.contains("3")){
                        addnlResistence="ud4";
                    }
                    String apiversion = "1";
                    //Log.d("disconnected sec", seconds+"");
                    //String letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampMeasurement + ",\"clampMeasurement\":" + entity.clampAmp + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + facilityTye + "\",\"additionalResistance\":\"" + addnlResistence + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + secondsMast+""+ ",\"apiVersion\":\""+apiversion+"\"}";
                    String letterJSON;
                    if(entity.estimatedTouchVoltage.equals("0.0")){
                        letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampAmp + ",\"clampMeasurement\":" + entity.clampMeasurement + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + facilityTye + "\",\"additionalResistance\":\"" + addnlResistence + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + secondsMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":null}";
                    }else{
                        letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampAmp + ",\"clampMeasurement\":" + entity.clampMeasurement + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + facilityTye + "\",\"additionalResistance\":\"" + addnlResistence + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + secondsMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+entity.estimatedTouchVoltage+"\"}";
                    }
                    //String letterJSON = "{\"altitude\":" + entity.altitude + ",\"approvedBy\":\"" + entity.approvedBy + "\",\"assignmentID\":" + entity.assignmentID + ",\"baseDataVersion\":\"" + entity.baseDataVersion + "\",\"clampAmp\":" + entity.clampAmp + ",\"clampMeasurement\":" + entity.clampMeasurement + ",\"comments\":\"" + entity.comments + "\",\"compassDirection\":" + entity.directionForward + ",\"compassDirectionBackwards\":" + entity.directionBackward + ",\"deleted\":" + entity.deleted + ",\"disconnectTime\":" + seconds + "" + ",\"distance\":" + entity.distance + ",\"earthFaultCurrent\":" + entity.earthFaultCurrent + ",\"earthType\":" + entity.earthType + ",\"electrode\":" + entity.electrode + ",\"electrodeType\":\"" + entity.electrodeType + "\",\"feftable\":" + entity.fefTable + ",\"folderId\":\"" + entity.folderId + "\",\"globalEarth\":" + entity.globalEarth + ",\"highVoltageActionTaken\":" + entity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + entity.input + "\",\"latitude\":" + entity.latitude + ",\"letterID\":" + entity.letterID + ",\"localElectrodeInput\":\"" + entity.localElectrodeInput + "\",\"locationDescription\":\"" + entity.locationDescription + "\",\"longitude\":" + entity.longitude + ",\"measurePointID\":\"" + entity.measurePointID + "\",\"measuredBy\":\"" + entity.measuredBy + "\",\"measuredReference\":\"" + entity.basicInstallation + "\",\"measurementDate\":\"" + entity.measurementDate + "\",\"moisture\":" + entity.moisture + ",\"noLocalElectrode\":" + entity.noLocalElectrode + ",\"published\":" + entity.published + ",\"refL\":" + entity.refL + ",\"refT\":" + entity.refT + ",\"registered\":\"" + entity.registered + "\",\"registeredBy\":" + entity.registeredBy + ",\"registeredByName\":\"" + entity.registeredByName + "\",\"revision\":" + entity.revision + ",\"satisfy\":" + entity.satisfy + ",\"season\":" + entity.season + ",\"trainorApproved\":" + entity.trainorApproved + ",\"trainorComments\":\"" + entity.trainorComments + "\",\"transformerPerformance\":" + entity.transformerPerformance + ",\"updatedBy\":" + entity.updatedBy + ",\"updatedByName\":\"" + entity.updatedByName + "\",\"voltage\":" + entity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + facilityTye + "\",\"additionalResistance\":\"" + addnlResistence + "\",\"electrodeInTraveledArea\":\"" + entity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + secondsMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+entity.estimatedTouchVoltage+"\"}";
                    uploadLetter(spManager.getLoginInfoValueByKeyName("Token"), letterJSON, true, hasImage, entity);
                }
            } else {
                AlertDialogManager.showDialog(MainActivity.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
            }
        } else if (entity == null) {
            showToast(getResources().getString(R.string.save_letter_first));
        }
    }

}