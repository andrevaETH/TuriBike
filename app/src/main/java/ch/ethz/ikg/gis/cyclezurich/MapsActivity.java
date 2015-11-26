package ch.ethz.ikg.gis.cyclezurich;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.CoordinateConversion;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.tasks.na.CostAttribute;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.NetworkDescription;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;

import java.util.List;

public class MapsActivity extends Activity implements LocationListener{
    // define variables used in the code
    Location location = null;
    double latitude;
    double longitude;
    protected LocationManager locationManager;
    private MapView mMapView;
    double dest_latitude;
    double dest_longitude;
    int newgraphic;
    int i = 0;
    int routeId;
    int currentid;

    GraphicsLayer graphicsLayer = new GraphicsLayer();
    GraphicsLayer newLayer = new GraphicsLayer();
    Graphic graphicCurrentLocation;
    Graphic graphicDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("Error2", "It's in mapsactivity");
        setContentView(R.layout.activity_maps);

        // Retrieve the MapView from XML layout
        mMapView = (MapView) findViewById(R.id.map);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String text_value = extras.getString("key1");
            String [] coord = extras.getString("coordinates").split(",");
            dest_latitude = Double.parseDouble(coord[1]);
            dest_longitude = Double.parseDouble(coord[0]);
            int number_value = extras.getInt("key2");
            displayOutput(text_value, number_value);
        }

        // find the location through LocationManager
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 5, this);

		/*
		 * if (location != null) { onLocationChanged(location); }
		 */
    }

    protected void onStop() {
        super.onStop();

        if (location != null) {
            onLocationChanged(location);
        }

    }

    private void displayOutput(String text_value, int number_value) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method retrieves the current location (latitude and longitude) from
     * the GPS provider and then creates markers on the map in the current
     * position and the position of the destination. It also calculates the
     * route beween current position and destination
     */
    @Override
    public void onLocationChanged(Location location) {

        i++;
        if (i == 1) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mMapView.centerAt(latitude, longitude, true);
            // Graphics
            // Current Location
            graphicCurrentLocation = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    String.valueOf(latitude) + ","
                                            + String.valueOf(longitude),
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.CROSS));
            currentid = graphicsLayer.addGraphic(graphicCurrentLocation);
            mMapView.addLayer(graphicsLayer);

            graphicDestination = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    dest_latitude + "," + dest_longitude,
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(Color.RED, 10, STYLE.CROSS));
            graphicsLayer.addGraphic(graphicDestination);

        }

        else {
            graphicsLayer.removeGraphic(currentid);
            graphicsLayer.removeGraphic(routeId);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mMapView.centerAt(latitude, longitude, true);
            // Graphics
            // Current Location
            graphicCurrentLocation = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    String.valueOf(latitude) + ","
                                            + String.valueOf(longitude),
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(Color.BLUE, 10, STYLE.CROSS));
            currentid = graphicsLayer.addGraphic(graphicCurrentLocation);
            mMapView.addLayer(graphicsLayer);

        }

        // Create the route calculator and start it.
        RouteCalculator myAsync = new RouteCalculator();
        myAsync.execute();

    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * This method calculates the route between the current location and the
     * destination
     *
     * @author Ariadni Gaki
     *
     */
    public class RouteCalculator extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected String doInBackground(String... params) {
            UserCredentials creds = new UserCredentials();
            creds.setUserAccount("IKGStud14", "i2rJVYT6c7");

            // String routeTaskURL =
            // "http://tasks.arcgisonline.com/ArcGIS/rest/services/NetworkAnalysis/ESRI_Route_EU/NAServer/Route";
            String routeTaskURL = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
            try {
                RouteTask routeTask = RouteTask.createOnlineRouteTask(
                        routeTaskURL, creds);

                // Create a parameters object and retrieve the network
                // description.
                RouteParameters routeParams = routeTask
                        .retrieveDefaultRouteTaskParameters();
                NetworkDescription description = routeTask
                        .getNetworkDescription();
                List<CostAttribute> costAttributes = description
                        .getCostAttributes();

                // set impedance for length
                routeParams.setImpedanceAttributeName("Length");

                // Assign the first cost attribute as the impedance.
                if (costAttributes.size() > 0)
                    routeParams.setImpedanceAttributeName(costAttributes.get(0)
                            .getName());

                // Create routing features class.
                NAFeaturesAsFeature naFeatures = new NAFeaturesAsFeature();
                Geometry mLocation = CoordinateConversion
                        .decimalDegreesToPoint(
                                String.valueOf(latitude + "," + longitude),
                                SpatialReference
                                        .create(SpatialReference.WKID_WGS84_WEB_MERCATOR));

                Geometry mDestination = CoordinateConversion
                        .decimalDegreesToPoint(
                                dest_latitude + "," + dest_longitude,
                                SpatialReference
                                        .create(SpatialReference.WKID_WGS84_WEB_MERCATOR));

                StopGraphic startPnt = new StopGraphic(mLocation);
                StopGraphic endPnt = new StopGraphic(mDestination);
                // Set features on routing feature class.
                naFeatures.setFeatures(new Graphic[] { startPnt, endPnt });
                // Set stops on routing feature class.
                naFeatures.setSpatialReference(mMapView.getSpatialReference());
                routeParams.setOutSpatialReference(mMapView
                        .getSpatialReference());
                routeParams.setStops(naFeatures);

                RouteResult mResults = routeTask.solve(routeParams);
                List<Route> routes = mResults.getRoutes();
                Route mRoute = routes.get(0);
                Log.d("", "" + mRoute.getTotalMinutes());
                Log.d("", mRoute.getRoutingDirections().toString());

                // Access the whole route geometry and add it as a graphic.
                Geometry routeGeom = mRoute.getRouteGraphic().getGeometry();
                Graphic symbolGraphic = new Graphic(routeGeom,
                        new SimpleLineSymbol(Color.BLUE, 3));

                Log.e("ADDING", "adding route");

                routeId = graphicsLayer.addGraphic(symbolGraphic);

                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
