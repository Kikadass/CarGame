package uk.ac.reading.jw021090.cargame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ViewFlipper;


public class MainActivity extends Activity {

    private GameView gameView;
    public static boolean started = false;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);



        final Button play = (Button) findViewById(R.id.play);
        final ViewFlipper viewFlipper;viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                viewFlipper.showNext();
                startGame();
            }
        });

    }

    private void startGame() {

        //Set up a new game, we don't care about previous states
        started = true;
        gameView = new GameView(this, null);

    }

	/*
	 * Activity state functions
	 */


    // when middle button pressed
    @Override
    protected void onPause() {
        super.onPause();

        if (started) {
            if (gameView.getThread().isRunning()) {
                gameView.getThread().pause();
            }
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (started) {
            gameView.cleanup();
            gameView = null;
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
