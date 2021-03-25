package com.trainor.controlandmeasurement.Activities;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.trainor.controlandmeasurement.AdapterClasses.UploadLettersAdapter;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UploadLetters extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.image_back)
    ImageView image_back;
    @BindView(R.id.ll_upload)
    LinearLayout ll_upload;
    ViewModel viewModel;
    UploadLettersAdapter adapter;
    SharedPreferenceClass spManager;
    ConnectionDetector connectionDetector;
    long adminID, _letterIDFromServer;
    SoapObject imageResponse, response;
    ProgressDialog dialog;
    boolean isMPIDUnique, isUploadBtnClicked;
    String faultString, loginToken;
    List<ImageEntity> uploadImagesList;
    List<LetterEntity> letterlist;
    HashMap<LetterEntity, String> errorMap;
    int notUploadedImageCount = 0;
    NetworkChangeReceiver networkChangeReceiver;
    long imageID=0;
    String isNewLetter = "true";
    Long letteridToCheckNewLetter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_letters);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        ButterKnife.bind(this);
        connectionDetector = new ConnectionDetector(UploadLetters.this);
        viewModel = ViewModelProviders.of(UploadLetters.this).get(ViewModel.class);
        spManager = new SharedPreferenceClass(UploadLetters.this);
        letterlist = new ArrayList<>();
        errorMap = new HashMap<>();
        adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        loginToken = spManager.getLoginInfoValueByKeyName("Token");
        List<LetterEntity> list = viewModel.getSavedLetter(adminID);
        adapter = new UploadLettersAdapter(UploadLetters.this);
        recycler_view.setLayoutManager(new LinearLayoutManager(UploadLetters.this));
        recycler_view.setAdapter(adapter);
        adapter.setLetterList(list);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadLetters.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ll_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letterlist = adapter.getUploadLetterList();
                if (letterlist.size() == 0) {
                    showToast(getResources().getString(R.string.select_letter_first));
                } else {
                    if (connectionDetector.isConnectingToInternet()) {
                        showWaitDialog(getResources().getString(R.string.uploading_letters));
                        uploadLetter(letterlist.get(0));
                    } else {
                        AlertDialogManager.showDialog(UploadLetters.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, null);
                    }
                }
            }
        });

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //jobSchedulerTask();
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

    public void showWaitDialog(String loaderMsg) {
        if (dialog == null) {
            dialog = new ProgressDialog(UploadLetters.this);
            dialog.setCancelable(false);
        }
        dialog.setMessage(loaderMsg);

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismissWaitDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(UploadLetters.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void uploadLetter(LetterEntity _entityEntity) {
        boolean hasImage;
        uploadImagesList = viewModel.getImagesToUpload(adminID, _entityEntity.letterID, _entityEntity.measurePointID);
        hasImage = uploadImagesList != null && uploadImagesList.size() != 0;
        if (_entityEntity.letterID == 0) {
            checkMPIDExists(_entityEntity.measurePointID, _entityEntity, hasImage);
        } else {
            isUploadBtnClicked = true;
            String _test = "testAndroid";
            String apiversion = "1";
            //String letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"fefTable\":" + _entityEntity.fefTable + ",\"folderId\":" + _entityEntity.folderId + ",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" + _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast + "}";
            String letterJSON;
            if(_entityEntity.estimatedTouchVoltage.equals("0.0")){
                letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + "" + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"feftable\":" + _entityEntity.fefTable + ",\"folderId\":\"" + _entityEntity.folderId + "\",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registered\":\"" + _entityEntity.registered + "\",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" +  _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":null}";
            }else{
                letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + "" + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"feftable\":" + _entityEntity.fefTable + ",\"folderId\":\"" + _entityEntity.folderId + "\",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registered\":\"" + _entityEntity.registered + "\",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" +  _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+_entityEntity.estimatedTouchVoltage+"\"}";
            }
            //String letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + "" + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"feftable\":" + _entityEntity.fefTable + ",\"folderId\":\"" + _entityEntity.folderId + "\",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registered\":\"" + _entityEntity.registered + "\",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" +  _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+_entityEntity.estimatedTouchVoltage+"\"}";
            uploadLetter(loginToken, letterJSON, true, hasImage, _entityEntity);
        }
    }

    public void uploadLetter(final String loginToken, final String letterJSON, final boolean forceSave, final boolean hasImages, final LetterEntity _entity) {
        letteridToCheckNewLetter = _entity.letterID;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                faultString = "";
                _letterIDFromServer = 0;
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
                                    viewModel.updateLetter(_entity);
                                }
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
                if (!faultString.equals("")) {
                    errorMap.put(_entity, getResources().getString(R.string.failed_to_upload_letter));
                    setDownloadQueue(_entity);
                } else {
                    if (hasImages && _letterIDFromServer != 0) {
                        if (uploadImagesList.size() > 0) {
                            //uploadImage(_letterIDFromServer, uploadImagesList.get(0), _entity,);
                            if (uploadImagesList.size() == 1) {
                                uploadImage(_letterIDFromServer, uploadImagesList.get(0), _entity, "true");
                            } else {
                                uploadImage(_letterIDFromServer, uploadImagesList.get(0), _entity, "false");
                            }
                        }

                    } else if (_letterIDFromServer != 0) {
                        errorMap.put(_entity, getResources().getString(R.string.letter_uploaded_sucessfully));
                        setDownloadQueue(_entity);
                    }
                }
            }
        }.execute();
    }

    public void uploadImage(final long letterID, final ImageEntity imageEntity, final LetterEntity _entity, String isLast) {
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
                    request.addProperty("loginToken", loginToken);
                    request.addProperty("letterId", letterID);
                    request.addProperty("letterImageGson", letterImageGson);
                    request.addProperty("isLast", isLast);
                    if (letteridToCheckNewLetter == 0) {
                        isNewLetter = "true";
                    } else {
                        isNewLetter = "false";
                    }
                    request.addProperty("isNewLetter", isNewLetter);
                    request.addProperty("imageIds", imageID+"");
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
                    } catch (Exception ex) {
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
                            uploadImage(letterID, uploadImagesList.get(0), _entity,"true");
                        } else {
                            uploadImage(letterID, uploadImagesList.get(0), _entity,"false");
                        }
                    } else {
                        if (notUploadedImageCount > 1) {
                            errorMap.put(_entity, notUploadedImageCount + "  " + getResources().getString(R.string.image_could_not_uploaded));
                        } else if (notUploadedImageCount == 1) {
                            errorMap.put(_entity, notUploadedImageCount + "  " + getResources().getString(R.string.image_could_not_uploaded));
                        } else if (notUploadedImageCount == 0) {
                            errorMap.put(_entity, "  " + getResources().getString(R.string.letter_uploaded_sucessfully));
                        }
                        notUploadedImageCount = 0;
                        setDownloadQueue(_entity);
                    }
                }
            }.execute();
        } else {
            imagefile.delete();
            viewModel.deleteImage(imageEntity);
            uploadImagesList.remove(uploadImagesList.get(0));
            if (uploadImagesList.size() > 0) {
                if (uploadImagesList.size() > 0) {
                    if (uploadImagesList.size() == 1) {
                        uploadImage(letterID, uploadImagesList.get(0), _entity,"true");
                    } else {
                        uploadImage(letterID, uploadImagesList.get(0), _entity,"false");
                    }
                }
            } else {
                setDownloadQueue(_entity);
            }
        }
    }

    public void checkMPIDExists(final String newMPID, final LetterEntity _entityEntity, final boolean hasImage) {
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
                request.addProperty("loginToken", loginToken);
                request.addProperty("measurePointId", newMPID);
                request.addProperty("companyId", spManager.getLoginInfoValueByKeyName("CompanyID"));
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.setOutputSoapObject(request);
                envelope.dotNet = false;
                try {
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URLs.URL);
                    androidHttpTransport.call("http://letter.services.ws.measurements.trainor.no/getLetters", envelope);
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
                        isUploadBtnClicked = true;
                        String _test = "testAndroid";
                        String apiversion = "1";
                        //String letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.compassDirection + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"fefTable\":" + _entityEntity.fefTable + ",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + "}";
                        //String letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + "" + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"feftable\":" + _entityEntity.fefTable + ",\"folderId\":\"" + _entityEntity.folderId + "\",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registered\":\"" + _entityEntity.registered + "\",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" +  _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+_entityEntity.estimatedTouchVoltage+"\"}";
                        String letterJSON;
                        if(_entityEntity.estimatedTouchVoltage.equals("0.0")){
                            letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + "" + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"feftable\":" + _entityEntity.fefTable + ",\"folderId\":\"" + _entityEntity.folderId + "\",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registered\":\"" + _entityEntity.registered + "\",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" +  _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":null}";
                        }else{
                            letterJSON = "{\"altitude\":" + _entityEntity.altitude + ",\"approvedBy\":\"" + _entityEntity.approvedBy + "\",\"assignmentID\":" + _entityEntity.assignmentID + ",\"baseDataVersion\":\"" + _entityEntity.baseDataVersion + "\",\"clampAmp\":" + _entityEntity.clampAmp + ",\"clampMeasurement\":" + _entityEntity.clampMeasurement + ",\"comments\":\"" + _entityEntity.comments + "\",\"compassDirection\":" + _entityEntity.directionForward + ",\"compassDirectionBackwards\":" + _entityEntity.directionBackward + ",\"deleted\":" + _entityEntity.deleted + ",\"disconnectTime\":" + _entityEntity.disconnectTime + "" + ",\"distance\":" + _entityEntity.distance + ",\"earthFaultCurrent\":" + _entityEntity.earthFaultCurrent + ",\"earthType\":" + _entityEntity.earthType + ",\"electrode\":" + _entityEntity.electrode + ",\"electrodeType\":\"" + _entityEntity.electrodeType + "\",\"feftable\":" + _entityEntity.fefTable + ",\"folderId\":\"" + _entityEntity.folderId + "\",\"globalEarth\":" + _entityEntity.globalEarth + ",\"highVoltageActionTaken\":" + _entityEntity.highVoltageActionTaken + ",\"images\":[],\"input\":\"" + _entityEntity.input + "\",\"latitude\":" + _entityEntity.latitude + ",\"letterID\":" + _entityEntity.letterID + ",\"localElectrodeInput\":\"" + _entityEntity.localElectrodeInput + "\",\"locationDescription\":\"" + _entityEntity.locationDescription + "\",\"longitude\":" + _entityEntity.longitude + ",\"measurePointID\":\"" + _entityEntity.measurePointID + "\",\"measuredBy\":\"" + _entityEntity.measuredBy + "\",\"measuredReference\":\"" + _entityEntity.basicInstallation + "\",\"measurementDate\":\"" + _entityEntity.measurementDate + "\",\"moisture\":" + _entityEntity.moisture + ",\"noLocalElectrode\":" + _entityEntity.noLocalElectrode + ",\"published\":" + _entityEntity.published + ",\"refL\":" + _entityEntity.refL + ",\"refT\":" + _entityEntity.refT + ",\"registered\":\"" + _entityEntity.registered + "\",\"registeredBy\":" + _entityEntity.registeredBy + ",\"registeredByName\":\"" + _entityEntity.registeredByName + "\",\"revision\":" + _entityEntity.revision + ",\"satisfy\":" + _entityEntity.satisfy + ",\"season\":" + _entityEntity.season + ",\"trainorApproved\":" + _entityEntity.trainorApproved + ",\"trainorComments\":\"" + _entityEntity.trainorComments + "\",\"transformerPerformance\":" + _entityEntity.transformerPerformance + ",\"updatedBy\":" + _entityEntity.updatedBy + ",\"updatedByName\":\"" + _entityEntity.updatedByName + "\",\"voltage\":" + _entityEntity.voltage + ",\"test\":\"" + _test + "\",\"facilityType\":\"" +  _entityEntity.facilityType + "\",\"additionalResistance\":\"" + _entityEntity.additionalResistance + "\",\"electrodeInTraveledArea\":\"" + _entityEntity.electrodeInTraveledArea + "\",\"disconnectTimeMast\":" + _entityEntity.disconnectTimeMast+""+ ",\"apiVersion\":\""+apiversion+"\",\"estimatedTouchVoltage\":\""+_entityEntity.estimatedTouchVoltage+"\"}";
                        }
                        uploadLetter(loginToken, letterJSON, true, hasImage, _entityEntity);
                    } else {
                        errorMap.put(_entityEntity, "  " + getResources().getString(R.string.measurement_pt_id_already_exists));
                        setDownloadQueue(_entityEntity);
                    }
                }
            }
        }.execute();
    }

    public void setDownloadQueue(LetterEntity _entity) {
        letterlist.remove(_entity);
        if (letterlist.size() > 0) {
            uploadLetter(letterlist.get(0));
        } else {
            dismissWaitDialog();
            if (errorMap.size() != 0) {
                String errorString = "";
                for (HashMap.Entry<LetterEntity, String> pair : errorMap.entrySet()) {
                    errorString += pair.getKey().measurePointID + " : " + pair.getValue() + "\n";
                }
                AlertDialogManager.showDialog(UploadLetters.this, getResources().getString(R.string.ok), "", getResources().getString(R.string.upload_result), errorString, false, new IClickListener() {
                    @Override
                    public void onClick() {
                        spManager.removePref("GeneralInfoPref");
                        spManager.removePref("VariablerPref");
                        spManager.removePref("CalculationPref");
                        spManager.removePref("MeasuredValuesPref");
                        Intent intent = new Intent(UploadLetters.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                showToast(getResources().getString(R.string.letter_uploaded_sucessfully));
                spManager.removePref("GeneralInfoPref");
                spManager.removePref("VariablerPref");
                spManager.removePref("CalculationPref");
                spManager.removePref("MeasuredValuesPref");
                Intent intent = new Intent(UploadLetters.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
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
                dismissWaitDialog();
                AlertDialogManager.showDialog(UploadLetters.this, getResources().getString(R.string.ok), "", getString(R.string.internetErrorTitle), getString(R.string.internetErrorMessage), false, new IClickListener() {
                    @Override
                    public void onClick() {
                        List<LetterEntity> list = viewModel.getSavedLetter(adminID);
                        adapter.setLetterList(list);
                    }
                });
            }
        }
    }
}
