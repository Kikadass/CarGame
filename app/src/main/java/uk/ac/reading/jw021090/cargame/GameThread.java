package uk.ac.reading.jw021090.cargame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

public class GameThread extends Thread {

	//Different mMode states
	public static final int STATE_LOSE = 1;
	public static final int STATE_PAUSE = 2;
	public static final int STATE_READY = 3;
	public static final int STATE_RUNNING = 4;
	public static final int STATE_WIN = 5;
	public static int FPS = 30;
	private double averageFPS;
	protected int mMode = 1;				//Control variable for the mode of the game (e.g. STATE_WIN)
	private boolean running = false;
	private SurfaceHolder surfaceHolder;	//The surface this thread (and only this thread) writes upon
	private Handler mHandler;				//the message handler to the View/Activity thread
	private Context mContext;				//Android Context - this stores almost all we need to know
	public GameView gameView;				//The view
	protected int canvasWidth = 1;			//We might want to extend this call - therefore protected
	protected int canvasHeight = 1;
	protected long mLastTime = 0;			//Last time we updated the game physics
	static final Integer monitor = 1;		//Used to ensure appropriate threading



	public GameThread(GameView gameView) {
		super();
		this.gameView = gameView;
		
		surfaceHolder = gameView.getHolder();
		mHandler = gameView.getmHandler();
		mContext = gameView.getContext();
		
	}
	
	/*
	 * Called when app is destroyed, so not really that important here
	 * But if (later) the game involves more thread, we might need to stop a thread, and then we would need this
	 * Dare I say memory leak...
	 */
	public void cleanup() {
		this.mContext = null;
		this.gameView = null;
		this.mHandler = null;
		this.surfaceHolder = null;
	}
	
	//Starting up the game
	public void doStart() {
		synchronized(monitor) {
			mLastTime = System.currentTimeMillis() + 100;

			setState(STATE_RUNNING);
		}
	}
	
	//The thread start
	@Override
	public void run() {
		long startTime;
		long timeMillis;
		long waitTime;
		long totalTime = 0;
		int frameCount =0;
		long targetTime = 1000/FPS;

		Canvas canvasRun;
		while (running) {
			canvasRun = null;
			startTime = System.nanoTime();


			try {
				canvasRun = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder){
					this.gameView.update();
					this.gameView.draw(canvasRun);
				}
			} catch(Exception e){}
			finally {
				if (canvasRun != null) {
					if(surfaceHolder != null)
						surfaceHolder.unlockCanvasAndPost(canvasRun);
				}
			}

			// to control the FPS
			timeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime-timeMillis;

			try{
				this.sleep(waitTime);
			}
			catch(Exception e){}

			totalTime += System.nanoTime()-startTime;
			frameCount++;
			if(frameCount == FPS){
				averageFPS = 1000/((totalTime/frameCount)/1000000);
				frameCount =0;
				totalTime = 0;
				System.out.println(averageFPS);
			}
		}
	}




	/*
	 * Game states
	 */
	public void pause() {
		synchronized (monitor) {
			if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
		}
	}
	
	public void unpause() {
		// Move the real time clock up to now
		synchronized (monitor) {
			mLastTime = System.currentTimeMillis();
		}
		setState(STATE_RUNNING);
	}	

	//Send messages to View/Activity thread
	public void setState(int mode) {
		synchronized (monitor) {
			setState(mode, null);
		}
	}

	public void setState(int mode, CharSequence message) {
		synchronized (monitor) {
			mMode = mode;

			if (mMode == STATE_RUNNING) {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString("text", "");
				b.putInt("viz", View.INVISIBLE);
				b.putBoolean("showAd", false);
				msg.setData(b);
				mHandler.sendMessage(msg);
			}
			else {
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();

				Resources res = mContext.getResources();
				CharSequence str = "";
				if (mMode == STATE_READY)
					str = res.getText(R.string.mode_ready);
				else
					if (mMode == STATE_PAUSE)
						str = res.getText(R.string.mode_pause);
					else
						if (mMode == STATE_LOSE)
							str = res.getText(R.string.mode_lose);
						else
							if (mMode == STATE_WIN) {
								str = res.getText(R.string.mode_win);
							}

				if (message != null) {
					str = message + "\n" + str;
				}

				b.putString("text", str.toString());
				b.putInt("viz", View.VISIBLE);

				msg.setData(b);
				mHandler.sendMessage(msg);
			}
		}
	}

	/*
	 * Getter and setter
	 */
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public int getMode() {
		return mMode;
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