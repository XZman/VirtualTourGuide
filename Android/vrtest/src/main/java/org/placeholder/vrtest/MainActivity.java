package org.placeholder.vrtest;

import android.os.Bundle;
import android.widget.ImageView;

import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends GvrActivity implements GvrView.StereoRenderer {

    private static final String TAG = "MainActivity";

    private GvrView vrView = null;
    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vrView = (GvrView)findViewById(R.id.gvr_view);
        vrView.setRenderer(this);
        setGvrView(vrView);

        imageView = new ImageView(this);
        imageView.setBackgroundResource(R.mipmap.ic_launcher);

        GvrView.LayoutParams layoutParams = new GvrView.LayoutParams(1920 / 2, 1080);
        layoutParams.setMargins(0, 0, 0, 0);
        vrView.addView(imageView, layoutParams);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
//        float[] orientation = new float[3];
//        headTransform.getEulerAngles(orientation, 0);
//        byte[] data = {(byte)orientation[0], (byte)orientation[1], (byte)orientation[2]};
//        try {
//            UDPClient.sendDatagram("", 23333, data, 500);
//            Log.i("msg sent", Arrays.toString(orientation));
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
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
