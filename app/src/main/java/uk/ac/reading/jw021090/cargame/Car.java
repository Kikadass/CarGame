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
    private int score;
    private int speed;
    private Random rnd = new Random();


    public Car(Bitmap res, int w, int h, int score){
        int random = rnd.nextInt(4);
        System.out.println(random);
        switch (random){
            case 0:
                xPos = 32;
                break;
            case 1:
                xPos = 72;
                break;
            case 2:
                xPos = 112;
                break;
            case 3:
                xPos = 152;
                break;
        }
        super.yPos = -h;
        width = w;
        height = h;
        this.score = score;

        this.speed = 2 +(int) (rnd.nextDouble()*score/30);

        // cap missile speed

        if (this.speed > 20) this.speed = 20;

        this.image = res;

    }

    public int getWidth(){
        return width;
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
