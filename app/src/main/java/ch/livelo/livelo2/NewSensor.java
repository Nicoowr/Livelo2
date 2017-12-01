package ch.livelo.livelo2;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place; // "Place" is not resolved
import com.google.android.gms.location.places.ui.PlacePicker; // "ui" is not resolved
import com.google.android.gms.maps.model.LatLng;
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

import static java.lang.String.valueOf;

/**
 * Created by Nico on 27/10/2017.
 * convention put extra:   String "id"
 * TODO démarer le GPS au début de cette activité
 * TODO réparer le place picker, ne marche pas quand il y a internet
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

    private int PLACE_PICKER_REQUEST = 1;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sensor);
       // if(getIntent().getBooleanExtra("new", false)) fillFields(getIntent().getStringExtra("id"));


        String id = getIntent().getStringExtra("id");

        name_edit = (EditText) findViewById(R.id.sensor_name);
        sensor_id_edit = (EditText) findViewById(R.id.sensor_id);
        latitude_edit = (EditText) findViewById(R.id.sensor_latitude);
        longitude_edit = (EditText) findViewById(R.id.sensor_longitude);
        depth_edit = (EditText) findViewById(R.id.sensor_depth);

        save = (Button) findViewById(R.id.save_sensor);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSensor(v);
            }
        });

        sensor = new Sensor(id); //

        sensorDAO = new SensorDAO(NewSensor.this);
        sensorDAO.open();
        if(sensorDAO.exists(id)) {
            sensor = sensorDAO.getSensor(id);
            fillFields();
        }
        sensorDAO.close();

       sensor_id_edit.setText(sensor.getId());

    }

    public void pickPlace(View view){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                sensor.setLatitude(place.getLatLng().latitude);
                sensor.setLongitude(place.getLatLng().longitude);
                latitude_edit.setText(valueOf(sensor.getLatitude()));
                longitude_edit.setText(valueOf(sensor.getLongitude()));
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }


    public void saveSensor(View view) {
        //public void saveSensor(){

       // String name = name_edit.getText().toString();
       // String sensor_id = sensor_id_edit.getText().toString();
       // double latitude = Double.parseDouble(latitude_edit.getText().toString());
       // double longitude = Double.parseDouble(longitude_edit.getText().toString());
       // double depth = Double.parseDouble(depth_edit.getText().toString());
        //Sensor sensor = new Sensor(name,id,latitude,longitude,depth);

        fillSensor();
        sensorDAO.open();
        if(sensor.getName().isEmpty() || sensor.getId().isEmpty()){
            Toast.makeText(NewSensor.this, "You must enter a name and a ID at least.", Toast.LENGTH_SHORT).show();
            return;
        }else if(sensorDAO.exists(sensor.getId())) {
            sensorDAO.deleteSensor(sensor.getId());
            Toast.makeText(NewSensor.this, "delete existing item", Toast.LENGTH_SHORT).show();
        }


        sensorDAO.addSensor(sensor);
        Toast.makeText(NewSensor.this, "Sensor saved, with id= " + sensor.getId(), Toast.LENGTH_SHORT).show();
        sensorDAO.close();
        NewSensor.this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorDAO.close();
    }

    private void fillFields(){ // fill the different input sections with the existing data
        // TODO fill the fields with the data of the existing sensor
        name_edit.setText(sensor.getName());
        latitude_edit.setText(valueOf(sensor.getLatitude()));
        longitude_edit.setText(valueOf(sensor.getLongitude()));
        depth_edit.setText(valueOf(sensor.getDepth()));
    }

    private void fillSensor(){
        sensor.setName(name_edit.getText().toString());
        sensor.setId(sensor_id_edit.getText().toString());
        sensor.setLatitude(Double.parseDouble(latitude_edit.getText().toString()));
        sensor.setLongitude(Double.parseDouble(longitude_edit.getText().toString()));
        sensor.setDepth(Double.parseDouble(depth_edit.getText().toString()));

    }
}
