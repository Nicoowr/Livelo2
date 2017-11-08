package ch.livelo.livelo2;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;

/**
 * Created by Remi on 27/10/2017.
 */

public class NfcLivelo {

    public static String getId(NfcV nfcv){
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
        for (int i = id.length-3; i > 1; i--) {//
            String hex = Integer.toHexString(0xFF & id[i]);
            if (hex.length() == 1) {//if string is empty
                idStr.append('0');
            }
            idStr.append(hex);
        }
        return idStr.toString();
    }


    public static int getBatteryLevel(){
        return 0;
    }

    public static int collectData(){
        /*1- Check sensor id
            If sensor doesn't exist, create it based on the next infos
          2- Collect sampling period
          3- Count number of samples
          4- Based on today's date, find all time stamps and convert them into ms
          5- add 1 by 1 the data
         */

           //Assume the sensor exists


        long now = System.currentTimeMillis()/1000; //current time in seconds from 1970

        /*** Generating random data ****/
        String sensor_id = "e01";
        int period = 15*60;//15 minutes in seconds
        int dataCount = 0;//initialize data counter
        List data = new ArrayList();
        List timeStamps = new ArrayList();


        for(int i = 0; i < 32000; i++){
            data.add((int) (10000*Math.random()));
        }

        dataCount = 32000;
        for(int i = 0; i < 32000; i++){
            timeStamps.add((now - (32000 - i)*period));
        }

        Iterator timeCursor = timeStamps.iterator();

        Iterator dataCursor = data.iterator();
        for(int i =0; i < 32000; i++){
            CurrentSensor.dataDAO.addData(sensor_id, (long)timeCursor.next(), (int) dataCursor.next());
        }




        return 0;
    }

    public static int getSamplingsNumber(){
        return 0;
    }

    public static boolean launchSampling(int period){
        return false;
    }
}
