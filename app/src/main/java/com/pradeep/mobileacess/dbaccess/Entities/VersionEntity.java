package com.pradeep.mobileacess.dbaccess.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "Version_table")
public class VersionEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "version")
    private int version;

    @ColumnInfo(name = "userId")
    private long userId;


    @ColumnInfo(name = "timestamp")
    public long timestamp = Instant.now().getEpochSecond();

    public VersionEntity(String type, int version, long userId) {
        this.type = type;
        this.version = version;
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VersionEntity{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", version=" + version +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
