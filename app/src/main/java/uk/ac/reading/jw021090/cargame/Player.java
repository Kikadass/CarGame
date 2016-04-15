package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Kikadass on 15/04/2016.
 */
public class Player extends Object{
    private Bitmap image;
    private int score;
    private boolean left;
    private boolean right;
    private boolean playing;
    private long startTime;


    public Player(Bitmap res, int w, int h) {

        yPos = GameView.HEIGHT - 100;
        xPos = GameView.WIDTH / 2 + 4;
        dx = 0;
        score = 0;
        height = h;
        width = w;

        image = res;

        startTime = System.nanoTime();

    }

    public int getScore(){return score;}
    public boolean isPlaying(){return playing;}
    public void setLeft(boolean b){left = b;}
    public void setRight(boolean b){right = b;}
    public void setPlaying(boolean b){playing = b;}




    public void draw(Canvas canvas){
        canvas.drawBitmap(image,xPos,yPos,null);
    }

    public void resetDy(){dy = 0;}
    public void resetScore(){score = 0;}

    public void update(){
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if(elapsed > 100){
            score++;
            startTime = System.nanoTime();
        }

        // if left or right are pressed go in that direcction increasing over time
        // starting with 1 in order to give movement if touched
        if(left){
            if (dx == 0) dx -= 1;
            else dx -= 0.3;
        }
        else if(right){
            if (dx == 0) dx += 1;
            else dx += 0.3;
        }
        else dx = 0;

        // maximum turning speed
        if(dx > 10) dx = 10;
        if(dx < -10) dx = -10;

        xPos += dx;

        //do not go over the sides
        if (xPos > 160) {
            xPos = 160;
        }
        if (xPos < 24){
            xPos = 24;
        }
    }
    
}
