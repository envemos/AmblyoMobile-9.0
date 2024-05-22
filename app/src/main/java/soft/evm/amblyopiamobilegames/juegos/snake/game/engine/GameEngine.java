package soft.evm.amblyopiamobilegames.juegos.snake.game.engine;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.juegos.snake.game.clases.Coordinate;
import soft.evm.amblyopiamobilegames.juegos.snake.game.enums.Direction;
import soft.evm.amblyopiamobilegames.juegos.snake.game.enums.GameState;
import soft.evm.amblyopiamobilegames.juegos.snake.game.enums.TileType;

/**
 * Created by AlumnoT on 20/12/2017.
 */

public class GameEngine {

    //public static final int GameWidth = 24;
    //public static final int GameHeight = 36;

    //public static final int GameWidth = 20;
    //public static final int GameHeight = 35;

    //public static final int GameWidth = 12;
    //public static final int GameHeight = 16;

    public static final int GameWidth = 20;
    public static final int GameHeight = 20;
    public static int mejor_puntuacion;

    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snake = new ArrayList<>();
    private List<Coordinate> apples = new ArrayList<>();

    private Random random = new Random();

    private boolean increaseTail = false;

    private Direction currentDirection = Direction.North;

    public static int manzanas, score;

    private boolean sonido;

    public static boolean muros;

    private Context context;

    private MediaPlayer come;

    public static int velocidad;

    public void setCurrentGameState(GameState currentGameState) {
        this.currentGameState = currentGameState;
    }

    private GameState currentGameState = GameState.Running;

    private Coordinate getSnakeHead(){
        return snake.get(0);
    }

    public GameEngine(Context c, boolean muro, boolean sonidos, int veloz){
        muros = muro;
        sonido = sonidos;
        context = c;
        come = MediaPlayer.create(c, R.raw.come);
        velocidad = veloz;
        manzanas = 0;
        score = 0;
    }

    public void initGame(){
        AddSnake();
        AddWalls();

        AddApples();
    }

    public void UpdateDirection(Direction newDirection){
        if(Math.abs(newDirection.ordinal() - currentDirection.ordinal()) % 2 == 1)
            currentDirection = newDirection;
    }

    public void Update(){
        switch (currentDirection) {
            //Update the snake
            case North:
                UpdateSnake(0, -1);
                break;
            case East:
                UpdateSnake(1, 0);
                break;
            case South:
                UpdateSnake(0, 1);
                break;
            case West:
                UpdateSnake(-1, 0);
                break;

        }

        if(muros) {
            //Check wall collision
            for (Coordinate w : walls) {
                if (snake.get(0).equals(w)) {
                    currentGameState = GameState.Lost;
                }
            }
        }

        //Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if(getSnakeHead().equals(snake.get(i))) {
                currentGameState = GameState.Lost;
                return;
            }
        }

        //Check apples
        Coordinate appleToRemove = null;
        for (Coordinate apple: apples) {
            if(getSnakeHead().equals(apple)){
                appleToRemove = apple;
                increaseTail = true;
                manzanas++;
                int multiplication = velocidad*10;
                float nuevo = 1+multiplication/100f;
                score+=10*nuevo;
                if(sonido)
                    come.start();
            }
        }
        if(appleToRemove != null){
            apples.remove(appleToRemove);
            AddApples();
        }

    }

    public TileType[][] getMap(){
        TileType[][] map = new TileType[GameWidth][GameHeight];

        for (int x = 0; x < GameWidth; x++){
            for (int y = 0; y < GameHeight; y++){
                map[x][y] = TileType.Nothing;
            }
        }

        for (Coordinate wall: walls)
            map[wall.getX()][wall.getY()] = TileType.Wall;

        for (Coordinate s: snake){
            map[s.getX()][s.getY()] = TileType.SnakeTail;
        }

        for (Coordinate a: apples){
            map[a.getX()][a.getY()] = TileType.Apple;
        }

        map[snake.get(0).getX()][snake.get(0).getY()] = TileType.SnakeHead;

        return map;
    }

    private void UpdateSnake(int x, int y){
        int newX = snake.get(snake.size() -1).getX();
        int newY = snake.get(snake.size() -1).getY();

        for (int i = snake.size() - 1; i > 0; i--) {
            snake.get(i).setX(snake.get(i - 1).getX());
            snake.get(i).setY(snake.get(i - 1).getY());
        }

        if(increaseTail){
            snake.add(new Coordinate(newX,newY));
            increaseTail = false;
        }

        if(muros){
            snake.get(0).setX(snake.get(0).getX() + x);
            snake.get(0).setY(snake.get(0).getY() + y);
        }else {
            int xpos = snake.get(0).getX()+x;
            int ypos = snake.get(0).getY()+y;

            if(xpos == -1)
                xpos = GameWidth-1;
            else if(xpos == GameWidth)
                xpos = 0;

            if(ypos == -1)
                ypos = GameHeight-1;
            else if(ypos == GameHeight)
                ypos = 0;

            snake.get(0).setX(xpos);
            snake.get(0).setY(ypos);
        }
    }

    private void AddSnake() {
        snake.clear();
        snake.add(new Coordinate(6,9));
        snake.add(new Coordinate(6,10));
        snake.add(new Coordinate(5,10));
        snake.add(new Coordinate(4,10));
    }

    private void AddWalls() {
        //Top and Bottoms walls
        for (int x = 0; x < GameWidth; x++){
            walls.add(new Coordinate(x,0));
            walls.add(new Coordinate(x,GameHeight-1));
        }

        //Left and Rights walls
        for (int y = 0; y < GameHeight; y++){
            walls.add(new Coordinate(0,y));
            walls.add(new Coordinate(GameWidth - 1,y));
        }
    }

    private void AddApples() {
        Coordinate coordinate = null;
        boolean added = false;
        while(!added){
            int x = 1 + random.nextInt(GameWidth - 2);
            int y = 1 + random.nextInt(GameHeight - 2);
            
            coordinate = new Coordinate(x,y);
            boolean collision = false;
            for (Coordinate s : snake) {
                if(s.equals(coordinate)){
                    collision = true;
                }
            }


            for (Coordinate a : apples) {
                if(a.equals(coordinate)){
                    collision = true;
                }
            }

            added = !collision;

        }

        apples.add(coordinate);
    }

    public GameState getCurrentGameState(){
        return currentGameState;
    }

    public int getScore() {
        return score;
    }

    public boolean isSonido() {
        return sonido;
    }

    public int getManzanas() { return manzanas; }

    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }
}
