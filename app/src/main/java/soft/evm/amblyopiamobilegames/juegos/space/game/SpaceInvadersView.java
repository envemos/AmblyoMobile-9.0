package soft.evm.amblyopiamobilegames.juegos.space.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;

import soft.evm.amblyopiamobilegames.juegos.Chronometer;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;
import soft.evm.amblyopiamobilegames.juegos.Insertar;

public class SpaceInvadersView extends SurfaceView implements Runnable{

    SharedPreferences prefs, user, space;
    Chronometer chronometer;

    private Context context;

    // This is our thread
    private Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paintSano, paintVago, paintCircle;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    public static int screenX;
    public static int screenY;

    // The players ship
    private PlayerShip playerShip;

    // The player's bullet
    private Bullet playerBullet;

    // The invaders bullets
    private ArrayList<Bullet> invadersBullets = new ArrayList<>();
    private int nextBullet;
    private int maxInvaderBullets = 10;

    // Up to 60 invaders
    private Invader[] invaders = new Invader[60];
    private int numInvaders = 0;

    // The player's shelters are built from bricks
    private DefenceBrick[] bricks = new DefenceBrick[400];
    private int numBricks;

    // For sound FX
    private SoundPool soundPool;
    private int playerExplodeID = -1;
    private int invaderExplodeID = -1;
    private int shootID = -1;
    private int damageShelterID = -1;
    private int uhID = -1;
    private int ohID = -1;

    // The score
    private int score = 0;

    // Lives
    private int lives = 10;

    //Waves
    private int wave;

    // How menacing should the sound be?
    private long menaceInterval = 1000;
    // Which menace sound should play next
    private boolean uhOrOh;
    // When did we last play a menacing sound
    private long lastMenaceTime = System.currentTimeMillis();

    // When the we initialize (call new()) on gameView
    // This special constructor method runs
    public SpaceInvadersView(Context context, int x, int y) {

        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paintSano = new Paint();
        paintVago = new Paint();
        paintCircle = new Paint();
        paintSano.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSano.setAntiAlias(true);
        paintVago.setStyle(Paint.Style.FILL_AND_STROKE);
        paintVago.setAntiAlias(true);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setAntiAlias(true);

        screenX = x;
        screenY = y;

        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("sounds/space/shoot.ogg");
            shootID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/space/invaderexplode.ogg");
            invaderExplodeID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/space/damageshelter.ogg");
            damageShelterID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/space/playerexplode.ogg");
            playerExplodeID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/space/damageshelter.ogg");
            damageShelterID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/space/uh.ogg");
            uhID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/space/oh.ogg");
            ohID = soundPool.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        chronometer = new Chronometer();
        new Thread(chronometer).start();

        Colores.ajustarColores(prefs);
        space = context.getSharedPreferences("Space", Context.MODE_PRIVATE);
        wave = space.getInt("level",1);
        prepareLevel();
    }

    private void prepareLevel(){

        // Here we will initialize all the game objects

        // Make a new player space ship
        playerShip = new PlayerShip(context, screenX, screenY);

        // Prepare the players bullet
        playerBullet = new Bullet(screenY);

        // Build an army of invaders
        numInvaders = 0;
        for(int column = 0; column < 6; column ++ ){
            for(int row = 0; row < 5; row ++ ){
                invaders[numInvaders] = new Invader(context, row, column, screenX, screenY);
                numInvaders ++;
            }
        }

        // Build the shelters
        numBricks = 0;
        for(int shelterNumber = 0; shelterNumber < 4; shelterNumber++){
            for(int column = 0; column < 10; column ++ ) {
                for (int row = 0; row < 5 -(wave-1); row++) {
                    bricks[numBricks] = new DefenceBrick(row, column, shelterNumber, screenX, screenY);
                    numBricks++;
                }
            }
        }

        // Build the invaders bullets
        invadersBullets.clear();

        // Reset the menace level
        menaceInterval = 1000;

    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if (!GameActivity.pause) {
                update();
            }

            // Draw the frame
            try {
                draw();
            }catch (NullPointerException e){
                //version 85
            }

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

            // We will do something new here towards the end of the project
            // Play a sound based on the menace level
            if (!GameActivity.pause) {
                if ((startFrameTime - lastMenaceTime) > menaceInterval) {
                    if (uhOrOh) {
                        // Play Uh
                        if(prefs.getBoolean("sonido",true))
                            soundPool.play(uhID, 1, 1, 0, 0, 1);

                    } else {
                        // Play Oh
                        if(prefs.getBoolean("sonido",true))
                            soundPool.play(ohID, 1, 1, 0, 0, 1);
                    }

                    // Reset the last menace time
                    lastMenaceTime = System.currentTimeMillis();
                    // Alter value of uhOrOh
                    uhOrOh = !uhOrOh;
                }
            }

        }

    }

    private void update(){

        // Did an invader bump into the side of the screen
        boolean bumped = false;

        // Has the player lost
        boolean lost = false;

        // Move the player's ship
        playerShip.update(fps);


        // Update the invaders if visible

        // Update the players bullet
        if(playerBullet.getStatus()){
            playerBullet.update(fps);
        }

        // Update all the invaders bullets if active
        if(!invadersBullets.isEmpty()) {
            for (int i = 0; i < invadersBullets.size(); i++) {
                if (invadersBullets.get(i).getStatus()) {
                    invadersBullets.get(i).update(fps);
                }
            }
        }

        // Update all the invaders if visible
        for(int i = 0; i < numInvaders; i++){
            if(invaders[i].getVisibility()) {

                // Move the next invader
                invaders[i].update(fps);

                // Does he want to take a shot?
                if(invaders[i].takeAim(playerShip.getX(), playerShip.getLength())){

                    // If so try and spawn a bullet
                    invadersBullets.add(new Bullet(screenY));
                    if(invadersBullets.get(invadersBullets.size()-1).shoot(invaders[i].getX() + invaders[i].getLength() / 2, invaders[i].getY(), playerBullet.DOWN)) {

                        // Shot fired
                        // Prepare for the next shot
                        nextBullet++;

                        // Loop back to the first one if we have reached the last
                        if (nextBullet == maxInvaderBullets) {
                            // This stops the firing of another bullet until one completes its journey
                            // Because if bullet 0 is still active shoot returns false.
                            nextBullet = 0;
                        }
                    }
                }

                // If that move caused them to bump the screen change bumped to true
                if (invaders[i].getX() > screenX - invaders[i].getLength()
                        || invaders[i].getX() < 0){

                    bumped = true;


                }
            }

        }


        // Did an invader bump into the edge of the screen
        if(bumped){
            // Move all the invaders down and change direction
            for(int i = 0; i < numInvaders; i++){
                invaders[i].dropDownAndReverse();

                // Have the invaders landed
                if(invaders[i].getY() > screenY - screenY / 10){
                    lost = true;
                }



            }

            // Increase the menace level
            // By making the sounds more frequent
            menaceInterval = menaceInterval - 80;


        }


        if(lost){
            //prepareLevel();
            GameActivity.gameOver=true;
        }




        // Has the player's bullet hit the top of the screen
        if(playerBullet.getImpactPointY() < 0){
            playerBullet = new Bullet(screenY);
            playerBullet.setInactive();
        }

        // Has an invaders bullet hit the bottom of the screen
        int eliminarIndex=0;
        boolean eliminar=false;
        if(!invadersBullets.isEmpty()) {
            for (int i = 0; i < invadersBullets.size(); i++) {
                if (invadersBullets.get(i).getImpactPointY() >= screenY) {
                    eliminarIndex = i;
                    eliminar = true;
                    invadersBullets.get(i).setInactive();
                }
            }
        }
        if(eliminar && eliminarIndex < invadersBullets.size())
            invadersBullets.remove(eliminarIndex);

        // Has the player's bullet hit an invader
        boolean won = false;
        if(playerBullet.getStatus()) {
            won = true;
            for (int i = 0; i < numInvaders; i++) {
                if (invaders[i].getVisibility()) {
                    won = false;
                    if (RectF.intersects(playerBullet.getRect(), invaders[i].getRect())) {
                        invaders[i].setInvisible();
                        if(prefs.getBoolean("sonido",true))
                            soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                        playerBullet = new Bullet(screenY);
                        playerBullet.setInactive();
                        score = score + 10;
                    }
                }
            }
        }

        // Has the player won
        if(won){
            wave++;
            lives+=5;
            //if(wave > 6)
            maxInvaderBullets++;
            prepareLevel();
        }

        // Has an alien bullet hit a shelter brick
        if(!invadersBullets.isEmpty()) {
            eliminarIndex=0;
            eliminar=false;
            for (int i = 0; i < invadersBullets.size(); i++) {
                if (invadersBullets.get(i).getStatus()) {
                    for (int j = 0; j < numBricks; j++) {
                        if (bricks[j].getVisibility()) {
                            if (RectF.intersects(invadersBullets.get(i).getRect(), bricks[j].getRect())) {
                                // A collision has occurred
                                eliminarIndex = i;
                                eliminar = true;
                                bricks[j].setInvisible();
                                if (prefs.getBoolean("sonido", true))
                                    soundPool.play(damageShelterID, 1, 1, 0, 0, 1);
                            }
                        }
                    }
                }
            }
            if(eliminar)
                invadersBullets.remove(eliminarIndex);
        }

        // Has a player bullet hit a shelter brick
        if(playerBullet.getStatus()){
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()){
                    if(RectF.intersects(playerBullet.getRect(), bricks[i].getRect())){
                        playerBullet = new Bullet(screenY);
                        playerBullet.setInactive();
                        bricks[i].setInvisible();
                        if(prefs.getBoolean("sonido",true))
                            soundPool.play(damageShelterID, 1, 1, 0, 0, 1);
                    }
                }
            }
        }

        // Has an invader bullet hit the player ship
        eliminarIndex = 0;
        eliminar=false;
        if(!invadersBullets.isEmpty()) {
            for (int i = 0; i < invadersBullets.size(); i++) {
                if (invadersBullets.get(i).getStatus()) {
                    if (RectF.intersects(playerShip.getRect(), invadersBullets.get(i).getRect()) && invadersBullets.get(i).getImpactPointY() < screenY) {
                        invadersBullets.get(i).setInactive();
                        eliminarIndex = i;
                        eliminar = true;
                        lives--;
                        if (prefs.getBoolean("sonido", true))
                            soundPool.play(playerExplodeID, 1, 1, 0, 0, 1);

                        // Is it game over?
                        if (lives <= 0) {
                            terminar();
                        }
                    }
                }
            }
        }
        if(eliminar && eliminarIndex < invadersBullets.size())
            invadersBullets.remove(eliminarIndex);


    }

    private void terminar() {
        insertar();
        playing=false;
        GameActivity.gameOver=true;
    }

    private void insertar() {
        Insertar.insertar = true;
        user = context.getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        Insertar.correo=user.getString("correo", "");
        Insertar.nombre=user.getString("nombre", "");
        Insertar.nombre_juego="SPACE";
        Insertar.score1=score;
        Insertar.score2=wave;
        Insertar.tiempo=chronometer.getMilis();
    }


    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Colores.getFondo());

            // Choose the brush color for drawing
            paintSano.setColor(Colores.getOjo_sano());
            paintVago.setColor(Colores.getOjo_vago());
            paintCircle.setColor(Colores.getOjo_vago());

            // Now draw the player spaceship
            canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), screenY - playerShip.getRect().height(), paintSano);


            // Draw the invaders
            for(int i = 0; i < numInvaders; i++){
                if(invaders[i].getVisibility()) {
                    if(uhOrOh) {
                        canvas.drawBitmap(invaders[i].getBitmap(), invaders[i].getX(), invaders[i].getY(), paintSano);
                    }else{
                        canvas.drawBitmap(invaders[i].getBitmap2(), invaders[i].getX(), invaders[i].getY(), paintSano);
                    }
                }
            }


            // Draw the bricks if visible
            for(int i = 0; i < numBricks; i++){
                if(bricks[i].getVisibility()) {
                    canvas.drawRect(bricks[i].getRect(), paintVago);
                }
            }


            // Draw the players bullet if active
            if(playerBullet.getStatus()){
                canvas.drawRect(playerBullet.getRect(), paintVago);
            }

            // Draw the invaders bullets

            // Update all the invader's bullets if active
            if(!invadersBullets.isEmpty()) {
                for (int i = 0; i < invadersBullets.size(); i++) {
                    if (invadersBullets.get(i).getStatus()) {
                        canvas.drawRect(invadersBullets.get(i).getRect(), paintVago);
                    }
                }
            }


            // Draw the score and remaining lives
            // Change the brush color
            paintVago.setColor(Colores.getOjo_sano());
            paintVago.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives + "    Wave: " + wave, 10,50, paintVago);



            drawTriangle(canvas, paintVago, screenX/12, screenY/2 + screenY / 10, screenX/16, -1);//L
            drawTriangle(canvas, paintVago, screenX-screenX/12, screenY/2 + screenY / 10, screenX/16, 1);//R
            //drawTriangle(canvas, paint, screenX/12, screenY/2 + screenY / 20 * 5, screenX/16, -1);//R



            canvas.drawCircle(screenX/16f,screenY/2f + screenY / 10f,screenX/24f, paintCircle);
            canvas.drawCircle(screenX-screenX/16f,screenY/2f + screenY / 10f,screenX/24f, paintCircle);
            //canvas.drawCircle(screenX/16f,screenY/2f + screenY / 20f * 5,screenX/24f,paint2);


            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);

        }
    }



    public void drawTriangle(Canvas canvas, Paint paint, int x, int y, int width, int n) {
        int halfWidth = width / 2;

        int c = width*width - halfWidth*halfWidth;
        double altura = Math.sqrt(c);
        altura = altura*n;

        Path path = new Path();
        /*path.moveTo(x, y - halfWidth); // Top
        path.lineTo(x - halfWidth, y + halfWidth); // Bottom left
        path.lineTo(x + halfWidth, y + halfWidth); // Bottom right
        path.lineTo(x, y - halfWidth); // Back to Top
        path.close();*/

        path.moveTo((float) (x + altura), y);
        path.lineTo(x, y - halfWidth);
        path.lineTo(x, y + halfWidth);
        path.moveTo((float) (x + altura), y);
        path.close();

        canvas.drawPath(path, paint);
    }

    // If SpaceInvadersGame is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If SpaceInvadersGame is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                GameActivity.pause = false;

                //if(motionEvent.getY() > screenY - screenY / 8) {
                    if (motionEvent.getX() < screenX/8f && motionEvent.getY() < screenY/2f + screenY && motionEvent.getY() > screenY/2f && motionEvent.getY() < screenY/2f + screenX/8f) {
                        playerShip.setMovementState(playerShip.LEFT);
                    } else if (motionEvent.getX() > screenX-screenX/8f && motionEvent.getY() > screenY/2f && motionEvent.getY() < screenY/2f + screenX/8f) {
                        playerShip.setMovementState(playerShip.RIGHT);
                    }


                //}

                //if(motionEvent.getY() < screenY - screenY / 8) {
                    // Shots fired
                    else if(playerBullet.shoot(playerShip.getX()+ playerShip.getLength()/2 - (float)playerBullet.getWidth()/2,screenY,playerBullet.UP)){
                        if(prefs.getBoolean("sonido",true))
                            soundPool.play(shootID, 1, 1, 0, 0, 1);
                    }
                //}

                break;


            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                //if(motionEvent.getY() > screenY - screenY / 10) {
                    playerShip.setMovementState(playerShip.STOPPED);
                //}
                break;
        }
        return true;
    }

}
