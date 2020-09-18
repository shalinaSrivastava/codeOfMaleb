package com.trainor.controlandmeasurement.MVVM.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LoginTable")
public class LoginEntity {
    @PrimaryKey
    public long adminID;

    public String canLogInApp;
    public String canWrite;
    public String companyId;
    public String firstname;
    public String lastname;
    public String hasAssignmentConstraints;
    public String token;
    public String trainorAdmin;
    public String userType;
    public String username;

    public long getAdminID() {
        return adminID;
    }
}

