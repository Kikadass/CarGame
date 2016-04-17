package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.Image;

import java.util.IdentityHashMap;

/**
 * Created by Kikadass on 14/04/2016.
 */

public class Background {
    private Bitmap image;
    private Bitmap tempImage;
    private Bitmap nextImage;
    public static boolean changed = false;
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

    public void resetChanging(){
        changing = false;
        changed = false;
    }

    public void draw(Canvas canvas){
        if (changed) {
            canvas.drawBitmap(tempImage, xPos, yPos, null);
            if (yPos > 50){
                GameView.roadLine.update();
            }
        }
        else canvas.drawBitmap(image, xPos, yPos, null);

        if(yPos < GameView.HEIGHT){
            if (changing) {
                canvas.drawBitmap(tempImage, xPos, yPos - GameView.HEIGHT, null);
            }
            else canvas.drawBitmap(image, xPos, yPos - GameView.HEIGHT, null);
        }
    }

    public void update(){
        yPos += dy;
        if(yPos > GameView.HEIGHT){
            yPos = 0;
            if (GameView.changingState){
                if (changing){
                    changing = false;
                    changed = true;
                    image = nextImage;
                    if (GameView.gameState == 0) GameView.roadLine = new RoadLine(154, 45, 193, 170);
                    if (GameView.gameState == 1) GameView.roadLine = new RoadLine(23, 170, 62, 45);

                }
                else changing = true;
            }
            else changed = false;
        }
    }
}