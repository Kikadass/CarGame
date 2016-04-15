package uk.ac.reading.jw021090.cargame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	public static final int WIDTH = 216;
	public static final int HEIGHT = 384;
	public static final int SCREENSPEED = 3;
	private long smokeTimer;
	private volatile GameThread thread;
	private Background background;
	private Player player;
	private ArrayList<Smoke> smoke;

	//private SensorEventListener sensorAccelerometer;

	//Handle communication from the GameThread to the View/Activity Thread
	private Handler mHandler;

	//Pointers to the views
	private TextView mScoreView;
	private TextView mStatusView;


	public GameView(Context context, AttributeSet attrs) {
		super(context);

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		thread = new GameThread(this);


		setFocusable(true);
	}

	//Used to release any resources.
	public void cleanup() {
		this.thread.setRunning(false);
		this.thread.cleanup();

		this.removeCallbacks(thread);
		thread = null;

		this.setOnTouchListener(null);

		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}

	/*
	 * Setters and Getters
	 */
	

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}


	
	/*
	 * Screen functions
	 */

	//ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(thread!=null) {
			if (!hasWindowFocus)
				thread.pause();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(thread!=null) {
			background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
			player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.car1), 32, 57);
			smoke = new ArrayList<Smoke>();

			smokeTimer = System.nanoTime();


			thread.setRunning(true);

			if(thread.getState() == Thread.State.NEW){
				//Just start the new thread
				thread.start();
			}
			else {
				if(thread.getState() == Thread.State.TERMINATED){
					//Start a new thread
					//Should be this to update screen with old game: new GameThread(this, thread);
					//The method should set all fields in new thread to the value of old thread's fields 
					thread.setRunning(true);
					thread.start();
				}
			}
		}
	}
	
	//Always called once after surfaceCreated. Tell the GameThread the actual size
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/*
	 * Need to stop the GameThread if the surface is destroyed
	 * Remember this doesn't need to happen when app is paused on even stopped.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		
		boolean retry = true;
		if(thread!=null) {
			thread.setRunning(false);
		}

		int counter = 0;

		//join the thread with this thread
		while (retry && counter < 1000) {
			try {
				thread.setRunning(false);
				if(thread!=null) {
					thread.join();
				}
				retry = false;
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
		}
	}

	@Override
	public  void draw(Canvas canvas){
		super.draw(canvas);
		final float scaleX = getWidth()/(float)WIDTH;
		final float scaleY = getHeight()/(float)HEIGHT;

		if(canvas!=null) {
			final int saved = canvas.save();
			canvas.scale(scaleX, scaleY);
			background.draw(canvas);
			player.draw(canvas);

			for(Smoke sm: smoke){
				sm.draw(canvas);
			}

			canvas.restoreToCount(saved);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		// if player touches the screen
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			// if he was not playing--> start playing
			if(!player.isPlaying()){
				player.setPlaying(true);
			}
			//else depending on where the finger is will be left or right

			//left
			else if(event.getX() < getWidth()/2){
				player.setLeft(true);
			}
			else if(event.getX() >= getWidth()/2){
				player.setRight(true);
			}
			System.out.println(event.getX() + "   " + event.getY());

		}



		// if player stops touching the screen stop turning
		if(event.getAction() == MotionEvent.ACTION_UP){
			player.setLeft(false);
			player.setRight(false);
			return true;
		}

		return true;

	}

	public void update(){
		if (player.isPlaying()) {
			player.update();
			background.update();

			// create Smoke every so often
			long elapsed = (System.nanoTime() - smokeTimer)/1000000;
			if(elapsed > 150){
				smoke.add(new Smoke(player.getxPos()+player.width/2-5, player.getyPos()+player.height));
				smokeTimer = System.nanoTime();
			}

			for(int i = 0; i < smoke.size(); i++){
				smoke.get(i).update();

				if(smoke.get(i).getyPos() > GameView.HEIGHT){
					smoke.remove(i);
				}
			}
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
