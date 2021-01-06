package com.trainor.controlandmeasurement.fragments;

import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.trainor.controlandmeasurement.Activities.CameraActivity;
import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.AdapterClasses.ImagesGridViewAdapter;
import com.trainor.controlandmeasurement.BuildConfig;
import com.trainor.controlandmeasurement.HelperClass.ImageCaptureClass;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.ViewModel;
import com.trainor.controlandmeasurement.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static androidx.media.MediaBrowserServiceCompat.RESULT_OK;

public class PicturesFragment extends Fragment {
    SharedPreferenceClass spManager;

    @BindView(R.id.ll_take_picture)
    LinearLayout ll_take_picture;

    @BindView(R.id.images_gridview)
    RecyclerView images_gridview;

    @BindView(R.id.tv_no_images)
    TextView tv_no_images;

    private final int REQUEST_IMAGE = 100;
    private Uri fileUri;
    long adminID;
    String measurePointID;
    List<File> listFile;
    ImagesGridViewAdapter imagesGridViewAdapter;
    ViewModel viewModel;
    public static final int MEDIA_TYPE_IMAGE = 1;

    public PicturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pictures, container, false);
        ButterKnife.bind(PicturesFragment.this, view);
        spManager = new SharedPreferenceClass(getActivity());
        getControls();
        return view;
    }

    public void getControls() {
        adminID = Long.parseLong(spManager.getLoginInfoValueByKeyName("AdminID"));
        measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
        imagesGridViewAdapter = new ImagesGridViewAdapter(getActivity());
        images_gridview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        images_gridview.setAdapter(imagesGridViewAdapter);
        viewModel = ViewModelProviders.of(MainActivity.getInstance()).get(ViewModel.class);
        final File file = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + measurePointID);
        if (file != null && file.listFiles() != null) {
            listFile = Arrays.asList(file.listFiles());
            String measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
            long letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
            for (int i = 0; i < listFile.size(); i++) {
                ImageEntity imageInfo = new ImageEntity();
                imageInfo.adminID = adminID;
                imageInfo.letterID = letterID;
                imageInfo.measurePointID = measurePointID;
                imageInfo.fileName = listFile.get(i).getName();
                imageInfo.filePath = listFile.get(i).getAbsolutePath();
                imageInfo.description = measurePointID;
                imageInfo.Tag = "saved";
                List<ImageEntity> _list = viewModel.imageExists(adminID, imageInfo.fileName);
                if (_list == null || _list.size() == 0) {
                    viewModel.insertImageData(imageInfo);
                }
            }
        }
        viewModel.getAllImages(adminID, measurePointID).observe(getActivity(), new Observer<List<ImageEntity>>() {
            @Override
            public void onChanged(@Nullable List<ImageEntity> imageEntities) {
                String mpid = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
                List<ImageEntity> imageExistsList = new ArrayList<>();
                if (!mpid.equals("")) {
                    if (!mpid.equals(measurePointID) || imageEntities.size() == 0) {
                        imageEntities = viewModel.imageMPIDExists(adminID, mpid);
                    }
                    if (imageEntities.size() > 0) {
                        tv_no_images.setVisibility(View.GONE);
                    } else {
                        tv_no_images.setVisibility(View.VISIBLE);
                    }
                    imagesGridViewAdapter.notifyDataSetChanged();
                    //imagesGridViewAdapter.setImagesList(imageEntities);
                    if (imageExistsList.size() > 0) {
                        imageExistsList.clear();
                    }
                    for (int i = 0; i < imageEntities.size(); i++) {
                        File file = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + measurePointID + "/" + imageEntities.get(i).fileName);
                        if (file.exists()) {
                            imageExistsList.add(imageEntities.get(i));
                        } else {
                            System.out.println("image id =" + imageEntities.get(i).imageID);
                            viewModel.deleteImage(imageEntities.get(i));
                        }
                    }
                    imagesGridViewAdapter.setImagesList(imageExistsList);
                }
            }
        });

        /*imagesGridViewAdapter = new ImagesGridViewAdapter(getActivity());
        images_gridview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        images_gridview.setAdapter(imagesGridViewAdapter);*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        images_gridview.setNestedScrollingEnabled(false);
    }

    @OnClick({R.id.ll_take_picture})
    public void takePicture() {
        if (!measurePointID.equals("")) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1005);
            } else {
                openCamera();
            }


        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.enter_measurement_pt_id_first), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1005) {
            if(grantResults[0] == -1){
                openSettings();
            }else{
                openCamera();
            }

        }else if (requestCode == 1006) {
            if(grantResults[0] == -1){
                openSettings();
            }else{
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                File _file = new ImageCaptureClass().getOutputMediaFileUri(MEDIA_TYPE_IMAGE, adminID, measurePointID);
                intent.putExtra("FilePath", _file.getAbsolutePath());
                startActivityForResult(intent, 786);
            }
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 786) {
            final File file = new File(android.os.Environment.getExternalStorageDirectory(), "Measurement/" + adminID + "/." + measurePointID);
            listFile = Arrays.asList(file.listFiles());
            String measurePointID = spManager.getGeneralInfoValueByKeyName("MeasurementPointID");
            long letterID = spManager.getGeneralInfoValueByKeyName("letterID").equals("") ? 0 : Long.parseLong(spManager.getGeneralInfoValueByKeyName("letterID"));
            for (int i = 0; i < listFile.size(); i++) {
                ImageEntity imageInfo = new ImageEntity();
                imageInfo.adminID = adminID;
                imageInfo.letterID = letterID;
                imageInfo.measurePointID = measurePointID;
                imageInfo.fileName = listFile.get(i).getName();
                imageInfo.filePath = listFile.get(i).getAbsolutePath();
                imageInfo.description = measurePointID;
                imageInfo.Tag = "saved";
                List<ImageEntity> _list = viewModel.imageExists(adminID, imageInfo.fileName);
                if (_list == null || _list.size() == 0) {
                    viewModel.insertImageData(imageInfo);
                } else {
                    ImageEntity imageEntity = _list.get(0);
                    if (imageEntity.Tag.equals("saved")) {
                        imageInfo.imageID = imageEntity.imageID;
                        viewModel.updatetImage(imageInfo);
                    }
                }
            }
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void openCamera() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1006);
        } else {
            Intent intent = new Intent(getActivity(), CameraActivity.class);
            File _file = new ImageCaptureClass().getOutputMediaFileUri(MEDIA_TYPE_IMAGE, adminID, measurePointID);
            intent.putExtra("FilePath", _file.getAbsolutePath());
            startActivityForResult(intent, 786);
        }

    }
}
