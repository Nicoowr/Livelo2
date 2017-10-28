package ch.livelo.livelo2.DB;

/**
 * Created by Nico on 27/10/2017.
 * TODO fonction check valid input pour vérifier avant d'enregistrer
 */

public class Sensor {

    private String name = null;
    private String sensor_id = null;
    private double latitude = 0;
    private double longitude = 0;
    private double depth = 0;
    private Data data = null;

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

    public String getName(){
        return this.name;
    }

    public String getId(){
        return this.sensor_id;
    }

    public double getDepth(){
        return this.depth;
    }

    // je crois que Ca marche pas ça, ça va renvoyer le pointeur mais pas le tableau, c'est dangereux
    public double[] getPosition(){
        double[] position = {this.latitude, this.longitude};
        return position;
    }



}
