package ch.ethz.ikg.gis.cyclezurich;

import android.location.Location;
import android.util.Log;



/**
 * Created by Admin on 23.10.2015.
 */
// Class for the CycleRouting
    // INPUT: Coordinates of start and end as well as preference
public class CycleRouting {

    // Method to calculate the direction from a start Location to end Location with a given
    // preference (false: attractive, true: direct)
    // Used like a constructor
    public void requestDirection(Location start, Location end, boolean pref) {

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
        // Output format at end
        String RRequest = "&ignoreInvalidLocations=true&accumulateAttributeNames=&" +
                "impedanceAttributeName=Schnellste&restrictionAttributeNames=&restrictUTurns=" +
                "esriNFSBAllowBacktrack&useHierarchy=false&returnDirections=true&returnRoutes=true&" +
                "returnStops=false&returnBarriers=false&directionsLanguage=en_UK&outputLines=" +
                "esriNAOutputLineTrueShapeWithMeasure&findBestSequence=false&preserveFirstStop=true" +
                "&preserveLastStop=true&useTimeWindows=false&startTime=&outputGeometryPrecision=&" +
                "outputGeometryPrecisionUnits=esriUnknownUnits&directionsTimeAttributeName=&" +
                "directionsLengthUnits=esriNAUMeters&f=pjson";

        // Append Rest
        RESTURL = RESTURL.concat(RRequest);

        // Log Request
        Log.d("myInfo", RESTURL);

        // Send to routing service
        Routing route = new Routing();
        route.execute(RESTURL);

    }

}
