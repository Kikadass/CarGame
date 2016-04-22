package uk.ac.reading.jw021090.cargame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	public static final int WIDTH = 216;
	public static final int HEIGHT = 384;
	public static final int SCREENSPEED = 3;
	public static int gameState = 0;
	public static boolean changingState = false;
    public static RoadLine roadLine;
    private long smokeTimer;
	private long carsTimer;
	private long startReset = 0;
	private volatile GameThread thread;
    private boolean newGame = false;
    private boolean paused = false;
    private boolean dead = false;
    private int highScore = 0;
    private float scaleX;
    private float scaleY;
    private Background background;
	private Player player;
	private ArrayList<Smoke> smoke;
	private Bullet bullet;
	private ArrayList<Car> cars;
	private Random rnd = new Random();
    private Explosion playerExplosion;
	private Controls controls;
    private Menu menu;
	//Handle communication from the GameThread to the View/Activity Thread
    private Handler mHandler;
	private Context context;
    private Level level;


	public GameView(Context context, AttributeSet attrs) {
		super(context);

		this.context = context;

		//Get the holder of the screen and register interest
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		thread = new GameThread(this);


		setFocusable(true);
	}

    public GameThread getThread(){
        return thread;
    }

	public void startGame(){
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

        menu.setVisible(false);
        paused = false;
		newGame = true;
	}

    private boolean isConnected(){
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(thread!=null) {
            String url = "http://www.kidscoding.tk/Levels.txt";
            File file = new File(context.getFilesDir(), "Levels.txt");
            ReadLevels reader = new ReadLevels(url, file);
            if (isConnected()){
                // if file doesn't exist --> create it:
                reader.execute(true);
            }
            else{
                new WritingScore(url, file, false);
                reader.execute(false);
            }

            List<Level> levelList = null;

            // make sure that the reading has finished before continuing
            while (reader.getLevelList().size() < 3) {
                levelList = reader.getLevelList();
            }

            level = levelList.get(GameActivity.level);


            scaleX = getWidth()/(float)WIDTH;
            scaleY = getHeight()/(float)HEIGHT;
            background = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.background));
            gameState = 0;
            player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player), 32, 57);
            controls = new Controls(BitmapFactory.decodeResource(getResources(), R.drawable.wheel_left), BitmapFactory.decodeResource(getResources(), R.drawable.wheel_right));
            menu = new Menu(BitmapFactory.decodeResource(getResources(), R.drawable.menu), BitmapFactory.decodeResource(getResources(), R.drawable.active_menu1), WIDTH - 45);
            smoke = new ArrayList<Smoke>();
            bullet = new Bullet();
            cars = new ArrayList<Car>();

            smokeTimer = System.nanoTime();
            carsTimer = System.nanoTime();

            thread.unPause();

            if(thread.getState() == Thread.State.NEW){
                //Just start the new thread
                thread.start();
            }
            else {
                if(thread.getState() == Thread.State.TERMINATED){
                    //Start a new thread
                    //Should be this to update screen with old game: new GameThread(this, thread);
                    //The method should set all fields in new thread to the value of old thread's fields
                    thread = new GameThread(this);
                    thread.start();
                }
            }
        }
        else {
            thread.unPause();

            if(thread.getState() == Thread.State.NEW){
                //Just start the new thread
                thread.start();
            }
            else {
                if(thread.getState() == Thread.State.TERMINATED){
                    //Start a new thread
                    //Should be this to update screen with old game: new GameThread(this, thread);
                    //The method should set all fields in new thread to the value of old thread's fields
                    thread = new GameThread(this);
                    thread.start();
                }
            }
        }
    }


    //Used to release any resources.
	public void cleanup() {
        System.out.println("CLEANUP");

        this.thread.pause();
		this.thread.cleanup();

		this.removeCallbacks(thread);
		thread = null;

		this.setOnTouchListener(null);

		SurfaceHolder holder = getHolder();
		holder.removeCallback(this);
	}

	
	/*
	 * Screen functions
	 */

	//ensure that we go into pause state if we go out of focus
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {

        if(thread!=null) {
            if (thread.isRunning()) {
                if (!hasWindowFocus) {
                    pause();
                    thread.pause();
                }
            }
            else if (hasWindowFocus && GameActivity.started) {
                thread.unPause();
            }
        }
	}


	//Always called once after surfaceCreated. Tell the GameThread the actual size
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

	/*
	 * Need to stop the GameThread if the surface is destroyed
	 * Remember this doesn't need to happen when app is paused or even stopped.
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
        startGame();

    }

    public int getHighScore(){
        return highScore;
    }

    public void setHighScore(int score){
        highScore = score;
    }

    public void saveScore(int highScore){
		String url = "http://www.kidscoding.tk/Scores.txt";
		File file = new File(context.getFilesDir(), "Scores.txt");

        ReadScore reader = new ReadScore(url, file);
        reader.execute(false);
        List<ScoreModel> scores = null;

        int size = 0;


        // make sure that the reading has finished before continuing
        while (size == 0) {
            size = reader.getScoreModelList().size();
            scores = reader.getScoreModelList();

		}



        boolean added = false;
		for (int i = 0; i < scores.size(); i++) {
			if (highScore >= scores.get(i).getScore()) {
				if (scores.size() < 10){
					scores.add(new ScoreModel(1));
				}

				for (int j = scores.size()-1; j > i; j--){
					scores.get(j).setModel(scores.get(j - 1));

				}

				scores.get(i).setModel(new ScoreModel(highScore));



				added = true;
				break;
			}
		}
		if (!added && scores.size() < 10){
			scores.add(new ScoreModel(highScore));
		}

		new WritingScore(url, file, true).execute(scores);
    }

    public void pause(){
        menu.setVisible(true);
        player.setPlaying(false);
        paused = true;
    }

    public void unPause(){
        paused = false;
        player.setPlaying(true);
        menu.setVisible(false);
    }

	public void die(){
		player.setPlaying(false);
		player.setVisible(false);
		newGame = false;
		startReset = System.nanoTime();
		playerExplosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion), player.getxPos(), player.getyPos(), 32, 32, 16);
	}


	@Override
	public  void draw(Canvas canvas){
        super.draw(canvas);

		if(canvas!=null) {
			final int saved = canvas.save();

            canvas.scale(scaleX, scaleY);

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

			if(!player.isVisible()) {
                playerExplosion.draw(canvas);
			}

			controls.draw(canvas);

			drawMessage(canvas);

            canvas.restoreToCount(saved);

		}
	}

	public void drawMessage(Canvas canvas){
        Resources res = getResources();

		menu.draw(canvas, res);

		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(20);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		canvas.drawText(res.getString(R.string.current_score) + ": " + (player.getScore()), 30, 20, paint);

		if (!player.isPlaying() && !paused) {
			if (player.getScore() > highScore) {
                highScore = player.getScore();
                saveScore(highScore);
            }
            canvas.drawText(res.getString(R.string.high_score) + ": " + highScore, WIDTH / 7, HEIGHT / 2 - 30, paint);
		}

		if(!player.isPlaying() && dead) {
            canvas.drawText(res.getString(R.string.press_start), WIDTH / 7, HEIGHT / 2, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		// if player touches the screen
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			// if he was not playing--> start playing
			if(!player.isPlaying() && newGame && !paused){
				player.setPlaying(true);
				dead = false;
			}
			//else depending on where the finger is will be left or right

			//left
			else if(event.getY() > getHeight() - getHeight()/ 5) {
				if (event.getX() < getWidth() / 3) {
					player.setLeft(true);
				} else if (event.getX() >= getWidth() - getWidth() / 3) {
					player.setRight(true);
				}
			}
            else if(event.getY() < getHeight()/6 && event.getX() > getWidth() - getWidth()/4) {
                pause();
            }


            if (paused){
                boolean x = (event.getX()/scaleX > menu.getActive_xPos() && event.getX()/scaleX < menu.getActive_xPos() + 110);

                // continue
                if(event.getY()/scaleY > menu.getActive_yPos()-15 && event.getY()/scaleY < menu.getActive_yPos()+5
                    && x) {
                    unPause();
                }
                else if(event.getY()/scaleY > menu.getActive_yPos()+menu.height-15 && event.getY()/scaleY < menu.getActive_yPos()+menu.height+5
                        && x) {
                    startGame();
                }
            }
            /*
			else if(event.getY() < getHeight() - getHeight()/ 5) {
				// Shots fired
				if(bullet.shoot(player.getxPos()+ player.width/2,player.getyPos(),bullet.UP)){
					//soundPool.play(shootID, 1, 1, 0, 0, 1);
				}
			}
			*/
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
		if (player.isPlaying() && thread.isRunning()) {
			player.update();

			// BACKGROUND
			// from one point the road passes from 4 lanes into 3
			if (player.getScore() > 10 && gameState == 0){
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
			// from one point the road passes from 3 lanes into 2
			if (player.getScore() > 200 && gameState == 1){
				if (!background.isChanged()) {
					background.setNextImage(BitmapFactory.decodeResource(getResources(), R.drawable.background2));
					background.setTempImage(BitmapFactory.decodeResource(getResources(), R.drawable.backgroundto2));
					changingState = true;
				}
				else{
					changingState = false;
					gameState = 2;
				}
			}
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

            // if player collides with wal while changing track dies
            if (background.isChanged()) {
                if (roadLine.collide(player)){
                    die();
                }
            }


			// create cars every 2 seconds-the score divided by 4
			long elapsedCars = (System.nanoTime() - carsTimer)/1000000;
			if (elapsedCars > (5000 - player.getScore()/10)){
				int random = rnd.nextInt(3);
				switch (random){
					case 0:
						cars.add(new Car(BitmapFactory.decodeResource(getResources(), R.drawable.car1_down), 32, 60, player.getScore()));
						break;
					case 1:
						cars.add(new Car(BitmapFactory.decodeResource(getResources(), R.drawable.car2_down), 34, 60, player.getScore()));
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
                if (collision(cars.get(i), player)) {
                    die();
                    break;
                }

                // if two cars collide, set the same speed in order not to go on top of each other
                for (int j = 0; j < cars.size(); j++) {
                    if (i != j) {
                        if (collision(cars.get(i), cars.get(j))) {
                            if (cars.get(i).getSpeed() > cars.get(j).getSpeed()) {
                                cars.get(i).setyPos(cars.get(i).getyPos() - cars.get(i).getSpeed() * 2);
                                cars.get(i).setSpeed(cars.get(j).getSpeed());
                            } else {
                                cars.get(j).setyPos(cars.get(j).getyPos() - cars.get(j).getSpeed() * 2);
                                cars.get(j).setSpeed(cars.get(i).getSpeed());
                            }
                        }
                    }
                }

                //remove car if it is way off the screen
                if (cars.get(i).getxPos() < -100) {
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
