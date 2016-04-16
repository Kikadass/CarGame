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
    private Bitmap tempImage;
    private Bitmap nextImage;
    private boolean changed = false;
    private boolean changing = false;
    private int xPos = 0;
    private int yPos;
    private int dy;

    public Background(Bitmap image){
        this.image = image;
        this.nextImage = image;
        this.dy = GameView.SCREENSPEED;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

    public void setNextImage(Bitmap image){
        this.nextImage = image;
    }

    public void setTempImage(Bitmap image){
        this.tempImage = image;
    }

    public boolean isChanged(){
        return changed;
    }

    public void draw(Canvas canvas){
        if (changed) {
            canvas.drawBitmap(tempImage, xPos, yPos, null);
        }
        else canvas.drawBitmap(image, xPos, yPos, null);

        if(yPos < GameView.HEIGHT){
            System.out.println("changed: " + changed + " Gameview " + GameView.changingState);
            if (changing) {
                canvas.drawBitmap(tempImage, xPos, yPos - GameView.HEIGHT, null);
            }
            else canvas.drawBitmap(image, xPos, yPos - GameView.HEIGHT, null);
        }
    }



    public void update(){
        yPos += dy;
        if(yPos > GameView.HEIGHT){
            System.out.println("ASDASD");
            yPos = 0;
            if (GameView.changingState){
                if (changing){
                    changing = false;
                    changed = true;
                    image = nextImage;
                }
                else changing = true;
            }
            else changed = false;
        }
    }
}