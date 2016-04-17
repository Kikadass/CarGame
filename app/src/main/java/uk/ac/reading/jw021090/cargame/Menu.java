package uk.ac.reading.jw021090.cargame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by Kikadass on 17/04/2016.
 */
public class Menu extends Object {
    private Bitmap image;
    private Bitmap active_menu;
    private int active_xPos;
    private int active_yPos;

    public Menu (Bitmap image, Bitmap active_menu, int xPos){
        this.image = image;
        this.active_menu = active_menu;
        this.xPos = xPos;
        yPos = 0;
        setVisible(false);
        // difference between the buttons
        height = 30;
        active_xPos =  GameView.WIDTH / 2 - 50;
        active_yPos = GameView.HEIGHT / 2 - 40;
    }

    public int getActive_xPos(){
        return active_xPos;
    }

    public int getActive_yPos(){
        return active_yPos;
    }

    public void draw(Canvas canvas){
        Paint alphaPaint = new Paint();
        alphaPaint.setAlpha(200);
        // now lets draw using alphaPaint instance
        canvas.drawBitmap(image, xPos, yPos, alphaPaint);

        if (isVisible()){
            canvas.drawBitmap(active_menu, GameView.WIDTH/2 - 75, GameView.HEIGHT/2 - 75, null);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setTextSize(15);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("CONTINUE", active_xPos, active_yPos, paint);
            canvas.drawText("RESTART", active_xPos, active_yPos+height, paint);
        }
    }
}
