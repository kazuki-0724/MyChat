package com.example.mychat.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mychat.dao.TextMessageDao;
import com.example.mychat.entity.TextMessage;

@Database(entities = {TextMessage.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TextMessageDao textMessageDao();
}
