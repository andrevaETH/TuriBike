package ch.ethz.ikg.gis.cyclezurich;

/**
 * This method reads the tags "name", "description" and "coordinates" from the KML file
 * and then assigns them to objects in order to create an object list
 * @author Ariadni Gaki
 *
 */

public class Placemark {

    private String name;
    private String description;
    private String coordinates;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }



    /**
     *Presents only the name of the Placemarks in the spinner
     */
    @Override
    public String toString() {
        return  name;
    }



}
