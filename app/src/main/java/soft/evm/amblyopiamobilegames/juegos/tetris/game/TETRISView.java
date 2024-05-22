package soft.evm.amblyopiamobilegames.juegos.tetris.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import soft.evm.amblyopiamobilegames.juegos.Colores;


/**
 * Created by AlumnoT on 31/01/2018.
 */

public class TETRISView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[][] board;
    private static boolean primera = true;
    private Shape shape;

    public TETRISView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTetrisViewBoard(int[][] Board, Shape s){
        board = Board;
        shape = s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(board!=null){
            float titleSizeX = canvas.getWidth() / board[0].length;
            float titleSizeY = titleSizeX;

            mPaint.setColor(Colores.getFondo());
            canvas.drawPaint(mPaint);

            if(!primera) {
                mPaint.setColor(Colores.getOjo_vago());
                for(int row = 0; row < shape.getCoords().length; row++)
                    for(int col = 0; col < shape.getCoords()[row].length; col++) {
                        if (board[shape.getY() + row][col + shape.getX()] == 0 && shape.getCoords()[row][col] == 1) {
                            canvas.drawRect((col + shape.getX()) * titleSizeX, (shape.getY() + row) * titleSizeY + titleSizeY + (canvas.getHeight() - titleSizeY * TetrisGame.boardHeight), (col + shape.getX()) * titleSizeX + titleSizeX, (shape.getY() + row) * titleSizeY + (canvas.getHeight() - titleSizeY * TetrisGame.boardHeight), mPaint);
                            canvas.drawCircle((col + shape.getX()) * titleSizeX + titleSizeX/2,(shape.getY() + row) * titleSizeY + titleSizeY/2 + (canvas.getHeight() - titleSizeY * TetrisGame.boardHeight),titleSizeX/2,mPaint);
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                canvas.drawRoundRect((col + shape.getX()) * titleSizeX, (shape.getY() + row) * titleSizeY + titleSizeY + (canvas.getHeight() - titleSizeY*TetrisGame.boardHeight), (col + shape.getX()) * titleSizeX + titleSizeX, (shape.getY() + row) * titleSizeY + (canvas.getHeight() - titleSizeY*TetrisGame.boardHeight), 10, 10, mPaint);
                            }*/
                        }
                    }
            }
            primera = false;

            mPaint.setColor(Colores.getOjo_sano());
            for(int row = 0; row < board.length; row++)
                for(int col = 0; col < board[row].length; col++)
                    if(board[row][col] == 2) {
                        canvas.drawRect(col * titleSizeX, row * titleSizeY + titleSizeY + (canvas.getHeight() - titleSizeY * TetrisGame.boardHeight), col * titleSizeX + titleSizeX, row * titleSizeY + (canvas.getHeight() - titleSizeY * TetrisGame.boardHeight), mPaint);
                        canvas.drawCircle(col * titleSizeX + titleSizeX/2,row * titleSizeY + titleSizeY/2 + (canvas.getHeight() - titleSizeY * TetrisGame.boardHeight),titleSizeX/2,mPaint);
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            canvas.drawRoundRect(col * titleSizeX, row * titleSizeY + titleSizeY + (canvas.getHeight() - titleSizeY*TetrisGame.boardHeight), col * titleSizeX + titleSizeX, row * titleSizeY + (canvas.getHeight() - titleSizeY*TetrisGame.boardHeight), 10, 10, mPaint);
                        }*/
                    }
            if(Colores.getFondo() == -1)
                mPaint.setColor(Color.BLACK);
            else
                mPaint.setColor(Color.WHITE);
            for(int i = 0; i < TetrisGame.boardHeight ; i++){
                canvas.drawLine(0,canvas.getHeight() - titleSizeY*i,canvas.getWidth(),canvas.getHeight() - titleSizeY*i,mPaint);
            }

            for(int j = 0; j < TetrisGame.boardWidth; j++){
                canvas.drawLine(titleSizeX*j,0,titleSizeX*j,canvas.getHeight(),mPaint);
            }
        }
    }
}

