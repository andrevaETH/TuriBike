package ch.ethz.ikg.gis.cyclezurich;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.CoordinateConversion;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
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
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.util.ArrayList;
import java.util.List;

/**This activity presents the map, which indicates the top 10 destinations in Zurich. In addition, the
 * route is presented from the current position of the user to the destination that has been selected
 * from the predefined list in the DestinationActivity
 */
public class MapsActivity extends Activity implements LocationListener{
    // define variables used in the code
    private Location location = null;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private MyPoint touchedPoint;
    private int selectedPointIndex;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private TextView popUpInfo;
    private MapView mMapView;
    private double dest_latitude;
    private double dest_longitude;
    private int newgraphic;
    private int i = 0;
    private MyPoint destinationPoint;
    private int routeId;
    private int currentid;
    PictureMarkerSymbol picDestination;
    private Graphic graphicTop10Dest;
    private List<MyPoint> attractionPoints;
    private List<MyAttractionInfo> attractionsInfo;
    GraphicsLayer graphicsLayer = new GraphicsLayer();
    GraphicsLayer top10graphicsLayer = new GraphicsLayer();
    Graphic graphicCurrentLocation;
    Graphic graphicDestination;
    private String routeTaskURL;
    private Button button_home;
    private Button button_back;
    private Button button_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attractionPoints = new ArrayList<MyPoint>();
        attractionsInfo = new ArrayList<MyAttractionInfo>();

        setContentView(R.layout.activity_maps);

        // Retrieve the MapView from XML layout
        mMapView = (MapView) findViewById(R.id.map);

        relativeLayout = (RelativeLayout) findViewById(R.id.mapRelative);

        /**the coordinates of the selected destination from the list are
         * sent through intents to this activity
        */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String text_value = extras.getString("key1");
            String [] coord = extras.getString("coordinates").split(",");
            dest_latitude = Double.parseDouble(coord[1]);
            dest_longitude = Double.parseDouble(coord[0]);
            int number_value = extras.getInt("key2");
            displayOutput(text_value, number_value);
        }


        // Add markers with top 10 destinations
        for (i=0; i<DestinationActivity.placemarks.size(); i++){

            String[] coord1 = DestinationActivity.placemarks.get(i).coordinates.split(",");

            graphicTop10Dest = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(coord1[1] + ","+ coord1[0],
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(Color.BLACK, 10, STYLE.SQUARE));
            top10graphicsLayer.addGraphic(graphicTop10Dest);

            com.esri.core.geometry.Point auxdestPoint = (CoordinateConversion
                    .decimalDegreesToPoint(coord1[1] + "," + coord1[0],
                            SpatialReference
                                    .create(21781)));

            MyPoint newdestPoint = new MyPoint(auxdestPoint.getX(), auxdestPoint.getY());
            attractionPoints.add(newdestPoint);
            MyAttractionInfo newdestInfo = new MyAttractionInfo(DestinationActivity.placemarks.get(i).name,
                    DestinationActivity.placemarks.get(i).description);
            attractionsInfo.add(newdestInfo);
        }
        mMapView.addLayer(top10graphicsLayer);



        // Relate to R.id
        button_home = (Button) findViewById(R.id.homeButton);
        button_back = (Button) findViewById(R.id.backButton);
        button_exit = (Button) findViewById(R.id.exitButton);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_homeClicked();

            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_backClicked();

            }
        });
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_exitClicked();

            }
        });

        // find the location through LocationManager
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 5, this);

        // Set on tap listener in order to find if the user tried to retrieve information for a point of interest
        // It is assumed that the user wants to see the information (pop up window) if the distance between the tapped
        // point and the closest point of the feature layer (based on Euclidean distance) is <2000m
        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float xscreen, float yscreen) {
                com.esri.core.geometry.Point auxTouchedPoint = mMapView.toMapPoint(xscreen, yscreen);
                String touchedPointTransf_string = CoordinateConversion.pointToDecimalDegrees(auxTouchedPoint, SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR), 20);
                String[] coord1 = touchedPointTransf_string.split("N");
                String[] coord2 = coord1[1].split("E");
                com.esri.core.geometry.Point auxTouchedPointTransf = CoordinateConversion.decimalDegreesToPoint(coord1[0] + "," + coord2[0], SpatialReference.create(21781));
                touchedPoint = new MyPoint(auxTouchedPointTransf.getX(), auxTouchedPointTransf.getY());
                selectedPointIndex = findClosestPoint(touchedPoint);
                if (selectedPointIndex > -1) {
                    layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popupwindow, null);
                    popupWindow = new PopupWindow(container, 400, 400, true);
                    popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, Math.round(xscreen), Math.round(yscreen)); //relativeLayout
                    popUpInfo = (TextView) container.findViewById(R.id.popuptext);
                    popUpInfo.setText("Top 10 Attractions\n" + "Name:\t" +
                            attractionsInfo.get(selectedPointIndex).name +
                            "\nDescription:\t" + attractionsInfo.get(selectedPointIndex).description);
                    container.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });

                }

            }


        });

    }

    // Home button functionality
    public void button_homeClicked()
    {
        Intent intent = new Intent(MapsActivity.this,StartPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // Back button functionality
    public void button_backClicked()
    {
        onBackPressed();
    }

    // Exit button functionality
    public void button_exitClicked() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    protected void onStop() {
        super.onStop();

        if (location != null) {
            onLocationChanged(location);
        }
        else {
            Toast.makeText(getApplicationContext(), "Please, check if GPS is enabled.\n TuriBike may crash!", Toast.LENGTH_LONG).show();
        }

    }

    private void displayOutput(String text_value, int number_value) {
        // TODO Auto-generated method stub

    }

    /**
     * This method finds the point that is closest to the tapped point, based on the Euclidean distance.
     */
    public int findClosestPoint(MyPoint originPoint) {
        // Declare an empty placeholder for the resulting Point.
        MyPoint pointOfSmallestDistance = null;

        // Declare a variable holding the largest possible distance.
        double smallestDistance = Double.MAX_VALUE;
        int index = 0;

        // Iterate over the points. If we find one with a smaller distance than
        // the Point we currently have, we store it and its distance. If we have
        // the same distance, we store the Point as well.
        for (int i = 0; i < this.attractionPoints.size(); i++) {
            destinationPoint = this.attractionPoints.get(i);

            double distance = Math.sqrt(Math.pow(destinationPoint.X - originPoint.X, 2.0) + Math.pow(destinationPoint.Y - originPoint.Y, 2.0));

            if (distance <= smallestDistance) {
                // new smallest distance
                smallestDistance = distance;
                index = i;

                // new closest point
                pointOfSmallestDistance = destinationPoint;
            }
        }

        if (smallestDistance < 2000) {
            Log.e("return<2000", "" + (i));
            return (index);
        } else {
            Log.e("return>=2000", "");
            return (index = -1);
        }
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
     * route between current position and destination
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
                    new SimpleMarkerSymbol(R.color.red20, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
            currentid = graphicsLayer.addGraphic(graphicCurrentLocation);
            mMapView.addLayer(graphicsLayer);


        ////////////////////////////////////////////////////////////////////////////////////////////
        //        Attemp to use svg instead of png in case it could improve the resolution of the icons
        //      ------------------------------------------------------------------------------------
//            SVG svg = SVGParser.getSVGFromResource(getResources(), R.drawable.monument);
//            Picture picture = svg.getPicture();
//            Drawable destloc = getResources().getDrawable(R.drawable.monument);
//
//           picDestination = new PictureMarkerSymbol(destloc);
//           picDestination.setOffsetX(3);
//           picDestination.setOffsetY(3);
//           graphicDestination = new Graphic (CoordinateConversion.decimalDegreesToPoint(
//                   dest_latitude + "," + dest_longitude, SpatialReference
//                           .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)), picDestination);
            ////////////////////////////////////////////////////////////////////////////////////////////
            // Current Location of the user and graphic
            graphicDestination = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    dest_latitude + "," + dest_longitude,
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(R.color.green20, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
            graphicsLayer.addGraphic(graphicDestination);


        }

        else {
            graphicsLayer.removeGraphic(currentid);
            graphicsLayer.removeGraphic(routeId);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mMapView.centerAt(latitude, longitude, true);
            // Current Location of the user and graphic
            graphicCurrentLocation = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    String.valueOf(latitude) + ","
                                            + String.valueOf(longitude),
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(R.color.red20, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
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

            ////////////////////////////////////////////////
//          Possibility to Use ArcGIS routing service layers
//          ------------------------------------------------
            // String routeTaskURL = "http://tasks.arcgisonline.com/ArcGIS/rest/services/NetworkAnalysis/ESRI_Route_EU/NAServer/Route";
            //String routeTaskURL = "http://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World";
            ////////////////////////////////////////////////
            if (UserProfile.selectedRouteidentification == 1){
                routeTaskURL = "http://www.gis.stadt-zuerich.ch/maps/rest/services/processing/RoutingVeloDirekt/NAServer/Route";
            }
            else if(UserProfile.selectedRouteidentification == 0){
                routeTaskURL = "http://www.gis.stadt-zuerich.ch/maps/rest/services/processing/RoutingVeloAttraktiv/NAServer/Route";
            }
            else{
                routeTaskURL = "http://www.gis.stadt-zuerich.ch/maps/rest/services/processing/RoutingVeloDirekt/NAServer/Route";
            }

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
                        new SimpleLineSymbol(R.color.black50, 3));

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
