package uk.ac.reading.jw021090.cargame;

import android.os.AsyncTask;
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

/**
 * Created by Kikadass on 21/04/2016.
 */
public class ReadScore extends AsyncTask<Boolean,String, List<ScoreModel> > {
    private String URL_TO_HIT;
    private File file;
    private List<ScoreModel> ScoreModelList = new ArrayList<>();

    public ReadScore(String string, File file) {
        this.URL_TO_HIT = string;
        this.file = file;

        // if file doesn't exist --> create it:
        new WritingScore(URL_TO_HIT, file);
    }

    private StringBuffer onlineReading(){
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try{
            URL url = new URL(URL_TO_HIT);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer;

        }catch (MalformedURLException e) {
            System.out.println("Error reading URL");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading connection");
            e.printStackTrace();
        }finally {
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

    private StringBuffer offlineReading() {
        BufferedReader reader = null;

        try {
            InputStream stream = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer;
        }catch (FileNotFoundException e){
            System.out.println("File not found!");

            e.printStackTrace();
        }catch (IOException e) {
            System.out.println("Error reading connection");
            e.printStackTrace();
        }finally {
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
    protected List<ScoreModel> doInBackground(Boolean... params) {
        try {
            StringBuffer buffer = null;

            //params[0] = isOnline
            System.out.println(params[0]);
            if (params[0]) buffer = onlineReading();
            else buffer = offlineReading();

            String finalJson = buffer.toString();

            JSONObject parentObject = new JSONObject(finalJson);
            JSONArray parentArray = parentObject.getJSONArray("scores");

            ScoreModelList = new ArrayList<>();


            Gson gson = new Gson();
            for (int i = 0; i < parentArray.length(); i++) {
                System.out.println(parentArray.getJSONObject(i));
                JSONObject finalObject = parentArray.getJSONObject(i);
                /**
                 * below single line of code from Gson saves you from writing the json parsing yourself
                 */
                ScoreModel ScoreModel = gson.fromJson(finalObject.toString(), ScoreModel.class);
                ScoreModelList.add(ScoreModel);
                System.out.println(ScoreModelList.size());
            }
            return ScoreModelList;

        } catch (JSONException e) {
            System.out.println("Error JASON reading");
            e.printStackTrace();
        }
        return null;
    }

    public List<ScoreModel> getScoreModelList(){
        return ScoreModelList;
    }


}