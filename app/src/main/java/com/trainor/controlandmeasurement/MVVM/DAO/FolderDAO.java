package com.trainor.controlandmeasurement.MVVM.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;

import java.util.List;

@Dao
public interface FolderDAO {

    @Insert
    void insert(FolderEntity entity);

    @Update
    void update(FolderEntity entity);

    @Delete
    void delete(FolderEntity entity);

    @Query("Delete From FolderTable")
    void deleteAllFolders();

    @Query("Select * From FolderTable where assignmentID=:assignmentid AND folderID=:folderid")
    List<FolderEntity> getParentID(String assignmentid, String folderid);

    @Query("Select * From FolderTable where assignmentID=:assignmentid and parentID=0 order by name")
    List<FolderEntity> getFolders(String assignmentid);

    @Query("Select * From FolderTable where assignmentID=:assignmentid AND folderID=:parentID")
    List<FolderEntity> getFolder(String assignmentid, String parentID);

    @Query("Select * From FolderTable where assignmentID=:assignmentid AND parentID =:folderid")
    List<FolderEntity> getFolderData(String assignmentid, String folderid);

    @Query("Select * From FolderTable where assignmentID=:assignmentid AND parentID=0 AND name like '%' || :folderName || '%'")
    List<FolderEntity> searchFolderName(String assignmentid, String folderName);

    @Query("Select * From FolderTable where assignmentID=:assignmentid AND parentID !=0 AND name like '%' || :folderName || '%'")
    List<FolderEntity> searchSubFolderName(String assignmentid, String folderName);

}
