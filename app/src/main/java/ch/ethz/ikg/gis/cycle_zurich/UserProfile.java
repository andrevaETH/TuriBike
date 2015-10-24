package ch.ethz.ikg.gis.cycle_zurich;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    // Declaration section
    //----------------------------------------------------------------------------------------------

    // Create boolean value which declares whether the user likes the direct or more attractive route
    // = true, User prefers direct route
    // = false, User prefers attractive route
    public boolean directRoute;

    // Declare button Spinner
    private Spinner spinnerProf;

    // Declare buttons
    private Button button_home;
    private Button button_back;
    private Button button_save;
    private Button button_exit;

    //----------------------------------------------------------------------------------------------
    // OnCreate (when the activity is first created)
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

    // Relate to layout elements
    //----------------------------------------------------------------------------------------------
        spinnerProf = (Spinner) findViewById(R.id.spinner_profile);
        button_home = (Button) findViewById(R.id.homeButton);
        button_back = (Button) findViewById(R.id.backButton);
        button_save = (Button) findViewById(R.id.saveButton);
        button_exit = (Button) findViewById(R.id.exitButton);

        // Create Spinner
        createProfileSpinner();

    // Listeners
    //----------------------------------------------------------------------------------------------
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
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_saveClicked();

            }
        });
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_exitClicked();

            }
        });


    }

    //----------------------------------------------------------------------------------------------
    // Functions
    //----------------------------------------------------------------------------------------------
    public void createProfileSpinner() {
        // Create list for values
        List<String> list = new ArrayList<String>();

        // Add items
        list.add("Direct");
        list.add("Attractive");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProf.setAdapter(dataAdapter);

    }

    public void button_homeClicked()
    {
        Intent intent = new Intent(UserProfile.this,StartPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void button_backClicked()
    {
        onBackPressed();
    }

    public void button_saveClicked() {
        // Predefine string
        String selProf = String.valueOf(spinnerProf.getSelectedItem());

        if (selProf.equals("Direct")) {
            this.directRoute = true;
        } else {
            this.directRoute = false;
        }

        // Return value
        Intent resultData = new Intent();
        resultData.putExtra("userProfile", directRoute);
        setResult(Activity.RESULT_OK, resultData);

        // Toast
        Context context = getApplicationContext();
        CharSequence text = "Your profile settings have been saved!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void button_exitClicked() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}