package com.trainor.controlandmeasurement.MVVM.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FolderTable")
public class FolderEntity {
    @PrimaryKey(autoGenerate = true)
    public long ID;

    public String assignmentID;
    public String folderID;
    public String parentID;
    public String name;
}
