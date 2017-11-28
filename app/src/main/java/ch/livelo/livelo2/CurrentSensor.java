package ch.livelo.livelo2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.livelo.livelo2.DB.Data;
import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.Sensor;
import ch.livelo.livelo2.DB.SensorDAO;
import ch.livelo.livelo2.MySensors.MySensors;
/**
 * TODO message success post pour data et sensors
 * TODO compute the timestamp from the loaded data
 * http://posttestserver.com/data/2017/11/13/livelo
 */
public class CurrentSensor extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    enum Action{INFO, COLLECT, LAUNCH, NEW, RESET}
    private NfcAdapter myNfcAdapter;
    private NfcV nfcv;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Action action = Action.INFO;
    private int period = 0;
    private RelativeLayout layout_wait;
    private TextView tv_wait;
    private TextView tv_post_test;
    private ProgressDialog progressDialog;
    private String token;
    private String response = "";

    public static SensorDAO sensorDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            //Action bar layout

     //   Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
     //   mActionBarToolbar.setTitle("");
     //   setSupportActionBar(mActionBarToolbar);
     //   getSupportActionBar().setTitle("My title");


        //Global layout
        setContentView(R.layout.activity_current_sensor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        layout_wait = (RelativeLayout) findViewById(R.id.layout_wait);
        tv_wait = (TextView) findViewById(R.id.tv_wait);
        tv_post_test = (TextView) findViewById(R.id.tv_post_test);

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        myNfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    //public void goToPost(View view) {
    //    layout_wait.setVisibility(View.INVISIBLE);
    //    new CurrentSensor.httpRequest().execute("http://posttestserver.com/post.php?dir=livelo", "POST", "asdfjlaksjdéfadsf");
    //}
    //public void goToDisp(View view) {
    //    tv_post_test.setText(response);
    //}

    public void goToInfo(View view) {
        tv_wait.setText("Connect the sensor to get information");
        action = Action.INFO;
        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
    }

    public void goToCollect(View view) {
        tv_wait.setText("Connect the sensor to collect data");
        /** For testing purpose **/
        //new LoadData().execute();
        /*************************/

        action = Action.COLLECT;
        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
    }

    public void goToLaunch(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sampling  period [s]");
        //final LinearLayout dialogLayout = new LinearLayout(this);
        final EditText edit_period = new EditText(this);
        edit_period.setInputType(InputType.TYPE_CLASS_NUMBER);
        //final TextView tv_unit = new TextView(this);

        edit_period.setText("1");
        //tv_unit.setText("minutes");
        //dialogLayout.addView(edit_period);
        //dialogLayout.addView(tv_unit);
        builder.setView(edit_period);
        //builder.setView(dialogLayout);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        period = Integer.valueOf(edit_period.getText().toString());
                        action = Action.LAUNCH;
                        tv_wait.setText("Connect the sensor to start sampling every " + period + " seconds");

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
                edit_period.setText("1");
            }
        });
    }

    public void goToNew(View view) {
        tv_wait.setText("Connect the sensor to register it");
        action = Action.NEW;
        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
        /********************* Pour les tests, pas besoin de détecter un senseur***************/
        //Intent intent = new Intent(this, NewSensor.class);
        //startActivity(intent);

    }

    /** For testing purpose **/
    public void goToReset(View view) {
        tv_wait.setText("Connect the sensor to reset it");
        action = Action.RESET;
        if (enableNfc()) layout_wait.setVisibility(View.VISIBLE);
    }
    /*************************/

    @Override
    public void onNewIntent(Intent intentNfc) {
        //if (myNfcAdapter != null) myNfcAdapter.disableForegroundDispatch(this);//Stop accepting any other intent
        layout_wait.setVisibility(View.INVISIBLE);

        Tag detectedTag = intentNfc.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcv = NfcV.get(detectedTag);
        try {
            nfcv.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final String id = NfcLivelo.getId(nfcv);
        if (id.isEmpty()){
            Toast.makeText(getBaseContext(), "Unable to read id", Toast.LENGTH_LONG).show();
            return;
        }else{
            //Toast.makeText(getBaseContext(), "id is " + id , Toast.LENGTH_SHORT).show();
        }

        Intent intent;

        switch (action){
            case INFO:
                try {
                    nfcv.close();
                } catch (IOException e) {e.printStackTrace();}

                SensorDAO sensorDAO = new SensorDAO(CurrentSensor.this);
                sensorDAO.open();

                if(!sensorDAO.exists(id)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This sensor is not in your data base");
                    builder.setMessage("Do you want to configure a new one?");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(CurrentSensor.this, NewSensor.class);
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
                } else {
                    intent = new Intent(CurrentSensor.this, SensorInfoActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                sensorDAO.close();
                break;

            case COLLECT:
                //NfcLivelo.collectData(id, CurrentSensor.this);
                //Toast.makeText(this,"Number of samplings = " + NfcLivelo.readNbSamples(this,nfcv),Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"Period = " + NfcLivelo.readSamplingFreq(nfcv),Toast.LENGTH_SHORT).show();
                new LoadData().execute(id);


                break;

            case LAUNCH:
                if(NfcLivelo.readNbSamples(this, nfcv) !=0){
                    Toast.makeText(getBaseContext(), "sensor already sampling, collect or reset it before", Toast.LENGTH_LONG).show();
                    try {
                        nfcv.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                };

                sensorDAO = new SensorDAO(this);
                sensorDAO.open();
                if(!sensorDAO.exists(id)) { // if does not exist, ask to edit it and store the sensor
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This sensor has not been registered");
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

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            action = Action.INFO;
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    try {
                        nfcv.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (NfcLivelo.launchSampling(period, nfcv)) {
                    sensorDAO.updateSensor(id, -1, -1, -1, period, -1, -1);
                    Toast.makeText(getBaseContext(), "Sampling launched every " + period + " seconds", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getBaseContext(), "Error: sampling not launched", Toast.LENGTH_SHORT).show();
                sensorDAO.close();

                try {
                    nfcv.close();
                } catch (IOException e) {e.printStackTrace();}
                break;

            case NEW: // dialog box si le capteur existe deja
                try {
                    nfcv.close();
                } catch (IOException e) {e.printStackTrace();}

                sensorDAO = new SensorDAO(CurrentSensor.this);
                sensorDAO.open();

                if(sensorDAO.exists(id)) {
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
                sensorDAO.close();
                break;

            case RESET:
                NfcLivelo.reset(nfcv);
                Toast.makeText(getBaseContext(), "Reset sent", Toast.LENGTH_LONG).show();
                try {
                    nfcv.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
        } else if(myNfcAdapter.isEnabled()) {
            myNfcAdapter.disableForegroundDispatch(this);
            action = Action.INFO;
            layout_wait.setVisibility(View.INVISIBLE);
        }else{
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
            case R.id.nav_my_account:
                intent = new Intent(CurrentSensor.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_current_sensor:
                //intent = new Intent(CurrentSensor.this, CurrentSensor.class);
                //startActivity(intent);
                break;
            case R.id.nav_my_sensors:
                intent = new Intent(CurrentSensor.this, MySensors.class);
                startActivity(intent);
                break;
            case R.id.nav_sensors_map:
                intent = new Intent(CurrentSensor.this, SensorMapsActivity.class);
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
            case R.id.nav_preferences:
                intent = new Intent(CurrentSensor.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_send:
                //ServerLivelo.sendSensors(this);
                //ServerLivelo.sendData();
                SensorDAO sensorDAO = new SensorDAO(this);
                sensorDAO.open();
                List<Sensor> sensorsList = sensorDAO.getAllSensors();


                for (Sensor sensor:sensorsList) {
                    sensor.send(this.token);
                    Data data = new Data(sensor.getId(), this);
                    data.send(this.token, this);
                }


                Notification noti = new Notification.Builder(this)
                        .setContentTitle("Livelo")
                        .setContentText("sensor and data sent successfully").setSmallIcon(R.mipmap.ic_launcher).build();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(0, noti);


                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class LoadData extends AsyncTask<String, Integer, List<Long>> {
        int numberSamples;
        float period;
        long now = System.currentTimeMillis(); //current time in milliseconds from 1970
        private TextView tv_load;
        private ProgressBar pb_load;
        private String id;

        protected void onPreExecute(){
            numberSamples = NfcLivelo.readNbSamples(CurrentSensor.this, nfcv);
            period = NfcLivelo.readSamplingFreq(nfcv);
            progressDialog = new ProgressDialog(CurrentSensor.this);
            progressDialog.setMax(100);
            progressDialog.setTitle("Loading " + numberSamples + " samples");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();

        }
        protected List<Long> doInBackground(String... strings) {
            if (numberSamples == 0) {
                this.cancel(true);
                return null;
            }

            id = strings[0];

            List<Long> values = new ArrayList();
            List<Long> currentDataList;
            byte i;
            int blockCount;
            // number of blocks (2048B) to read - 1, such that it only reads the nb of new samples
            //if (k < 1){//nbBlocksToRead) {
            for(int k = 0; k<1; k++){
                NfcLivelo.requestOneBlock(nfcv);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //values.addAll(NfcLivelo.readOneBlock(nfcv));
                i = 0x44; //Start block of data
                blockCount = 0;

                //////////////////Start transfer/////////////////////
                //First blocks from 0x644 to 0x6FF
                for (int j = 0;j<188; j++){
                    // to slow down the readings, to check what hapends if we disconnec tduring readings
                    //try {
                    //    Thread.sleep(1000);
                    //} catch (InterruptedException e) {
                    //    e.printStackTrace();
                    //}

                    // TODO retester cette partie
                    currentDataList = NfcLivelo.readBlockOne(nfcv, i, blockCount, (byte)0x06);
                    if(currentDataList == null){
                        return null;
                    }
                    values.addAll(currentDataList);
                    i++;
                    blockCount++;
                    publishProgress(blockCount/8*100/255, (k+1));
                    if (1024*k+blockCount*4>=numberSamples)
                        return values;
                }

                //Second blocks
                for (int j = 0; j<68; j++){
                    currentDataList = NfcLivelo.readBlockOne(nfcv, i, blockCount, (byte)0x07);
                    if(currentDataList == null){
                        return null;
                    }
                    values.addAll(currentDataList);
                    i++;
                    blockCount++;
                    publishProgress(blockCount/8*100/255, (k+1));
                    if (1024*k+blockCount*4>=numberSamples)
                        return values;
                }

                if (isCancelled()) return null;
            }

            Toast.makeText(CurrentSensor.this, "debug: not enough blocks read for nb of samples", Toast.LENGTH_SHORT).show();
            return null;
        }

        protected void onCancelled(){
            Toast.makeText(CurrentSensor.this, "data has already been collected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            layout_wait.setVisibility(View.INVISIBLE);
        }
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
            progressDialog.setMessage("loading block " + progress[1] +"/" + 1);
        }

        protected void onPostExecute(List<Long> values) {
            if (values == null) {
                try {
                    nfcv.close();
                } catch (IOException e) {e.printStackTrace();}
                Toast.makeText(CurrentSensor.this, "error reading samples, try it again", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                return;
            }

            progressDialog.setProgress(progressDialog.getMax());
            progressDialog.setMessage("saving data");
            List<Long> timeStamp = new ArrayList();

            for(int k = numberSamples; k>0; k--){
                timeStamp.add(now - (long)((float)k*period));
            }

            values = values.subList(0, numberSamples);

            Data data = new Data(id, timeStamp, values, CurrentSensor.this);

            // store data in the db
            DataDAO dataDAO = new DataDAO(CurrentSensor.this);
            dataDAO.open();
            dataDAO.transactionBegins();
            long dataCount = dataDAO.addAllData(data);
            //TODO: so as to interpolate, we need last launch time
            dataDAO.transactionEnds();
            dataDAO.close();

            sensorDAO = new SensorDAO(CurrentSensor.this);
            sensorDAO.open();
            sensorDAO.updateSensor(data.getSensorID(), -1, -1, -1, -1, numberSamples, now);
            sensorDAO.close();

            NfcLivelo.reset(nfcv);
            try {
                nfcv.close();
            } catch (IOException e) {e.printStackTrace();}

            Toast.makeText(getBaseContext(), "" + numberSamples + " samples collected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    class httpRequest extends AsyncTask<String, String, String> {

        private Exception exception;
        TextView tv_post;
        protected String doInBackground(String... string) {
            try {
                URL url = new URL(string[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
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
            tv_post_test.setText(result);
        }

        @Override
        protected void onPreExecute() {}

        //@Override
        //protected void onProgressUpdate(Void... values) {}

    }

}

