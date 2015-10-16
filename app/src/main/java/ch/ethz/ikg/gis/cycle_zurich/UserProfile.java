package ch.ethz.ikg.gis.cycle_zurich;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    // Create boolean value which declares whether the user likes the direct or more attractive route
    // = true, User prefers direct route
    // = false, User prefers attractive route
    public boolean directRoute;

    // Assign Spinner
    private Spinner spinnerProf;
    private Button button_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Assign spinner and button
        spinnerProf = (Spinner) findViewById(R.id.spinner_profile);
        button_ok = (Button) findViewById(R.id.button_ok);

        // Create Spinner
        createProfileSpinner();

        // Add Button listener
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeValue();

            }
        });
    }

    public void createProfileSpinner()
    {
        // Create list for values
        List<String> list = new ArrayList<String>();

        // Add items
        list.add("Direct");
        list.add("Attractive");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProf.setAdapter(dataAdapter);

    }

    public void storeValue()
    {
        // Predefine string
        String selProf = String.valueOf(spinnerProf.getSelectedItem());

        if (selProf.equals("Direct")) {
            this.directRoute = true;
        }
        else {
            this.directRoute = false;
        }

        // Go back to Startpage
        super.onBackPressed();
    }

    public boolean getProfile()
    {
        return this.directRoute;
    }
}
