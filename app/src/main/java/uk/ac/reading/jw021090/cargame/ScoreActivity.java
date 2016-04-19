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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");

        lvScores = (ListView)findViewById(R.id.lvScores);

        // To start fetching the data when app start, uncomment below line to start the async task.
        new JSONTask().execute(URL_TO_HIT);
    }


    public class JSONTask extends AsyncTask<String,String, List<ScoreModel> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        private void createFile(File file){
            try {
                if (!file.exists()) {
                    file.createNewFile();

                    String content = ("{\"scores\": []}");
                    OutputStream outputStream = new FileOutputStream(file);
                    Writer out = new OutputStreamWriter(outputStream);


                    out.write(content);
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected List<ScoreModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                /*
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                */

                File file = new File(getFilesDir(), "output.txt");
                createFile(file);
                InputStream stream = new FileInputStream(file);

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("scores");

                List<ScoreModel> ScoreModelList = new ArrayList<>();


                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                    System.out.println(parentArray.getJSONObject(i));
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself which is commented below
                     */
                    ScoreModel ScoreModel = gson.fromJson(finalObject.toString(), ScoreModel.class);
                    ScoreModelList.add(ScoreModel);
                }
                return ScoreModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<ScoreModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row, result);
                lvScores.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public class MovieAdapter extends ArrayAdapter{

        private List<ScoreModel> scoreModelList;
        private int resource;
        private LayoutInflater inflater;
        public MovieAdapter(Context context, int resource, List<ScoreModel> objects) {
            super(context, resource, objects);
            scoreModelList = objects;


            new WritingScore(URL_TO_HIT, new File(getFilesDir(), "output.txt")).execute(scoreModelList);



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
            new JSONTask().execute(URL_TO_HIT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

