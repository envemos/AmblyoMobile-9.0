package soft.evm.amblyopiamobilegames.juegos.space.game;


import android.graphics.RectF;

public class DefenceBrick {
    private RectF rect;

    private boolean isVisible;

    public DefenceBrick(int row, int column, int shelterNumber, int screenX, int screenY){

        int width = screenX / 60;
        int height = screenY / 40;

        isVisible = true;

        int brickPadding = 2;
        // The number of shelters
        int shelterPadding = (int) (screenX / 9.5f);
        int startHeight = (int) (screenY - (screenY /8 * 2)*1.1f);

        rect = new RectF(column * width + brickPadding + (shelterPadding * shelterNumber) + shelterPadding + shelterPadding * shelterNumber,
                row * height + brickPadding + startHeight,
                column * width + width - brickPadding + (shelterPadding * shelterNumber) + shelterPadding + shelterPadding * shelterNumber,
                row * height + height - brickPadding + startHeight);
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
