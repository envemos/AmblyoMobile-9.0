package soft.evm.amblyopiamobilegames.juegos.breaker.game;

import android.graphics.RectF;

public class Brick {

    private RectF rect;

    private boolean isVisible;

    public Brick(float row, float column, float width, float height){

        isVisible = true;

        float padding = width/25;

        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}

