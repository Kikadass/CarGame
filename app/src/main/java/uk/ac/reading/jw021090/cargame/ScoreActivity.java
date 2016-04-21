package uk.ac.reading.jw021090.cargame;

/**
 * Created by Kikadass on 19/04/2016.
 */
    import android.content.Intent;
    import android.app.Activity;
    import android.app.ProgressDialog;
    import android.content.Context;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.gson.Gson;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.io.OutputStreamWriter;
    import java.io.Writer;
    import java.net.HttpURLConnection;
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.List;

public class ScoreActivity extends Activity {

    private final String URL_TO_HIT = "http://www.kidscoding.tk/Scores.txt";
    private ListView lvScores;
    private ProgressDialog dialog;
    private boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);

        Intent intent = getIntent();
        String isOnline = intent.getStringExtra("isOnline");

        if (isOnline.equals("true")) this.isOnline = true;
        else this.isOnline = false;

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        lvScores = (ListView)findViewById(R.id.lvScores);

        read();
    }

    private void read(){

        File file = new File(getFilesDir(), "Scores.txt");
        ReadScore reader =  new ReadScore(URL_TO_HIT, file);
        reader.execute(isOnline);
        ScoreAdapter adapter = null;

        // make sure that the reading has finished before continuing
        while (reader.getScoreModelList().size() == 0) {
            adapter = new ScoreAdapter(getApplicationContext(), R.layout.row, reader.getScoreModelList());
        }
        lvScores.setAdapter(adapter);
    }

    public class ScoreAdapter extends ArrayAdapter{

        private List<ScoreModel> scoreModelList;
        private int resource;
        private LayoutInflater inflater;
        public ScoreAdapter(Context context, int resource, List<ScoreModel> objects) {
            super(context, resource, objects);
            scoreModelList = objects;


            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if(convertView == null){
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.score = (TextView)convertView.findViewById(R.id.score);
                holder.date = (TextView)convertView.findViewById(R.id.date);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.score.setText("Score: " + scoreModelList.get(position).getScore());
            holder.date.setText(scoreModelList.get(position).getDate());
            holder.name.setText(scoreModelList.get(position).getName());

            return convertView;
        }

        class ViewHolder{
            private TextView score;
            private TextView date;
            private TextView name;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            read();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        System.out.println("DESTROY");
        super.onDestroy();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

