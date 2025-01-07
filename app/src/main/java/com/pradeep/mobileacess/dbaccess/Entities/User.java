package com.pradeep.mobileacess.dbaccess.Entities;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "number")
    private String number;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "image_location")
    private String imageLocation;

    @ColumnInfo(name = "timestamp")
    public long timestamp = Instant.now().getEpochSecond();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", imageLocation='" + imageLocation + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
