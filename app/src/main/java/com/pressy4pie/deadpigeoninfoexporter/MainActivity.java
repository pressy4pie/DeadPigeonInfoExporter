package com.pressy4pie.deadpigeoninfoexporter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    /*


    App to be able to retrieve information such as IMEI, device serial, android_id


     */

    /*
 * works by using getprop to retrieve android prop
 */

    //the big button to be used.
    public Button send;

    //the various strings to be used for sending.
    public String imei;
    public String serialnumber;
    public String deviceid;

    //stole this from myself from a different app. Lot's of fun here.
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

        //define the button and set it to red for fun.
        send = (Button) findViewById(R.id.BIGBUTTON);
        send.setBackgroundColor(Color.RED);

        //set up a telephonymanager.
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        //this is the easiest way but if the device doesnt have an imei it it will return null so we add some checks.
        if(tm != null) {
            imei = tm.getDeviceId();
        }
        else imei = "ERROR";

        //easy getprop for serial number
        serialnumber = readProp("ro.boot.serialno");

        //device id. No very sure what this number is but it's here.
        deviceid = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);



        //log those for good measure.
        Log.d("pigeon", "imei : " + imei);
        Log.d("pigeon", "Serial Number : " + serialnumber);
        Log.d("pigeon", "android_id : " + deviceid);

    }

    public void send(View view){
        send.setBackgroundColor(Color.GREEN);
        send.setText("SENDING");

        //create the intent and give it a MIME type
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //build the stuff to be shared.
        String shareBody =
                "IMEI : " + imei +"\n" +
                "SERIAL NUMBER : " + serialnumber + "\n" +
                "ANDORID_ID : " + deviceid;

        //package and putExtra it
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DEAD PIGEONS");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        //send.
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

}
