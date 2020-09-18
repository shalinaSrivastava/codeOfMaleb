package com.trainor.controlandmeasurement.HelperClass;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageCaptureClass {
    String IMAGE_DIRECTORY_NAME;
    public static final int MEDIA_TYPE_IMAGE = 1;

    public File getOutputMediaFileUri(int type, long adminID, String measurementPointID) {
        IMAGE_DIRECTORY_NAME = adminID + "/." + measurementPointID;
        return getOutputMediaFile(type);
    }

    private File getOutputMediaFile(int type) {
        String filePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Measurement/" + IMAGE_DIRECTORY_NAME;
        File root = new File(filePath);
        if (root.exists() == false) {
            root.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(root.getPath() + File.separator + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }
}
