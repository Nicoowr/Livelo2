package ch.livelo.livelo2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;
import ch.livelo.livelo2.MySensors.MySensors;

// TODO ajouter un bouton modification qui remmplit automatiquement l'activité new sensor
// TODO ajouter un bouton "view on the map"

public class SensorInfoActivity extends AppCompatActivity {
    private String id = "";
    private Sensor sensor;

    private SensorDAO sensorDAO;
    private SQLiteDatabase mDb;

    private ListView sensorInfoView;
    private SensorInfoAdapter sensorInfoAdapter;
    private List<String[]> sensorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);
        getSupportActionBar().setTitle("Sensor information"); // TODO mettre en string resource


        id = getIntent().getStringExtra("id");

        sensorInfoView = (ListView) findViewById(R.id.sensor_info);
        sensorDAO = new SensorDAO(SensorInfoActivity.this);
        sensorDAO.open();
        mDb = sensorDAO.getDb();

        sensor = sensorDAO.getSensor(id);
        sensorInfo = sensor.getInfo();

        sensorInfoAdapter = new SensorInfoAdapter(this, android.R.layout.simple_list_item_1, sensorInfo);
        sensorInfoView.setAdapter(sensorInfoAdapter);



       // TextView tv_id = (TextView) findViewById(R.id.tv_id);
       // tv_id.setText(id);
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
