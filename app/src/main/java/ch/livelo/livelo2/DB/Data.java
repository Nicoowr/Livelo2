package ch.livelo.livelo2.DB;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import ch.livelo.livelo2.CurrentSensor;
import ch.livelo.livelo2.DataViewActivity;
import ch.livelo.livelo2.SensorInfoActivity;

/**
 * Created by Nico on 27/10/2017.
 */

public class Data {
    private String sensor_id;
    private List<Long> timeStamp;
    private List<Long> values;
    private Context call_context;
    private long lastCollectDataNb;

    public Data(String id, Context context) {
        SensorDAO sensorDAO = new SensorDAO(context);
        sensorDAO.open();
        //TODO : what to do when the sensor doesnt exist
        this.sensor_id = id;
        Sensor sensor = sensorDAO.getSensor(id);
        this.lastCollectDataNb = sensor.getLastCollectDataNb();

        sensorDAO.close();

        DataDAO dataDAO = new DataDAO(context);
        dataDAO.open();

        this.timeStamp = dataDAO.getSensorTimestamp(id);
        this.values = dataDAO.getSensorData(id);
        dataDAO.close();


    }

    public Data(String id, List<Long> timeStamp, List<Long> values, Context context){
        SensorDAO sensorDAO = new SensorDAO(context);
        sensorDAO.open();

        Sensor sensor = sensorDAO.getSensor(id);
        this.sensor_id = id;
        this.timeStamp = timeStamp;
        this.values = values;
        this.lastCollectDataNb = sensor.getLastCollectDataNb();

        sensorDAO.close();
    }

    public String getSensorID(){
        return this.sensor_id;
    }
    public List<Long> getTimeStamp(){
        return this.timeStamp;
    }

    public List<Long> getValues(){
        return this.values;
    }

    public void setTimeStamp(List<Long> timeStamp){
        this.timeStamp = timeStamp;
    }

    public void setValues(List<Long> values){
        this.values = values;
    }

    public boolean send(String token, Context context){
        SensorDAO sensorDAO = new SensorDAO(context);
        sensorDAO.open();
        Sensor sensor = sensorDAO.getSensor(sensor_id);

        if(sensor.getLastCollectDataNb() ==0) {
            Toast.makeText(context, "No data to send", Toast.LENGTH_LONG).show();
            sensorDAO.close();
            return true;
        }

        call_context = context;
        JSONObject postData = new JSONObject();
        try {
            postData.put("cmd_key", "press");
            // data info
            postData.put("id", sensor_id);
            //postData.put("start", this.timeStamp.get(0));
            postData.put("start", this.timeStamp.get(timeStamp.size() - (int) this.lastCollectDataNb));
            //WARNING: collect twice without sending is dangerous
            postData.put("stop", this.timeStamp.get(timeStamp.size() - 1));
            postData.put("data", new JSONArray(this.values.subList(values.size() - (int) this.lastCollectDataNb,
                    values.size() - 1)));

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        new Data.httpRequest().execute("https://alpha.thinkee.ch/http/livelo", "POST", postData.toString(), token);
        sensor.setLastCollectDataNb(0);//To be sure it does not send twice any data
        sensorDAO.updateSensor(sensor.getId(), "null", -1, -1, -1, -1, -1, -1, -1, 0);

        sensorDAO.close();
        return true;

    }

    class httpRequest extends AsyncTask<String, String, String> {

        private Exception exception;

        protected String doInBackground(String... string) {
            String response = "";
            try {

                URL url = new URL(string[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("token", string[3]);
                connection.setRequestMethod(string[1]);
                OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
                request.write(string[2]);
                request.flush();
                request.close();

                // get the response, maybe not necessary
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                response = sb.toString();

                isr.close();
                reader.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(call_context, "sensors and data uploaded successfully", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {}

        //@Override
        //protected void onProgressUpdate(Void... values) {}

    }
}


