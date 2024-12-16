package com.example.mychat;

import static com.example.mychat.constant.CommonConst.DATE_FORMAT;
import static com.example.mychat.constant.CommonConst.FROM_MYSELF;
import static com.example.mychat.constant.CommonConst.FROM_OTHERS;
import static com.example.mychat.constant.CommonConst.REQUEST_PERMISSION_CODE;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.mychat.db.AppDatabase;
import com.example.mychat.db.AppDatabaseSingleton;
import com.example.mychat.listener.SubmitButtonListener;
import com.example.mychat.task.DataStoreDeleteAllAsyncTask;
import com.example.mychat.task.DataStoreInsertAsyncTask;
import com.example.mychat.task.DataStoreSelectAllAsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    /** テキスト入力 */
    private EditText editText;
    /** 送信ボタン */
    private Button button;
    /** GUIスレッド */
    private static Handler guiThreadHandler;
    /** スクロール用コンテナ */
    private LinearLayout container;
    /** レイアウト */
    private LayoutInflater inflater;
    /** MainActivity */
    private static MainActivity instance;
    /** スクロールビュー */
    private ScrollView scrollView;
    /** 設定ボタン */
    private Button settingButton;
    /** DB */
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // インスタンスを確保
        instance = this;

        // DB取得
        db = AppDatabaseSingleton.getInstance(getApplicationContext());

        // 通知の許可を得る
        checkAndRequestNotificationPermission();

        // 画面更新用レシーバ
        UpdateReceiver receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("DO_ACTION");
        registerReceiver(receiver, filter);

        // Viewの取得
        button = findViewById(R.id.submit);
        container = findViewById(R.id.container);
        editText = findViewById(R.id.editText);
        scrollView = findViewById(R.id.scroll_view);
        settingButton = findViewById(R.id.setting_button);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        guiThreadHandler = new Handler();

        // 送信ボタンのリスナー
        SubmitButtonListener sbl = new SubmitButtonListener(editText,this, db);
        button.setOnClickListener(sbl);

        String deviceToken = getMyTokenFromSharedPreference();

        // 設定ボタン（デバイストークン表示用）
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Device Token")
                        .setMessage(deviceToken)
                        .setPositiveButton("COPY", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ClipboardManager clipboardManager =
                                        (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                                if (null == clipboardManager) {
                                    return;
                                }
                                clipboardManager
                                        .setPrimaryClip(ClipData.newPlainText("",deviceToken ));
                            }
                        })
                        .setNegativeButton("DB-RESET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new DataStoreDeleteAllAsyncTask(db).execute();
                            }
                        })
                        .show();
            }
        });

        // 画面の初期ロード
        initMainView();
    }

    /**
     *
     * @param text 追加するメッセージ
     * @param type 自分／他人
     */
    public void addTextAsync(final String text, final int type){
        guiThreadHandler.post(new Runnable(){
            @Override
            public void run() {

                // テキスト用のビュー
                View content;

                //コンテントの追加
                if(type == FROM_MYSELF){
                    //自分のとき
                    content = inflater.inflate(R.layout.my_msg, null);
                }else{
                    //他人からの時
                    content = inflater.inflate(R.layout.other_msg, null);
                }

                TextView timeStamp = content.findViewById(R.id.timeStamp);
                TextView textView = content.findViewById(R.id.text);

                //EditTextの内容をSetする
                textView.setText(text);
                //日時登録
                timeStamp.setText(getTimeStamp());

                //メインビューにメッセージビューを追加
                container.addView(content);
                // 最下部までスクロール
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

            }
        });
    }

    /**
     * タイムスタンプを取得する
     * @return
     */
    public String getTimeStamp(){
        //現在時刻を取得
        Date now = new Date();
        // 表示形式を指定
        SimpleDateFormat sdf =
                new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(now);
    }

    /**
     * ブロードキャストを受ける
     * （メッセージ受信時にコールされる）
     */
    protected class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle extras = intent.getExtras();
            String msg = extras.getString("message");
            Log.i("MyChat："+this.getClass().getName(),msg);

            // 受信したメッセージを画面に反映させる
            addTextAsync(msg,FROM_OTHERS);
        }
    }

    /**
     * MainActivityのインスタンスを返す
     * @return MainActivity
     */
    public static MainActivity getInstance() {
        return instance;
    }

    /**
     * DBからメッセージを取得してきて画面に反映させる
     * （起動時に）
     */
    private void initMainView(){
        new DataStoreSelectAllAsyncTask(db).execute();
    }

    private void checkAndRequestNotificationPermission() {
        // 権限が既に許可されているかを確認
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // 権限が許可されていなければリクエスト
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * SharedPreferenceからデバイストークンを取得する
     * @return デバイストークン
     */
    public String getMyTokenFromSharedPreference(){
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences("myChat_pref", MODE_PRIVATE);
        return prefs.getString("myToken","hogehoge");
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // ユーザーが許可した場合
//                Toast.makeText(this, "通知の権限が許可されました", Toast.LENGTH_SHORT).show();
//            } else {
//                // ユーザーが拒否した場合
//                Toast.makeText(this, "通知の権限が拒否されました", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}