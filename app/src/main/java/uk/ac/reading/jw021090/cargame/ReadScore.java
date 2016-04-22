package uk.ac.reading.jw021090.cargame;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kikadass on 21/04/2016.
 */
public class ReadScore extends AsyncTask<Boolean,String, List<ScoreModel> > {
    private List<ScoreModel> ScoreModelList = new ArrayList<>();
    private Read read;

    public ReadScore(String string, File file) {
        read = new Read(string, file);
        // if file doesn't exist --> create it:
        new WritingScore(read.URL_TO_HIT, file, true);
    }

    @Override
    protected List<ScoreModel> doInBackground(Boolean... params) {
        try {
            StringBuffer buffer = null;

            //params[0] = isOnline
            if (params[0]) buffer = read.onlineReading();
            else buffer = read.offlineReading();

            String finalJson = buffer.toString();

            JSONObject parentObject = new JSONObject(finalJson);
            JSONArray parentArray = parentObject.getJSONArray("scores");

            List<ScoreModel> tmpScoreModelList = new ArrayList<>();


            Gson gson = new Gson();
            for (int i = 0; i < parentArray.length(); i++) {
                System.out.println(parentArray.getJSONObject(i));
                JSONObject finalObject = parentArray.getJSONObject(i);
                /**
                 * below single line of code from Gson saves you from writing the json parsing yourself
                 */
                ScoreModel ScoreModel = gson.fromJson(finalObject.toString(), ScoreModel.class);
                tmpScoreModelList.add(ScoreModel);
            }
            ScoreModelList = tmpScoreModelList;
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