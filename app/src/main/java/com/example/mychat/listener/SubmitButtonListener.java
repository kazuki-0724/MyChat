package com.example.mychat.listener;

import static android.content.Context.MODE_PRIVATE;

import static com.example.mychat.constant.CommonConst.FROM_MYSELF;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.example.mychat.MainActivity;
import com.example.mychat.db.AppDatabase;
import com.example.mychat.task.DataStoreInsertAsyncTask;
import com.example.mychat.util.FcmUtil;
import com.example.mychat.util.FirebaseUtil;

import java.io.File;

public class SubmitButtonListener implements View.OnClickListener {

    /** 入力エリア */
    private EditText editText;
    /** コンテキスト */
    private Context context;
    /** 入力マネージャ */
    private InputMethodManager imm;
    /** 鍵ファイル */
    private File file;
    /** DBインスタンス */
    private AppDatabase db;


    /**
     * コンストラクタ
     * @param editText
     * @param mainActivity
     */
    public SubmitButtonListener(EditText editText, MainActivity mainActivity, AppDatabase db){
        this.editText = editText;
        this.context = mainActivity;
        this.db = db;
        this.file = new File(context.getFilesDir(), "mychat-76f04-ec74f5b76f50.json");
        //ソフトウェアキーボードを表示させるマネージャ
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View view) {

        /** 入力内容の取得 */
        String input = editText.getText().toString();
        resetEditText(editText);

        // 空入力を許さない
        if(input == null || input.equals("")){
            return;
        }

        MainActivity mainActivity = MainActivity.getInstance();
        // DB登録
        new DataStoreInsertAsyncTask(input, FROM_MYSELF, db).execute();
        // 送信内容を画面に反映させる
        mainActivity.addTextAsync(input,FROM_MYSELF);

        // デバイストークンをSharedPreferenceから取得する
        String myDeviceToken = MainActivity.getInstance().getMyTokenFromSharedPreference();
        // トークン群を取得する
        String[] ids = FcmUtil.getDeviceTokensFromGAS();

        try {
            for(String deviceToken : ids){
                // 自分以外の人に送る
                if(!myDeviceToken.equals(deviceToken)) {
                    FcmUtil.sendPushNotification(getAccessToken(), input, deviceToken);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //ソフトウェアキーボードを非表示にする
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 入力欄を初期化する
     * @param editText 入力欄
     */
    public void resetEditText(EditText editText){
        //フォーカスを外す
        editText.clearFocus();
        //入力をクリア
        editText.setText("");
    }

    /**
     * アクセストークンを取得する
     * @return アクセストークン
     * @throws Exception
     */
    private String getAccessToken() throws Exception {
        String accessToken = FirebaseUtil.getAccessToken(this.file);
        return accessToken;
    }
}
