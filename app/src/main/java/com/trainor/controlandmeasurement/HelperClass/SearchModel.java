package com.trainor.controlandmeasurement.HelperClass;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModel implements Searchable {
    private String mTitle;
    private long assignmentID;
    private String companyName;

    public SearchModel(String title, long _assignmentID, String _companyName) {
        this.mTitle = title;
        this.assignmentID = _assignmentID;
        this.companyName = _companyName;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public long getAssignmentID() {
        return assignmentID;
    }

    public String getCompanyName() {
        return companyName;
    }
}

