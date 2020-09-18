package com.trainor.controlandmeasurement.MVVM.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;

import java.util.List;

@Dao
public interface LetterDAO {

    @Query("Select * From LetterTable where adminID=:adminID AND Tag<>'uploaded' Order By timestamp desc")
    LiveData<List<LetterEntity>> getDownloadedLetter(long adminID);

    @Query("Select * From LetterTable where adminID=:adminID AND letterID=:letterID AND measurePointID=:mpid AND Tag = 'Saved'")
    List<LetterEntity> getSavedLetter(long adminID, long letterID, String mpid);

    @Insert
    void insert(LetterEntity entity);

    @Update
    void update(LetterEntity entity);

    @Delete
    void delete(LetterEntity entity);

    @Query("Delete From LetterTable where adminID=:adminID AND letterID=:letterID AND measurePointID=:measurePointID")
    void deleteBasedOnMeasurePointID(long adminID, long letterID, String measurePointID);

    @Query("Select * From LetterTable where adminID=:adminID AND letterID=:letterID AND measurePointID=:measurePointID")
    List<LetterEntity> letterLocalExists(long adminID, long letterID, String measurePointID);

    @Query("Select LT.* From LetterTable LT where LT.adminID =:adminID AND LT.Tag='uploaded'")
    LiveData<List<LetterEntity>> getUploadedLetters(long adminID);

    @Query("Select * From LetterTable where adminID=:adminID AND measurePointID = :measurePointID")
    List<LetterEntity> measurePointIDExists(long adminID, String measurePointID);

    @Query("Select * From LetterTable where adminID=:adminID AND Tag='Saved' Order By timestamp desc")
    List<LetterEntity> getSavedLetter(long adminID);

    @Query("Select Tag From LetterTable where adminID=:adminID AND measurePointID = :measurePointID AND letterID = :letterId")
    String alreadyDownloaded(long adminID, String measurePointID,long letterId);
}
