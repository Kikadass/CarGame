package uk.ac.reading.jw021090.cargame;

import android.graphics.Rect;

/**
 * Created by Kikadass on 15/04/2016.
 */
public abstract class Object {

    protected int xPos;
    protected int yPos;
    protected int dy;
    protected float dx;
    protected int height;
    protected int width;

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public Rect getRectangle(){
        return new Rect(xPos,yPos, xPos+width, yPos+height);
    }

}
