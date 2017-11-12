package ch.livelo.livelo2;

import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.DataDB;
import ch.livelo.livelo2.DB.SensorDAO;
import ch.livelo.livelo2.DB.SensorDB;

/**
 * Created by Remi on 27/10/2017.
 */

public class NfcLivelo {

    public static DataDAO dataDAO;//will be opened in the collectdata action
    public static SensorDAO sensorDAO;

    public static String getId(NfcV nfcv) {
        StringBuilder idStr = new StringBuilder();
        byte id[] = {0};

        try {
            nfcv.connect();
            if (nfcv.isConnected()) {
                id = nfcv.transceive(new byte[]{0x00, 0x2B});
                nfcv.close();
            }
        } catch (IOException e) {
        }
        for (int i = id.length - 3; i > 1; i--) {//
            String hex = Integer.toHexString(0xFF & id[i]);
            if (hex.length() == 1) {//if string is empty
                idStr.append('0');
            }
            idStr.append(hex);
        }
        return idStr.toString();
    }


    public static int getBatteryLevel() {
        return 0;
    }

    //TODO implement async task


    public static int collectData(Context context) {
        /*1- Check sensor id
            If sensor doesn't exist, create it based on the next infos
          2- Collect sampling period
          3- Count number of samples
          4- Based on today's date, find all time stamps and convert them into ms
          5- add 1 by 1 the data
         */

        //Assume the sensor exists
        final Context mcontext = context;


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                sensorDAO = new SensorDAO(mcontext);
                sensorDAO.open();
                dataDAO = new DataDAO(mcontext);
                dataDAO.open();
                dataDAO.transactionBegins();
                long now = System.currentTimeMillis(); //current time in milliseconds from 1970

                /*** Generating random data ****/
                String sensor_id = "e01";
                int period = 15 * 60 * 1000;//15 minutes in milliseconds
                int dataCount = 0;//initialize data counter
                List data = new ArrayList();
                List timeStamps = new ArrayList();

                //Harvesting data and time stamps
                dataCount = 10000;
                for (int i = 0; i < 10000; i++) {
                    data.add((int) (10000 * Math.random()));
                    Log.i("data iteration " + i, "data added to the list");
                }

                for (int i = 0; i < dataCount; i++) {
                    timeStamps.add((now - (dataCount - i) * period));
                    Log.i("data iteration " + i, "timestamp added to the list");
                }


                //Adding all the data to the DB
                Iterator timeCursor = timeStamps.iterator();
                Iterator dataCursor = data.iterator();
                for (int i = 0; i < dataCount; i++) {
                    dataDAO.addData(sensor_id, (long) timeCursor.next(), (int) dataCursor.next());
                    Log.i("data iteration " + i, "data added to the db");
                }

                //Update sensor info
                sensorDAO.updateSensor(sensor_id, -1, -1, -1, -1, dataCount, now);
                dataDAO.transactionEnds();
                dataDAO.close();
                sensorDAO.close();


            }
        });
        t.start();


        //TODO: do all of this in another thread


        return 0;
    }

    public static int getSamplingsNumber() {
        return 0;
    }

    public static boolean launchSampling(int period) {
        return false;
    }
}
