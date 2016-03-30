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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int WASH_PRICE = 200;
    public static final String KEY_TOTAL_FEE = "fee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);

        initTotalFee();

        fabAdd.setOnClickListener(view -> updateFee(WASH_PRICE));
        fabRemove.setOnClickListener(view -> updateFee(-WASH_PRICE));
    }

    private void initTotalFee() {
        updateFee(0);
    }

    private void updateFee(int price) {
        TextView text = (TextView) findViewById(R.id.fee_text);

        // TODO: なんか名前が気持ち悪くなってきたけど考える気力が起きない
        int updatedFee = getTotalFee() + price;
        
        if (updatedFee < 0) {
            return;
        }

        text.setText(updatedFee + getString(R.string.yen));

        saveTotalFee(updatedFee);
    }

    private void saveTotalFee(int totalFee) {
        SharedPreferences pref = getSharedPreferences(KEY_TOTAL_FEE, Context.MODE_PRIVATE);
        pref.edit().putInt(KEY_TOTAL_FEE, totalFee).apply();
    }

    private int getTotalFee() {
        SharedPreferences pref = getSharedPreferences(KEY_TOTAL_FEE, Context.MODE_PRIVATE);
        return pref.getInt(KEY_TOTAL_FEE, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_reset:
                saveTotalFee(0);
                initTotalFee();

                Toast.makeText(this, getString(R.string.reset_toast), Toast.LENGTH_LONG).show();

                return true;
            default:
                // some action
        }

        return super.onOptionsItemSelected(item);
    }
}
