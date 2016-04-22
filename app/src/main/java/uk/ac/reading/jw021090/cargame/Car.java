package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Random;

import uk.ac.reading.jw021090.cargame.R;

/**
 * Created by Kikadass on 14/04/2016.
 */


public class Car extends Object{
    //Will store the image of a car
    private Bitmap image;
    private int speed;
    private int maxSpeed;
    private Random rnd = new Random();


    public Car(Bitmap res, int w, int h, int score, int maxSpeed){
        int random = rnd.nextInt(4 - GameView.gameState);
        if (GameView.changingState && random != 0)random--;
        switch (random){
            case 0:
                xPos = 112;
                break;
            case 1:
                xPos = 72;
                break;
            case 2:
                xPos = 32;
                break;
            case 3:
                xPos = 152;
                break;
        }
        super.yPos = -h;
        width = w;
        height = h;
        this.maxSpeed = maxSpeed;

        this.speed = 3 +(int) (rnd.nextDouble()*score/50);

        // cap car speed

        if (this.speed > maxSpeed) this.speed = maxSpeed;

        this.image = res;

    }


    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image,xPos,yPos,null);
    }

    public void update(){
        yPos += speed;

    }

}
