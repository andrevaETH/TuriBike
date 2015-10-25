package ch.ethz.ikg.gis.cycle_zurich;

import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Admin on 23.10.2015.
 */
public class CycleRouting {

    // Method to calculate the direction from a start Location to end Location with a given
    // preference (false: attractive, true: direct
    public String requestDirection(Location start, Location end, boolean pref) {

        // Define String
        String RESTURL;
        String amp = "&";

        // Get the values
        double sLat, sLong, eLat, eLong;

        // Starting point
        sLat = start.getLatitude();
        sLong = start.getLongitude();

        // End point
        eLat = end.getLatitude();
        eLong = end.getLongitude();

        // Create the Request for the REST
        if (pref)
        {
            // Direct Route
            RESTURL = "http://www.gis.stadt-zuerich.ch/maps/rest/services/processing/" +
                    "RoutingVeloDirekt/NAServer/Route/solve?stops=";
        }
        else
        {
            // Attractive Route
            RESTURL = "http://www.gis.stadt-zuerich.ch/maps/rest/services/processing/" +
                    "RoutingVeloAttraktiv/NAServer/Route/solve?stops=";
        }

        // Convert double to string
        String stLat = Double.toString(sLat);
        String stLong = Double.toString(sLong);
        String enLat = Double.toString(eLat);
        String enLong = Double.toString(eLong);

        // Append the values
        // First the Longitude of the start Point
        RESTURL = RESTURL.concat(stLong);
        RESTURL = RESTURL.concat("%2C");

        // Then latitude
        RESTURL = RESTURL.concat(stLat);
        RESTURL = RESTURL.concat("%3B");

        // Then destination
        RESTURL = RESTURL.concat(enLong);
        RESTURL = RESTURL.concat("%2C");
        RESTURL = RESTURL.concat(enLat);

        // Add other Parameters
        RESTURL = RESTURL.concat(amp);

        // Define coordinate system of output
        RESTURL = RESTURL.concat("outSR=3857");
        RESTURL = RESTURL.concat(amp);

        // Rest of Request
        String RRequest = "&ignoreInvalidLocations=true&accumulateAttributeNames=&" +
                "impedanceAttributeName=Schnellste&restrictionAttributeNames=&restrictUTurns=" +
                "esriNFSBAllowBacktrack&useHierarchy=false&returnDirections=true&returnRoutes=true&" +
                "returnStops=false&returnBarriers=false&directionsLanguage=en_UK&outputLines=" +
                "esriNAOutputLineTrueShapeWithMeasure&findBestSequence=false&preserveFirstStop=true" +
                "&preserveLastStop=true&useTimeWindows=false&startTime=&outputGeometryPrecision=&" +
                "outputGeometryPrecisionUnits=esriUnknownUnits&directionsTimeAttributeName=&" +
                "directionsLengthUnits=esriNAUMeters&f=html";

        // Append Rest
        RESTURL = RESTURL.concat(RRequest);

        // Log Request
        Log.d("myInfo", RESTURL);

        URL url;

        //-----------------------
        // Send Request to server
        //-----------------------

        // SOMEWHERE IN THIS THERE IS AN ERROR
        try {
            url = new URL(RESTURL);
        }
        catch (MalformedURLException e){
            Log.d("myInfo", e.getMessage());
            return e.getMessage();
        }

        HttpURLConnection conn;

        // Try to connect
        try {
             conn = (HttpURLConnection) url.openConnection();
        }
        catch (IOException e) {
            Log.d("myInfo", e.getMessage());
            return e.getMessage();
        }

        // Store result into String
        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        catch (IOException e) {
            Log.d("myInfo", e.getMessage());
            return e.getMessage();
        }

        StringBuilder answer = new StringBuilder();
        String line;

        try {
            while ((line = rd.readLine()) != null) {
                answer.append(line);
            }
        }
        catch (IOException e) {
            Log.d("myInfo", e.getMessage());
        }

        try {
            rd.close();
        }
        catch (IOException e) {
            Log.d("myInfo", e.getMessage());
        }

        conn.disconnect();

        // Store result
        String reqRes = answer.toString();


        return "hello";

    }
}
