package org.placeholder.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.placeholder.gimbalcontrol.OrientationSensor;
import org.placeholder.gimbalcontrol.UDPClient;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class VRActivity extends Activity {

    final OrientationSensor sensor = OrientationSensor.getOrientationSensor();
    Thread imageReceiveAgent;

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
                while (true) {
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

        final byte[] data = new byte[100000];
        final ImageView image = (ImageView)findViewById(R.id.image);
        if (imageReceiveAgent == null) {
            imageReceiveAgent = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(Thread.currentThread().getName(), "Starting receive image");
                    while (true) {
                        byte[] received = null;
                        try {
                            received = UDPClient.receiveDatagram(5657, data);
                        }
                        catch (Exception e) {
                            Log.e("receiveException", e.getMessage());
                            e.printStackTrace();
                        }
                        Log.i("length received", received.length + "");
                        Log.i("received", "image");
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(received, 0, received.length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                image.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            });
            imageReceiveAgent.start();
        }
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
