package soft.evm.amblyopiamobilegames.juegos.pong.game;

import android.graphics.Paint;

class Ball {

    float cx;
    float cy;
    float dx;
    float dy;
    int radius;
    Paint paint;

    Ball(int radius, Paint paint) {
        this.radius = radius;
        this.paint = paint;
    }

}
