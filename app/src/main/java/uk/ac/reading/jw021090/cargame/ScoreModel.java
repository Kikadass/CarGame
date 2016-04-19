package uk.ac.reading.jw021090.cargame;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kikadass on 19/04/2016.
 */
public class ScoreModel {
    private int score;
    private String date;
    private String name;

    public ScoreModel(int score){
        setDate();
        setScore(score);
    }

    public void setModel(ScoreModel model){
        this.score = model.getScore();
        this.date = model.getDate();
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss dd MMM yyyy");
        this.date = df.format(c.getTime());
    }

    public void setScore(int score) {
        this.score = score;
    }


}
