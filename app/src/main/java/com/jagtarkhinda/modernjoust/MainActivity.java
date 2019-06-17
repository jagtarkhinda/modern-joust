package com.jagtarkhinda.modernjoust;


import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    GameEngine joust;
    MediaPlayer Music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Music = MediaPlayer.create(MainActivity.this,R.raw.gamemusic);
        Music.setLooping(true);
        Music.start();
        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
        joust = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
        setContentView(joust);
    }

    // Android Lifecycle functions
    // ----------------------------

    // This function gets run when user switches from the game to some other app on the phone
    @Override
    protected void onPause() {
        super.onPause();
        Music.release();
        // Pause the game
        joust.pauseGame();
    }

    // This function gets run when user comes back to the game
    @Override
    protected void onResume() {

        super.onResume();

        // Start the game
        joust.startGame();
    }
}
