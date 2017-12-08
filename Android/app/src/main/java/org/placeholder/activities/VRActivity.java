package org.placeholder.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.placeholder.gimbalcontrol.OrientationSensor;
import org.placeholder.gimbalcontrol.UDPClient;

public class VRActivity extends Activity {

    final OrientationSensor sensor = OrientationSensor.getOrientationSensor();
    Thread imageReceiveAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);
        sensor.registerSensor(this);

        final TextView sensorDisplayText = (TextView) findViewById(R.id.test_sensor_data);
        final TextView orientationDisplayText = (TextView) findViewById(R.id.orientation);

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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        final byte[] data = new byte[5001];
        final ImageView image = (ImageView) findViewById(R.id.image);
        if (imageReceiveAgent == null) {
            imageReceiveAgent = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(Thread.currentThread().getName(), "Starting receive image");

                    while (true) {
                        byte[] received = null;
                        try {
                            received = UDPClient.receiveDatagram(5657, data);
                            if (received.length == 4) {
                                int imageBytesSize = UDPClient.getIntFromBytes(received);
//                                Log.i("received image", "length = " + imageBytesSize);
                                int packageNum = imageBytesSize % 5000 == 0 ? imageBytesSize / 5000 : (imageBytesSize / 5000 + 1);

                                byte[] imageBytes = new byte[imageBytesSize];
                                for (int i = 0; i < packageNum; i++) {
                                    received = UDPClient.receiveDatagram(5657, data);
//                                    Log.i("package #", i + ", length = " + received.length);
                                    for (int j = 0; j < received.length; j++) {
                                        imageBytes[i * 5000 + j] = received[j];
                                    }
                                }
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        image.setImageBitmap(bitmap);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.e("receiveException", e.getMessage());
                            e.printStackTrace();
                        }

                    }

//                    while (true) {
//                        byte[] imageBytes = null;
//                        byte[] received = null;
//                        int length = 0;
//                        int packageNum = 0;
//                        int packageCount = 0;
//                        try {
//                            received = UDPClient.receiveDatagram(5657, data);
//                        } catch (Exception e) {
//                            Log.e("receiveException", e.getMessage());
//                            e.printStackTrace();
//                        }
//                        Log.i("length received", received.length + "");
//                        if (received.length == 4) {
//                            length = UDPClient.getIntFromBytes(received);
//                            Log.i("received", "byte array length = " + length);
//                            imageBytes = new byte[length];
//                            packageNum = length / 5000 + 1;
//                            packageCount = 0;
//                        } else {
//                            Log.i("received", "image, length = " + received.length);
//                            if (packageCount < packageNum) {
//                                for (int i = 0; i < received.length; i++)
//                                    imageBytes[i + packageCount * 5000] = received[i];
//                                packageCount++;
//                            }
//                            if (packageCount == packageCount) {
//                                final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        image.setImageBitmap(bitmap);
//                                    }
//                                });
//                            }
//                        }
//                    }
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
