package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Kikadass on 15/04/2016.
 */
public class Bullet extends Object {
    private Rect rect;

    // Which way is it shooting
    public final int UP = 0;
    public final int DOWN = 1;

    // Going nowhere
    int heading = -1;
    int speed =  350;

    private int width = 1;
    private int height;

    public Bullet() {
        height = 5;
        visible = false;
        rect = new Rect();
    }

    public Rect getRect(){
        return  rect;
    }

    public int getImpactPointY(){
        if (heading == DOWN){
            return yPos + height;
        }else{
            return  yPos;
        }

    }

    public boolean shoot(int startX, int startY, int direction) {
        if (!visible) {
            xPos = startX;
            yPos = startY;
            heading = direction;
            visible = true;
            return true;
        }

        // Bullet already active
        return false;
    }

    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        // Draw the players bullet if active
        if(this.isVisible()){
            canvas.drawRect(this.getRect(), paint);
        }
    }

    public void update(int fps){
        // Just move up or down
        if(heading == UP){
            yPos = yPos - speed / fps;
        }else{
            yPos = yPos + speed / fps;
        }

        // Update rect
        rect.left = xPos;
        rect.right = xPos + width;
        rect.top = yPos;
        rect.bottom = yPos + height;



    }
}
