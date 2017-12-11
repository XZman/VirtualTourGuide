package org.placeholder.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startVirtualTour(View v) {
        Toast.makeText(this, "Starting Virtual Tour...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, VRActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_menu_about:
                // TO-DO: start AboutActivity
                return true;
            case R.id.activity_main_menu_setting:
                // TO-DO: start SettingActivity
                return true;

            // for fun poi
            case R.id.activity_main_menu_poi:
                alertPoi();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // for fun poi
    private void alertPoi() {
        new AlertDialog.Builder(this)
                .setMessage("Poi!")
                .setTitle("Poi?")
                .setCancelable(true)
                .create()
                .show();
    }
}
