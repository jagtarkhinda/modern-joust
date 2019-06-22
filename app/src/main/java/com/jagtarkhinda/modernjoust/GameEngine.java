package com.jagtarkhinda.modernjoust;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable, GestureDetector.OnGestureListener {

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
    int enemy_Counter = 0;
    Player playerHeight;
    Player player;
    int eggX = 0;
    int eggY = 0;
    Sprite demo;
    Sprite dog;
    Sprite egg;
    Sprite eggDemo;
    Sounds sound;
    Player cat;
    private GestureDetector gestureDetector;

    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    //Creating an array to store all enemies
    ArrayList<Sprite> enemies = new ArrayList<Sprite>();
    ArrayList<Integer> speed = new ArrayList<Integer>();
    ArrayList<Sprite> eggs = new ArrayList<Sprite>();
    ArrayList<Integer> eggTime = new ArrayList<Integer>();
    int speed_count = 0;
    int eggSpawnTime = 0;
    int isMoving = 0;
    int playerLevelNumber = 4;
    int newY = 0;
    Boolean playerUp = false;
    Boolean playerDown = false;
    // ----------------------------
    // ## SPRITES
    // ----------------------------
    Sprite e1;
    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------
    int lives = 5;
    int score = 0;

    public GameEngine(Context context, int w, int h) {
        super(context);


        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        gestureDetector = new GestureDetector(getContext(),this);
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

        //adding player to the engine
        playerHeight = new Player(getContext(),0,0,R.drawable.pikachu);
        this.player = new Player(getContext(), 50, level4 - this.playerHeight.getImage().getHeight(), R.drawable.pikachu);

        //cat sprite to get the width and height properties
        demo = new Sprite(getContext(), 100, 200, R.drawable.cat);
        dog = new Sprite(getContext(),400,level2 - demo.image.getHeight(),R.drawable.dogbig);
        eggDemo = new Sprite(getContext(),0,0,R.drawable.egg);
        //creating instance of Sounds class
        sound = new Sounds(context);




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

    //Egg will be created each time this function is called (xs and ys will be passed randomly)
    public void makeEgg(int xs, int ys) {
        egg = new Sprite(getContext(), xs, ys, R.drawable.egg);
        eggs.add(egg);
    }



    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    // getting newY coordinate of player while jumping
    public int getNewY(String l){
        Log.d("LevelString",l);
        int newY = this.level4 - this.playerHeight.getImage().getHeight();;
        if (l.equals("level1")) {
            Log.d("LevelUpdate", "You are on level 1");
            newY = this.level1 - this.playerHeight.getImage().getHeight();
        } else if (l.equals("level2")) {
            Log.d("LevelUpdate", "You are on level 2");
            newY = this.level2 - this.playerHeight.getImage().getHeight();
        } else if (l.equals("level3")) {
            Log.d("LevelUpdate", "You are on level 3");
            newY = this.level3 - this.playerHeight.getImage().getHeight();
        } else if (l.equals("level4")) {
            Log.d("LevelUpdate", "You are on level 4");
            newY = this.level4 - this.playerHeight.getImage().getHeight();
        }
        return newY;
    }

    public int randomLevel() {
        Random r = new Random();
        int level = r.nextInt(4);
        level = level + 1;

        if (level == 1) {
                return this.level1;
        }
        else if (level == 2) {
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

    public void youWin(){
        // huds background
        p.setStyle(Paint.Style.FILL);
        // argb == alpha, red, green, blue where alpha is used for transparency
        p.setColor(Color.argb(250,169, 223, 191));

        // RectF is a function to draw rounded rectangle
        RectF hudBg = new RectF(
                10,
                20,
                this.screenWidth - 10,
                this.screenHeight - 20
        );
        this.canvas.drawRoundRect(hudBg, 50, 30, p);

        // Text style
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        // Text Color
        p.setColor(Color.rgb(30, 132, 73));
        // Text Size
        p.setTextSize(100);
        // Text transparency
        // Displaying Lives Status
        String lose = "YOU WIN!!!";
        //Code to find the center of the screen to display text
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        p.setTextAlign(Paint.Align.LEFT);
        p.getTextBounds(lose, 0, lose.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(lose, x, y, p);
    }

    public void youLose() {
        // huds background
        p.setStyle(Paint.Style.FILL);
        // argb == alpha, red, green, blue where alpha is used for transparency
        p.setColor(Color.argb(250,245, 183, 177));

        // RectF is a function to draw rounded rectangle
        RectF hudBg = new RectF(
                10,
                20,
                this.screenWidth - 10,
                this.screenHeight - 20
        );
        this.canvas.drawRoundRect(hudBg, 50, 30, p);

        // Text style
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        // Text Color
        p.setColor(Color.rgb(148, 49, 38));
        // Text Size
        p.setTextSize(100);
        // Text transparency
        // Displaying Lives Status
        String lose = "YOU LOSE!!!";
        //Code to find the center of the screen to display text
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        p.setTextAlign(Paint.Align.LEFT);
        p.getTextBounds(lose, 0, lose.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(lose, x, y, p);

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



        //updating enemy positions
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


                //detecting when enemy touches player
                if(t.getHitbox().intersect(this.player.getHitboxTop()))
                {
                    if((player.getyPosition() != level4 - this.playerHeight.getImage().getHeight()) || (player.getxPosition() != 50)) {
                        lives--;
                        isMoving = 0;
                        sound.getPlayerDie();
                        playerUp = false;
                        playerDown = false;
                        player.setxPosition(50);
                        player.setyPosition(level4 - this.playerHeight.getImage().getHeight());
                        player.updateHitBoxTop();
                        player.updateHitboxBottom();
                    }

                }

                //detecting when plyer jumps on enemy
                if(t.getHitbox().intersect(this.player.getHitboxBottom()))
                {

                    eggX = t.getxPosition();
                    eggY = t.getyPosition();

                    //Making Egg Appear randomly
                    sound.getEnemyDie();

                    makeEgg((int) ((Math.random() * (((this.screenWidth - this.demo.image.getWidth()) - 0) + 1)) + 0),
                            this.randomLevel() - this.demo.image.getHeight());

                    eggTime.add((int) System.currentTimeMillis());
                    //removing enemy and its speed variable from scene
                    enemies.remove(t);
                    speed.remove(i);
                }


            }

            //detecting player collision with egg
            for(int i =0; i<eggs.size();i++) {

                //detecting player collision with egg
                if (player.getHitboxTop().intersect(eggs.get(i).getHitbox())
                        || player.getHitboxBottom().intersect(eggs.get(i).getHitbox())) {
                    eggs.remove(i);
                    eggTime.remove(i);
                    sound.getCollectEgg();
                    score++;

                }
            }

            //Removing egg after 10 seconds and creating cat again
            for(int i =0; i<eggs.size();i++)
            {

                if((int) System.currentTimeMillis() - eggTime.get(i)  > 10000)
                {
                    makeEnemy(eggs.get(i).getxPosition(),eggs.get(i).getyPosition());
                    speed.add((int) ((Math.random() * (((30 - 9) + 1)) + 9)));
                    speed_count++;
                    sound.getEggTimeUp();
                    eggs.remove(i);
                    eggTime.remove(i);
                }
            }

            //MOVING DOG (TEMP CODE)
            dog.setxPosition(dog.getxPosition() + 20);
            if(dog.getxPosition()> this.screenWidth)
            {
                dog.setxPosition( - (dog.image.getWidth()));
            }
            dog.updateHitbox();

        }


        //player control
        if (isMoving != 0) {
            // -------------------------------------
            // Moving player right or left side on swipe
            // -------------------------------------

            if (isMoving == 1) {
                Log.d("Moving", "Right");
                if (this.player.getxPosition() >= this.screenWidth) {
                    this.player.setxPosition((0 - this.player.getImage().getWidth()));
                }
                this.player.setxPosition(this.player.getxPosition() + 30);
                this.player.updateHitboxBottom();
                this.player.updateHitBoxTop();
                Log.d("Moving", "X == " + this.player.getxPosition());
            } else if (isMoving == 2) {
                if ((this.player.getxPosition() + this.player.getImage().getWidth()) <= 0) {
                    this.player.setxPosition(this.screenWidth);
                }
                this.player.setxPosition(this.player.getxPosition() - 30);
                this.player.updateHitboxBottom();
                this.player.updateHitBoxTop();
                Log.d("Moving", "Left");
                Log.d("Moving", "X == " + this.player.getxPosition());

                // --------------------------------------
                // End of Moving player right or left side on swipe
                // --------------------------------------

                // --------------------------------------
                // Jumping to another level
                // --------------------------------------

            } else if (isMoving == 3) {
                Log.d("Moving", "Up");
                if (playerLevelNumber != 1)
                    playerLevelNumber--;
                else
                    playerLevelNumber = 4;

                // New coordinates of player
                String l = "level" + this.playerLevelNumber;
                Log.d("LevelNumber", "" + l);
                isMoving = 0;
                playerUp = true;
                newY = getNewY(l);
                Log.d("COORD", "// Old Y == " + this.player.getyPosition());
                Log.d("COORD", "// New Y == " + newY);

                // ---
                // need code to animate jump
                // ---

            } else if (isMoving == 4) {
                Log.d("Moving", "down");
                if (playerLevelNumber != 4)
                    playerLevelNumber++;
                else
                    playerLevelNumber = 1;

                // New coordinates of player
                String l = "level" + this.playerLevelNumber;
                Log.d("LevelNumber", "" + l);
                isMoving = 0;
                playerDown = true;
                newY = getNewY(l);
                Log.d("COORD", "// Old Y == " + this.player.getyPosition());
                Log.d("COORD", "// New Y == " + newY);

                // ---
                // need code to animate jump
                // ---
            }
            // --------------------------------------
            // End of jumping to another level
            // --------------------------------------

        }

        //MOVING PLAYER UP

        if(this.player.getyPosition() != newY && playerUp == true)
        {

            this.player.setyPosition(this.player.getyPosition() - 250);


            if(this.player.getyPosition() <= newY){
                this.player.setyPosition(newY);
                playerUp = false;
            }
            this.player.updateHitboxBottom();
            this.player.updateHitBoxTop();

        }

        //MOVING PLAYER DOWN
        if(this.player.getyPosition() != newY && playerDown == true)
        {


            Log.d("LevelNumber", " level = " + playerLevelNumber);

            if (this.player.getyPosition() <= newY) {
                this.player.setyPosition(this.player.getyPosition() + 250);
            }
            else if(this.player.getyPosition() > newY){
                this.player.setyPosition(0);
                this.player.setyPosition(this.player.getyPosition() + 250);
            }
            if((this.player.getyPosition() >=0) && (this.player.getyPosition() >= newY) ) {
                this.player.setyPosition(newY);
                playerDown = false;
            }
            this.player.updateHitboxBottom();
            this.player.updateHitBoxTop();
        }
    //this.player.setyPosition(newY);

    // -------------------------------------
        // End of Moving player right or left side on swipe
        //




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
            // Creating Player
            // -----------------------------

            this.canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(),this.player.getyPosition(), p);
            p.setColor(Color.BLUE);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(5);
            canvas.drawRect(this.player.getHitboxBottom(),p);
            p.setColor(Color.RED);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(5);
            canvas.drawRect(this.player.getHitboxTop(),p);

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

                if (enemy_Counter < 8)
                {
                    makeEnemy((int) ((Math.random() * (((this.screenWidth - this.demo.image.getWidth()) - 0) + 1)) + 0),
                            this.randomLevel() - this.demo.image.getHeight());
                    //setting speed for each enemy
                    speed.add((int) ((Math.random() * (((30 - 9) + 1)) + 9)));
                    speed_count++;
                    enemy_Counter++;
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



            //CREATING EGGS
            for(int i =0; i< eggs.size(); i++) {
                Sprite eg = eggs.get(i);
                canvas.drawBitmap(eg.getImage(), eg.getxPosition(), eg.getyPosition(), p);
                p.setColor(Color.RED);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(5);
                canvas.drawRect(eg.getHitbox(),p);
            }

            //@TODO: Draw game statistics (lives, score, etc)
            // huds background
            p.setStyle(Paint.Style.FILL);
            // argb == alpha, red, green, blue where alpha is used for transparency
            p.setColor(Color.argb(200,86, 101, 115));

            // RectF is a function to draw rounded rectangle
            RectF hudBg = new RectF(
                    10,
                    20,
                    this.screenWidth - 10,
                    150
            );
            this.canvas.drawRoundRect(hudBg, 30, 30, p);

            // Text style
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            // Text Color
            p.setColor(Color.BLACK);
            // Text Size
            p.setTextSize(100);
            // Text transparency
            p.setAlpha(200);
            // Displaying Lives Status
            this.canvas.drawText(("Lives: " + lives), hudBg.left+20, hudBg.bottom-25, p);
            // Calculating Score Text x position
            int scoreX = (int) (hudBg.left + ((hudBg.right - hudBg.left) / 2));
            // Displaying Lives Status
            this.canvas.drawText(("Score: " + score), scoreX, hudBg.bottom-25, p);

            //----------------

            if(lives == 0)
            {
                youLose();
            }
            if(score == 8)
            {
                youWin();
            }

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

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float velocityX, float velocityY) {
        Log.d("Fling Tap", "You Tapped");
        if (motionEvent1.getY() - motionEvent2.getY() > 50) {

            Log.d("Swipe", "Swipe Up");
            Toast.makeText(getContext(), " Swipe Up ", Toast.LENGTH_LONG).show();
            sound.getPlayerJumpUp();
            isMoving = 3;
            return true;
        }

        if (motionEvent2.getY() - motionEvent1.getY() > 50) {

            Log.d("Swipe", "Swipe Down");
            Toast.makeText(getContext(), " Swipe Down ", Toast.LENGTH_LONG).show();
            sound.getPlayerJumpDown();
            isMoving = 4;
            return true;
        }

        if (motionEvent1.getX() - motionEvent2.getX() > 100) {

            Log.d("Swipe", "Swipe Left");
            Toast.makeText(getContext(), " Swipe Left ", Toast.LENGTH_LONG).show();
            isMoving = 2;
            return true;
        }

        if (motionEvent2.getX() - motionEvent1.getX() > 100) {

            Log.d("Swipe", "Swipe Right");
            Toast.makeText(getContext(), " Swipe Right ", Toast.LENGTH_LONG).show();
            isMoving = 1;
            return true;
        } else {

            return true;
        }
    }


    @Override
    public void onLongPress(MotionEvent arg0) {
        Log.d("Long Tap", "You Tapped");
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        Log.d("Scroll Tap", "You Tapped");
        // TODO Auto-generated method stub

        return true;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        Log.d("Single Tap", "You Tapped");
        return true;
    }

}
