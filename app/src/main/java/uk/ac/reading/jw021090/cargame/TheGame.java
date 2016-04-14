/*
package uk.ac.reading.jw021090.cargame;


//Other parts of the android libraries that we use
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;

public class TheGame extends GameThread{

    private ArrayList<Ball> balls = new ArrayList<Ball>();
    private int NUMBEROFBALLS = 3;

    //This is run before anything else, so we can prepare things here
    public TheGame(GameView gameView) {
        //House keeping
        super(gameView);

        //Prepare the image so we can draw it on the screen (using a canvas)
        for (int i = 0; i < NUMBEROFBALLS; i++) {
            balls.add(new Ball(gameView));
        }
    }

    //This is run before a new game (also after an old game)
    @Override
    public void setupBeginning() {

        //Place the ball in the middle of the screen.
        //mBall.Width() and mBall.getHeigh() gives us the height and width of the image of the ball
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).setX(mCanvasWidth / 2);
            balls.get(i).setY(mCanvasHeight / 2);
        }
    }

    @Override
    protected void doDraw(Canvas canvas) {
        //If there isn't a canvas to draw on do nothing
        //It is ok not understanding what is happening here
        if(canvas == null) return;

        super.doDraw(canvas);

        //draw the image of the ball using the X and Y of the ball
        //drawBitmap uses top left corner as reference, we use middle of picture
        //null means that we will use the image without any extra features (called Paint)
        for (int i = 0; i < balls.size(); i++) {
            canvas.drawBitmap(balls.get(i).getImage(), balls.get(i).getX() - balls.get(i).getImage().getWidth() / 2, balls.get(i).getY() - balls.get(i).getImage().getHeight() / 2, null);
        }
    }


    // This is the game
    //@Override
    private void updatePhysics() {
        wallCollisions();
        System.out.print(mGameView.getLeft());
    }

    private void wallCollisions(){

    }

    //This is run whenever the phone is touched by the user

	@Override
	protected void actionOnTouch(float x, float y) {
		//Increase/decrease the speed of the ball making the ball move towards the touch

	}

	

	//This is run whenever the phone moves around its axises 
	@Override
	protected void actionWhenPhoneMoved(float xDirection, float yDirection, float zDirection) {
		/*
		Increase/decrease the speed of the ball.
		If the ball moves too fast try and decrease 70f
		If the ball moves too slow try and increase 70f


		//mBallSpeedX = mBallSpeedX + 70f * xDirection;
		//mBallSpeedY = mBallSpeedY - 70f * yDirection;
	}


    //This is run just before the game "scenario" is printed on the screen
    @Override
    protected void updateGame(float secondsElapsed) {
        //Move the ball's X and Y using the speed (pixel/sec)
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).update(mGameView, secondsElapsed);
        }
    }
}

// This file is part of the course "Begin Programming: Build your first mobile game" from futurelearn.com
// Copyright: University of Reading and Karsten Lundqvist
// It is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// It is is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// 
// You should have received a copy of the GNU General Public License
// along with it.  If not, see <http://www.gnu.org/licenses/>.
*/