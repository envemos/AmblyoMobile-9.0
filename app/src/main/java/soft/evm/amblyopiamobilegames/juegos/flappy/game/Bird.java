package soft.evm.amblyopiamobilegames.juegos.flappy.game;

/**
 * Created by programador on 04/02/2018.
 */

public class Bird {
    public float radio,x, y, vy, width;
    public boolean primeraVez = true;

    public Bird() {}

    public Bird(int width) {
        radio = width/20;
        this.width = width;
    }

    public void physics() {
        vy+=width/700;
        y+=vy;
    }

    public void jump() {
        if(vy >= 0)
            vy = -width/45;
    }
}
