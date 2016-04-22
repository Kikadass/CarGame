package uk.ac.reading.jw021090.cargame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        final Intent intent1 = new Intent(this, GameActivity.class);
        final Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                final String[] levels = {"Level 1", "Level 2", "Level 3"};

                dialogBuilder.setTitle("Level");
                dialogBuilder.setSingleChoiceItems(levels, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent1.putExtra("level", Integer.toString(i));
                        startActivity(intent1);
                        finish();
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();

            }
        });

        final Intent intent2 = new Intent(this, ScoreActivity.class);
        final Button scores = (Button) findViewById(R.id.scores);
        scores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                intent2.putExtra("isOnline","false");
                startActivity(intent2);
                finish();
            }
        });

        final Button onlineScores = (Button) findViewById(R.id.onlineScores);
        onlineScores.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (isConnected()) {
                    intent2.putExtra("isOnline","true");
                    startActivity(intent2);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    private boolean isConnected(){
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
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
