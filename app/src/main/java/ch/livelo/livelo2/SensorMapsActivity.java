package ch.livelo.livelo2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.ILocationSourceDelegate;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.android.gms.maps.internal.IUiSettingsDelegate;
import com.google.android.gms.maps.internal.zzab;
import com.google.android.gms.maps.internal.zzad;
import com.google.android.gms.maps.internal.zzaf;
import com.google.android.gms.maps.internal.zzaj;
import com.google.android.gms.maps.internal.zzal;
import com.google.android.gms.maps.internal.zzan;
import com.google.android.gms.maps.internal.zzap;
import com.google.android.gms.maps.internal.zzar;
import com.google.android.gms.maps.internal.zzat;
import com.google.android.gms.maps.internal.zzav;
import com.google.android.gms.maps.internal.zzax;
import com.google.android.gms.maps.internal.zzaz;
import com.google.android.gms.maps.internal.zzbb;
import com.google.android.gms.maps.internal.zzbd;
import com.google.android.gms.maps.internal.zzbf;
import com.google.android.gms.maps.internal.zzbs;
import com.google.android.gms.maps.internal.zzc;
import com.google.android.gms.maps.internal.zzh;
import com.google.android.gms.maps.internal.zzl;
import com.google.android.gms.maps.internal.zzn;
import com.google.android.gms.maps.internal.zzr;
import com.google.android.gms.maps.internal.zzt;
import com.google.android.gms.maps.internal.zzv;
import com.google.android.gms.maps.internal.zzx;
import com.google.android.gms.maps.internal.zzz;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.internal.IPolylineDelegate;
import com.google.android.gms.maps.model.internal.zzd;
import com.google.android.gms.maps.model.internal.zzg;
import com.google.android.gms.maps.model.internal.zzj;
import com.google.android.gms.maps.model.internal.zzp;
import com.google.android.gms.maps.model.internal.zzs;
import com.google.android.gms.maps.model.internal.zzw;

import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;

import static java.lang.String.valueOf;

/**
 *
 * TODO prendre l'id, mettre la camera dessus et le selectionner si possible
 * TODO si pas d'id, centrer sur le GPS*
 *
 */

public class SensorMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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
            mMap.addMarker(new MarkerOptions().position(sydney).title(sensor.getId()));
        }
        if(centering){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sensor.getLatitude(), sensor.getLongitude()), 12.0f));
        }

        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = null;
                try {
                    sensorDAO = new SensorDAO(SensorMapsActivity.this);
                    sensorDAO.open();
                    sensor = sensorDAO.getSensor(arg0.getTitle());
                    sensorDAO.close();
                    // Getting view from the layout file info_window_layout
                    v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                    // Getting reference to the TextView to set latitude
                    TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
                    TextView tv_id = (TextView) v.findViewById(R.id.tv_id);
                    TextView tv_depth = (TextView) v.findViewById(R.id.tv_depth);
                    tv_name.setText(sensor.getName());
                    tv_id.setText(arg0.getTitle());
                    tv_depth.setText(valueOf(sensor.getDepth()).toString() + " m");

                    /*Drawable get_info = getResources().getDrawable(R.drawable.get_info_sized);
                    get_info.setBounds(0, 0, (int)(get_info.getIntrinsicWidth()*0.5),(int)(get_info.getIntrinsicHeight()*0.5));
                    ScaleDrawable sd1 = new ScaleDrawable(get_info, 0, 30, 30);
                    Button button_info_map = (Button) findViewById(R.id.button_info_map);
                    button_info_map.setCompoundDrawables(sd1.getDrawable(), null, null, null);*/
                    v.setClickable(true);

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            sensorDAO = new SensorDAO(SensorMapsActivity.this);
                            sensorDAO.open();
                            sensor = sensorDAO.getSensor(marker.getTitle());
                            sensorDAO.close();

                            Intent intent = new Intent(SensorMapsActivity.this, SensorInfoActivity.class);
                            intent.putExtra("id", sensor.getId());
                            startActivity(intent);
                        }
                    });
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            sensorDAO = new SensorDAO(SensorMapsActivity.this);
                            sensorDAO.open();
                            sensor = sensorDAO.getSensor(marker.getTitle());
                            sensorDAO.close();

                            Intent intent = new Intent(SensorMapsActivity.this, SensorInfoActivity.class);
                            intent.putExtra("id", sensor.getId());
                            startActivity(intent);

                            return false;
                        }
                    });
                } catch (Exception ev) {
                    System.out.print(ev.getMessage());
                }

                return v;
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        sensorDAO = new SensorDAO(SensorMapsActivity.this);
        sensorDAO.open();
        sensor = sensorDAO.getSensor(marker.getTitle());
        sensorDAO.close();

        Intent intent = new Intent(SensorMapsActivity.this, SensorInfoActivity.class);
        intent.putExtra("id", sensor.getId());
        startActivity(intent);

        return false;
    }
}
