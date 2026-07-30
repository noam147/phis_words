package com.example.viewpagertry2;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TTSHelper {
    private static final String TTS_URL = "https://translate.google.com/translate_tts?ie=UTF-8&q=%s&tl=%s&client=tw-ob";

    public interface TTSCallback {
        void onSuccess(File file);
        void onFailure(Exception e);
    }

    private static boolean containsRussian(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CYRILLIC) {
                return true;
            }
        }
        return false;
    }

    public static void downloadAudio(Context context, String word, TTSCallback callback) {
        String lang = containsRussian(word) ? "ru" : "en";
        String encodedWord;
        try {
            encodedWord = java.net.URLEncoder.encode(word, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            encodedWord = word;
        }
        
        String url = String.format(TTS_URL, encodedWord, lang);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                File dir = new File(context.getFilesDir(), "custom_recordings");
                if (!dir.exists()) dir.mkdirs();

                // Replace spaces and special chars for filename, preserve Cyrillic
                String safeName = word.toLowerCase().replaceAll("[^a-z0-9\\u0400-\\u04FF]", "_").trim();
                File file = new File(dir, safeName + ".mp3");
                
                try (InputStream is = response.body().byteStream();
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[2048];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                }
                callback.onSuccess(file);
            } catch (Exception e) {
                Log.e("TTSHelper", "Error downloading audio for " + word, e);
                if (callback != null) callback.onFailure(e);
            }
        }).start();
    }
}
