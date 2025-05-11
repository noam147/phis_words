package com.example.viewpagertry2;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MakeViewPlayAudio {
    public static MediaPlayer mediaPlayer;
    public static MediaPlayer mediaPlayerForWords;

    public static void playRecordingOfWord(Context context, int recId)
    {
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
