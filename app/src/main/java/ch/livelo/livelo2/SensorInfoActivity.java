package ch.livelo.livelo2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;
import ch.livelo.livelo2.MySensors.MySensors;

/**
 *
 * TODO transmettre l'ID quand on appelle la map
 *
 */

public class SensorInfoActivity extends AppCompatActivity {
    private String id = "";
    private Sensor sensor;

    private SensorDAO sensorDAO;
    private DataDAO dataDAO;
    private SQLiteDatabase mDb;

    private ListView sensorInfoView;
    private SensorInfoAdapter sensorInfoAdapter;
    private List<String[]> sensorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);
        getSupportActionBar().setTitle("Sensor information");

        id = getIntent().getStringExtra("id");

        sensorInfoView = (ListView) findViewById(R.id.sensor_info);
        sensorDAO = new SensorDAO(SensorInfoActivity.this);
        sensorDAO.open();

        dataDAO = new DataDAO(SensorInfoActivity.this);
        dataDAO.open();

        mDb = sensorDAO.getDb();

        sensor = sensorDAO.getSensor(id);
        sensorInfo = sensor.getInfo();

        sensorInfoAdapter = new SensorInfoAdapter(this, android.R.layout.simple_list_item_1, sensorInfo);
        sensorInfoView.setAdapter(sensorInfoAdapter);

        Button button_map = (Button)findViewById(R.id.button_map);
        button_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorInfoActivity.this, SensorsMapsActivity.class);
                intent.putExtra("id", sensor.getId());
                startActivity(intent);
            }
        });



        // TextView tv_id = (TextView) findViewById(R.id.tv_id);
       // tv_id.setText(id);
    }

    public void goToEdit(View view) {
        Intent intent = new Intent(SensorInfoActivity.this, NewSensor.class);
        intent.putExtra("id", id);
        intent.putExtra("new", true);
        startActivity(intent);
    }

    public void goToDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this item?");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sensorDAO.deleteSensor(sensor.getId());
                dataDAO.deleteSensorData(sensor.getId());
                SensorInfoActivity.this.finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void goToDeleteData(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this sensor's data?");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                long deletedData = dataDAO.deleteSensorData(sensor.getId());
                sensorDAO.updateSensor(sensor.getId(),-1, -1, -1 ,-1 , 0, -1);
                Toast.makeText(SensorInfoActivity.this,"" + deletedData + " erased from sensor " + sensor.getName(), Toast.LENGTH_SHORT).show();
                SensorInfoActivity.this.finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
