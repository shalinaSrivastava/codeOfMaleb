package com.trainor.controlandmeasurement.MVVM.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;

import java.util.List;

@Dao
public interface ImageDAO {

    @Insert
    void insertImageData(ImageEntity entity);

    @Query("Select * From ImageTable where adminID=:adminID AND letterID=:letterID AND measurePointID=:mpID AND Tag = 'saved'")
    List<ImageEntity> getImagesToUpload(long adminID, long letterID, String mpID);

    @Query("Select * From ImageTable where adminID=:adminID AND measurePointID=:measurePointID")
    LiveData<List<ImageEntity>> getImages(long adminID, String measurePointID);

    @Query("Select * From ImageTable where adminID=:adminID AND measurePointID=:measurePointID")
    List<ImageEntity> imageMPIDExists(long adminID, String measurePointID);

    @Query("Select * From ImageTable where adminID=:adminID AND fileName=:fileName")
    List<ImageEntity> imageExists(long adminID, String fileName);

    @Update
    void updateImageData(ImageEntity entity);

    @Delete
    void deleteImageEntity(ImageEntity entity);

    @Query("Delete From ImageTable where adminID =:adminID AND Tag = 'uploaded'")
    void deleteAllImages(long adminID);

    @Query("Delete From ImageTable where adminID =:adminID AND measurePointID=:mpid")
    void deleteImagesMPID(long adminID, String mpid);
}
