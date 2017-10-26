package org.placeholder.gimbalcontrol;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;

import org.placeholder.activities.R;

/**
 * Created by XZman on 19/10/2017.
 */

public class OrientationSensor {

    private static OrientationSensor instance = null;

    public static OrientationSensor getOrientationSensor() {
        if (instance == null)
            synchronized (OrientationSensor.class) {
                if (instance == null)
                    return instance = new OrientationSensor();
            }
        return instance;
    }

    private OrientationSensor() {
    }

    private SensorManager mSensorManager;
    private Sensor rotationSensor;
    private Sensor gyroscope;
    private SensorEventListener mSensorListener;

    public synchronized void registerSensor(final Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        // TO-DO: get gyroscope
        gyroscope = null;
        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ROTATION_VECTOR:
                        computeOrientation(event.values);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        computeSpeed(event.values);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        mSensorManager.registerListener(mSensorListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // TO-DO: register listener for gyroscope

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("Starting Thread: ", Thread.currentThread().getName());
                while(true) {
                    sendOrientationData();
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void unregisterSensor() {
        mSensorManager.unregisterListener(mSensorListener, rotationSensor);
    }

    // unit: degree
    private volatile float xOrientation;
    private volatile float yOrientation;
    private volatile float zOrientation;

    // unit: radian??
    private volatile float xSpeed;
    private volatile float ySpeed;
    private volatile float zSpeed;

    // unused getters
    public float getXDegree() {
        return xOrientation;
    }

    public float getYDegree() {
        return yOrientation;
    }

    public float getZDegree() {
        return zOrientation;
    }

    public float getXSpeed() {
        return xSpeed;
    }

    public float getYSpeed() {
        return ySpeed;
    }

    public float getZSpeed() {
        return zSpeed;
    }

    private synchronized void setOrientation(final float x, final float y, final float z) {
        xOrientation = x;
        yOrientation = y;
        zOrientation = z;
    }

    @Deprecated
    private static float constrain(final float value) {
        if (value > 180)
            return 180f;
        if (value < 0)
            return 0f;
        return value;
    }

    private synchronized void computeOrientation(final float[] values) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        float x = (float)(orientation[0] * 180 / Math.PI + 90);
        float y = (float)(orientation[1] * 180 / Math.PI + 90);
        float z = (float)(orientation[2] * 180 / Math.PI + 180);

        x = x > 180 ? 180f : x < 0 ? 0f : x;
        // y does not need constrain
        z = z > 270 ? 0f : z > 180 ? 180f : z;

        setOrientation(x, y, z);
    }

    private void sendOrientationData() {
        byte[] orientationByte = {(byte)xOrientation, (byte)yOrientation, (byte)zOrientation};
        try {
            UDPClient.sendDatagram("192.168.2.4", 23333, orientationByte, 500);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void computeSpeed(final float[] values) {
        // TO-DO: process data
        // TO-DO: send data to gimbal
    }
}