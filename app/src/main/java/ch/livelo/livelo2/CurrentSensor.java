package ch.livelo.livelo2;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;
import ch.livelo.livelo2.MySensors.MySensors;
/**
 * TODO
 */
public class CurrentSensor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    enum Action{INFO, COLLECT, LAUNCH, NEW}
    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Action action = Action.INFO;
    private int period = 0;
    private RelativeLayout layout_wait;

    public static DataDAO dataDAO;//will be opened in the collectdata action
    public static SensorDAO sensorDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_sensor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout_wait = (RelativeLayout) findViewById(R.id.layout_wait);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // NFC Intent filter
        myNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter nfcv = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[]{
                nfcv,
        };
        mTechLists = new String[][]{new String[]{NfcV.class.getName()},
                new String[]{NdefFormatable.class.getName()}};
    }

    public void goToInfo(View view) {
        action = Action.INFO;
        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
    }
    public void goToCollect(View view) {
        /** For testing purpose **/
        dataDAO = new DataDAO(CurrentSensor.this);
        dataDAO.open();
        sensorDAO = new SensorDAO(CurrentSensor.this);
        sensorDAO.open();

        NfcLivelo.collectData();

        dataDAO.close();
        sensorDAO.close();
        Toast.makeText(getBaseContext(), "empty function", Toast.LENGTH_SHORT).show();

        /*************************/

        action = Action.COLLECT;
        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
    }
    public void goToLaunch(View view) {
        // TODO dialog box to define the period
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sampling  period");
        final EditText edit_period = new EditText(this);
        builder.setView(edit_period);
        edit_period.setText("15");

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        period = Integer.valueOf(edit_period.getText().toString());
                        action = Action.LAUNCH;
                        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
                        dialog.dismiss();

                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setNeutralButton("Default",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                edit_period.setText("15");
            }
        });
    }
    public void goToNew(View view) {
        /********************* Pour les tests, pas besoin de détecter un senseur***************/
        //action = Action.NEW;
        //if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, NewSensor.class);
        startActivity(intent);


    }


    @Override
    public void onNewIntent(Intent intentNfc) {

        layout_wait.setVisibility(View.INVISIBLE);

        Tag detectedTag = intentNfc.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NfcV nfcv = NfcV.get(detectedTag);
        final String id = NfcLivelo.getId(nfcv);
        if (id.isEmpty()){
            //Toast.makeText(getBaseContext(), "Unable to read id", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent;

        switch (action){
            case INFO:
                intent = new Intent(CurrentSensor.this, SensorInfoActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case COLLECT:

                dataDAO = new DataDAO(CurrentSensor.this);
                dataDAO.open();
                dataDAO.transactionBegins();
                NfcLivelo.collectData();
                dataDAO.transactionEnds();
                dataDAO.close();
                Toast.makeText(getBaseContext(), "empty function", Toast.LENGTH_SHORT).show();
                break;
            case LAUNCH:
                if (NfcLivelo.launchSampling(period)) Toast.makeText(getBaseContext(), "empty function", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getBaseContext(), "error launching samplings", Toast.LENGTH_SHORT).show();
                break;
            case NEW: // dialog box si le capteur existe deja
                SensorDAO sensorDAO = new SensorDAO(CurrentSensor.this);
                sensorDAO.open();

                if(sensorDAO.exists(new Sensor(id))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This sensor is already registered");
                    builder.setMessage("Do you want to edit it?");

                    builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(CurrentSensor.this, NewSensor.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });

                    builder.setNeutralButton("Info", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(CurrentSensor.this, SensorInfoActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            action = Action.INFO;
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    intent = new Intent(CurrentSensor.this, NewSensor.class);
                    intent.putExtra("id", id);
                    intent.putExtra("new", true);
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean enableNfc(){
        if (myNfcAdapter == null){
            Toast.makeText(getBaseContext(), "NFC is not available on this device",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!myNfcAdapter.isEnabled()) {
            Toast.makeText(getBaseContext(), "You should turn NFC on before",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(intent);
            }
            return false;
        }
        if (myNfcAdapter != null) myNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.current_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.nav_current_sensor:
                intent = new Intent(CurrentSensor.this, CurrentSensor.class);
                startActivity(intent);
                break;
            case R.id.nav_my_sensors:
                intent = new Intent(CurrentSensor.this, MySensors.class);
                startActivity(intent);
                break;
            case R.id.nav_sensors_map:
                intent = new Intent(CurrentSensor.this, SensorsMapsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_my_data:
                //intent = new Intent(CurrentSensor.this, SettingsActivity.class);
                //startActivity(intent);
                break;
            case R.id.nav_help:
                intent = new Intent(CurrentSensor.this, HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(CurrentSensor.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_send:
                //intent = new Intent(CurrentSensor.this, SettingsActivity.class);
                //startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
