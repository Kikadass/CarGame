package uk.ac.reading.jw021090.cargame;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created by Kikadass on 19/04/2016.
 */
public class WritingScore extends AsyncTask<List<ScoreModel>,String, String > {
    private String URL_TO_HIT;
    private File file;

    public WritingScore(String string, File file){
        this.URL_TO_HIT = string;
        this.file = file;
        createFile();
    }

    private void createFile(){
            try {
                if (!file.exists()) {
                    if (!file.createNewFile()){
                        System.out.println("File has not been created");
                    }

                    String content = ("{\"scores\": [{\"date\":\"03:38:13 21 Apr 2016\",\"score\": 200}]}");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    Writer out = new OutputStreamWriter(outputStream);


                    out.write(content);
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private OutputStream onlineWriting(){
         try {
             URL url = new URL(URL_TO_HIT);
             URLConnection connection = url.openConnection();
             connection.setDoOutput(true);

             return connection.getOutputStream();

         } catch (MalformedURLException e) {
             System.out.println("Error with writing URL");
             e.printStackTrace();
         } catch (IOException e) {
             System.out.println("Error with writing connection");
             e.printStackTrace();
         }

        return null;
    }

    private OutputStream offlineWriting(){
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e){
            System.out.println("Writing file not found");
            e.printStackTrace();
        }
        return null;
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

            OutputStreamWriter out = new OutputStreamWriter(offlineWriting());

            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return null;

    }
}
