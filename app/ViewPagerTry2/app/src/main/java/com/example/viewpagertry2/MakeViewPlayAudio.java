package com.example.viewpagertry2;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MakeViewPlayAudio {
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer mediaPlayerForWords;

    public static void playRecordingOfWord(Context context, int recId, String word)
    {
        // Try internal storage first (custom words)
        // preserve Cyrillic in filename match
        String safeName = word.toLowerCase().replaceAll("[^a-z0-9\\u0400-\\u04FF]", "_").trim();
        java.io.File internalFile = new java.io.File(context.getFilesDir(), "custom_recordings/" + safeName + ".mp3");
        
        if (internalFile.exists()) {
            try {
                if (mediaPlayerForWords != null) {
                    mediaPlayerForWords.release();
                }
                mediaPlayerForWords = new MediaPlayer();
                mediaPlayerForWords.setDataSource(internalFile.getAbsolutePath());
                mediaPlayerForWords.prepare();
                mediaPlayerForWords.start();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Fallback to assets
        String fileName = "recordings/" + recId + ".mp3";
        AssetManager assetManager = context.getAssets();

        try {
            // Release any previous media player if it's already playing
            if (mediaPlayerForWords != null) {
                mediaPlayerForWords.release();
            }

            mediaPlayerForWords = new MediaPlayer();
            mediaPlayerForWords.setDataSource(assetManager.openFd(fileName).getFileDescriptor(),
                    assetManager.openFd(fileName).getStartOffset(),
                    assetManager.openFd(fileName).getLength());
            mediaPlayerForWords.prepare();
            mediaPlayerForWords.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void playRecording(Context context,String fileName,boolean loopMusic)
    {
        AssetManager assetManager = context.getAssets();

        try {
            // Release any previous media player if it's already playing
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetManager.openFd(fileName).getFileDescriptor(),
                    assetManager.openFd(fileName).getStartOffset(),
                    assetManager.openFd(fileName).getLength());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(loopMusic);

            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
