package com.pressy4pie.deadpigeoninfoexporter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.provider.Settings.Secure;

public class MainActivity extends AppCompatActivity {
    /*


    App to be able to retrieve information such as IMEI, device serial, android_id


     */

    /*
 * works by using getprop to retrieve android prop
 */
    public static String readProp(String prop) {
        //this reads a prop as SH (not root) into a string
        Process p = null;
        String line = "";
        String propRead = "";


        try {
            p = new ProcessBuilder("/system/bin/getprop", prop).redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line=br.readLine()) != null){
                propRead = line;
            }
            p.destroy();

        } catch (IOException e) {
            Log.e("pigeon getprop", "Failed to read prop");
        }
        return propRead;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //set up a telephonymanager.
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        //this is the easiest way but if the device doesnt have an imei it it will return null so we add some checks.
        String imei;
        if(tm != null) {
            imei = tm.getDeviceId();
        }
        else imei = "ERROR";

        //easy getprop for serial number
        String serialnumber = readProp("ro.boot.serialno");

        //device id. No very sure what this number is but it's here.
        String deviceid = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);



        //log those for good measure. 
        Log.d("pigeon", "imei : " + imei);
        Log.d("pigeon", "Serial Number : " + serialnumber);
        Log.d("pigeon", "android_id : " + deviceid);

    }

}
