package com.example.aya.nantokaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final int WASH_PRICE = 200;
    private static final String TIME_ZONE = "Asia/Tokyo";
    public static final String KEY_TOTAL_FEE = "fee";
    public static final String KEY_LAST_SHOW_YEAR = "lastShowYear";
    public static final String KEY_LAST_SHOW_MONTH = "lastShowMonth";

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

        totalOfLastMonth();
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

        if (price != 0) {
            saveTotalFee(updatedFee);
        }
    }

    private void saveTotalFee(int totalFee) {
        SharedPreferences pref = getSharedPreferences(KEY_TOTAL_FEE, Context.MODE_PRIVATE);
        pref.edit().putInt(KEY_TOTAL_FEE, totalFee).apply();
    }

    private int getTotalFee() {
        SharedPreferences pref = getSharedPreferences(KEY_TOTAL_FEE, Context.MODE_PRIVATE);
        return pref.getInt(KEY_TOTAL_FEE, 0);
    }

    private void resetTotalFee() {
        saveTotalFee(0);
        initTotalFee();
    }

    private void totalOfLastMonth() {
        // あれ、Date and Time APIつかえないの…？？（しょぼーん
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        Calendar nowTime = Calendar.getInstance(timeZone);

        SharedPreferences prefYear = getSharedPreferences(KEY_LAST_SHOW_YEAR, Context.MODE_PRIVATE);
        SharedPreferences prefMonth = getSharedPreferences(KEY_LAST_SHOW_MONTH, Context.MODE_PRIVATE);

        int lastYear = prefYear.getInt(KEY_LAST_SHOW_YEAR, 0);
        int lastMonth = prefMonth.getInt(KEY_LAST_SHOW_MONTH, 0);

        if (lastYear == 0 && lastMonth == 0) {
            saveLastDate(prefYear, prefMonth, nowTime);
            return;
        }

        if (nowTime.compareTo(getPrefLastTime(lastYear, lastMonth)) <= 0) {
            return;
        }

        saveTotalOfLastMonth(nowTime);
        saveLastDate(prefYear, prefMonth, nowTime);
        resetTotalFee();
    }

    private void saveLastDate(SharedPreferences prefYear, SharedPreferences prefMonth, Calendar nowTime) {
        prefYear.edit().putInt(KEY_LAST_SHOW_YEAR, nowTime.get(Calendar.YEAR)).apply();
        prefMonth.edit().putInt(KEY_LAST_SHOW_MONTH, nowTime.get(Calendar.MONTH) + 1).apply();
    }

    private Calendar getPrefLastTime(int lastYear, int lastMonth) {
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        Calendar prefTime = Calendar.getInstance(timeZone);
        prefTime.set(lastYear, lastMonth - 2, 1);
        prefTime.set(Calendar.DATE, prefTime.getActualMaximum(Calendar.DATE));
        return prefTime;
    }

    private void saveTotalOfLastMonth(Calendar nowTime) {
        String monthTotalName = nowTime.get(Calendar.MONTH) + "monthTotal";
        SharedPreferences prefTotal = getSharedPreferences(monthTotalName, Context.MODE_PRIVATE);
        prefTotal.edit().putInt(monthTotalName, getTotalFee()).apply();
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
                resetTotalFee();
                Toast.makeText(this, getString(R.string.reset_toast), Toast.LENGTH_LONG).show();
                break;

            case R.id.action_total:
                startActivity(new Intent(MainActivity.this, TotalActivity.class));
                break;

            default:
                // some action
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // 結局使わないかも（わかんない
        int layoutHeight = findViewById(R.id.content_main_layout).getHeight();
        System.out.println("height: " + layoutHeight);
    }
}
