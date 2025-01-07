package com.pradeep.mobileacess.dbaccess.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pradeep.mobileacess.dbaccess.Dao.UserDao;
import com.pradeep.mobileacess.dbaccess.Dao.VersionDao;
import com.pradeep.mobileacess.dbaccess.Entities.User;
import com.pradeep.mobileacess.dbaccess.Entities.VersionEntity;


@Database(entities = {User.class, VersionEntity.class}, exportSchema = false, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase instance;

    public abstract UserDao userDao();
    public abstract VersionDao versionDao();
   
    public static synchronized UserDatabase getInstance(Context context) {

        if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, "UserDatabase.db")
                        .fallbackToDestructiveMigration() // or use a migration strategy
                    .build();
        }
        return instance;
    }
}