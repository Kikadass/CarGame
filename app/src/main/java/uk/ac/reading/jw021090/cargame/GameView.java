package uk.ac.reading.jw021090.cargame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	public static final int WIDTH = 216;
	public static final int HEIGHT = 384;
	public static final int SCREENSPEED = 3;
	public static int gameState = 0;
	public static boolean changingState = false;
	private long smokeTimer;
	private long carsTimer;
	private long startReset = 0;
	private volatile GameThread thread;
	private Background background;
	private Player player;
	private ArrayList<Smoke> smoke;
	private Bullet bullet;
	private ArrayList<Car> cars;
	private Random rnd = new Random();
	private boolean newGame = false;
	private Explosion playerExplosion;
	private boolean dead = false;
	private int best = 0;

	//Handle communication from the GameThread to the View/Activity Thread
	private Handler mHandler;


	public GameView(Context context, AttributeSet attrs) {
		super(context);

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		thread = new GameThread(this);


		setFocusable(true);
	}

	public void startGame(){
		// updating best score
		gameState = 0;
		changingState = false;
		smoke.clear();
		cars.clear();
		player.resetScore();
		player.setxPos(112);
		player.resetDx();
		player.setVisible(true);
		bullet.setInactive();
		background.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.background));
		background.resetChanging();

		newGame = true;
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
			gameState = 0;
			player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.car1), 32, 57);
			smoke = new ArrayList<Smoke>();
			bullet = new Bullet();
			cars = new ArrayList<Car>();

			smokeTimer = System.nanoTime();
			carsTimer = System.nanoTime();

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
				thread = null;
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

			if (player.getScore() > 20 && gameState == 0){
				if (!background.isChanged()) {
					background.setNextImage(BitmapFactory.decodeResource(getResources(), R.drawable.background3));
					background.setTempImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroundto3));
					changingState = true;
				}
				else{
					changingState = false;
					gameState = 1;
				}
			}
			background.draw(canvas);


			bullet.draw(canvas);

			for(Smoke sm: smoke){
				sm.draw(canvas);
			}

			for (Car c: cars){
				c.draw(canvas);
			}

			if (player.isVisible()) {
				player.draw(canvas);
			}

			if(!player.isVisible()){
				playerExplosion.draw(canvas);
			}

			drawMessage(canvas);

			canvas.restoreToCount(saved);
		}
	}

	public void drawMessage(Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(20);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		canvas.drawText("DISTANCE: " + (player.getScore()), 30, 20, paint);

		if (!player.isPlaying()) {
			if (player.getScore() > best){
				best = player.getScore();
			}
			canvas.drawText("BEST: " + best, WIDTH / 7, HEIGHT / 2 - 30, paint);
		}

		if(!player.isPlaying() && dead) {
			Paint paint1 = new Paint();
			paint1.setColor(Color.RED);
			paint1.setTextSize(20);
			paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
			canvas.drawText("PRESS TO START", WIDTH / 7, HEIGHT / 2, paint1);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		// if player touches the screen
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			// if he was not playing--> start playing
			if(!player.isPlaying() && newGame){
				player.setPlaying(true);
				dead = false;
			}
			//else depending on where the finger is will be left or right

			//left
			else if(event.getY() > getHeight() - getHeight()/ 7) {
				if (event.getX() < getWidth() / 2) {
					player.setLeft(true);
				} else if (event.getX() >= getWidth() / 2) {
					player.setRight(true);
				}
			}
			else if(event.getY() < getHeight() - getHeight()/ 7) {
				// Shots fired
				if(bullet.shoot(player.getxPos()+ player.width/2,player.getyPos(),bullet.UP)){
					//soundPool.play(shootID, 1, 1, 0, 0, 1);
				}
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

	public boolean collision(Object a, Object b){
		if (Rect.intersects(a.getRectangle(), b.getRectangle())){
			return true;
		}
		return false;
	}

	public void update(){
		if (player.isPlaying()) {
			player.update();
			background.update();
			// Update the players bullet
			if(bullet.getStatus()){
				bullet.update(GameThread.FPS);
			}
			// Has the player's bullet hit the top of the screen
			if(bullet.getImpactPointY() < 0){
				bullet.setInactive();
			}

			// bullets colliding:
			/*
			if (RectF.intersects(bullet.getRect(), invaders[i].getRect())) {
				invaders[i].setInvisible();
				soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
				bullet.setInactive();
				score = score + 10;
			}
			*/

			// create cars every 2 seconds-the score divided by 4
			long elapsedCars = (System.nanoTime() - carsTimer)/1000000;
			if (elapsedCars > (2000 - player.getScore()/4)){
				int random = rnd.nextInt(3);
				switch (random){
					case 0:
						cars.add(new Car(BitmapFactory.decodeResource(getResources(), R.drawable.car2), 32, 60, player.getScore()));
						break;
					case 1:
						cars.add(new Car(BitmapFactory.decodeResource(getResources(), R.drawable.car3), 34, 60, player.getScore()));
						break;
					case 2:
						cars.add(new Car(BitmapFactory.decodeResource(getResources(), R.drawable.police_car), 32, 60, player.getScore()));
						break;
					default:
						break;
				}
				// reset timer
				carsTimer = System.nanoTime();
			}

			//loop through every car and check collision and remove
			for(int i = 0; i < cars.size();i++) {
				//update cars
				cars.get(i).update();

				// if cars collide with player pause game and restart it
				if(collision(cars.get(i),player)){
					cars.remove(i);
					player.setPlaying(false);
					player.setVisible(false);
					newGame = false;
					startReset = System.nanoTime();
					playerExplosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion), player.getxPos(), player.getyPos(), 32, 32, 16);

					break;
				}

				// if two cars collide, set the same speed in order not to go on top of each other
				for(int j = 0; j < cars.size(); j++) {
					if (i != j) {
						if (collision(cars.get(i), cars.get(j))) {
							if (cars.get(i).getSpeed() > cars.get(j).getSpeed()){
								cars.get(i).setyPos(cars.get(i).getyPos()-cars.get(i).getSpeed()*2);
								cars.get(i).setSpeed(cars.get(j).getSpeed());
							}
							else {
								cars.get(j).setyPos(cars.get(j).getyPos()-cars.get(j).getSpeed()*2);
								cars.get(j).setSpeed(cars.get(i).getSpeed());
							}
						}
					}
				}

				//remove car if it is way off the screen
				if(cars.get(i).getxPos()<-100){
					cars.remove(i);
					break;
				}
			}

			// add smoke on timer
			long elapsedSmoke = (System.nanoTime() - smokeTimer)/1000000;
			if(elapsedSmoke > 150){
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

		else {

			//player exploded
			if (playerExplosion != null){
				playerExplosion.update();
				long elapsedExplosion = (System.nanoTime() - startReset)/1000000;
				if (elapsedExplosion > 2000 && !newGame) {
					startGame();
					dead = true;
					playerExplosion = null;
				}
			}
			else if(!newGame) {
				startGame();
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
