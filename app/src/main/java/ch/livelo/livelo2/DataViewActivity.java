package ch.livelo.livelo2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        /** Assigning dates **/
        if(!pressureTimestamps.isEmpty()) {
            Date date0;//First point date
            Date dateEnd;//Last point date
            date0 = new Date((long) timeCursor.next());
            dataPoints[0] = new DataPoint(date0, (long) pressureCursor.next());
            //Other points
            for (int i = 1; i < dataPoints.length - 1; i++) {

                Date date = new Date((long) timeCursor.next());
                dataPoints[i] = new DataPoint(date, (long) pressureCursor.next());
            }
            dateEnd = new Date((long) timeCursor.next());
            dataPoints[dataPoints.length - 1] = new DataPoint(dateEnd, (long) pressureCursor.next());
            /********************/


            GraphView graph = (GraphView) findViewById(R.id.graph);

            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);

            //enabling scaling and scrolling
            graph.getViewport().setScalable(true);
            graph.getViewport().setScalableY(true);

            //Xlabel format
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(DataViewActivity.this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            graph.getViewport().setMinX(date0.getTime());
            graph.getViewport().setMaxX(dateEnd.getTime());
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getGridLabelRenderer().setHumanRounding(false);

            //Tap to get data label
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date toDate = new Date((long) dataPoint.getX());
                    String collectDate = df.format(toDate);

                    Toast.makeText(DataViewActivity.this, "Date: " + collectDate +", Pressure: " + dataPoint.getY() , Toast.LENGTH_SHORT).show();
                }
            });
            graph.addSeries(series);
        }else{
            Toast.makeText(DataViewActivity.this,"This sensor has no recorded data", Toast.LENGTH_SHORT).show();
        }

        dataDAO.close();
    }
}
