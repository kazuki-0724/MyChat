package com.example.mychat.task;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mychat.MainActivity;
import com.example.mychat.dao.TextMessageDao;
import com.example.mychat.db.AppDatabase;
import com.example.mychat.entity.TextMessage;

import java.util.List;

public class DataStoreDeleteAllAsyncTask extends AsyncTask<Void, Void, Integer> {

    private AppDatabase db;

    private List<TextMessage> textMessageList;

    public DataStoreDeleteAllAsyncTask(AppDatabase db){
        this.db = db;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        TextMessageDao textMessageDao = db.textMessageDao();
        textMessageDao.deleteAll();
        return 0;
    }
}
