package com.trainor.controlandmeasurement.MVVM;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.trainor.controlandmeasurement.MVVM.DAO.AssignmentDAO;
import com.trainor.controlandmeasurement.MVVM.DAO.FolderDAO;
import com.trainor.controlandmeasurement.MVVM.DAO.ImageDAO;
import com.trainor.controlandmeasurement.MVVM.DAO.LetterDAO;
import com.trainor.controlandmeasurement.MVVM.DAO.LoginDAO;
import com.trainor.controlandmeasurement.MVVM.Entities.AssignmentEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.FolderEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.ImageEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LetterEntity;
import com.trainor.controlandmeasurement.MVVM.Entities.LoginEntity;

import java.util.List;

public class Repository {

    private LoginDAO loginDAO;
    private AssignmentDAO assignmentDAO;
    private LetterDAO letterDAO;
    private ImageDAO imageDAO;
    private FolderDAO folderDAO;

    public Repository(Application application) {
        Database db = Database.getInstance(application);
        loginDAO = db.getLoginDao();
        assignmentDAO = db.getAssignmentDao();
        letterDAO = db.getLetterDao();
        imageDAO = db.getImageDao();
        folderDAO = db.getFolderDao();
    }

    //--------------------------------------- Start Login-------------------------------------------
    public void insertLoginDetails(LoginEntity entity) {
        loginDAO.insert(entity);
    }

    public void deleteLoginEntity() {
        loginDAO.delete();
    }
    //---------------------------------------- End Login -------------------------------------------

    //------------------------------------- Start Assignment Data-----------------------------------
    public LiveData<List<AssignmentEntity>> getAllAssignment() {
        return assignmentDAO.getAllAssignment();
    }

    public LiveData<List<AssignmentEntity>> getAssignment() {
        return assignmentDAO.getAssignment();
    }

    public AssignmentEntity getAssignment(long assignmentID) {
        return assignmentDAO.getAssignment(assignmentID);
    }

    public void insertAssignment(AssignmentEntity entity) {
        assignmentDAO.insert(entity);
    }

    public void updatetAssignment(AssignmentEntity entity) {
        assignmentDAO.update(entity);
    }

    public void deleteAssignment(AssignmentEntity entity) {
        assignmentDAO.delete(entity);
    }

    public void deleteAllAssignment() {
        assignmentDAO.deleteAll();
    }

    List<AssignmentEntity> getAssignmentByName(String name) {
        return assignmentDAO.getAssignmentByName(name);
    }
    //---------------------------------- End Assignment Data----------------------------------------

    //----------------------------------- Start Letter Data-----------------------------------------
    public LiveData<List<LetterEntity>> getAllLetter(long adminID) {
        return letterDAO.getDownloadedLetter(adminID);
    }

    public List<LetterEntity> getSavedLetter(long _adminID, long _letterID, String mpid) {
        return letterDAO.getSavedLetter(_adminID, _letterID, mpid);
    }

    public void insertLetter(LetterEntity entity) {
        letterDAO.insert(entity);
    }

    public void updatetLetter(LetterEntity entity) {
        letterDAO.update(entity);
    }

    public void deleteLetter(LetterEntity entity) {
        letterDAO.delete(entity);
    }

    public void deleteLetterBasedOnMeasurePointID(long adminID, long letterID, String measurePointID) {
        letterDAO.deleteBasedOnMeasurePointID(adminID, letterID, measurePointID);
    }

    public List<LetterEntity> letterLocalExists(long _adminID, long letterID, String _measurePointID) {
        return letterDAO.letterLocalExists(_adminID, letterID, _measurePointID);
    }

    public LiveData<List<LetterEntity>> getUploadedLetters(long adminID) {
        return letterDAO.getUploadedLetters(adminID);
    }

    List<LetterEntity> measurePointIDExists(long adminID, String measurePointID) {
        return letterDAO.measurePointIDExists(adminID, measurePointID);
    }

    public List<LetterEntity> getSavedLetter(long adminID) {
        return letterDAO.getSavedLetter(adminID);
    }

    public String getTagStatus(long adminID, String measurementPointID, long letterID) {
        return letterDAO.alreadyDownloaded(adminID, measurementPointID, letterID);
    }
    //------------------------------------ End Letter Data------------------------------------------

    //------------------------------------- Start Image Data----------------------------------------
    public LiveData<List<ImageEntity>> getAllImages(long adminID, String measurePointID) {
        return imageDAO.getImages(adminID, measurePointID);
    }

    public List<ImageEntity> getImagesToUpload(long adminID, long letterID, String mpID) {
        return imageDAO.getImagesToUpload(adminID, letterID, mpID);
    }

    public List<ImageEntity> imageExists(long adminID, String fileName) {
        return imageDAO.imageExists(adminID, fileName);
    }

    public void insertImageData(ImageEntity entity) {
        imageDAO.insertImageData(entity);
    }

    public void updatetImage(ImageEntity entity) {
        imageDAO.updateImageData(entity);
    }

    public void deleteImage(ImageEntity entity) {
        imageDAO.deleteImageEntity(entity);
    }

    public void deleteAllImage(long adminID) {
        imageDAO.deleteAllImages(adminID);
    }

    public List<ImageEntity> imageMPIDExists(long adminID, String measurePointID) {
        return imageDAO.imageMPIDExists(adminID, measurePointID);
    }

    public void deleteImagesMPID(long adminID, String mpid) {
        imageDAO.deleteImagesMPID(adminID, mpid);
    }
    //----------------------------------- End Image Data--------------------------------------------

    //---------------------------------- Start Folder Data -----------------------------------------
    public void insertFolderDetails(FolderEntity entity) {
        folderDAO.insert(entity);
    }

    public List<FolderEntity> getSubFolderName(String assignmentid, String folderID) {
        return folderDAO.getFolderData(assignmentid, folderID);
    }

    public List<FolderEntity> getFolders(String assignmentid) {
        return folderDAO.getFolders(assignmentid);
    }

    public void deleteAllFolders() {
        folderDAO.deleteAllFolders();
    }

    public List<FolderEntity> getParentID(String assignmentid, String folderid) {
        return folderDAO.getParentID(assignmentid, folderid);
    }

    public List<FolderEntity> getFolder(String assignmentid, String parentID) {
        return folderDAO.getFolder(assignmentid, parentID);
    }
    public List<FolderEntity> searchFolderName(String assignmentid, String folderName){
        return folderDAO.searchFolderName(assignmentid, folderName);
    }

    public List<FolderEntity> searchSubFolderName(String assignmentid, String folderName){
        return folderDAO.searchSubFolderName(assignmentid, folderName);
    }
    //-------------------------------------- End Folder Data ---------------------------------------
}