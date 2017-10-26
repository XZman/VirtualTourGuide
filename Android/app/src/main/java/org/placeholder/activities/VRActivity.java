package org.placeholder.activities;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.placeholder.gimbalcontrol.OrientationSensor;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class VRActivity extends Activity {

    final OrientationSensor sensor = OrientationSensor.getOrientationSensor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);
        sensor.registerSensor(this);

        final TextView sensorDisplayText = (TextView)findViewById(R.id.test_sensor_data);
        final TextView orientationDisplayText = (TextView)findViewById(R.id.orientation);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("Starting Thread: ", Thread.currentThread().getName());
                while(true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            orientationDisplayText.setText("\nX: " + sensor.getXDegree() + "\nY: " + sensor.getYDegree() + "\nZ: " + sensor.getZDegree());
                        }
                    });

                    try {
                        Thread.sleep(10);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensor.unregisterSensor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensor.registerSensor(this);
    }
}
