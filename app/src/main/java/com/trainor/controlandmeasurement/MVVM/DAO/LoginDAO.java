package com.trainor.controlandmeasurement.MVVM.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.trainor.controlandmeasurement.MVVM.Entities.LoginEntity;

@Dao
public interface LoginDAO {

    @Query("Select * From LoginTable")
    LoginEntity getLoginDetails();

    @Insert
    void insert(LoginEntity entity);

    @Query("Delete From LoginTable")
    void delete();

}
