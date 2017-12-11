package org.placeholder.vrtest;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class VrPanoActivity extends Activity {

    private VrPanoramaView vrPanoView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_pano);

        vrPanoView = (VrPanoramaView)findViewById(R.id.vr_pano);

        vrPanoView.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), null);
        
    }
}
