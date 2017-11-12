package ch.livelo.livelo2;

import android.app.Activity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Iterator;
import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;

/**
 * Visualize sensor's data
 */

public class DataViewActivity extends Activity{
    private String id = "";
    private DataDAO dataDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        id = getIntent().getStringExtra("id");
        dataDAO = new DataDAO(DataViewActivity.this);
        dataDAO.open();

        List<Long> pressureData = dataDAO.getSensorData(id);
        List<Long> pressureTimestamps = dataDAO.getSensorTimestamp(id);
        Iterator pressureCursor = pressureData.iterator();
        Iterator timeCursor = pressureTimestamps.iterator();
        DataPoint[] dataPoints = new DataPoint[pressureData.size()];

        for(int i = 0; i < dataPoints.length; i++){
            dataPoints[i] = new DataPoint((long) timeCursor.next(), (long) pressureCursor.next());
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
        graph.addSeries(series);

        dataDAO.close();
    }
}
