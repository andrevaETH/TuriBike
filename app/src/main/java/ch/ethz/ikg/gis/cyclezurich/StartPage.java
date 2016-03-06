package ch.ethz.ikg.gis.cyclezurich;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;


/**
 * StartPage is the gateway of the app. The user can be transferred to a specific activity by
 * clicking on the corresponding imagebutton.
 */
public class StartPage extends AppCompatActivity {
    // Declare variables
    private Button button_exit;
    private ImageButton button_startMap, button_startQuestions,  button_top10Destinations, button_pumpingStation, button_rentalStation, button_bikeParking;
    private boolean userProfile;
    int REQUEST_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        // Assign Buttons
        button_exit = (Button) findViewById(R.id.exitButton);
        button_top10Destinations = (ImageButton) findViewById(R.id.top10DestinationsButton);
        button_startQuestions = (ImageButton) findViewById(R.id.profileButton);
        button_startMap = (ImageButton) findViewById(R.id.offlineMapButton);
        button_pumpingStation = (ImageButton) findViewById(R.id.pumpingStationButton);
        button_rentalStation = (ImageButton) findViewById(R.id.rentABikeButton);
        button_bikeParking = (ImageButton) findViewById(R.id.bikeParkingButton);

        // Add listeners
        button_startQuestions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_startQuestionsClicked();
            }
        });

        button_startMap.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_startMapClicked();
            }
        });

        button_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_exitClicked();
            }
        });

        button_top10Destinations.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_top10DestinationsClicked();
            }
        });

        button_pumpingStation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_pumpingStationClicked();
            }
        });

        button_rentalStation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_rentalStationClicked();
            }
        });

        button_bikeParking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                button_bikeParkingClicked();
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_page, menu);
        return true;
    }
    // Methods for clicked buttons

    public void button_startMapClicked()
    {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);

    }

    public void button_exitClicked()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void button_startQuestionsClicked()
   {
        Intent userProfileIntent = new Intent(this, UserProfile.class);
        // Start Activity and return the result
        startActivityForResult(userProfileIntent, REQUEST_ID);
    }

    public void button_top10DestinationsClicked()
    {
        Intent mapIntent = new Intent(this, DestinationActivity.class);
        startActivity(mapIntent);
    }

    public void button_pumpingStationClicked()
    {
        Intent mapIntent = new Intent(this, PumpingStation.class);
        startActivity(mapIntent);
    }

    private void button_rentalStationClicked() {
        Intent mapIntent = new Intent(this, RentalStation.class);
        startActivity(mapIntent);
    }

    private void button_bikeParkingClicked() {
        Intent mapIntent = new Intent(this, BikeParking.class);
        startActivity(mapIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent Data)
    {
        // Get values from User Profile
       if(requestCode == REQUEST_ID)
       {
           if(resultCode == RESULT_OK){
                userProfile = Data.getBooleanExtra("userProfile", userProfile);
            }
        }
   }

}

