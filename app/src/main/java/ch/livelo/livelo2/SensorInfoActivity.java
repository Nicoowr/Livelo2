package ch.livelo.livelo2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import ch.livelo.livelo2.DB.Sensor;

// TODO ajouter un bouton modification qui remmplit automatiquement l'activité new sensor
// TODO ajouter un bouton "view on the map"

public class SensorInfoActivity extends AppCompatActivity {
    private String id = "";
    private Sensor sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);
        getSupportActionBar().setTitle("Sensor information"); // TODO mettre en string resource
        id = getIntent().getStringExtra("id");

        TextView tv_id = (TextView) findViewById(R.id.tv_id);
        tv_id.setText(id);
    }

    public void goToEdit(View view) {
        Intent intent = new Intent(SensorInfoActivity.this, NewSensor.class);
        intent.putExtra("id", id);
        intent.putExtra("new", true);
        startActivity(intent);
    }

    public void goToMap(View view) {

        //LatLng pos = new LatLng(sensor.getLat(), sensor.getLng());

        Intent intent = new Intent(SensorInfoActivity.this, SensorsMapsActivity.class);
        //intent.putExtra("lat", sensor.getLat());
        //intent.putExtra("lng", sensor.getLng());
        intent.putExtra("lat", 46.519606d);
        intent.putExtra("lng", 6.565030d);
        startActivity(intent);
    }


}
