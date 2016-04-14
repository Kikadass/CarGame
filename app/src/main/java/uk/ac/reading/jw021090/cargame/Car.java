package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

import uk.ac.reading.jw021090.cargame.R;

/**
 * Created by Kikadass on 14/04/2016.
 */


public class Car {
    //Will store the image of a ball
    private Bitmap image;

    //The X and Y position of the ball on the screen (middle of ball)
    private float xPos = 0;
    private float yPos = 0;

    public Car(GameView mGameView){
        //Prepare the image so we can draw it on the screen (using a canvas)
        this.image = BitmapFactory.decodeResource (mGameView.getContext().getResources(), R.drawable.car1);
        Random rnd = new Random();


    }

    public float getX() {
        return xPos;
    }

    public float getY() {
        return yPos;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setY(float yPos) {
        this.yPos = yPos;
    }

    public void setX(float xPos) {
        this.xPos = xPos;
    }

    public void update(GameView mGameView){

    }

}
