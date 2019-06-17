package com.jagtarkhinda.modernjoust;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Sounds {

    private SoundPool soundPool;
    private int playerMove;
    private int playerJumpUp;
    private int playerJumpDown;
    private int enemyDie;
    private int playerDie;
    private int eggTimeUp;
    private int collectEgg;

    public Sounds(Context context){

       // soundPool(int maxStreams, int streamType, int srcQuality)
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);


    }

}
