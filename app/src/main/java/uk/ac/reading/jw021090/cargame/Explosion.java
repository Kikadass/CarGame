package uk.ac.reading.jw021090.cargame;
import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by Kikadass on 16/04/2016.
 */

public class Explosion {
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private int row;
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Explosion(Bitmap res, int xPos, int yPos, int w, int h, int numFrames){
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = w;
        this.height = h;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i<image.length; i++){
            if(i%4 == 0 && i > 0)row++;
            image[i] = Bitmap.createBitmap(spritesheet, (i-(4*row))*width, row*height, width, height);
        }
        animation.setFrames(image);
        animation.setDelay(20);
    }

    public void draw(Canvas canvas){
        if(!animation.isPlayedOnce()){
            canvas.drawBitmap(animation.getImage(), xPos, yPos, null);
        }

    }

    public void update(){
        if(!animation.isPlayedOnce()){
            animation.update();
        }
    }
}