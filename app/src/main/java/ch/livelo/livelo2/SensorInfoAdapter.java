package ch.livelo.livelo2;

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

public class SensorInfoAdapter extends ArrayAdapter<String[]> {
    public SensorInfoAdapter(Context context, List<String[]> sensorInfo) {
        super(context, 0, sensorInfo);
    }

    public SensorInfoAdapter(Context context, int textViewResourceId, List<String[]> sensorInfo) {
        super(context, textViewResourceId, sensorInfo);
    }

    public String[] getItem(int position){
        return super.getItem(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        String[] sensor_item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.sensor_single_info, parent, false);
        }

        TextView item_text = (TextView) convertView.findViewById(R.id.sensor_info_item_text);
        TextView item = (TextView) convertView.findViewById(R.id.sensor_info_item);

        item_text.setText(sensor_item[0]);
        item.setText(sensor_item[1]);



        return convertView;
    }

    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }
}
