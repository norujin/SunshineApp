package com.noruxjin.sunshineapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText city;
    private Button getResut;
    private TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = (EditText) findViewById(R.id.typeCityEdtTxtId);

        getResut = (Button) findViewById(R.id.getWeathrbtnId);

        result = (TextView) findViewById(R.id.weatherResultTxtId);

        getResut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadTask task = new DownloadTask();
                try {
                    String encodeCityName = URLEncoder.encode(city.getText().toString(), "UTF-8");

                task.execute("https://openweathermap.org/data/2.5/weather?q="+encodeCityName+"&appid=b6907d289e10d714a6e88b30761fae22");

                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(city.getWindowToken(),0);

                } catch (UnsupportedEncodingException e) {

                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG).show();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                String message= "";

                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")){

                        message += main + " : " + description + "\r\n";

                    }
                }

                if (!message.equals("")){

                    result.setText(message);

                } else {

                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG).show();

                }

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }

        }
    }

}
