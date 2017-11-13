package ch.livelo.livelo2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

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
import java.util.Arrays;
import java.util.List;

import ch.livelo.livelo2.DB.Data;
import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;

/**
 * Created by Remi on 11/11/2017.
 * Descrittion here:
 *
 * <a href="https://docs.google.com/document/d/1z0cgWoqcFKSJzhEZ-QeP2fDH0947Sg1Jnz9677uyO5M/edit">
 *
 * TODO tester post pour data et sensor
 */

public class ServerLivelo {

    // post a new sensor or an update of an existing sensor
    public static boolean postSensor(Sensor sensor) {

        JSONObject postSensor = new JSONObject();
        try {
            //
            postSensor.put("cmd_key", "sensor");

            // sensor info
            postSensor.put("id", sensor.getId());
            postSensor.put("depth", sensor.getDepth());
            postSensor.put("lat", sensor.getLatitude());
            postSensor.put("lng", sensor.getLongitude());
            postSensor.put("depth", sensor.getDepth());
            //postSensor.put("alt", sensor.getAltitude()); // or it is found on the server with altitude api
            postSensor.put("name", sensor.getName());
            //postSensor.put("run", sensor.isRunning());
            //postSensor.put("period", sensor.getPeriod());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return post(postSensor.toString());
    }

    // post the data collected on a sensor
    public static boolean postData(Data data) {
        //JSONArray dataJSONArray = new JSONArray(Arrays.asList(data.getData()));
        JSONObject postData = new JSONObject();
        try {
            //
            postData.put("cmd_key", "raw_pressure");

            // sensor info
            // postData.put("id", data.getId());
            // postData.put("period", data.getPeriod());
            // //postData.put("cal_start", data.getLatitude());
            // //postData.put("cal_stop", data.get());
            // postData.put("start", data.getStart());
            // postData.put("stop", data.getStop());
            // postData.put("num", data.getNum());
            // postData.put("data", dataJSONArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return post(data.toString());
    }

    // to get all the sensors (only the coordinates and id)
    public static boolean getSensors() {
        return false;
    }

    // to get all the information about one sensor
    public static boolean getSensor(String id) {
        return false;
    }

    // to get the data collected by a sensor
    public static boolean getData(String id) {
        return false;
    }

    private static boolean post(String string) {
        try {
            URL url = new URL(" http://posttestserver.com/post.php?dir=livelo");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
            request.write(string);
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
            String response = sb.toString();

            isr.close();
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static boolean get(String string) {

        try {
            URL url = new URL(" http://posttestserver.com/post.php?dir=livelo");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("GET");
            OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
            request.write(string);
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
            String response = sb.toString();

            isr.close();
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void sendSensors(Context context) {
        SensorDAO sensorDAO;
        DataDAO dataDAO;

        sensorDAO = new SensorDAO(context);
        sensorDAO.open();
        dataDAO = new DataDAO(context);
        dataDAO.open();

        List<Sensor> sensors = sensorDAO.getAllSensors();

        for (Sensor sensor:sensors) {
            postSensor(sensor);
        }

    }



}

