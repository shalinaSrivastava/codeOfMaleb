package com.trainor.controlandmeasurement.MVVM.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LetterTable")
public class LetterEntity {
    @PrimaryKey(autoGenerate = true)
    public long ID;

    public long letterID;
    public long adminID;
    public String companyName;
    public String altitude;
    public String approvedBy;
    public String assignmentID;
    public String baseDataVersion;
    public String clampAmp;
    public String clampMeasurement;
    public String comments;
    public String compassDirection;
    public String deleted;
    public String disconnectTime;
    public String distance;
    public String earthFaultCurrent;
    public String earthType;
    public String electrode;
    public String electrodeType;
    public String fefTable;
    public String globalEarth;
    public String highVoltageActionTaken;
    public String input;
    public String latitude;
    public String locationDescription;
    public String longitude;
    public String measurePointID;
    public String measuredBy;
    public String measurementDate;
    public String moisture;
    public String noLocalElectrode;
    public String published;
    public String refL;
    public String refT;
    public String registered;
    public String registeredBy;
    public String registeredByName;
    public String revision;
    public String satisfy;
    public String season;
    public String trainorApproved;
    public String trainorComments;
    public String transformerPerformance;
    public String updated;
    public String updatedBy;
    public String updatedByName;
    public String voltage;
    public String images;
    public String Tag;
    public String isSelected;
    public String validID;
    public long timestamp;
    public String directionForward;
    public String directionBackward;
    public String lokalElektrodeVal;
    public String globalElektrodeVal;
    public String basicInstallation;
    public String folderId;
    public String localElectrodeInput;
}
