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
import java.util.List;

public class DestinationActivity extends Activity implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener{
    // reference to the GUI elements
    Spinner spinner; // a spinner indicates all the options(destinations)
    List<Placemark> placemarks = null; // a list including the placemarks from
    // the KML file
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
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                // File file = new
                // File(Environment.getExternalStorageDirectory()
                // + "/" + "Example.kml");

                File directory = Environment.getExternalStorageDirectory();
                FilenameFilter filter = new FilterFileExtension();
                File[] file = directory.listFiles(filter);

                if (file == null || file.length == 0) { // if there is no KML file
                    Toast.makeText(
                            getApplicationContext(),
                            "There is no destination data in your external memory card",
                            Toast.LENGTH_LONG).show();
                } else if (file.length > 1) {

                    Toast.makeText(
                            getApplicationContext(),
                            "Please, leave only one destination data file in your external memory card",
                            Toast.LENGTH_LONG).show();
                } else {
                    File kmlfile = file[0];
                    placemarks = SAXXMLParser
                            .parse(new InputSource(new InputStreamReader(
                                    new FileInputStream(kmlfile))));
                    ;
                    ArrayAdapter<Placemark> adapter = new ArrayAdapter<Placemark>(
                            this, R.layout.list_item, placemarks);
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

            } catch (IOException e) {
                e.printStackTrace();

            }

        }
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

            Placemark placemark = (Placemark) parent.getItemAtPosition(pos);
            // Toast.makeText(parent.getContext(), placemark.getDetails(),
            // Toast.LENGTH_LONG).show();

            Intent myFirstIntent = new Intent(this, MapsActivity.class);
            myFirstIntent.putExtra("key1", "ActivityTwo has been launched");
            Log.e("Error0", "Now it should enter mapsactivity");
            myFirstIntent.putExtra("coordinates", placemark.getCoordinates());
            myFirstIntent.putExtra("key2", 2014);
            Log.e("Error1", "Now it should enter mapsactivity");
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
