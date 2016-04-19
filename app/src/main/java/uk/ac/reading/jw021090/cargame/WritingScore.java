package uk.ac.reading.jw021090.cargame;

import android.os.AsyncTask;

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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created by Kikadass on 19/04/2016.
 */
public class WritingScore extends AsyncTask<List<ScoreModel>,String, String > {
    private String URL_TO_HIT;
    private File file;

    public WritingScore(String string, File file){
        createFile(file);
        this.URL_TO_HIT = string;
        this.file = file;
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
    protected String doInBackground(List<ScoreModel>... params) {
        List<ScoreModel> scoreModelList = params[0];

        try {
            Gson gson = new Gson();
            String content = gson.toJson(scoreModelList);
            System.out.println("content: " + content);
            try {
                content = ("{  \"scores\": " + content + "}");
                JSONObject parentObject = new JSONObject(content);
                System.out.println("parent: " + parentObject.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }

            /*
            URL url = new URL(URL_TO_HIT);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());*/
            OutputStream outputStream = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(outputStream);


            out.write(content);
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


/*
        HttpURLConnection connection = null;
        OutputStreamWriter out = null;
        //Writer out = null;
        try {
            Gson gson = new Gson();
            String content = gson.toJson(scoreModelList);
            URL url = new URL(URL_TO_HIT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);

            //OutputStream outputStream = new FileOutputStream(new File(getFilesDir(), "lol.txt"));
            //out = new OutputStreamWriter(outputStream);


            out = new OutputStreamWriter(connection.getOutputStream());
            out.write(content);
            out.flush();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
                System.out.println("MMMMMMM OKEEE");
                // otherwise, if any other status code is returned, or no status
                // code is returned, do stuff in the else block
            } else {
                // Server returned HTTP error code.
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (out != null) {
                    out.flush();
                    System.out.println("flushed");
                    out.close();
                    System.out.println("Closing");
                }
            } catch (IOException e) {
                System.out.println("Not closed");
                e.printStackTrace();
            }
        }*/
        return null;

    }

    public List<ScoreModel> read(){
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            //uncoment this to do it through URL
            /*
            URL url = new URL(URL_TO_HIT);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();*/

            //comment next line to do it through URL
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
}
