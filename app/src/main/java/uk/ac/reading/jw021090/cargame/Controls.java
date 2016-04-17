package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by Kikadass on 17/04/2016.
 */
public class Controls extends Object {
    private Bitmap left, right;

    public Controls (Bitmap left, Bitmap right) {
        width = 60;
        this.left = Bitmap.createScaledBitmap(left, width, width, false);
        this.right = Bitmap.createScaledBitmap(right, width, width, false);;
        yPos = GameView.HEIGHT - width - 10;
    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(left, 0, yPos, null);
        canvas.drawBitmap(right, GameView.WIDTH - width, yPos, null);
    }

}
