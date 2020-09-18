package com.trainor.controlandmeasurement.MVVM.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CompaniesTable")
public class CompaniesEntity {

    @PrimaryKey(autoGenerate = true)
    public String companyId;
    public String contactPerson;
    public String department;
    public String companyName;
    public String totalLetters;
}
