package org.placeholder.gimbalcontrol;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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

    public void registerSensor(final Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        // TO-DO: get gyroscope
        gyroscope = null;
        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ROTATION_VECTOR:
                        processRotationFromSensor(event.values);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        processSpeedFromSensor(event.values);
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
    }

    public void unregisterSensor() {
        mSensorManager.unregisterListener(mSensorListener, rotationSensor);
    }

    // unit: degree
    private float xOrientation;
    private float yOrientation;
    private float zOrientation;

    // unit: radian??
    private float xSpeed;
    private float ySpeed;
    private float zSpeed;

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

    private void processRotationFromSensor(final float[] values) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);
        xOrientation = (float)(orientation[1] * 180 / Math.PI);
        yOrientation = (float)(orientation[2] * 180 / Math.PI + 90);
        zOrientation = (float)(orientation[0] * 180 / Math.PI);

        byte[] orientationByte = {(byte)xOrientation, (byte)yOrientation, (byte)zOrientation};
        try {
            UDPClient.sendDatagram(Resources.getSystem().getString(R.string.gimbal_ip),
                    Resources.getSystem().getInteger(R.integer.gimbal_address_port),
                    orientationByte,
                    Resources.getSystem().getInteger(R.integer.gimbal_sendData_timeout));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processSpeedFromSensor(final float[] values) {
        // TO-DO: process data
        // TO-DO: send data to gimbal
    }
}