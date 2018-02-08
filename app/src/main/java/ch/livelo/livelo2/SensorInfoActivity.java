package ch.livelo.livelo2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;

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


        // TextView tv_id = (TextView) findViewById(R.id.tv_id);
       // tv_id.setText(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensor_information_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId()) {
            case R.id.edit_sensor:
                Intent intent = new Intent(SensorInfoActivity.this, NewSensor.class);
                intent.putExtra("id", id);
                intent.putExtra("new", true);
                startActivity(intent);
                return true;


            case R.id.delete_sensor:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete this sensor?");
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
                return true;

            case R.id.see_on_map:
                intent = new Intent(SensorInfoActivity.this, SensorMapsActivity.class);
                intent.putExtra("id", sensor.getId());
                startActivity(intent);
                return true;

            case R.id.view_data:
                Intent intent2 = new Intent(SensorInfoActivity.this, DataViewActivity.class);
                intent2.putExtra("id",id);
                startActivity(intent2);
                return true;

            case R.id.delete_data:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Delete this sensor's data?");
                builder2.setMessage("Are you sure?");

                builder2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        long deletedData = dataDAO.deleteSensorData(sensor.getId());
                        sensorDAO.updateSensor(sensor.getId(), "null", -1, -1, -1 ,-1 , -1, 0, -1, -1);
                        Toast.makeText(SensorInfoActivity.this,"" + deletedData + " erased from sensor " + sensor.getName(), Toast.LENGTH_SHORT).show();
                        SensorInfoActivity.this.finish();
                    }
                });

                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
                sensorDAO.updateSensor(sensor.getId(), "null", -1, -1, -1 ,-1 , -1, 0, -1, -1);
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

    public void goToViewData(View view){
        Intent intent = new Intent(SensorInfoActivity.this, DataViewActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

}
