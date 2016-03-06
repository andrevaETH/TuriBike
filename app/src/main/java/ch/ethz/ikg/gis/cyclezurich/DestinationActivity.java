package ch.ethz.ikg.gis.cyclezurich;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity enables the user to select their favourite destination from a predefined list.
 */
public class DestinationActivity extends Activity implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener{
    // reference to the GUI elements
    Spinner spinner; // a spinner indicates all the options(destinations)
    public static List<Placemark100> placemarks = new ArrayList<Placemark100>();
    public static List<String> placemarksnames = new ArrayList<String>();
    public List<String> placemarksdescription = new ArrayList<String>();
    public List<String> placemarkscoordinates = new ArrayList<String>();
    public Placemark100 pl1;
    public Placemark100 pl2;
    public Placemark100 pl3;
    public Placemark100 pl4;
    public Placemark100 pl5;
    public Placemark100 pl6;
    public Placemark100 pl7;
    public Placemark100 pl8;
    public Placemark100 pl9;
    public Placemark100 pl10;
    public Placemark100 pl11;
    public Placemark100 pl12;
    public Placemark100 pl13;
    public Placemark100 pl14;
    public Placemark100 pl15;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        findViewsById();

        /**
         *If there is an external memory card, a filter is implemented which only selects the files with the extension .kml and
         *reads these files.Afterwards, a parser is used which reads the elements of the KML file and creates a list with the placemarks.
         *Then, a spinner is used, which presents only the names of the placemarks.
         */
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            try {
//                // File file = new
//                // File(Environment.getExternalStorageDirectory()
//                // + "/" + "Example.kml");
//
//                File directory = Environment.getExternalStorageDirectory();
//                FilenameFilter filter = new FilterFileExtension();
//                File[] file = directory.listFiles(filter);
//
//                if (file == null || file.length == 0) { // if there is no KML file
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "There is no destination data in your external memory card",
//                            Toast.LENGTH_LONG).show();
//                } else if (file.length > 1) {
//
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "Please, leave only one destination data file in your external memory card",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    File kmlfile = file[0];
//                    placemarks = SAXXMLParser
//                            .parse(new InputSource(new InputStreamReader(
//                                    new FileInputStream(kmlfile))));
//
//                    ArrayAdapter<Placemark> adapter = new ArrayAdapter<Placemark>(
//                            this, R.layout.list_item, placemarks);
//                    spinner.setAdapter((SpinnerAdapter) new NothingSelectedSpinnerAdapter(
//                            adapter,
//                            R.layout.contact_spinner_row_nothing_selected,
//                            // R.layout.contact_spinner_nothing_selected_dropdown,
//                            // //
//                            // Optional
//                            this));
//
//                    spinner.setOnItemSelectedListener(this);
//
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setPrompt("Select your favorite Destination!");
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//
//        }
        // Elements of the predefined list
        pl1 = new Placemark100("Zuerichsee (Buerkliplatz)", "Nature", "8.541768,47.366395");
        pl2 = new Placemark100("Grossmuenster", "Heritage", "8.543599,47.370177");
        pl3 = new Placemark100("Fraumuenster", "Heritage", "8.541798,47.369871");
        pl4 = new Placemark100("Bahnhofstrasse (Paradeplatz)", "Heritage", "8.539275,47.369893");
        pl5 = new Placemark100("Kunsthaus", "Museum", "8.548604,47.370425");
        pl6 = new Placemark100("Ueetliberg", "Nature", "8.491403,47.349703");
        pl7 = new Placemark100("ETH", "Heritage", "8.548699,47.376614");
        pl8 = new Placemark100("Niederdorf", "Heritage", "8.543980,47.371503");
        pl9 = new Placemark100("Zoo", "Nature", "8.574489,47.384048");
        pl10 = new Placemark100("Lindenhof", "Nature", "8.541378,47.373006");
        pl11 = new Placemark100("Landesmuseum", "Museum", "8.540715,47.378639");
        pl12 = new Placemark100("Opernhaus", "Heritage", "8.546574,47.365346");
        pl13 = new Placemark100("Botanischer Garten", "Museum", "8.534284,47.371131");
        pl14 = new Placemark100("Zuerihorn (China Garten)", "Nature", "8.551677,47.354832");
        pl15 = new Placemark100("Schipfe (Gemuesebruecke)", "Heritage", "8.542266,47.371723");
        placemarks.add(pl1);
        placemarks.add(pl2);
        placemarks.add(pl3);
        placemarks.add(pl4);
        placemarks.add(pl5);
        placemarks.add(pl6);
        placemarks.add(pl7);
        placemarks.add(pl8);
        placemarks.add(pl9);
        placemarks.add(pl10);
        placemarks.add(pl11);
        placemarks.add(pl12);
        placemarks.add(pl13);
        placemarks.add(pl14);
        placemarks.add(pl15);
        placemarksnames.add(pl1.name);
        placemarksnames.add(pl2.name);
        placemarksnames.add(pl3.name);
        placemarksnames.add(pl4.name);
        placemarksnames.add(pl5.name);
        placemarksnames.add(pl6.name);
        placemarksnames.add(pl7.name);
        placemarksnames.add(pl8.name);
        placemarksnames.add(pl9.name);
        placemarksnames.add(pl10.name);
        placemarksnames.add(pl11.name);
        placemarksnames.add(pl12.name);
        placemarksnames.add(pl13.name);
        placemarksnames.add(pl14.name);
        placemarksnames.add(pl15.name);


        String coor2 = String.valueOf(8.543599) + "," + String.valueOf(47.370177);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.list_item, placemarksnames);
                    spinner.setAdapter((SpinnerAdapter) new NothingSelectedSpinnerAdapter(
                            adapter,
                            R.layout.contact_spinner_row_nothing_selected,
                            // R.layout.contact_spinner_nothing_selected_dropdown,
                            // //
                            // Optional
                            this));

                    spinner.setOnItemSelectedListener(this);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setPrompt("Select your favorite Destination!");
    }

    private void findViewsById() {
        spinner = (Spinner) findViewById(R.id.spinner);
    }

    private boolean isSpinnerInitial = true;









    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {

        if (isSpinnerInitial) {
            isSpinnerInitial = false;
        } else {



            Intent myFirstIntent = new Intent(this, MapsActivity.class);
            myFirstIntent.putExtra("key1", "ActivityTwo has been launched");

            myFirstIntent.putExtra("coordinates", placemarks.get(pos - 1).coordinates);
            myFirstIntent.putExtra("key2", 2014);

                    startActivity(myFirstIntent);
        }

    }
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }



    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }
}
