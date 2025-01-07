package com.pradeep.mobileacess.dbaccess.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pradeep.mobileacess.dbaccess.Entities.VersionEntity;

import java.util.List;

@Dao
public interface VersionDao {

    @Query("SELECT * FROM Version_table WHERE type = :type AND userId =:userid")
    VersionEntity getVersionByType(String type, int userid);

    @Query("DELETE FROM Version_table")
    void deleteAllData();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VersionEntity> entities);

}
