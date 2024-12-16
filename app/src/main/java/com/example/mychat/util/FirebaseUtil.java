package com.example.mychat.util;


import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FirebaseUtil {

    public static String getAccessToken(File file) throws Exception {

        // GoogleCredentialsを使ってトークンを取得
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file))
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/firebase.messaging"));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            // 非同期処理を行う
            System.out.println("Network request is being executed in the background...");
            // ここでネットワーク処理を行う
            try {
                credentials.refreshIfExpired();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);

        AccessToken accessToken = credentials.getAccessToken();

        return accessToken.getTokenValue();
    }
}
