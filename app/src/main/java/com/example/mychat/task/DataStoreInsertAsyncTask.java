package com.example.mychat.task;

import android.os.AsyncTask;

import com.example.mychat.dao.TextMessageDao;
import com.example.mychat.db.AppDatabase;
import com.example.mychat.entity.TextMessage;

public class DataStoreInsertAsyncTask extends AsyncTask<Void, Void, Integer> {

    private String text;

    private Integer from;

    private AppDatabase db;

    public DataStoreInsertAsyncTask(String text, Integer from, AppDatabase db){
        this.text = text;
        this.from = from;
        this.db = db;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        TextMessageDao textMessageDao = db.textMessageDao();
        textMessageDao.insert(new TextMessage(text,from));
        return 0;
    }
}
