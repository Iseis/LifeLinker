package com.apps.andrew.lifelinker;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;


public class BluToothTwoPlayerActivity extends AppCompatActivity {

    public static final int GET_DEVICE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blu_tooth_two_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blu_tooth_two_player, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.connect_bluTooth) {

            BlueToothDevicesDialog dialog = BlueToothDevicesDialog.newInstance();
            dialog.setTargetFragment(BlueToothTwoPlayerFragment.newInstance(), GET_DEVICE);
            dialog.show(getSupportFragmentManager(), "hello");

        }

        return super.onOptionsItemSelected(item);
    }
}
