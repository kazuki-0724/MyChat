package com.example.mychat.util;

import static com.example.mychat.constant.CommonConst.FCM_API_URL;

import android.util.Log;

import com.google.gson.JsonObject;


import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FcmUtil {


    /**
     * FCMサーバにPUSHを送る
     * @param accessToken FCMのアクセストークン
     * @param body メッセージ本文
     * @param deviceToken 送信先デバイストークン
     * @throws Exception
     */
    public static void sendPushNotification(String accessToken, String body, String deviceToken){

        // HTTPリクエスト用
        OkHttpClient client = new OkHttpClient();

        // JSONボディを構成
        JsonObject message = new JsonObject();
        // notification部分
        //JsonObject notification = new JsonObject();
        //notification.addProperty("title", "My Chat");
        //notification.addProperty("body", body);
        // data部分
        JsonObject data = new JsonObject();
        data.addProperty("user", deviceToken);
        data.addProperty("text", body);
        data.addProperty("title","MyChat");

        message.add("message", new JsonObject());
        JsonObject messageData = message.getAsJsonObject("message");

        messageData.addProperty("token", deviceToken);  // ターゲットのデバイストークン
        //messageData.add("notification", notification);
        messageData.add("data", data);

        Log.i("FcmService",messageData.toString());

        // リクエストを作成
        RequestBody bodyRequest = RequestBody.create(
                MediaType.parse("application/json"), message.toString());

        Request request = new Request.Builder()
                .url(FCM_API_URL)
                .header("Authorization", "Bearer " + accessToken)
                .post(bodyRequest)
                .build();

        // リクエストを送信
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Failed to send push notification: ");
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Push notification sent successfully!");
                return;
            }
        });
    }

    /**
     *
     * @return デバイストークン群
     */
    public static String[] getDeviceTokensFromGAS(){
        /** デバイストークン群 */
        String[] tokens = { //kyyのほうがNothing
                "cURc-kyyR0-QFTk_cWHWkW:APA91bFTsdfw-ca0yBRXkbq_kn4M2uj6HApRllZpyhY1whW_k9H6TkSe6bzsd5FdzoO8jxrBsTzgKWoCIqiKDcdjeSN8oQBnyGK2E6ZWpYsqVPMQCihaXCk",
                "cd5IQE3aTdCkm_t9uKPaIY:APA91bGTongEQMZUYX31YIoHHptRyu6HWbMEJKUFa4TNA99otacXfSKTFxdAY969ffcfv_g4DC8JiVzwAk-8XXpEm2fvjUxPabQTgJQ_cbMvEQ2BHTAiPxo"
        };
        return tokens;
    }
}