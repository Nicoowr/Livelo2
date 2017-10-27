package ch.livelo.livelo2.DB;

/**
 * Created by Nico on 27/10/2017.
 */

public class Sensor {

    private String name;
    private String sensor_id;
    private double latitude;
    private double longitude;
    private double depth;
    private Data data = null;

    //////////////////////Constructors///////////////////
    public Sensor(String name, String id){
        this.name = name;
        this.sensor_id = id;
    }

    public Sensor(String name, String id, int latitude, int longitude){
        this.name = name;
        this.sensor_id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Sensor(String name, String id, int latitude, int longitude, int depth){
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

    public void setPosition(int latitude, int longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setDepth(int depth){
        this.depth = depth;
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

    public double[] getPosition(){
        double[] position = {this.latitude, this.longitude};
        return position;
    }



}
