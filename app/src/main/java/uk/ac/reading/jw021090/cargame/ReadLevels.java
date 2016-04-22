package uk.ac.reading.jw021090.cargame;


import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kikadass on 21/04/2016.
 */
public class ReadLevels extends AsyncTask<Boolean,String, List<Level> > {
    private List<Level> levelList = new ArrayList<>();
    private Read read;

    public ReadLevels(String string, File file) {
        read = new Read(string, file);
    }

    @Override
    protected List<Level> doInBackground(Boolean... params) {
        try {
            StringBuffer buffer = null;

            //params[0] = isOnline
            if (params[0]) {
                buffer = read.onlineReading();
            }
            else buffer = read.offlineReading();

            String finalJson = buffer.toString();

            JSONObject parentObject = new JSONObject(finalJson);
            JSONArray parentArray = parentObject.getJSONArray("levels");

            levelList = new ArrayList<>();


            Gson gson = new Gson();
            for (int i = 0; i < parentArray.length(); i++) {
                System.out.println(parentArray.getJSONObject(i));
                JSONObject finalObject = parentArray.getJSONObject(i);
                /**
                 * below single line of code from Gson saves you from writing the json parsing yourself
                 */
                Level level = gson.fromJson(finalObject.toString(), Level.class);
                levelList.add(level);
                System.out.println(levelList.size());
            }

            // safe levels localy for future games without connection
            if (params[0]) {
                safeLevelsLocaly(levelList);
            }
            return levelList;

        } catch (JSONException e) {
            System.out.println("Error JASON reading");
            e.printStackTrace();
        }
        return null;
    }

    private void safeLevelsLocaly(List<Level> levelList){
        try {
            Gson gson = new Gson();
            String content = gson.toJson(levelList);
            System.out.println("content: " + content);

            try {
                content = ("{  \"levels\": " + content + "}");
                JSONObject parentObject = new JSONObject(content);
                System.out.println("parent: " + parentObject.toString());
            }catch(JSONException e){
                e.printStackTrace();
            }

            OutputStream stream = null;
            try {
                stream = new FileOutputStream(read.file);
            } catch (FileNotFoundException e){
                System.out.println("Writing file not found");
                e.printStackTrace();
            }

            OutputStreamWriter out = new OutputStreamWriter(stream);

            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Level> getLevelList(){
        return levelList;
    }


}