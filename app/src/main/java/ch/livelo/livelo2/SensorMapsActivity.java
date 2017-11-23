package ch.livelo.livelo2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;

/**
 *
 * TODO prendre l'id, mettre la camera dessus et le selectionner si possible
 * TODO si pas d'id, centrer sur le GPS
 * TODO mettre un marker pour chaque capteur dans la database
 * TODO onClick sur chaque marker (soit direct sur sensor info soit un petit menu qui propose sensor info)
 *
 *
 */

public class SensorMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String id;
    private SensorDAO sensorDAO;
    private List<Sensor> sensors;
    private Sensor sensor = null;
    private boolean centering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        id = getIntent().getStringExtra("id");

        sensorDAO = new SensorDAO(this);
        sensorDAO.open();
        sensors = sensorDAO.getAllSensors();
        if(id!=null){
            sensor = sensorDAO.getSensor(id);
            centering = true;
        }
        sensorDAO.close();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(0, 0);
        // Add a marker in Sydney and move the camera
        for(Sensor sensor:sensors){
            sydney = new LatLng(sensor.getLatitude(), sensor.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title(sensor.getName()));
        }
        if(centering){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sensor.getLatitude(), sensor.getLongitude()), 12.0f));
        }
    }
}
