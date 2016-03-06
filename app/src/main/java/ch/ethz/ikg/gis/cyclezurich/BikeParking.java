package ch.ethz.ikg.gis.cyclezurich;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.AsyncTask;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.ogc.WMSLayer;
import com.esri.android.map.popup.Popup;
import com.esri.android.map.popup.PopupContainer;
import com.esri.core.geometry.CoordinateConversion;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.na.CostAttribute;
import com.esri.core.tasks.na.NAFeaturesAsFeature;
import com.esri.core.tasks.na.NetworkDescription;
import com.esri.core.tasks.na.Route;
import com.esri.core.tasks.na.RouteParameters;
import com.esri.core.tasks.na.RouteResult;
import com.esri.core.tasks.na.RouteTask;
import com.esri.core.tasks.na.StopGraphic;

import net.sf.json.filters.TruePropertyFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.esri.core.geometry.CoordinateConversion.utmToPoint;

/**
 * This activity presents the map, which indicates the bike parking spaces in Zurich. There is also a
 * popup for each feature providing additional information. Furthermore, it identifies the closest bike
 * parking space to the user s current location and indicates the route from the current location to the
 * closest feature on the map.
 */

public class BikeParking extends Activity implements LocationListener {

    // Declare variables
    private MapView parkingMapView;
    private ArcGISFeatureLayer bikeParkingLayer;
    private WMSLayer parkingMapLayer;
    private String bikeParkingURL;
    private Envelope envelope;
    private PopupContainer popupContainer;
    private Popup popup;
    private PopupDialog popupDialog;
    private String fieldType;
    private ArrayList arrayList;
    private Geometry geometry;
    private MultiPoint multiPoint;
    private int i = 0;
    private Location location = null;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private MyPoint currentLocation;
    private MyPoint destinationPoint;
    private MyPoint touchedPoint;
    private List<MyPoint> parkingPoints;
    private int selectedparkingPointIndex;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private TextView popUpInfo;
    private List<MyparkingInfo> parkingInfo;
    private int j = 0;
    private int closestparkingPointIndex;
    private GraphicsLayer graphicsLayer = new GraphicsLayer();
    private Graphic graphicCurrentLocation;
    private Graphic graphicDestination;
    private int currentid;
    private int routeId;
    private String destinationPoint_string;
    private String [] coord11;
    private String [] coord22;
    private PictureMarkerSymbol picCurrentLocation;
    private PictureMarkerSymbol picDestination;
    private String routeTaskURL;
    private Button button_home;
    private Button button_back;
    private Button button_exit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Relate to R.layout
        setContentView(R.layout.activity_parking_station);
        parkingMapView = (MapView) findViewById(R.id.parkingmap);
        relativeLayout = (RelativeLayout) findViewById(R.id.parkingRelative);
        parkingPoints = new ArrayList<MyPoint>();

        ////////////////////////////////////////////////////////
//        WMS Layer (working properly when comments are removed)
//      --------------------------------------------------------
//        WMSLayer parkingMapLayer = new WMSLayer(""
//                + "http://www.gis.stadt-zuerich.ch/maps/services/wms/WMS-ZH-STZH-OGD/MapServer/WMSServer?");
//        parkingMapView.addLayer(parkingMapLayer);
//        parkingMapLayer.setVisibleLayer(new String[]{"Stadtplan"});
        ////////////////////////////////////////////////////////

        // Through this URl the WFS service is requested
        bikeParkingURL = "http://services1.arcgis.com/i9MtZ1vtgD3gTnyL/arcgis/rest/services/StadtZurichWFS_transformed3/FeatureServer/0";
        //a layer is created including the features retrived from the aforementioned URL
        bikeParkingLayer = new ArcGISFeatureLayer(bikeParkingURL, ArcGISFeatureLayer.MODE.SNAPSHOT);
        //the layer is added to the map
        parkingMapView.addLayer(bikeParkingLayer);


        /**
         * The envelope is the bounding box including the coordinates in the Swiss coordinates system 21781.
         * The coordinates correspond to the bottom left and upper right points respectively
         */

        envelope = new Envelope(670000.00, 230000.00, 690000.00, 260000.00);


        popupContainer = new PopupContainer(parkingMapView);

        // build a query
        Query query = new Query();
        //all the fields are selected
        query.setOutFields(new String[]{"*"});
        query.setGeometry(envelope);

        // Relate to R.id
        button_home = (Button) findViewById(R.id.homeButton);
        button_back = (Button) findViewById(R.id.backButton);
        button_exit = (Button) findViewById(R.id.exitButton);
        // Set on click listeners
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

        /**
         * This method retrieves the features and their attributes from the layer.
         */
        bikeParkingLayer.queryFeatures(query, new CallbackListener<FeatureSet>() {
            @Override
            public void onCallback(FeatureSet featureSet) {
                multiPoint = new MultiPoint();
                parkingInfo = new ArrayList<MyparkingInfo>();
                double smallestDistance = Double.MAX_VALUE;
                if (featureSet.getGraphics().length == 0) {
                    Log.d("Error again", "featureSet empty");
                }
                if (featureSet.getGraphics().length > 0) {
                    for (i = 0; i < featureSet.getGraphics().length; i++) {
                        multiPoint.add(((MultiPoint) featureSet.getGraphics()[i].getGeometry()).getPoint(0));
                        MyPoint newparkingPoint = new MyPoint(multiPoint.getPoint(i).getX(), multiPoint.getPoint(i).getY());
                        parkingPoints.add(newparkingPoint);
                        MyparkingInfo newparkingInfo = new MyparkingInfo(String.valueOf(featureSet.getGraphics()[i].getAttributeValue("Name")),
                                String.valueOf(featureSet.getGraphics()[i].getAttributeValue("Tel")));
                        parkingInfo.add(newparkingInfo);

                    }

                }

            }

            @Override
            public void onError(Throwable throwable) {

            }
        });

        //create Location Manager instance as reference to the location service through  getSystemService method
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //request current location from the Location Manager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        //if location is changed then the onLocationChanged method is triggered

        // Set on tap listener in order to find if the user tried to retrieve information for a point of interest
        // It is assumed that the user wants to see the information (pop up window) if the distance between the tapped
        // point and the closest point of the feature layer (based on Euclidean distance) is <2000m
        parkingMapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float xscreen, float yscreen) {
                com.esri.core.geometry.Point auxTouchedPoint = parkingMapView.toMapPoint(xscreen, yscreen);
                String touchedPointTransf_string = CoordinateConversion.pointToDecimalDegrees(auxTouchedPoint, SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR), 20);
                String[] coord1 = touchedPointTransf_string.split("N");
                String[] coord2 = coord1[1].split("E");
                com.esri.core.geometry.Point auxTouchedPointTransf = CoordinateConversion.decimalDegreesToPoint(coord1[0] + "," + coord2[0], SpatialReference.create(21781));
                touchedPoint = new MyPoint(auxTouchedPointTransf.getX(), auxTouchedPointTransf.getY());
                selectedparkingPointIndex = findClosestPoint(touchedPoint);
                if (selectedparkingPointIndex > -1) {
                    layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popupwindow, null);

                    popupWindow = new PopupWindow(container, 400, 400, true);

                    popupWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY, Math.round(xscreen), Math.round(yscreen));
                    popUpInfo = (TextView) container.findViewById(R.id.popuptext);
                    popUpInfo.setText("Bikeparking Station\n" + "Name:\t" +
                            parkingInfo.get(selectedparkingPointIndex).Name +
                            "\nTel:\t" + parkingInfo.get(selectedparkingPointIndex).Tel);

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
        Intent intent = new Intent(BikeParking.this,StartPage.class);
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
        for (int i = 0; i < this.parkingPoints.size(); i++) {
            destinationPoint = this.parkingPoints.get(i);

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

    /**
     * This method calculates the euclidean distance between the current position of the user and each
     * feature (bike parking space point) from the feature layer and then identifies the smallest one. It returns
     * the feature with the smallest distance to the user.
     */
    public int findClosestbikeParking(MyPoint originPoint) {
        // Declare an empty placeholder for the resulting Point.
        MyPoint pointOfSmallestDistance = null;

        // Declare a variable holding the largest possible distance.
        double smallestDistance = Double.MAX_VALUE;
        int index = 0;

        // Iterate over the points. If we find one with a smaller distance than
        // the Point we currently have, we store it and its distance. If we have
        // the same distance, we store the Point as well.
        for (int i = 0; i < this.parkingPoints.size(); i++) {
            MyPoint destinationPoint = this.parkingPoints.get(i);

            double distance = Math.sqrt(Math.pow(destinationPoint.X - originPoint.X, 2.0) + Math.pow(destinationPoint.Y - originPoint.Y, 2.0));

            if (distance <= smallestDistance) {
                // new smallest distance
                smallestDistance = distance;
                index = i;

                // new closest point
                pointOfSmallestDistance = destinationPoint;
            }
        }

        return (index);
    }


    @Override
    public void onLocationChanged(Location location) {


        j++;
        if (j == 1) {
            // Current Location of the user and graphic
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            parkingMapView.centerAt(latitude, longitude, true);
            com.esri.core.geometry.Point auxPoint = CoordinateConversion.decimalDegreesToPoint(String.valueOf(latitude) + "," + String.valueOf(longitude), SpatialReference.create(21781));
            currentLocation = new MyPoint(auxPoint.getX(), auxPoint.getY());
            closestparkingPointIndex = findClosestbikeParking(currentLocation);
            graphicCurrentLocation = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    String.valueOf(latitude) + ","
                                            + String.valueOf(longitude),
                                    SpatialReference
                                            .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(R.color.red20, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
            currentid = graphicsLayer.addGraphic(graphicCurrentLocation);

            //////////////////////////////////////////////////////////////////////////////////////
//          Use of  Picture Marker Symbol - Difficulty in changing size and resolution of the icon
//          --------------------------------------------------------------------------------------
//           Drawable curloc = getResources().getDrawable(R.drawable.currentlocation);
//           picCurrentLocation = new PictureMarkerSymbol(curloc);
//           picCurrentLocation.setOffsetX(3);
//           picCurrentLocation.setOffsetY(3);
//           graphicCurrentLocation = new Graphic (CoordinateConversion.decimalDegreesToPoint(
//                   String.valueOf(latitude) + "," + String.valueOf(longitude), SpatialReference
//                           .create(SpatialReference.WKID_WGS84_WEB_MERCATOR)), picCurrentLocation);
//           currentid = graphicsLayer.addGraphic(graphicCurrentLocation);
            ////////////////////////////////////////////////////////
            // Destination of the user and graphic
            com.esri.core.geometry.Point destinationPoint = new com.esri.core.geometry.Point(parkingPoints.get(closestparkingPointIndex).X, parkingPoints.get(closestparkingPointIndex).Y);
            destinationPoint_string = CoordinateConversion.pointToDecimalDegrees(destinationPoint, SpatialReference.create(21781), 20);
            coord11 = destinationPoint_string.split("N");
            coord22 = coord11[1].split("E");

            graphicDestination = new Graphic(
                    CoordinateConversion
                            .decimalDegreesToPoint(
                                    coord11[0] + "," + coord22[0], SpatialReference.create(SpatialReference.WKID_WGS84_WEB_MERCATOR)),
                    new SimpleMarkerSymbol(R.color.green20, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
            graphicsLayer.addGraphic(graphicDestination);

            parkingMapView.addLayer(graphicsLayer);


        }
        else {
            graphicsLayer.removeGraphic(currentid);
            graphicsLayer.removeGraphic(routeId);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            parkingMapView.centerAt(latitude, longitude, true);
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

            parkingMapView.addLayer(graphicsLayer);
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

                // Create a parameters object and retrieve the network description.
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
                                coord11[0] + "," + coord22[0],
                                SpatialReference
                                        .create(SpatialReference.WKID_WGS84_WEB_MERCATOR));

                StopGraphic startPnt = new StopGraphic(mLocation);
                StopGraphic endPnt = new StopGraphic(mDestination);
                // Set features on routing feature class.
                naFeatures.setFeatures(new Graphic[] { startPnt, endPnt });
                // Set stops on routing feature class.
                naFeatures.setSpatialReference(parkingMapView.getSpatialReference());
                routeParams.setOutSpatialReference(parkingMapView
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
                        new SimpleLineSymbol(R.color.black50, 3, SimpleLineSymbol.STYLE.SOLID));

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
