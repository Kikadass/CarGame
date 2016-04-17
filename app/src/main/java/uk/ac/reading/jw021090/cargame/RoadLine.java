package uk.ac.reading.jw021090.cargame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Kikadass on 17/04/2016.
 */
public class RoadLine extends Object{
    private int y2Pos;
    private int x2Pos;

    public RoadLine(int xPos, int yPos, int x2Pos, int y2Pos) {
        super.xPos = xPos;
        super.yPos = yPos;
        this.x2Pos = x2Pos;
        this.y2Pos = y2Pos;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawLine(xPos, yPos, x2Pos, y2Pos, paint);
    }

    public boolean collide(Player player){
        // find the point of intersection of both lines only in x
        int ix = (int) ((player.yPos-yPos+xPos*((y2Pos-yPos)/(x2Pos-xPos)))/((y2Pos-yPos)/(x2Pos-xPos)));

        // if that point of intersecction is in the section of the line wanted and the player is there return true.
        if (ix < xPos && player.xPos > 122 && GameView.gameState == 1){
                player.xPos = 122;
        }

        if (ix > x2Pos && player.xPos < 22 && GameView.gameState == 2){
            player.xPos = 22;
        }

        if (xPos < ix && ix < x2Pos){
            if (player.xPos > ix-player.width && GameView.gameState == 1) {
                return true;
            }
            if (player.xPos < ix && GameView.gameState == 2) {
                return true;
            }
        }
        return false;
    }

    public void update(){
        yPos += GameView.SCREENSPEED;
        y2Pos += GameView.SCREENSPEED;
    }
}
