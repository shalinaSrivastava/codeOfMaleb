package com.trainor.controlandmeasurement.AdapterClasses;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trainor.controlandmeasurement.Activities.MainActivity;
import com.trainor.controlandmeasurement.HelperClass.AlertDialogManager;
import com.trainor.controlandmeasurement.HelperClass.ConnectionDetector;
import com.trainor.controlandmeasurement.HelperClass.IClickListener;
import com.trainor.controlandmeasurement.HelperClass.SharedPreferenceClass;
import com.trainor.controlandmeasurement.HelperClass.URLs;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
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
import java.util.List;

public class ImagesGridViewAdapter extends RecyclerView.Adapter<ImagesGridViewAdapter.MyViewHolder> {
    private List<ImageEntity> imagesList = new ArrayList<>();
    SharedPreferenceClass spManager;
    Context context;
    String faultString = "";
    SoapObject imageResponse;
    ViewModel viewModel;
    ProgressDialog dialog;
    AlertDialog _dialog = null;
    ConnectionDetector connectionDetector;
    String isNewLetter = "true";
    public ImagesGridViewAdapter(Context con) {
        this.context = con;
        spManager = new SharedPreferenceClass(con);
        viewModel = MainActivity.getInstance().viewModel;
        connectionDetector = new ConnectionDetector(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gridview_pictures, parent, false);
        return new MyViewHolder(itemView);
    }

    public void setImagesList(List<ImageEntity> _list) {
        this.imagesList = _list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ImageEntity imageInfo = imagesList.get(position);
        if (imageInfo.Tag.equals("saved")) {
            holder.ll_upload.setVisibility(View.VISIBLE);
        } else {
            holder.ll_upload.setVisibility(View.GONE);
        }
        holder.ll_upload.setTag(imageInfo);
        if (imageInfo.fileName != null && !imageInfo.fileName.equals("") && imageInfo.fileName.contains("_")) {
            holder.tvTitle.setText(imageInfo.fileName.substring(0, imageInfo.fileName.indexOf("_")));
        }

        Glide.with(context)
                .load(new File(imageInfo.filePath))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .centerCrop()
                .override(500, 250)
                .into(holder.gridBackground);
        holder.ll_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectingToInternet()) {
                    ImageEntity _entity = (ImageEntity) v.getTag();
                    if (_entity.letterID != 0) {
                        showWaitDialog();
                        uploadImage(_entity.letterID, _entity, "true");
                    } else {
                        showToast(context.getResources().getString(R.string.upload_letter_first));
                    }
                } else {
                    AlertDialogManager.showDialog(context, context.getResources().getString(R.string.ok), "", context.getString(R.string.internetErrorTitle), context.getString(R.string.internetErrorMessage), false, null);
                }
            }
        });

        holder.img_delete.setTag(imageInfo);
        holder.img_enlarge.setTag(imageInfo);
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageEntity _imageEntity = (ImageEntity) v.getTag();
                AlertDialogManager.showDialog(context, context.getResources().getString(R.string.delete), context.getResources().getString(R.string.cancel), context.getResources().getString(R.string.delete_image), context.getResources().getString(R.string.want_to_delete_image), true, new IClickListener() {
                    @Override
                    public void onClick() {
                        File imagefile = new File(_imageEntity.filePath);
                        if (imagefile.exists()) {
                            imagefile.delete();
                            notifyDataSetChanged();
                        }
                        if (!imagefile.exists()) {
                            viewModel.deleteImage(_imageEntity);
                            showToast(context.getResources().getString(R.string.image_deleted_sucessfully));
                        }
                    }
                });
            }
        });

        holder.img_enlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageEntity _imageInfo = (ImageEntity) v.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.full_image, null, false);
                ImageView img = view.findViewById(R.id.img);
                TextView img_name = view.findViewById(R.id.img_name);
                ImageView btn_dismiss = view.findViewById(R.id.btn_dismiss);
                ImageView rotate = view.findViewById(R.id.rotate);
                img_name.setText(_imageInfo.fileName);

                Glide.with(context)
                        .load(new File(_imageInfo.filePath))
                        .dontTransform()
                        .centerCrop()
                        .into(img);
                btn_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (_dialog != null && _dialog.isShowing()) {
                            _dialog.dismiss();
                        }
                    }
                });

                rotate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img.setRotation(img.getRotation() + 90);
                    }
                });
                builder.setView(view);
                _dialog = builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView gridBackground, img_delete, img_enlarge;
        LinearLayout ll_upload;

        public MyViewHolder(RelativeLayout view) {
            super(view);
            tvTitle = view.findViewById(R.id.title);
            gridBackground = view.findViewById(R.id.backgroudImage);
            img_delete = view.findViewById(R.id.img_delete);
            img_enlarge = view.findViewById(R.id.img_enlarge);
            ll_upload = view.findViewById(R.id.ll_upload);
        }
    }

    public void uploadImage(final long letterID, final ImageEntity imageEntity, final String isLast) {
        imageResponse = null;
        faultString = "";
        File imagefile = new File(imageEntity.filePath);
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
            protected Void doInBackground(Void... voids) {
                SoapObject request = new SoapObject(URLs.NAMESPACE, URLs.METHOD_NAME_UPLOAD_IMAGE);
                request.addProperty("loginToken", spManager.getLoginInfoValueByKeyName("Token"));
                request.addProperty("letterId", letterID);
                if(letterID == 0){
                    isNewLetter = "true";
                }else{
                    isNewLetter = "false";
                }
                request.addProperty("letterImageGson", letterImageGson);
                request.addProperty("isLast", isLast);
                request.addProperty("isNewLetter", isNewLetter);
                request.addProperty("imageIds", "0");
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
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dismissWaitDialog();
                if (!faultString.equals("")) {
                    showToast(faultString);
                } else {
                    if (imageResponse != null) {
                        for (int i = 0; i < imageResponse.getPropertyCount(); i++) {
                            SoapObject soapObj = (SoapObject) imageResponse.getProperty(i);
                            for (int j = 0; j < soapObj.getPropertyCount(); j++) {
                                PropertyInfo info = new PropertyInfo();
                                soapObj.getPropertyInfo(j, info);
                                if (info.getName().equals("imageId")) {
                                    imageEntity.Tag = "uploaded";
                                    viewModel.updatetImage(imageEntity);
                                }
                            }
                        }
                        showToast(context.getResources().getString(R.string.image_uploaded_successfully));
                    }
                }
            }
        }.execute();
    }

    public void showWaitDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage(context.getResources().getString(R.string.please_wait));
        }
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
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}