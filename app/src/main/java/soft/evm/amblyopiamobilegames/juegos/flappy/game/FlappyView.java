package soft.evm.amblyopiamobilegames.juegos.flappy.game;

/**
 * Created by programador on 04/02/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import soft.evm.amblyopiamobilegames.juegos.Colores;

/**
 * Created by AlumnoT on 31/01/2018.
 */

public class FlappyView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bird bird = new Bird();
    private ArrayList<Rect> rects = new ArrayList<>();

    public FlappyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Colores.getFondo());
        canvas.drawPaint(mPaint);

        mPaint.setColor(Colores.getOjo_vago());
        if(bird.primeraVez) {
            bird.y=canvas.getHeight() / 2;
            bird.x=canvas.getWidth() / 4;
            bird.primeraVez=false;
        }
        canvas.drawCircle(bird.x, bird.y, bird.radio, mPaint);
        mPaint.setColor(Colores.getOjo_sano());
        int i = 0;
        for (Rect r : rects) {
            i++;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRoundRect(new RectF(r), 360, 360, mPaint);
                if(i%2!=0)//rellenar la parte de arriba
                    canvas.drawRect(r.left, r.top, r.right, r.bottom / 2, mPaint);
                else {//rellenar la parte de abajo
                    canvas.drawRect(r.left, r.top * 120 / 100, r.right, r.bottom, mPaint);
                    /*canvas.drawRect(r.left, r.top * 2, r.right, r.bottom, mPaint);
                    canvas.drawRect(r.left, r.top * 4, r.right, r.bottom, mPaint);
                    canvas.drawRect(r.left, r.top * 8, r.right, r.bottom, mPaint);*/
                }
            } else
                canvas.drawRect(r, mPaint);
        }

        mPaint.setColor(Colores.getOjo_vago());
        // Draw the score
        mPaint.setTextSize(FlappyGame.width/5);
        canvas.drawText(String.valueOf(FlappyGame.saltos/2), FlappyGame.width/2- FlappyGame.width/20, FlappyGame.width/6, mPaint);

    }

    public void setBird(Bird bird, ArrayList<Rect> rects) {
        this.bird = bird;
        this.rects = rects;
    }
}

