package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;

import java.util.IdentityHashMap;

/**
 * Created by Kikadass on 14/04/2016.
 */

public class Background {
    private Bitmap image;
    private int xPos;
    private int yPos;
    private int dy;

    public Background(Bitmap image){
        this.image = image;
        this.dy = GameView.SCREENSPEED;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(image, xPos, yPos, null);

        if(yPos < GameView.HEIGHT){
            canvas.drawBitmap(image, xPos, yPos  - GameView.HEIGHT, null);
        }
    }



    public void update(){
        yPos += dy;
        if(yPos > GameView.HEIGHT){
            yPos = 0;
        }
    }
}