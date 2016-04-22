package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * Created by Kikadass on 17/04/2016.
 */
public class Controls extends Object {
    private Bitmap left, right;
    private Bitmap machinegun;

    public Controls (Bitmap left, Bitmap right, Bitmap machinegun) {
        width = 60;
        this.left = Bitmap.createScaledBitmap(left, width, width, false);
        this.right = Bitmap.createScaledBitmap(right, width, width, false);
        this.machinegun = Bitmap.createScaledBitmap(machinegun, 20, 56, false);
        yPos = GameView.HEIGHT - width - 10;
    }

    public void draw(Canvas canvas, boolean shooting){
        canvas.drawBitmap(left, 0, yPos, null);
        canvas.drawBitmap(right, GameView.WIDTH - width, yPos, null);
        if (shooting){
            canvas.drawBitmap(machinegun, 0, yPos - 70, null);
            canvas.drawBitmap(machinegun , GameView.WIDTH - 25, yPos - 70, null);
        }
    }

}
