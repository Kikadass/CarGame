package uk.ac.reading.jw021090.cargame;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Kikadass on 21/04/2016.
 */
public class Read {
    public String URL_TO_HIT;
    public File file;

    public Read(String string, File file) {
        this.URL_TO_HIT = string;
        this.file = file;
    }

    public StringBuffer onlineReading(){
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

    public StringBuffer offlineReading() {
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
}
