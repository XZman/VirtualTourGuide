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

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        OrientationSensor.getOrientationSensor().registerSensor(this);

        final TextView sensorDisplayText = (TextView)findViewById(R.id.test_sensor_data);
        final TextView orientationDisplayText = (TextView)findViewById(R.id.orientation);

        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                sensorDisplayText.setText("Xsin(theta/2): " + event.values[0] + "\nYsin(theta/2): " + event.values[1] + "\nZsin(theta/2): " + event.values[2] + "\ncos(theta/2): " + event.values[3]);

                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                float[] orien = new float[3];
                SensorManager.getOrientation(rotationMatrix, orien);
                float xOrien = (float)(orien[1] * 180 / Math.PI);
                float yOrien = (float)(orien[2] * 180 / Math.PI + 90);
                float zOrien = (float)(orien[0] * 180 / Math.PI);

                orientationDisplayText.setText("\nX: " + xOrien + "\nY: " + yOrien + "\nZ: " + zOrien);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
