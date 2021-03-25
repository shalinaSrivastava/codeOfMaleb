package com.trainor.controlandmeasurement.MVVM;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;

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

@androidx.room.Database(entities = {LoginEntity.class, AssignmentEntity.class, LetterEntity.class, ImageEntity.class, FolderEntity.class}, version = 11, exportSchema = false)
public abstract class Database extends RoomDatabase {
    private static Database instance;

    public abstract AssignmentDAO getAssignmentDao();

    public abstract LoginDAO getLoginDao();

    public abstract LetterDAO getLetterDao();

    public abstract ImageDAO getImageDao();

    public abstract FolderDAO getFolderDao();

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "TrainorMeasurement")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
