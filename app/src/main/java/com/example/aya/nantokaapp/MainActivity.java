package com.example.aya.nantokaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);

        updateFee(0);

        fabAdd.setOnClickListener(view -> updateFee(200));
        fabRemove.setOnClickListener(view -> updateFee(-200));
    }

    private void updateFee(int price) {
        TextView text = (TextView) findViewById(R.id.text);
        int fee = getFee();

        i = fee + price;
        text.setText(i + getString(R.string.yen));

        saveFee();
    }

    private void saveFee() {
        SharedPreferences pref = getSharedPreferences("Fee", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("Fee", i);
        editor.apply();
    }

    private int getFee() {
        SharedPreferences pref = getSharedPreferences("Fee", Context.MODE_PRIVATE);
        int fee = pref.getInt("Fee", 0);

        return fee;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
