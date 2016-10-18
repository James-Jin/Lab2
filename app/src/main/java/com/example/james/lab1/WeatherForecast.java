package com.example.james.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    public static final String TAG = "jamesdebug";
    ProgressBar progressBar;
    TextView curTemp, maxTemp, minTemp;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        curTemp = (TextView) findViewById(R.id.current_temp);
        maxTemp = (TextView) findViewById(R.id.max_temp);
        minTemp = (TextView) findViewById(R.id.min_temp);
        imageView = (ImageView) findViewById(R.id.weather_image);
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "Progress bar, just initialized:" + progressBar.getProgress());

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute();
        Log.i(TAG, "Progress bar, finished:" + progressBar.getProgress());

    }


    class ForecastQuery extends AsyncTask<String, Integer, String>{

        String min = "Min: ";
        String max = "Max: ";
        String cur = "Temperature: ";
        String icon;
        Bitmap pic;
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                inputStream = downloadUrl(urlString);

                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser myParser = xmlFactoryObject.newPullParser();
                myParser.setInput(inputStream, null);

                int event = myParser.getEventType();
                publishProgress(5);
                while (event != XmlPullParser.END_DOCUMENT){
                    String name= myParser.getName();
                    switch (event){
                        case XmlPullParser.START_TAG:
                            if(name.equals("temperature")){
                                Log.i(TAG, "in temperature!");
                                cur = "Temperature: "+ myParser.getAttributeValue(null,"value");
                                publishProgress(25);
                                min = "Min: " + myParser.getAttributeValue(null, "min");
                                publishProgress(50);
                                max = "Max: " +  myParser.getAttributeValue(null, "max");
                                publishProgress(75);
                            }else if(name.equals("weather")){
                                icon = myParser.getAttributeValue(null, "icon");

                                File file = getBaseContext().getFileStreamPath(icon + ".png");
                                if(file.exists()){
                                    FileInputStream fis = null;
                                    try{
                                        fis = new FileInputStream(file);
                                    }catch (FileNotFoundException e){
                                        e.printStackTrace();
                                    }
                                    pic = BitmapFactory.decodeStream(fis);
                                    Log.i(TAG, "image name: " + icon + ".png" + "\nFile exist");
                                }else {
                                    String imageURL = "http://openweathermap.org/img/w/" + icon + ".png";
                                    pic = getImage(imageURL);

                                    FileOutputStream outputStream = openFileOutput(icon + ".png", Context.MODE_PRIVATE);
                                    pic.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                    Log.i(TAG, "image name: " + icon + ".png" + "\nDownload image");
                                }
                                publishProgress(100);
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            break;
                    }
                    event = myParser.next();
                }

            } catch (IOException|XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            curTemp.setText(cur);
            maxTemp.setText(max);
            minTemp.setText(min);
            imageView.setImageBitmap(pic);
//            try{
//                Thread.sleep(1000);
                progressBar.setVisibility(View.INVISIBLE);
//            }catch(Exception e){
//                e.printStackTrace();
//            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            progressBar.setVisibility(View.VISIBLE);
            Log.i(TAG, "onProgressUpdate: " + values[0]);
        }

        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }

        //code from :
        //http://www.java2s.com/Code/Android/2D-Graphics/GetBitmapfromUrlwithHttpURLConnection.htm
        public Bitmap getImage(URL url) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else
                    return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        public Bitmap getImage(String urlString) {
            try {
                URL url = new URL(urlString);
                return getImage(url);
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }
}
