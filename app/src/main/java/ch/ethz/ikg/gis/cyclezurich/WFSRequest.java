package ch.ethz.ikg.gis.cyclezurich;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Admin on 14.11.2015.
 */
public class WFSRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {

        URL url;

        //-----------------------
        // Send Request to server
        //-----------------------
        try {
            // First entry is the request
            url = new URL(urls[0]);
        }
        catch (MalformedURLException e){
            Log.d("myInfo", urls[0]);
            Log.d("myInfo", "URL Creation");
            Log.d("myInfo", e.getMessage());
            return e.getMessage();
        }

        Log.d("myInfo", url.toString());

        InputStream input;

        // Try to connect
        try {
            input = url.openStream();
        }
        catch (IOException e) {
            Log.d("myInfo", "OpenStream");
            Log.d("myInfo", e.getMessage());
            return e.getMessage();
        }

        // Convert to StringBuilder
        String result = convertStreamToString(input);

        int length = result.length();

        // Return result
        Log.d("myInfo", result);
        Log.d("myInfo", Integer.toString(length));
        return result;
    }

    @Override
    protected void onPostExecute(String res) {
        // Overlay the data
        Log.d("myInfo", "postexec");

    }

    // Method to convert an InputStream to a String
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
