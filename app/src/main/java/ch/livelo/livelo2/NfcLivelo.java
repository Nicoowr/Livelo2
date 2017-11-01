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
import android.widget.Toast;


import java.io.IOException;
import java.util.List;

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
        return 0;
    }

    public static int getSamplingsNumber(){
        return 0;
    }

    public static boolean launchSampling(int period){
        return false;
    }
}
