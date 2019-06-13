package com.jagtarkhinda.modernjoust;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // -----------------------------------
    // ## ANDROID DEBUG VARIABLES
    // -----------------------------------

    // Android debug variables
    final static String TAG = "JOUST";

    // -----------------------------------
    // ## SCREEN & DRAWING SETUP VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;
    int level1;
    int level2;
    int level3;
    int level4;
    Sprite demo;
    Sprite dog;


    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    //Creating an array to store all enemies
    ArrayList<Sprite> enemies = new ArrayList<Sprite>();
    ArrayList<Integer> speed = new ArrayList<Integer>();
    int speed_count = 0;

    // ----------------------------
    // ## SPRITES
    // ----------------------------
    Sprite e1;
    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------


    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        //defining the position of all 4 levels
        this.level1 = ((screenHeight / 4) - 100);
        this.level2 = ((screenHeight / 2) - 100);
        this.level3 = (((screenHeight / 2) + (screenHeight / 4)) - 100);
        this.level4 = screenHeight - 200;

        this.printScreenInfo();

        // @TODO: Add your sprites to this section
        // This is optional. Use it to:
        //  - setup or configure your sprites
        //  - set the initial position of your sprites

        //cat sprite to get the width and height properties
        demo = new Sprite(getContext(), 100, 200, R.drawable.cat);
        dog = new Sprite(getContext(),200,level2 - demo.image.getHeight(),R.drawable.dogbig);


        // @TODO: Any other game setup stuff goes here


    }

    // ------------------------------
    // HELPER FUNCTIONS
    // ------------------------------

    // Enemy will be created each time this function is called (xs and ys will be passed randomly
    public void makeEnemy(int xs, int ys) {
        e1 = new Sprite(getContext(), xs, ys, R.drawable.cat);
        enemies.add(e1);
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    public int randomLevel() {
        Random r = new Random();
        int level = r.nextInt(4);
        level = level + 1;
        if (level == 1) {
            return this.level1;
        } else if (level == 2) {
            return this.level2;
        }
        else if (level == 3) {
            return this.level3;
        }
        else if (level == 4) {
            return this.level4;
        }
        return 0;
    }


    // This funciton prints the screen height & width to the screen.
    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }


    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    // 1. Tell Android the (x,y) positions of your sprites
    public void updatePositions() {
        // @TODO: Update the position of the sprites


        //updating enemy positions (I will modify this)
        if (enemies.size() > 0) {
            for (int i = 0; i < enemies.size(); i++) {
                Sprite t = enemies.get(i);
                t.setxPosition(t.getxPosition() + speed.get(i));

                t.updateHitbox();

                //Making enemies appear from other side of screen
                if(t.getxPosition()> this.screenWidth)
                {
                    t.setxPosition( - (t.image.getWidth()));
                }

                // Collision Detection
                if(t.getHitbox().intersect(this.dog.getHitbox()))
                {
                    //removing enemy and its speed variable from scene
                    enemies.remove(t);
                    speed.remove(i);

                    //Creating an egg

                }

            }
        }




        // @TODO: Collision detection code

    }

    Paint p;
    long currentTime = 0;
    long previousTime = 0;


    // 2. Tell Android to DRAW the sprites at their positions
    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //@TODO: Draw the sprites (rectangle, circle, etc)

            //----------------
            // Put all your drawing code in this section

            // configure the drawing tools
            p = new Paint();
            //setting background images of canvas
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
            b = Bitmap.createScaledBitmap(b, screenWidth, screenHeight, false);
            p.setColor(Color.RED);
            canvas.drawBitmap(b, 0, 0, p);

            //building levels
            //level image variable
            Bitmap level = BitmapFactory.decodeResource(getResources(), R.drawable.level);
            level = Bitmap.createScaledBitmap(level, screenWidth, 100, false);
            p.setColor(Color.GREEN);
            //adding level to canvas
            canvas.drawBitmap(level, 0, level1, p);
            canvas.drawBitmap(level, 0, level2, p);
            canvas.drawBitmap(level, 0, level3, p);
            canvas.drawBitmap(level, 0, level4, p);

            // ------------------------------
            // CREATING ENEMIES
            // ------------------------------

            //getting level
            int get_level = randomLevel();

            //keeping track of time

            // get current time
            currentTime = System.currentTimeMillis();

            if ((currentTime - previousTime) > 2000) {

                //setting random position for the enemies after every 2 seconds (max enemies limit = 8)

                if (enemies.size() < 8)
                {
                    makeEnemy((int) ((Math.random() * (((this.screenWidth - this.demo.image.getWidth()) - 0) + 1)) + 0),
                            this.randomLevel() - this.demo.image.getHeight());
                    //setting speed for each enemy
                    speed.add((int) ((Math.random() * (((30 - 9) + 1)) + 9)));
                    speed_count++;
                }
                previousTime = currentTime;
            }
            //drawing all the enemies from array
            if (enemies.size() > 0) {
                for (int i = 0; i < enemies.size(); i++) {
                    Sprite t = enemies.get(i);

                    canvas.drawBitmap(t.getImage(), t.getxPosition(), t.getyPosition(), p);
                    p.setColor(Color.RED);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(5);
                    canvas.drawRect(t.getHitbox(),p);
                }
            }

            // ------------------------------
            // CREATING DOG
            // ------------------------------
            canvas.drawBitmap(dog.getImage(),dog.getxPosition(),dog.getyPosition(),p);
            canvas.drawRect(dog.getHitbox(),p);

            //@TODO: Draw game statistics (lives, score, etc)

            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game
    public void setFPS() {
        try {
            gameThread.sleep(50);
        } catch (Exception e) {

        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            // user pushed down on screen
            Log.d("abc", "tap");
        } else if (userAction == MotionEvent.ACTION_UP) {
            // user lifted their finger
        }
        return true;
    }
}