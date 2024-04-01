package com.example.notebook.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {
    @PrimaryKey(autoGenerate = true)
    long id;
    @ColumnInfo(name = "surname")
    private String surname;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "phone")
    private String phone;
    @ColumnInfo(name = "image",typeAffinity = ColumnInfo.BLOB)
    private byte[] image;
    @Ignore
    public Contact(long id, String surname, String name, String phone, byte[] image){
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public Contact(String surname, String name, String phone, byte[] image){
        this.surname = surname;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public byte[] getImage() {
        return image;
    }
    public void setId(long id) {
        this.id = id;
    }
}
