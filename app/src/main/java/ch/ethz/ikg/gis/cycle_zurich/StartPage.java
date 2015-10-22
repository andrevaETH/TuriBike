package ch.ethz.ikg.gis.cycle_zurich;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;

public class StartPage extends AppCompatActivity {

    private ImageButton button_startMap, button_startQuestions;
    private boolean userProfile;
    int REQUEST_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        // Assign Buttons //Chara: changed button id 22/10/2015
        button_startMap = (ImageButton) findViewById(R.id.top10DestinationsButton);
        button_startQuestions = (ImageButton) findViewById(R.id.profileButton);

        //------------------------------------------------------------------------------------------
        //Chara: Commented some parts because the app was crashing... we can remove comments later | 22/10/2015
        //------------------------------------------------------------------------------------------
        
        // Add listeners
//        button_startQuestions.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                button_startQuestionsClicked();
//            }
//        });
//
//        button_startMap.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                button_startMapClicked();
//            }
//        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_start_page, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    public void button_startMapClicked()
//    {
//        Intent mapIntent = new Intent(this, MapsActivity.class);
//        startActivity(mapIntent);
//
//    }
//
//    public void button_startQuestionsClicked()
//    {
//        Intent userProfileIntent = new Intent(this, UserProfile.class);
//
//        // Start Activity and return the result
//        startActivityForResult(userProfileIntent, REQUEST_ID);
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent Data)
//    {
//        // Get values from User Profile
//        if(requestCode == REQUEST_ID)
//        {
//            if(resultCode == RESULT_OK){
//                userProfile = Data.getBooleanExtra("userProfile", userProfile);
//            }
//        }
//    }

}

