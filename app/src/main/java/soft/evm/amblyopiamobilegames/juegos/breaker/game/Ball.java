package soft.evm.amblyopiamobilegames.juegos.breaker.game;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    RectF rect;
    public float xVelocity;
    public float yVelocity;
    public static float ballWidth;
    float ballHeight;

    public Ball(float screenX, float screenY, int level){

        float velocidadx = screenX*20;
        float velocidady = screenY*20;
        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = screenX + velocidadx*level + velocidadx / 2;
        yVelocity = -screenY - velocidady*level + velocidady / 2;

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square
        rect = new RectF();

        ballWidth = screenX/15;
        ballHeight = ballWidth;

    }

    public RectF getRect(){
        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(float x, float y){
        rect.left = x / 2;
        rect.top = (y - ballHeight) - ballHeight;
        rect.right = x / 2 + ballWidth;
        rect.bottom = (y - ballHeight * 2) - ballHeight;
    }

}