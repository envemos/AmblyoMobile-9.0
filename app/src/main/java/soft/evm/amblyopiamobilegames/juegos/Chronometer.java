package soft.evm.amblyopiamobilegames.juegos;

public class Chronometer implements Runnable {

    /*Thread t = new Thread(){
        public void run(){
            your_stuff();
        }
    };
t.start();*/

    public Chronometer() {}

    private long milis = 0;

    public void run() {
        try {
            while (!GameActivity.gameOver) {
                Thread.sleep(1);
                if(!GameActivity.pause)
                    milis++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            // recommended because catching InterruptedException clears interrupt flag
            Thread.currentThread().interrupt();
            // you probably want to quit if the thread is interrupted
        }
    }

    public long getMilis() {
        return milis;
    }
}
