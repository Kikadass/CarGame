package uk.ac.reading.jw021090.cargame;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Kikadass on 15/04/2016.
 */


public class Smoke extends Object{
    public int r;

    public Smoke(int xPos, int yPos) {
        r = 3;
        super.xPos = xPos;
        super.yPos = yPos;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(xPos-r, yPos-r, r, paint);
        canvas.drawCircle(xPos-r-1, yPos-r+2,r,paint);
        canvas.drawCircle(xPos-r, yPos-r+4, r, paint);
    }

    public void update() {
        yPos += 7;
    }
}