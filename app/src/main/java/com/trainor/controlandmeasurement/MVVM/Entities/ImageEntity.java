package com.trainor.controlandmeasurement.MVVM.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ImageTable")
public class ImageEntity {

    @PrimaryKey(autoGenerate = true)
    public long imageID;

    public long adminID;
    public long letterID;
    public String measurePointID;
    public String fileName;
    public String filePath;
    public String description;
    public String Tag;
}
