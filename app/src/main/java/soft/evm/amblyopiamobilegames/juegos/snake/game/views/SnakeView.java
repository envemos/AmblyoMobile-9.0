package soft.evm.amblyopiamobilegames.juegos.snake.game.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.juegos.Colores;
import soft.evm.amblyopiamobilegames.juegos.snake.game.SnakeGame;
import soft.evm.amblyopiamobilegames.juegos.snake.game.engine.GameEngine;
import soft.evm.amblyopiamobilegames.juegos.snake.game.enums.TileType;

/**
 * Created by AlumnoT on 20/12/2017.
 */

public class SnakeView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TileType snakeViewMap[][];

    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSnakeViewMap(TileType[][] snakeViewMap) {
        this.snakeViewMap = snakeViewMap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(snakeViewMap!=null) {
            float titleSizeX = canvas.getWidth() / snakeViewMap.length;
            float titleSizeY = titleSizeX;//canvas.getHeight() / snakeViewMap[0].length;

            //para poder centrar la imagen hay que calcular el espacio sobrante;
            float alturaJuego = titleSizeY * GameEngine.GameHeight;
            float alturaPantalla = canvas.getHeight();
            float diferencia = alturaPantalla - alturaJuego;
            float d = diferencia/2;


            float circleSize = Math.min(titleSizeX, titleSizeY) / 2;

            mPaint.setColor(Colores.getFondo());
            canvas.drawPaint(mPaint);
            for (int x = 0; x < snakeViewMap.length; x++) {

                for (int y = 0; y < snakeViewMap[x].length; y++) {

                    switch (snakeViewMap[x][y]) {

                        case Nothing:
                            break;
                        case Wall:
                            if(GameEngine.muros)
                                mPaint.setColor(Colores.getOjo_sano());
                            else
                                mPaint.setColor(Colores.getFondo());
                            canvas.drawRect(x * titleSizeX, y * titleSizeY + titleSizeY + d,  x * titleSizeX + titleSizeX, y * titleSizeY + d,mPaint);
                            canvas.drawCircle(x * titleSizeX + titleSizeX / 2f, y * titleSizeY + titleSizeY / 2f + d, circleSize, mPaint);
                            break;
                        case SnakeHead:
                            mPaint.setColor(Colores.getOjo_vago());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                canvas.drawRoundRect(x * titleSizeX, y * titleSizeY + titleSizeY + d,  x * titleSizeX + titleSizeX, y * titleSizeY + d,20,20, mPaint);
                            else
                                canvas.drawRect(x * titleSizeX, y * titleSizeY + titleSizeY + d,  x * titleSizeX + titleSizeX, y * titleSizeY + d, mPaint);
                            canvas.drawCircle(x * titleSizeX + titleSizeX / 2f, y * titleSizeY + titleSizeY / 2f + d, circleSize, mPaint);
                            break;
                        case SnakeTail:
                            mPaint.setColor(Colores.getOjo_vago());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                canvas.drawRoundRect(x * titleSizeX, y * titleSizeY + titleSizeY + d,  x * titleSizeX + titleSizeX, y * titleSizeY + d,15,15, mPaint);
                            else
                                canvas.drawRect(x * titleSizeX, y * titleSizeY + titleSizeY + d,  x * titleSizeX + titleSizeX, y * titleSizeY + d, mPaint);
                            canvas.drawCircle(x * titleSizeX + titleSizeX / 2f, y * titleSizeY + titleSizeY / 2f + d, circleSize, mPaint);
                            break;
                        case Apple:
                            mPaint.setColor(Colores.getOjo_sano());
                            canvas.drawCircle(x * titleSizeX + titleSizeX / 2f, y * titleSizeY + titleSizeY / 2f + d, circleSize, mPaint);
                            break;

                    }

                }

            }

            //dibujar líneas -> en caso de no muros, se delimitará bien el espacio de juego
            float anchoPantalla = canvas.getWidth();
            mPaint.setColor(Colores.getOjo_sano());
            canvas.drawLine(0,d,anchoPantalla,d, mPaint);
            canvas.drawLine(0,d + alturaJuego,anchoPantalla,d + alturaJuego, mPaint);

            //dibujar texto
            mPaint.setTextSize(getWidth()/17);

            canvas.drawText(getContext().getString(R.string.tiempo) + ": " + formatearMilis(SnakeGame.chronometer.getMilis()), titleSizeX, d/2 - d/4, mPaint);
            canvas.drawText(getContext().getString(R.string.velocidad) + ": " + GameEngine.velocidad * 10 + "%", titleSizeX,d/2 + d/4, mPaint);
            canvas.drawText(getContext().getString(R.string.comidos_) + " " +  GameEngine.manzanas, titleSizeX, alturaJuego + d + d/2 - d/4, mPaint);
            canvas.drawText(getContext().getString(R.string._score) + " " + GameEngine.score, titleSizeX, alturaJuego + d + d/2 + d/4, mPaint);
        }
    }

    public String formatearMilis(long milis) {
        String formato = "%02d:%02d:%02d";
        long minutosReales = TimeUnit.MILLISECONDS.toMinutes(milis);
        long segundosReales = TimeUnit.MILLISECONDS.toSeconds(milis) - TimeUnit.MILLISECONDS.toSeconds(minutosReales);
        if(segundosReales > 59)
            segundosReales -= 59;
        long decimasReales = (TimeUnit.MILLISECONDS.toMillis(milis) - TimeUnit.MILLISECONDS.toMillis(minutosReales) - TimeUnit.MILLISECONDS.toSeconds(segundosReales))%1000/10;
        return String.format(formato, minutosReales, segundosReales, decimasReales);
    }
}
