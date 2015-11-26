package ch.ethz.ikg.gis.cyclezurich;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * This class reads the tags of the KML file and then creates the list with the placemarks
 * @author Ariadni Gaki
 *
 */
public class SAXXMLHandler extends DefaultHandler {

    private List<Placemark> placemarks;
    private String tempVal;
    private Placemark tempPl = new Placemark();

    public SAXXMLHandler() {
        placemarks = new ArrayList<Placemark>();
    }

    public List<Placemark> getPlacemarks() {
        return placemarks;
    }

    // Event Handlers
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("Placemark")) {
            // create a new instance of placemark
            tempPl = new Placemark();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }


    /**
     * This method checks if the names of the tags in the KML file are equal to certain values
     * either in capital letters or not and then adds the placemrks to the list
     *
     */

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("placemark")) {
            // add it to the list
            placemarks.add(tempPl);
        } else if (qName.equalsIgnoreCase("name")) {
            tempPl.setName(tempVal);
        } else if (qName.equalsIgnoreCase("description")) {
            tempPl.setDescription(tempVal);
        } else if (qName.equalsIgnoreCase("coordinates")) {
            tempPl.setCoordinates(tempVal);
        }
    }

    public Object getpaStatePackage() {
        // TODO Auto-generated method stub
        return null;
    }


}
