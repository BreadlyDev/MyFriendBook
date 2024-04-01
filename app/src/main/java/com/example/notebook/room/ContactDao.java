package com.example.notebook.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notebook.models.Contact;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts")
    List<Contact> getAll();
    @Insert
    void insert(Contact student);
    @Delete
    void delete(Contact student);
    @Update
    void update(Contact student);
    @Query("SELECT * FROM contacts ORDER BY surname ASC")
    List<Contact> sortAll();
}
