package com.example.mychat.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mychat.MainActivity;
import com.example.mychat.dao.TextMessageDao;
import com.example.mychat.db.AppDatabase;
import com.example.mychat.entity.TextMessage;

import java.util.List;

public class DataStoreSelectAllAsyncTask extends AsyncTask<Void, Void, Integer> {

    private AppDatabase db;

    private List<TextMessage> textMessageList;

    public DataStoreSelectAllAsyncTask(AppDatabase db){
        this.db = db;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        TextMessageDao textMessageDao = db.textMessageDao();
        textMessageList = textMessageDao.getAll();
        Log.d(this.getClass().getName(), "Data size is "+ textMessageList.size());
        return 0;
    }

    @Override
    protected void onPostExecute(Integer code) {
        MainActivity activity = MainActivity.getInstance();
        if(activity == null) {
            return;
        }

        // 取得件数分まとめて画面に反映させる
        textMessageList.forEach( msg ->{
            activity.addTextAsync(msg.getTextMessage(),msg.getFrom());
        });
    }
}
