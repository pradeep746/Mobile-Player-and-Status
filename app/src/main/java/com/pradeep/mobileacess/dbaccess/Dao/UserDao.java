package com.pradeep.mobileacess.dbaccess.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pradeep.mobileacess.dbaccess.Entities.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_table WHERE id =:userid")
    User getUser(int userid);

    @Query("SELECT * FROM user_table WHERE (email =:user OR number=:user) AND password =:pass")
    User getLogin(String user,String pass);

    @Query("DELETE FROM user_table")
    void deleteAllData();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<User> entities);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void register(User entities);

}