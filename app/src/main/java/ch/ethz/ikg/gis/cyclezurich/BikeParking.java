package ch.ethz.ikg.gis.cyclezurich;

/**
 * Created by Admin on 14.11.2015.
 */
public class BikeParking {

    public void requestParkingSpaces() {

        /*
        String req = "http://www.gis.stadt-zuerich.ch/maps/services/wms/WMS-ZH-STZH-OGD/MapServer/" +
                "WMSServer?Version=1.1.1&REQUEST=GetMap&SERVICE=WMS&SRS=EPSG:3857&FORMAT=image/png" +
                "&layers=Uebersichtsplan_2014&BBOX=8.427483,47.310593,8.633024,47.447433&Styles=" +
                "default&width=1000&height=1000";



        String wmsREQ = "www.gis.stadt-zuerich.ch/maps/services/wms/WMS-ZH-STZH-OGD/MapServer/" +
                "WMSServer?Version=1.3.0&REQUEST=GetMap&SERVICE=WMS&SRS=EPSG:3857&FORMAT=image/png&" +
                "Layers=Zweiradabstellplatz&BBOX=8.403826,47.317715,8.671922,47.456818&Styles=default" +
                "&width=1920&height=1080";
                */

        // String for the wfs Request
        String wfsREQ = "http://www.gis.stadt-zuerich.ch/maps/services/wms/WMS-ZH-STZH-OGD/MapServer" +
                "/WFSServer?version=1.1.0&REQUEST=GetFeature&TypeName=ogdzurich:Zweiradabstellplatz" +
                "&SRSNAME=EPSG:4326";

        // Create Request
        WFSRequest bikeparking = new WFSRequest();
        bikeparking.execute(wfsREQ);
    }

    public void liveData() {
        // String for the request
        String zuriRollt = "http://zh.suisseroule.ch/gestion/stats/rep_text.php";

        // HTML Website has to be parsed
    }
}
