package com.trainor.controlandmeasurement.MVVM.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "AssignmentTable")
public class AssignmentEntity {

    @PrimaryKey
    public int assignmentID;

    public long adminID;
    public int companyID;
    public String assignmentName;
    public String companyName;
    public int status;
}
