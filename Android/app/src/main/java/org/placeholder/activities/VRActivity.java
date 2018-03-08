package org.placeholder.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import org.placeholder.gimbalcontrol.OrientationSensor;
import org.placeholder.gimbalcontrol.UDPClient;

import javax.microedition.khronos.egl.EGLConfig;

public class VRActivity extends GvrActivity implements GvrView.StereoRenderer {

    private static final String TAG = "VRActivity";

    private GvrView vrView = null;
    private ImageView imageViewLeft = null;
    private ImageView imageViewRight = null;

    final OrientationSensor sensor = OrientationSensor.getOrientationSensor();
    Thread imageReceiveAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);
        sensor.registerSensor(this);

        vrView = (GvrView)findViewById(R.id.gvr_view);
        vrView.setRenderer(this);
        setGvrView(vrView);

        imageViewLeft = new ImageView(this);
        imageViewRight = new ImageView(this);

        GvrView.LayoutParams layoutParamsLeft = new GvrView.LayoutParams(1920 / 2, 1080);
        layoutParamsLeft.setMargins(0, 0, 0, 0);
        vrView.addView(imageViewLeft, layoutParamsLeft);
        GvrView.LayoutParams layoutParamsRight = new GvrView.LayoutParams(1920 / 2, 1080);
        layoutParamsRight.setMargins(1920 / 2, 0, 0, 0);
        vrView.addView(imageViewRight, layoutParamsRight);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final int port = Integer.parseInt(sharedPref.getString("CAMERA_PORT", "-1"));
        Log.i("video", "camera port: " + port + "\n");
        final byte[] data = new byte[5001];
        if (imageReceiveAgent == null) {
            imageReceiveAgent = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(Thread.currentThread().getName(), "Starting receive image");

                    while (true) {
                        byte[] received = null;
                        try {
                            received = UDPClient.receiveDatagram(port, data);
                            Log.i("video", "received length: " + received.length + "\n");
                            if (received.length == 4) {
                                int imageBytesSize = UDPClient.getIntFromBytes(received);
                                int packageNum = imageBytesSize % 5000 == 0 ? imageBytesSize / 5000 : (imageBytesSize / 5000 + 1);

                                int checkPackageSize = 0;
                                byte[] imageBytes = new byte[imageBytesSize];
                                for (int i = 0; i < packageNum; i++) {
                                    received = UDPClient.receiveDatagram(port, data);
                                    checkPackageSize += received.length;
                                    System.arraycopy(received, 0, imageBytes, i * 5000, received.length);
                                }
                                if (Math.abs(checkPackageSize - imageBytesSize) <= 0) {
                                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageViewLeft.setImageBitmap(bitmap);
                                            imageViewRight.setImageBitmap(bitmap);
                                        }
                                    });
                                }
                            }
                        }
                        catch (Exception e) {
                            Log.e("receiveException", e.getMessage());
                            e.printStackTrace();
                        }
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

    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(Eye eye) {

    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

    }

    @Override
    public void onRendererShutdown() {

    }
}
