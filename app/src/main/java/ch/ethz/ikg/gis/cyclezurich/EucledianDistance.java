package ch.ethz.ikg.gis.cyclezurich;

/**
 * Calculation of euclidean distance
 */
public class EucledianDistance{
    public double calcEucledianDistance(MyPoint originPoint, MyPoint destinationPoint){
        return Math.sqrt(Math.pow(destinationPoint.X - originPoint.X, 2.0) + Math.pow(destinationPoint.Y - originPoint.Y, 2.0));
    }
}
