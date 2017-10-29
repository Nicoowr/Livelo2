package ch.livelo.livelo2;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

import ch.livelo.livelo2.DB.Sensor;

/**
 * Created by Nico on 27/10/2017.
 * convention put extra:   String "id", boolean "new"
 * TODO démarer le GPS au début de cette activité
 *
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

    private Sensor sensor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sensor);
       // if(getIntent().getBooleanExtra("new", false)) fillFields(getIntent().getStringExtra("id"));


        sensorDAO = new SensorDAO(NewSensor.this);
        sensorDAO.open();

        name_edit = (EditText) findViewById(R.id.sensor_name);
        sensor_id_edit = (EditText) findViewById(R.id.sensor_id);
        latitude_edit = (EditText) findViewById(R.id.sensor_latitude);
        longitude_edit = (EditText) findViewById(R.id.sensor_longitude);
        depth_edit = (EditText) findViewById(R.id.sensor_depth);

        //sensor_id_edit.setText(getIntent().getStringExtra("id"));

        save = (Button) findViewById(R.id.save_sensor);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSensor();
            }
        });

       /* if(getIntent().getBooleanExtra("new", false)){
            // TODO récupérer les info du capteur qui existe déja
            sensor = new Sensor(getIntent().getStringExtra("id")); //
            // sensor.complete(); // fonction qui prend l'id et qui remplit les champs du sensor depuis la database, ça peut aussi etre fait dans le constructeur
            fillFields();
        }
        else {
            sensor = new Sensor("my new sensor", getIntent().getStringExtra("id"));
        }*/
    }

    public void saveSensor(){


        String name = name_edit.getText().toString();
        String sensor_id = sensor_id_edit.getText().toString();
        double latitude = Double.parseDouble(latitude_edit.getText().toString());
        double longitude = Double.parseDouble(longitude_edit.getText().toString());
        double depth = Double.parseDouble(depth_edit.getText().toString());

        Sensor sensor = new Sensor(name,sensor_id,latitude,longitude,depth);
        if(sensorDAO.exists(sensor)) {
            Toast.makeText(NewSensor.this, "This sensor already exists", Toast.LENGTH_SHORT).show();
            sensorDAO.close();
            NewSensor.this.finish();
        }else {
            sensorDAO.addSensor(name, sensor_id, latitude, longitude, depth);
            Toast.makeText(NewSensor.this, "Sensor saved, with id= " + sensor_id, Toast.LENGTH_SHORT).show();
            sensorDAO.close();
            NewSensor.this.finish();
        }
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

    private void fillFields(){ // fill the different input sections with the existing data
        // TODO fill the fields with the data of the existing sensor
        // ex: sensor_name.setText((sensor.getName()));
    }
}
