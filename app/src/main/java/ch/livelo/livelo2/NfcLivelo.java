package ch.livelo.livelo2;

import android.content.Context;
import android.nfc.tech.NfcV;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.livelo.livelo2.DB.DataDAO;
import ch.livelo.livelo2.DB.SensorDAO;

import static java.lang.StrictMath.round;

/**
 * Created by Remi on 27/10/2017.
 *
 * TODO data en integer suffisant non?
 *
 */

public class NfcLivelo {

    public static DataDAO dataDAO;//will be opened in the collectdata action
    public static SensorDAO sensorDAO;
    private int k = 0;

    public static String getId(NfcV nfcv) {
        StringBuilder idStr = new StringBuilder();
        byte id[] = {0};

        if (nfcv.isConnected()) {
                id = nfcv.getTag().getId();
                //id = nfcv.transceive(new byte[]{0x20, 0x2B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});

        }
        for (int i = id.length-1; i > -1; i--) {//
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


    public static void collectData(String id, final Context context) {
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
                dataCount = 10;
                for (int i = 0; i < dataCount; i++) {
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
                sensorDAO.updateSensor(sensor_id, -1, -1, -1, -1, 0, dataCount, now);
                dataDAO.transactionEnds();
                dataDAO.close();
                sensorDAO.close();


            }
        });
        t.start();
    }

    public static void requestOneBlock(NfcV nfcv) {
        final byte readCommand[] = new byte[]{0x00, 0x21, (byte) 0, 0x01, 0x00, 0x20, 0x03, 0x01, 0x01, 0x00, 0x00};
        try {
            nfcv.transceive(readCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Long> readOneBlock(NfcV nfcv) {
        byte i = 0x44; //Start block of data
        int j = 0;
        int blockCount = 0;
        byte[] buffer = {0};        // il fallait l'initialiser
        List<Long> data = new ArrayList();

        //////////////////Start transfer/////////////////////
        //First blocks from 0x644 to 0x6FF
        while (j < 188) {
            try {
                buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x06});//Read single block
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int l = 0; l < buffer.length; l++) {
                if (l % 2 == 1) {
                    Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                    Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l + 1] & 0xFF)).replace(' ', '0'));
                    long currentData = ((((buffer[l] & 0xff) << 8) | (buffer[l + 1] & 0xff)) << 1);
                    data.add(currentData);
                }
            }
            j++;
            i++;
            blockCount++;

        }

        //Second blocks
        j = 0;
        while (j < 68) {
            try {
                buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, 0x07});
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int l = 0; l < buffer.length; l++) {
                if (l % 2 == 1) {
                    Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                    Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l + 1] & 0xFF)).replace(' ', '0'));
                    long currentData = ((((buffer[l] & 0xff) << 8) | (buffer[l + 1] & 0xff)) << 1);
                    data.add(currentData);
                }
            }
            j++;
            i++;
            blockCount++;

        }
        return data;
    }

    public static List<Long> readBlockOne(NfcV nfcv, byte i, int blockCount, byte address) {
        byte[] buffer = {0};        // il fallait l'initialiser
        List<Long> currentDataList = new ArrayList<>();
        try {
            buffer = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, i, address});//Read single block
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        for (int l = 0; l < buffer.length; l++) {
            if (l % 2 == 1) {
                Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l] & 0xFF)).replace(' ', '0'));
                Log.i(String.format("%1$d", blockCount), String.format("%8s", Integer.toBinaryString(buffer[l + 1] & 0xFF)).replace(' ', '0'));
                long currentData = ((((buffer[l] & 0xff) << 8) | (buffer[l + 1] & 0xff)) << 1);
                currentDataList.add(currentData);
            }
        }
        return currentDataList;
    }


    public static int readNbSamples(Context context,NfcV nfcv){
        byte c[] = {0,0,0,0,0,0,0,0};
        byte c2[] = {0,0,0,0,0,0,0,0};
        int numberSamples;
        try {
            c = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x41, 0x06}); //read block 641h from RAM
        } catch (IOException e) {
            Toast.makeText(context, "Error in reading number of samples", Toast.LENGTH_SHORT).show();
        }
        int count = ((c[6] & 0xff) << 8) | (c[5] & 0xff);//Warning: order of bytes inversed! and one dummy byte
        count = (count >> 1) - 1;
        if (count < 0)
            count = 0;

        if(count != 0) numberSamples = count; //Stocks the nb of samples
        else numberSamples = 0;

        try {
            c2 = nfcv.transceive(new byte[]{0x00, (byte) -64, 0x07, 0x42, 0x06}); //read block 642h from RAM to know if 2nd part of FRAM is used
        } catch (IOException e) {
            Toast.makeText(context, "Error in reading number of samples", Toast.LENGTH_SHORT).show();
        }
        int count2 = ((c2[4] & 0xff) << 8) | (c2[3] & 0xff);
        if(count2 == 81){//81 is the second part of extFRAM address
            numberSamples += 32768;
        }

        return numberSamples;
    }

    public static float readSamplingFreq(NfcV nfcv) {
        byte[] p = new byte[8];
        try {
            p = nfcv.transceive(new byte[]{0x00, 0x20, 0x03}); //read block 3 from FRAM
        } catch (IOException e) {
            e.printStackTrace();
        }
        int period = ((p[4] & 0xff) << 24) | ((p[3] & 0xff) << 16) | ((p[2] & 0xff) << 8) | (p[1] & 0xff);//Warning: order of bytes inversed!
        float periodInMs = (float) period; //period in ms
        return periodInMs;
    }

    public static boolean launchSampling(int period, NfcV nfcv) {
        resetCollect(nfcv);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        resetWrite(nfcv);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int periodInMs = period*1000 - 10; //period in ms //rajouter 60* // 15 is to compensate sampling time

        byte periodInMsB[] = new byte[4];

        for(int i=0; i<4; i++ ) {
            periodInMsB[i] = (byte) (periodInMs >>(i*8));
        }

        byte command1[] = new byte[]{
                0x00,
                0x21,
                (byte) 3,
                periodInMsB[0],
                periodInMsB[1],
                periodInMsB[2],
                periodInMsB[3],
                0x00,
                0x00,
                0x00,
                0x00
        };

        try {
            nfcv.transceive(command1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start pressure every periodInMs msecs
        byte command[] = new byte[]{
                0x00,
                0x21,
                (byte) 0,
                0x01, //General control register
                0x00, //Firmware Status register
                0x10, //Sensor control register
                0x10, //Frequency control register: custom time
                0x02, //Number of passes register
                0x01, //Averaging register
                0x01, //Interrupt control register: infinite sampling
                0x00 //Error control register
        };

        try {
            nfcv.transceive(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Check if sampling is launched
        byte samplingIsLaunched[] = new byte[0];
        try {
            samplingIsLaunched = nfcv.transceive(new byte[]{0x00, 0x20, (byte) 0});
        } catch (IOException e) {
            e.printStackTrace();
        }

        //FIXME
        //if((samplingIsLaunched[3] & (byte)16)== (1 << 4))
            return true;
        //else
        //    return false;

    }

    //Reset Collect (allows to redo a collect if last failed)
    public static void resetWrite(NfcV nfcv) {
        final byte readCommand[] = new byte[]{0x00, 0x21, (byte) 0, 0x01, 0x00, 0x40, 0x03, 0x01, 0x01, 0x00, 0x00};
        try {
            nfcv.transceive(readCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Reset Write (including number of samples), called by watchdog
    public static void resetCollect(NfcV nfcv) {
        final byte readCommand[] = new byte[]{
                0x00,
                0x21,
                (byte) 0,
                -128, //General control register
                0x00, //Firmware Status register
                0x00, //Sensor control register
                0x00, //Frequency control register: custom time
                0x00, //Number of passes register
                0x00, //Averaging register
                0x00, //Interrupt control register: infinite sampling
                0x00 //Error control register
        };
        try {
            nfcv.transceive(readCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
