package ch.livelo.livelo2.DB;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by Nico on 27/10/2017.
 * TODO fonction check valid input pour vérifier avant d'enregistrer
 *
 * TODO champs period, last_start
 *
 * TODO boolean isRunning() pour savoir quand on collect les pressions atmosphériques et ou sur le serveur
 *
 * Champs boolean edited pour savoir si il faut l'envoyer sur le serveur ou pas
 */

public class Sensor {


    private String name = null;
    private String sensor_id = null;
    private double latitude = 0;
    private double longitude = 0;
    private double depth = 0;
    private double frequency = 0;
    private long dataNb = 0;
    private long lastCollect = 0;

    //////////////////////Constructors///////////////////
    public Sensor(){
    }

    public Sensor(String id){
        this.sensor_id = id;
    }
    public Sensor(String name, String id){
        this.name = name;
        this.sensor_id = id;
    }

    public Sensor(String name, String id, double latitude, double longitude){
        this.name = name;
        this.sensor_id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Sensor(String name, String id, double latitude, double longitude, double depth){
        this.name = name;
        this.sensor_id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
    }

    //////////////////////Setters & getters///////////////

    public void setName(String name){
        this.name = name;
    }

    public void setId(String id){
        this.sensor_id = id;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setDepth(double depth){
        this.depth = depth;
    }

    public void setFrequency(double frequency){
        this.frequency = frequency;
    }

    public void setDataNb(long dataNb){
        this.dataNb = dataNb;
    }

    public void setLastCollect(long lastCollect){
        this.lastCollect = lastCollect;
    }

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.sensor_id;
    }

    public double getDepth(){
        return this.depth;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public double[] getPosition(){
        double[] position = {this.latitude, this.longitude};
        return position;
    }

    public double getFrequency(){
        return this.frequency;
    }

    public long getDataNb(){
        return this.dataNb;
    }

    public double getLastCollect(){
        return this.lastCollect;
    }

    public String[] getArrayInfo(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date toDate = new Date(this.lastCollect);
        String lastCollectDate = df.format(toDate);

        return new String[]{this.name, this.sensor_id, String.valueOf(this.latitude), String.valueOf(this.longitude),
                String.valueOf(this.depth), String.valueOf(this.frequency), String.valueOf(this.dataNb),
                String.valueOf(lastCollectDate)};
    }

    public List<String[]> getInfo(){
        List<String[]> infoList = new ArrayList<String[]>();
        String[] infoArray = this.getArrayInfo();

        for(int i = 1; i< SensorDB.NB_OF_FIELDS; i++){
            String[] item_couple = new String[]{SensorDB.allColumns[i], infoArray[i-1]};
            infoList.add(item_couple);
        }

        return infoList;

    }

}
