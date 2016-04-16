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
    protected boolean visible = true;


    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public Rect getRectangle(){
        return new Rect(xPos,yPos, xPos+width, yPos+height);
    }

    public boolean isVisible(){return visible;}

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setVisible(boolean b){
        visible = b;
    }

}
