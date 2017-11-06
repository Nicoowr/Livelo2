package ch.livelo.livelo2;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;

/**
 *
 * TODO prendre l'id, mettre la camera dessus et le selectionner si possible
 * TODO si pas d'id, centrer sur le GPS
 * TODO mettre un marker pour chaque capteur dans la database
 * TODO onClick sur chaque marker (soit direct sur sensor info soit un petit menu qui propose sensor info)
 */

public class SensorsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private String id;
    private Sensor sensor;
    private SensorDAO sensorDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FIXME peut etre un probleme d'API key
        setContentView(R.layout.activity_sensors_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        id = getIntent().getStringExtra("id");
        sensorDAO = new SensorDAO(SensorsMapsActivity.this);
        sensorDAO.open();
        sensor = sensorDAO.getSensor(id);

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng pos = new LatLng(sensor.getLatitude(), sensor.getLongitude());
        mMap.addMarker(new MarkerOptions().position(pos).title(sensor.getName()));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
    }



    //@Override
    //public boolean onMarkerClick(final Marker marker) {
    //    Intent intent = new Intent(CurrentSensor.this, SensorInfoActivity.class);
    //    intent.putExtra("id", "unknown id");
    //    startActivity(intent);
    //    }
    //}
}
