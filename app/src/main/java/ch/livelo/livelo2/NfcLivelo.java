package ch.livelo.livelo2;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;

import java.io.IOException;

/**
 * Created by Remi on 27/10/2017.
 */

public class NfcLivelo {
    private NfcAdapter myNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    public String getId(){
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
        //for (int i = 2; i < id.length-2; i++) {//
        for (int i = id.length-3; i > 1; i--) {//
            String hex = Integer.toHexString(0xFF & id[i]);
            if (hex.length() == 1) {//if string is empty
                idStr.append('0');
            }
            idStr.append(hex);
        }
        return idStr.toString();
    }
    public int getBatteryLevel(){
        return 0;
    }
}
