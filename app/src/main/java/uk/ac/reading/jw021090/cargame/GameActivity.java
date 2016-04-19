package uk.ac.reading.jw021090.cargame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ViewFlipper;

import java.io.File;

/**
 * Created by Kikadass on 19/04/2016.
 */
public class GameActivity extends Activity{
    private GameView gameView;
    public static boolean started = false;
    private SharedPreferences sharedPreferences;
    public static File files;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        files = getFilesDir();

        sharedPreferences = getPreferences(MODE_PRIVATE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.game_layout);


        startGame();

    }

    private void startGame() {

        //Set up a new game, we don't care about previous states
        started = true;
        gameView = new GameView(this, null);
        gameView.setHighScore(sharedPreferences.getInt(getString(R.string.high_score), 0));

    }

	/*
	 * Activity state functions
	 */

    // when middle button pressed
    @Override
    protected void onPause() {
        System.out.println("PAUSE");

        super.onPause();

        if (started) {
            if (gameView.getThread().isRunning()) {
                sharedPreferences.edit().putInt(getString(R.string.high_score), gameView.getHighScore()).commit();
                gameView.getThread().pause();
            }
        }
    }


    @Override
    protected void onDestroy() {
        System.out.println("DESTROY");
        super.onDestroy();

        if (started) {
            gameView.cleanup();
            gameView = null;
        }

        sharedPreferences = null;
        files = null;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }



}
