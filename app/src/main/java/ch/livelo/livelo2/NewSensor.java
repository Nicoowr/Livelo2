package ch.livelo.livelo2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ch.livelo.livelo2.DB.SensorDAO;

/**
 * Created by Nico on 27/10/2017.
 */

// put extra:   "id", "new"

public class NewSensor extends Activity {

    private Button save = null;
    private EditText name_edit = null;
    private EditText sensor_id_edit = null;
    private EditText latitude_edit = null;
    private EditText longitude_edit = null;
    private EditText depth_edit = null;

    private SensorDAO sensorDAO;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sensor);
        if(getIntent().getBooleanExtra("new", false)) fillFields(getIntent().getStringExtra("id"));


        sensorDAO = new SensorDAO(NewSensor.this);
        sensorDAO.open();

        name_edit = (EditText) findViewById(R.id.sensor_name);
        sensor_id_edit = (EditText) findViewById(R.id.sensor_id);
        latitude_edit = (EditText) findViewById(R.id.sensor_latitude);
        longitude_edit = (EditText) findViewById(R.id.sensor_longitude);
        depth_edit = (EditText) findViewById(R.id.sensor_depth);

        save = (Button) findViewById(R.id.save_sensor);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSensor();
            }
        });
    }

    public void saveSensor(){


        String name = name_edit.getText().toString();
        String sensor_id = sensor_id_edit.getText().toString();
        double latitude = Double.parseDouble(latitude_edit.getText().toString());
        double longitude = Double.parseDouble(longitude_edit.getText().toString());
        double depth = Double.parseDouble(depth_edit.getText().toString());

        sensorDAO.addSensor(name,sensor_id,latitude,longitude,depth);

        //close the DAO here ?
    }

    @Override
    protected void onResume() {
        sensorDAO.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorDAO.close();
        super.onPause();
    }

    private void fillFields(String id){
        // TODO fill the fielde with the data of the existing sensor
    }
}
