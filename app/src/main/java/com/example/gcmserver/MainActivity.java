package com.example.gcmserver;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);

        new GCMRequest().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class GCMRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            // Key Concepts: https://developers.google.com/cloud-messaging/gcm
            // An API key saved on the app server that gives the app server authorized access to Google services
            final String API_KEY = "AI............................";
            // An ID issued by the GCM connection servers to the client app that allows it to receive messages. Note that registration tokens must be kept secret.
            final String CLIENT_REGISTRATION_TOKEN = "f4zZXS-............";
            final String POST_DATA = "{ \"registration_ids\": [ \"" + CLIENT_REGISTRATION_TOKEN + "\" ], " +
                    "\"delay_while_idle\": true, " +
                    "\"data\": {\"tickerText\":\"My Ticket\", " +
                    "\"contentTitle\":\"My Title\", " +
                    "\"message\": \"GCM message from GCMServer...\"}}";

            try {
                JSONObject jsonBody = new JSONObject();
                JSONObject jsonData = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(CLIENT_REGISTRATION_TOKEN);
                // jsonBody.put("registration_ids", jsonArray);
                jsonBody.put("to", CLIENT_REGISTRATION_TOKEN);
                jsonBody.put("delay_while_idle", true);
                jsonData.put("tickerText", "My Ticket");
                jsonData.put("contentTitle", "My Title");
                jsonData.put("message", "GCM message from GCMServer...");
                jsonBody.put("data", jsonData);
                String message = jsonBody.toString();

                URL url = new URL("https://android.googleapis.com/gcm/send");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "key=" + API_KEY);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                // writer.write(POST_DATA);
                writer.write(message);
                writer.flush();
                writer.close();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                InputStream inputStream;
                if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
                    inputStream = urlConnection.getInputStream();
                } else {
                    inputStream = urlConnection.getErrorStream();
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp, response = "";
                while ((temp = bufferedReader.readLine()) != null) {
                    response += temp;
                }
                return response;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            if (mTextView != null) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    mTextView.setText(jsonObject.toString(5));
                } catch (JSONException e) {
                    e.printStackTrace();
                    mTextView.setText(e.toString());
                }
            }
        }
    }
}
