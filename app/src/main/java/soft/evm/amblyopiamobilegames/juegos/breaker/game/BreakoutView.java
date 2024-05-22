package soft.evm.amblyopiamobilegames.juegos.breaker.game;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.GameActivity;

import static soft.evm.amblyopiamobilegames.juegos.breaker.game.BreakoutGame.display;
import static soft.evm.amblyopiamobilegames.juegos.breaker.game.BreakoutGame.level;
import static soft.evm.amblyopiamobilegames.juegos.breaker.game.BreakoutGame.lives;
import static soft.evm.amblyopiamobilegames.juegos.breaker.game.BreakoutGame.score;
import static soft.evm.amblyopiamobilegames.juegos.breaker.game.BreakoutGame.sonido;


// Here is our implementation of BreakoutView
// It is an inner class.
// Note how the final closing curly brace }
// is inside the BreakoutGame class

// Notice we implement runnable so we have
// A thread and can override the run method.
class BreakoutView extends SurfaceView implements Runnable {

    //A PARTIR DE LA 41 NO USAMOS HILO INTERNO
    // This is our thread
    //Thread gameThread = null;

    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    SurfaceHolder ourHolder;

    // Game is paused at the start
    boolean paused = true;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    float screenX;
    float screenY;

    // The players paddle
    Paddle paddle;

    // A ball
    Ball ball;

    // Up to 200 bricks
    Brick[] bricks = new Brick[200];
    int numBricks = 0;

    // For sound FX
    SoundPool soundPool;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;
    int explodeID = -1;

    // When the we initialize (call new()) on gameView
    // This special constructor method runs
    public BreakoutView(Context context) {
        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;

        paddle = new Paddle(screenX, screenY);

        // Create a ball
        ball = new Ball(screenX, screenY, level);

        // Load the sounds

        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("sounds/breaker/beep1.ogg");
            beep1ID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/breaker/beep2.ogg");
            beep2ID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/breaker/beep3.ogg");
            beep3ID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/breaker/loseLife.ogg");
            loseLifeID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sounds/breaker/explode.ogg");
            explodeID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }

        createBricksAndRestart();

    }

    public void createBricksAndRestart() {

        // Put the ball back to the start
        ball.reset(screenX, screenY);

        int filas = 6;
        int columnas = 8;
        float brickWidth = screenX / 8;
        float brickHeight = screenY / 20;

        // Build a wall of bricks
        numBricks = 0;
        for (int column = 0; column < columnas; column++) {
            for (int row = 0; row < filas; row++) {
                bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                numBricks++;
            }
        }
    }

    @Override
    public void run() {
        while (!GameActivity.gameOver) {

            if (!GameActivity.pause) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();
                // Update the frame
                if (!paused) {
                    update();
                }
                // Draw the frame
                try {
                    draw();
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Log.d("42","42");
                }
                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 100000 / timeThisFrame;
                }

            }

        }

    }

    // Everything that needs to be updated goes in here
    // Movement, collision detection etc.
    public void update() {

        // Move the paddle if required
        paddle.update(fps);

        ball.update(fps);

        RectF RectClonBallYUp = new RectF(ball.getRect().centerX(), ball.getRect().top + ball.getRect().height(), ball.getRect().centerX(), ball.getRect().bottom + ball.getRect().height()/2);
        RectF RectClonBallYDown = new RectF(ball.getRect().centerX(), ball.getRect().top - ball.getRect().height(), ball.getRect().centerX(), ball.getRect().bottom - ball.getRect().height()/2);

        RectF RectClonBallXRigth = new RectF(ball.getRect().right, ball.getRect().centerY(),ball.getRect().right , ball.getRect().centerY());
        RectF RectClonBallXLeft = new RectF(ball.getRect().left, ball.getRect().centerY(), ball.getRect().left, ball.getRect().centerY());

        // Check for ball colliding with a brick
        for (int i = 0; i < numBricks; i++) {
            if (bricks[i].getVisibility()) {


                if(RectF.intersects(RectClonBallYUp, bricks[i].getRect()) || RectF.intersects(RectClonBallYDown, bricks[i].getRect())){
                    bricks[i].setInvisible();
                    ball.reverseYVelocity();
                    score = score + 10 * level;
                    if(sonido)
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                }
                else if (RectF.intersects(RectClonBallXRigth, bricks[i].getRect()) || RectF.intersects(RectClonBallXLeft, bricks[i].getRect())){
                    bricks[i].setInvisible();
                    ball.reverseXVelocity();
                    score = score + 10 * level;
                    if(sonido)
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                }
            }
        }

        // Check for ball colliding with paddle
        RectF RectClonBall2 = new RectF(ball.getRect().centerX(), ball.getRect().top - ball.getRect().height()/2, ball.getRect().centerX(), ball.getRect().bottom - ball.getRect().height());
        RectF zonaFail = new RectF(0,paddle.getRect().top, screenX, paddle.getRect().bottom);
        if (RectF.intersects(paddle.getRect(), RectClonBall2)) {
            if(ball.yVelocity > 0)
                ball.reverseYVelocity();
            if(sonido)
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
        }

        // EL TOP SERA A NIVEL DE PADDLE
        // Bounce the ball back when it hits the bottom of screen
        else if (RectF.intersects(RectClonBall2, zonaFail)){//ball.getRect().centerY() > screenY + ball.getRect().height()/2 && ball.getRect().centerY() < screenY) {
            ball.reverseYVelocity();

            // Lose a life
            lives--;
            if(sonido)
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);


            if (lives < 0) {
                GameActivity.gameOver = true;
                    /*paused = true;
                    createBricksAndRestart();*/
            }
        }
        if (ball.getRect().bottom > paddle.getRect().top) {
            ball.reverseYVelocity();
            ball.clearObstacleY(screenY - paddle.getRect().height()+2);

            // Lose a life
            lives--;
            if(sonido)
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

            if (lives < 0)
                GameActivity.gameOver = true;
        }


        // Bounce the ball back when it hits the top of screen
        if (ball.getRect().top < -ball.getRect().height())
        {
            float n = ball.getRect().top;
            float m = ball.getRect().height();
            ball.reverseYVelocity();
            if(ball.getRect().centerY() < 0)
                ball.clearObstacleY(ball.getRect().width()+2);

            if(sonido)
                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
        }

        // If the ball hits left wall bounce
        if (ball.getRect().left == 0)
        {
            ball.reverseXVelocity();
            if(sonido)
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }
        else if (ball.getRect().left < 0)
        {
            ball.reverseXVelocity();
            ball.clearObstacleX(0);
            if(sonido)
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // If the ball hits right wall bounce
        if (ball.getRect().right == screenX) {

            ball.reverseXVelocity();

            if(sonido)
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }
        else if (ball.getRect().right > screenX) {

            ball.reverseXVelocity();

            ball.clearObstacleX(screenX - ball.getRect().width());

            if(sonido)
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
        }

        // Pause if cleared screen
        if (nohaybloques() && !paused)
        {
            paused = true;
            createBricksAndRestart();
            levelUp();
        }

    }


    private boolean nohaybloques() {
        for (int i = 0; i < numBricks; i++)
            if (bricks[i].getVisibility())
                return false;
        return true;
    }

    private void levelUp() {
        level++;
        lives++;
        ball.xVelocity+=screenX/20;
        ball.yVelocity-=screenY/20;
    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Colores.getFondo());

            // Choose the brush color for drawing
            paint.setColor(Colores.getOjo_vago());

            // Draw the ball
            canvas.drawOval(ball.getRect(), paint);

            // Change the brush color for drawing
            paint.setColor(Colores.getOjo_sano());

            // Draw the paddle
            canvas.drawRect(paddle.getRect(), paint);

            // Draw the bricks if visible
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    // Change the brush color for drawing
                    paint.setColor(Colores.getOjo_sano());
                    canvas.drawRect(bricks[i].getRect(), paint);
                }
            }

            // Choose the brush color for drawing
            paint.setColor(Colores.getOjo_vago());

            canvas.drawLine(0, paddle.getRect().top, screenX, paddle.getRect().top, paint);

            // Draw the score
            paint.setTextSize(getWidth()/17);
            canvas.drawText(getContext().getString(R.string.level_) + " " + level + " " + getContext().getString(R.string._score) + " " + score + " " + getContext().getString(R.string.lives_) + " " + lives, 10, 50, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // If SimpleGameEngine Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        GameActivity.pause = true;

        //A partir de la 41 no eliminamos el hilo
        /*try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }*/
    }

    // If SimpleGameEngine Activity is started then
    // start our thread.
    public void resume() {
        GameActivity.pause = false;

        //Antes de la 41
        /*GameActivity.pause = false;
        gameThread = new Thread(this);
        gameThread.start();*/
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (motionEvent.getX() > screenX / 2)
                    paddle.setMovementState(paddle.RIGHT);
                else
                    paddle.setMovementState(paddle.LEFT);
                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                paddle.setMovementState(paddle.STOPPED);
                break;
        }

        return true;
    }

}
