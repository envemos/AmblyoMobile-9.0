package soft.evm.amblyopiamobilegames.juegos.tetris.game;

public class Shape {

    //private BufferedImage block;
    private int[][] coords;
    private TetrisGame board;
    private int deltaX = 0;
    private int x, y;

    public int[][] getCoords() {
        return coords;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private int color;

    private boolean collision = false, moveX = false;

    private int normalSpeed, speedDown = 60, currentSpeed, cada_cinco_figuras;
    //300 - 2000 = 1700

    public static long time, lastTime;

    public static int derribadas = 0, score = 0, level;

    public static boolean auto;

    public Shape(int[][] coords, TetrisGame board, int color, int speed){

        this.coords = coords;
        this.board = board;
        this.color = color;

        level = speed;
        normalSpeed = 2000 - (speed * 200) + 210;
        currentSpeed = normalSpeed;
        time = 0;
        lastTime = System.currentTimeMillis();

        x = 3;
        y = 0;

    }

    public void update(){
        time += System.currentTimeMillis() - lastTime;
        lastTime = System.currentTimeMillis();


        if(!(x + deltaX + coords[0].length > TetrisGame.boardWidth) && !(x + deltaX < 0))
        {

            for(int row = 0; row < coords.length; row++)
                for(int col = 0; col < coords[row].length; col++)
                    if (coords[row][col] == 1) {
                        if (board.getBoard()[y + row][x + deltaX + col] == 2)
                            moveX = false;

                    }
            if(moveX)
                x += deltaX;
        }

        if(!(y + 1 + coords.length > TetrisGame.boardHeight))
        {

            for(int row = 0; row < coords.length; row++)
                for(int col = 0; col < coords[row].length; col++)
                    if (coords[row][col] == 1) {
                        if (board.getBoard()[y + row + 1][col + x] != 0) {
                            collision = true;
                            normalSpeed();
                        }
                    }

            if(time > currentSpeed)
            {
                y++;
                time = 0;
            }
        }else{
            collision = true;
        }

        if(collision)
        {
            for(int row = 0; row < coords.length; row++)
                for(int col = 0; col < coords[row].length; col++)
                    if(coords[row][col] == 1)
                        board.getBoard()[y + row][x + col] = 2;


            checkLine();
            board.setNextShape();
        }

        deltaX = 0;
        moveX = true;

    }

    private void checkLine(){
        int height = board.getBoard().length - 1;

        int filas = 0;

        for(int i = height; i > 0; i--){

            int count = 0;
            for(int j = 0; j < board.getBoard()[0].length; j++){

                if(board.getBoard()[i][j] == 2)
                    count ++;

                board.getBoard()[height][j] = board.getBoard()[i][j];

            }
            if(count < board.getBoard()[0].length) {
                height--;
                filas++;
            }

        }

        int nuevas = TetrisGame.boardHeight - 1 - filas;
        derribadas += nuevas;
        if(nuevas == 1)
            score = score + (4 + level) * 2;
        else
            score = score + (4 + nuevas + level) * 2 * nuevas;
        if(auto){
            cada_cinco_figuras += nuevas;
            if(cada_cinco_figuras >= 5){
                cada_cinco_figuras = 0;
                if(level > 10)
                    level++;
            }
        }

    }


    public void rotate(){

        if(collision)
            return;

        int[][] rotatedMatrix;

        rotatedMatrix = getTranspose(coords);

        rotatedMatrix = getReverseMatrix(rotatedMatrix);

        if(x + rotatedMatrix[0].length > TetrisGame.boardWidth || y + rotatedMatrix.length > TetrisGame.boardHeight)
            return;

        for(int row = 0; row < rotatedMatrix.length; row++)
        {
            for(int col = 0; col < rotatedMatrix[0].length; col++)

                if(board.getBoard()[y + row][x + col] == 2){
                    return;
                }

        }

        coords = rotatedMatrix;

    }


    private int[][] getTranspose(int[][] matrix){
        int[][] newMatrix = new int[matrix[0].length][matrix.length];

        for(int i = 0; i < matrix.length; i++)
            for(int j = 0; j < matrix[0].length; j++)
                newMatrix[j][i] = matrix[i][j];

        return newMatrix;


    }

    private int[][] getReverseMatrix(int[][] matrix){
        int middle = matrix.length / 2;

        for(int i = 0; i < middle; i++){
            int[] m = matrix[i];
            matrix[i] = matrix[matrix.length - i - 1];
            matrix[matrix.length - i - 1] = m;
        }
        return matrix;
    }

    public void setDeltaX(int deltaX){
        this.deltaX = deltaX;
    }

    public void normalSpeed(){
        currentSpeed = normalSpeed;
    }

    public void speedDown(){
        currentSpeed = speedDown;
    }

    public int getColor(){
        return color;
    }

}
