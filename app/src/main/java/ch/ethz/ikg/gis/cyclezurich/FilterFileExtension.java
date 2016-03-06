package ch.ethz.ikg.gis.cyclezurich;

import java.io.File;
import java.io.FilenameFilter;


/**
 *This class implements a filter in the files so as to identify only files with the extension .kml
 *
 */
public class FilterFileExtension implements FilenameFilter {

    @Override
    public boolean accept(File dir, String filename) {
        if (filename.endsWith(".kml")){
            return true;
        }
        return false;
    }

}