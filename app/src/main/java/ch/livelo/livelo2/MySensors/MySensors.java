package ch.livelo.livelo2.MySensors;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;
import ch.livelo.livelo2.DB.SensorDB;
import ch.livelo.livelo2.R;

/**
 * Created by Nico on 27/10/2017.
 */

public class MySensors extends AppCompatActivity {

    private SensorDAO sensorDAO = null;
    private ListView sensorsView = null;
    private List<Sensor> sensorList = null;
    private SensorAdapter sensorAdapter = null;
    private ArrayList<HashMap<String,String>> sensorsArrayList;
    private String[] from = null;
    private int[] to = null;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sensors);


        sensorsView = (ListView) findViewById(R.id.sensorsView);
        sensorDAO = new SensorDAO(MySensors.this);
        sensorDAO.open();
        sensorList = sensorDAO.getAllSensors();

     //   //Setting the adapter
     //   sensorsArrayList=new ArrayList<>();
     //   for (int i=0;i<sensorList.size();i++)
     //   {
     //       HashMap<String,String> hashMap=new HashMap<>();//create a hashmap to store the data in key value pair
     //       hashMap.put("name",sensorList.get(i).getName());
     //       hashMap.put("id",sensorList.get(i).getId());
     //       sensorsArrayList.add(hashMap);//add the hashmap into arrayList
     //   }
     //   from = new String[]{"name","id"};//string array
     //   to = new int[]{android.R.id.text2,android.R.id.text1};//int array of views id's

        sensorAdapter = new SensorAdapter(MySensors.this,android.R.layout.simple_list_item_1,sensorList);

        sensorsView.setAdapter(sensorAdapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_sensors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.select_sensors:
                sensorsView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                sensorAdapter = new SensorAdapter(this, android.R.layout.simple_list_item_multiple_choice,sensorList){
                  @Override
                  public View getView(int position, View convertView, ViewGroup parent) {

                      Sensor sensor = getItem(position);

                      if (convertView == null) {
                          convertView = LayoutInflater.from(this.getContext())
                                  .inflate(R.layout.sensor_short_layout_multiple_choice, parent, false);
                      }

                      CheckedTextView name = (CheckedTextView) convertView.findViewById(R.id.mysensor_name);
                      name.setText(sensor.getName());
                      TextView coord = (TextView) convertView.findViewById(R.id.myposition);
                      coord.setText("Latitude: " + sensor.getPosition()[0] + " Longitude: " + sensor.getPosition()[1]);

                      return convertView;
                  }
                };
                sensorsView.setAdapter(sensorAdapter);
                return true;

            case R.id.delete_sensors:
                SparseBooleanArray sparseBooleanArray = sensorsView.getCheckedItemPositions();
                long lsuppr = 0;
                for(int i = 0; i < sparseBooleanArray.size(); i++){
                    if(sparseBooleanArray.valueAt(i)){
                        Sensor toBeDeleted = sensorAdapter.getItem(sparseBooleanArray.keyAt(i));
                        lsuppr += sensorDAO.deleteSensor(toBeDeleted.getId());
                        sensorList.remove(toBeDeleted);
                    }
                }

                sensorAdapter.notifyDataSetChanged();

                Toast.makeText(MySensors.this, lsuppr +" sensors were deleted.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.delete_all:
                long lsuppr2 = 0;
                lsuppr2 = sensorDAO.deleteAll();
                sensorList.clear();
                sensorAdapter.notifyDataSetChanged();

                Toast.makeText(MySensors.this, lsuppr2 +" sensors were deleted.", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

   /* public void updateReceiptsList(List<Sensor> newlist) {
        sensorList.clear();
        sensorList.addAll(newlist);
        this.notifyDataSetChanged();
    }*/
}
