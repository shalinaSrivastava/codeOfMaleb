package com.trainor.controlandmeasurement.MVVM.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;

import java.util.List;

@Dao
public interface AssignmentDAO {

    @Insert
    void insert(AssignmentEntity entity);

    @Update
    void update(AssignmentEntity entity);

    @Delete
    void delete(AssignmentEntity entity);

    @Query("Select * From AssignmentTable")
    LiveData<List<AssignmentEntity>> getAllAssignment();

    @Query("Select * From AssignmentTable where assignmentID =:assignmentID")
    AssignmentEntity getAssignment(long assignmentID);

    @Query("Delete From AssignmentTable")
    void deleteAll();

    @Query("Select * From AssignmentTable where assignmentName like '%' || :name || '%' order by assignmentName")
    List<AssignmentEntity> getAssignmentByName(String name);

    @Query("Select * From AssignmentTable order by assignmentName")
    LiveData<List<AssignmentEntity>> getAssignment();
}
