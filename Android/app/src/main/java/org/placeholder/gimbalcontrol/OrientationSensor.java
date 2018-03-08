package org.placeholder.gimbalcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by XZman on 19/10/2017.
 */

public class OrientationSensor {

    private Context myContext = null;

    private static OrientationSensor instance = null;

    private Thread sendOrientationDataAgent = null;

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
        myContext = context;

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorListener = new SensorEventListener() {
            private boolean isStartup = true;

            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        if (isStartup) {
                            setInitialOrientation(event.values);
                            isStartup = false;
                        }
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
        mSensorManager.registerListener(mSensorListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(myContext);
        final int sendRate = Integer.parseInt(sharedPref.getString("GIMBAL_SEND_RATE", "30"));
        if (sendOrientationDataAgent == null) {
            sendOrientationDataAgent = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(Thread.currentThread().getName(), "Starting sendOrientationData");
                    while (true) {
//                        if (checkOrientationValidity())
                        sendOrientationData();
                        try {
                            Thread.sleep(sendRate);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            sendOrientationDataAgent.start();
        }
    }

    public void unregisterSensor() {
        mSensorManager.unregisterListener(mSensorListener, rotationSensor);
        mSensorManager.unregisterListener(mSensorListener, gyroscope);
    }

    // unit: degree
    private volatile float xOrientation;
    private volatile float yOrientation;
    private volatile float zOrientation;

    // unit: radian
    // initial device orientation
    private float initX;
    private float initY;
    private float initZ;

    // unit: degree
    private volatile float xSpeed;
    private volatile float ySpeed;
    private volatile float zSpeed;

    // getters
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

//        Log.i("setOrientation", x + ", " + y + ", " + z);
    }

    private synchronized void setSpeed(final float x, final float y, final float z) {
        xSpeed = x;
        ySpeed = y;
        zSpeed = z;
    }

    @Deprecated
    private static float constrain(final float value) {
        if (value > 180)
            return 180f;
        if (value < 0)
            return 0f;
        return value;
    }

    private void setInitialOrientation(final float[] values) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        initX = orientation[0];
        initY = orientation[1];
        initZ = orientation[2];

        Log.i("setInitOrientation", initX + ", " + initY + ", " + initZ);
    }

    private synchronized void computeOrientation(final float[] values) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, values);
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

//        float x = (float) ((orientation[0] - initX) * 180 / Math.PI + 90);

        float x = (float) (orientation[0] - initX);
        if (x > Math.PI)
            x -= 2 * Math.PI;

        x = (float) (x * 180 / Math.PI + 90);
        float y = (float) (orientation[1] * 180 / Math.PI + 90);
        float z = (float) (orientation[2] * 180 / Math.PI + 180);

//        Log.i("computeOrient ation", x + ", " + y + ", " + z);
        x = x > 180 ? 180f : x < 0 ? 0f : x;
//        // y does not need constrain
        z = z > 270 ? 0f : z > 180 ? 180f : z;

//        // constrain x, y, and z to [45, 135]
//        x = x > 135 ? 135f : x < 45 ? 45f : x;
//        y = y > 135 ? 135f : y < 45 ? 45f : y;
//        z = z > 135 ? 135f : z < 45 ? 45f : z;

        setOrientation(x, y, z);
    }

    private boolean checkOrientationValidity() {
        return checkOrienValidityHelper(xOrientation) && checkOrienValidityHelper(yOrientation) && checkOrienValidityHelper(zOrientation);
    }

    private boolean checkOrienValidityHelper(float orien) {
        return orien >= 45 && orien <= 135;
    }

    private void sendOrientationData() {
        byte[] orientationByte = {(byte) xOrientation, (byte) yOrientation, (byte) zOrientation, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        UDPClient.getBytesFromFloat(xSpeed, orientationByte, 3);
        UDPClient.getBytesFromFloat(ySpeed, orientationByte, 7);
        UDPClient.getBytesFromFloat(zSpeed, orientationByte, 11);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(myContext);
        final String ip = sharedPref.getString("GIMBAL_IP_ADDRESS", "0.0.0.0");
        final int port = Integer.parseInt(sharedPref.getString("GIMBAL_PORT", "-1"));
        try {
            UDPClient.sendDatagram(ip, port, orientationByte, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.i("sent", xOrientation + ", " + yOrientation + ", " + zOrientation);
    }

    private synchronized void computeSpeed(final float[] values) {
        float x = (float) (values[0] * 180 / Math.PI);
        float y = (float) (values[1] * 180 / Math.PI);
        float z = (float) (values[2] * 180 / Math.PI);

        setSpeed(x, y, z);
    }
}