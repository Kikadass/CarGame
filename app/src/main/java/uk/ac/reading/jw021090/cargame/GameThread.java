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

	public static int FPS = 30;
	private double averageFPS;
	private boolean running = false;
	private boolean ended = false;
	private SurfaceHolder surfaceHolder;	//The surface this thread (and only this thread) writes upon
	private Context mContext;				//Android Context - this stores almost all we need to know
	public GameView gameView;				//The view

	static final Integer monitor = 1;		//Used to ensure appropriate threading



	public GameThread(GameView gameView) {
		super();
		this.gameView = gameView;

		surfaceHolder = gameView.getHolder();
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
		this.surfaceHolder = null;
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
		while (!ended) {
			while (running) {

				canvasRun = null;
				startTime = System.nanoTime();


				try {
					canvasRun = surfaceHolder.lockCanvas();
					synchronized (surfaceHolder) {
						this.gameView.update();
						this.gameView.draw(canvasRun);

					}
				} catch (Exception e) {
				} finally {
					if (canvasRun != null) {
						if (surfaceHolder != null)
							surfaceHolder.unlockCanvasAndPost(canvasRun);
					}
				}

				// to control the FPS
				timeMillis = (System.nanoTime() - startTime) / 1000000;
				waitTime = targetTime - timeMillis;

				try {
					this.sleep(waitTime);
				} catch (Exception e) {
				}

				totalTime += System.nanoTime() - startTime;
				frameCount++;
				if (frameCount == FPS) {
					averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
					frameCount = 0;
					totalTime = 0;
					System.out.println(averageFPS);
				}
			}
		}
	}


	/*
	 * Game states
	 */
	public void pause() {
		this.running = false;
	}

	public void unPause() {
		this.running = true;
	}


	/*
	 * Getter and setter
	 */
	public boolean isRunning() {
		return running;
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