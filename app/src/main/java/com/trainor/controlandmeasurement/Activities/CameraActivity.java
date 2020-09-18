package com.trainor.controlandmeasurement.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.graphics.Matrix;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.trainor.controlandmeasurement.R;

import java.io.File;
import java.util.concurrent.Executor;

import static androidx.camera.core.AspectRatio.RATIO_16_9;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    File file;
    String filePath = "";
    Preview preview;

    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        filePath = getIntent().getExtras().get("FilePath").toString();
        textureView = findViewById(R.id.view_finder);
        textureView.setSurfaceTextureListener(this);

        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTransform();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateTransform();
            }
        });


    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        return  true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,int arg2) {
        Log.d("suraface Texture", "suraface Texture changed");
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        Log.d("suraface Texture", "suraface Texture updated");
    }



    private void startCamera() {
        CameraX.unbindAll();

        PreviewConfig pConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(RATIO_16_9)
                .build();
        preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(@NonNull Preview.PreviewOutput output) {
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView, 0);
                SurfaceTexture st = output.getSurfaceTexture();

                textureView.setSurfaceTexture(st);
                updateTransform();
            }
        });


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        findViewById(R.id.imgCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file = new File(filePath);
                imgCap.takePicture(file, new Executor() {
                    @Override
                    public void execute(Runnable runnable) {
                        setResult(0);
                        finish();
                    }
                }, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        Log.d("cam orientation","problem");
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = getResources().getString(R.string.picture_capture_failed)+" " + message;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        if (cause != null) {
                            cause.printStackTrace();
                        }
                    }
                });

            }
        });

        CameraX.bindToLifecycle(this, preview, imgCap);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }
        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, getResources().getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        //handle orientation change
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
              if(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT==orientation){
                  Log.d("Potrait","Potrait");
              }else if(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE==orientation){
                  Log.d("Potrait","Potrait");
                }
            }
        };
        orientationEventListener.enable();
    }*/
}
