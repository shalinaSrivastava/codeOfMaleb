package com.trainor.controlandmeasurement.MVVM;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LoginEntity;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    // --------------------------------------- Start Login------------------------------------------
    public void insertLoginDetails(LoginEntity entity) {
        repository.insertLoginDetails(entity);
    }

    public void deleteLoginEntity() {
        repository.deleteLoginEntity();
    }
    // ---------------------------------------- End Login-------------------------------------------

    //--------------------------------------- Start Assignment -------------------------------------
    public void insertAssignment(AssignmentEntity entity) {
        repository.insertAssignment(entity);
    }

    public LiveData<List<AssignmentEntity>> getAllAssignment() {
        return repository.getAllAssignment();
    }

    public LiveData<List<AssignmentEntity>> getAssignment() {
        return repository.getAssignment();
    }

    public AssignmentEntity getAssignment(long assignmentID) {
        return repository.getAssignment(assignmentID);
    }

    public void updateAssignment(AssignmentEntity entity) {
        repository.updatetAssignment(entity);
    }

    public void deleteAssignment(AssignmentEntity entity) {
        repository.deleteAssignment(entity);
    }

    public void deleteAllAssignment() {
        repository.deleteAllAssignment();
    }

    public List<AssignmentEntity> getAssignmentByName(String name) {
        return repository.getAssignmentByName(name);
    }
    //---------------------------------------- End Assignment --------------------------------------

    //----------------------------------------- Start Letter ---------------------------------------
    public LiveData<List<LetterEntity>> getDownloadedLetters(long adminID) {
        return repository.getAllLetter(adminID);
    }

    public List<LetterEntity> getSavedLetter(long _adminID, long _letterID, String mpid) {
        return repository.getSavedLetter(_adminID, _letterID, mpid);
    }

    public List<LetterEntity> letterLocalExists(long adminID, long letterID, String measurePointID) {
        return repository.letterLocalExists(adminID, letterID, measurePointID);
    }

    public void insertLetter(LetterEntity entity) {
        repository.insertLetter(entity);
    }

    public void updateLetter(LetterEntity entity) {
        repository.updatetLetter(entity);
    }

    public void deleteLetter(LetterEntity entity) {
        repository.deleteLetter(entity);
    }

    public void deleteLetterBasedOnMeasurePointID(long adminID, long letterID, String measurePointID) {
        repository.deleteLetterBasedOnMeasurePointID(adminID, letterID, measurePointID);
    }

    public LiveData<List<LetterEntity>> getHistoryLetters(long adminID) {
        return repository.getUploadedLetters(adminID);
    }

    public List<LetterEntity> measurePointIDExists(long adminID, String measurePointID) {
        return repository.measurePointIDExists(adminID, measurePointID);
    }

    public List<LetterEntity> getSavedLetter(long adminID) {
        return repository.getSavedLetter(adminID);
    }

    public String getTag(long adminID, String measurementPointId, long letterId) {
        return repository.getTagStatus(adminID, measurementPointId, letterId);
    }
    //----------------------------------------- End Letter -----------------------------------------

    //----------------------------------------- Start Image ----------------------------------------
    public LiveData<List<ImageEntity>> getAllImages(long adminID, String measurePointID) {
        return repository.getAllImages(adminID, measurePointID);
    }

    public List<ImageEntity> getImagesToUpload(long adminID, long letterID, String mpID) {
        return repository.getImagesToUpload(adminID, letterID, mpID);
    }

    public List<ImageEntity> imageExists(long adminID, String fileName) {
        return repository.imageExists(adminID, fileName);
    }

    public void insertImageData(ImageEntity entity) {
        repository.insertImageData(entity);
    }

    public void updatetImage(ImageEntity entity) {
        repository.updatetImage(entity);
    }

    public void deleteImage(ImageEntity entity) {
        repository.deleteImage(entity);
    }

    public void deleteAllImage(long adminID) {
        repository.deleteAllImage(adminID);
    }

    public List<ImageEntity> imageMPIDExists(long adminID, String measurePointID) {
        return repository.imageMPIDExists(adminID, measurePointID);
    }

    public void deleteImagesMPID(long adminID, String mpid) {
        repository.deleteImagesMPID(adminID, mpid);
    }
    //------------------------------------- End Image ----------------------------------------------

    //--------------------------------- Start Folder Data ------------------------------------------
    public void insertFolderDetails(FolderEntity entity) {
        repository.insertFolderDetails(entity);
    }

    public List<FolderEntity> getSubFolderName(String assignmentid, String folderID) {
        return repository.getSubFolderName(assignmentid, folderID);
    }

    public void deleteAllFolders() {
        repository.deleteAllFolders();
    }

    public List<FolderEntity> getFolders(String assignmentid) {
        return repository.getFolders(assignmentid);
    }

    public List<FolderEntity> getParentID(String assignmentid, String folderid) {
        return repository.getParentID(assignmentid, folderid);
    }

    public List<FolderEntity> getFolder(String assignmentid, String parentID) {
        return repository.getFolder(assignmentid, parentID);
    }

    public List<FolderEntity> searchFolderName(String assignmentid, String folderName) {
        return repository.searchFolderName(assignmentid, folderName);
    }


    public List<FolderEntity> searchSubFolderName(String assignmentid, String folderName){
        return repository.searchSubFolderName(assignmentid, folderName);
    }

    //---------------------------------- End Folder Data -------------------------------------------
}