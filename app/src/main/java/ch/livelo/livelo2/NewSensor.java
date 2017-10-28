package ch.livelo.livelo2;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Nico on 27/10/2017.
 */

// put extra:   "id", "new"

public class NewSensor extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sensor);
        if(getIntent().getBooleanExtra("new", false)) fillFields(getIntent().getStringExtra("id"));
    }

    private void fillFields(String id){
        // TODO fill the fielde with the data of the existing sensor
    }
}
