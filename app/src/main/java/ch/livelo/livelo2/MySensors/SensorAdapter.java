package ch.livelo.livelo2.MySensors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.R;

/**
 * Created by Nico on 28/10/2017.
 */

public class SensorAdapter extends ArrayAdapter<Sensor> {
    public SensorAdapter(Context context, List<Sensor> sensors) {
        super(context, 0, sensors);
    }

    public SensorAdapter(Context context, int textViewResourceId, List<Sensor> sensors) {
        super(context, textViewResourceId, sensors);
    }

    public Sensor getItem(int position){
        return super.getItem(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Sensor sensor = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.sensor_short_layout, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.mysensor_name);
        TextView coord = (TextView) convertView.findViewById(R.id.myposition);
        name.setText(sensor.getName());
        //coord.setText("Latitude: " + sensor.getPosition()[0] + "\nLongitude: " + sensor.getPosition()[1]);
        TextView sensor_id = (TextView) convertView.findViewById(R.id.mysensor_id);
        sensor_id.setText("Sensor ID: " + sensor.getId());


        return convertView;
    }

    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }
}
