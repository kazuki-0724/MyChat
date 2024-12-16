package com.example.mychat.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mychat.entity.TextMessage;

import java.util.List;

@Dao
public interface TextMessageDao {
    @Query("SELECT * FROM textmessage")
    List<TextMessage> getAll();

    @Query("SELECT * FROM textmessage WHERE id IN (:ids)")
    List<TextMessage> loadAllByIds(int[] ids);

    @Insert
    void insertAll(TextMessage... textMessages);

    @Insert
    void insert(TextMessage textMessage);

    @Delete
    void delete(TextMessage textMessage);

    @Query("DELETE FROM textmessage")
    void deleteAll();
}
