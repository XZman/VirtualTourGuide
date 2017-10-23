package org.placeholder.gimbalcontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by XZman on 19/10/2017.
 */

public class OrientationSensor {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mSensorListener;
    private Context mContext;

    private OrientationSensor(Context mContext) {
        this.mContext = mContext;
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }



    private float X;
    private float Y;
    private float Z;

    public float getXDegree() {
        return X;
    }
    public float getYDegree() {
        return Y;
    }
    public float getZDegree() {
        return Z;
    }
}
