package ch.ethz.ikg.gis.cyclezurich;

import android.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.util.List;

import javax.xml.parsers.SAXParserFactory;

/**
 * This class creates a reader which can read an XML file and as a consequence a KML file
 * and then creates a list with the placemarks existing in the KML file
 * @author Ariadni Gaki
 *
 */
public class SAXXMLParser {


    public static List<Placemark> parse(InputSource inputSource) {
        List<Placemark> placemarks = null;
        try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(inputSource);
            // get the `Placemark list`
            placemarks = saxHandler.getPlacemarks();

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("XML", "SAXXMLParser: parse() failed");
        }

        // return Placemark list
        return placemarks;
    }
}
