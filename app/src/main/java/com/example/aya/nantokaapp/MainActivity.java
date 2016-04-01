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
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");
        Calendar nowTime = Calendar.getInstance(timeZone);
        Calendar prefTime = Calendar.getInstance(timeZone);

        SharedPreferences prefYear = getSharedPreferences("lastShowYear", Context.MODE_PRIVATE);
        SharedPreferences prefMonth = getSharedPreferences("lastShowMonth", Context.MODE_PRIVATE);

        int lastYear = prefYear.getInt("lastShowYear", 0);
        int lastMonth = prefMonth.getInt("lastShowMonth", 0);

        if (lastYear == 0 && lastMonth == 0) {
            saveLastDate(prefYear, prefMonth, nowTime);
            return;
        }

        prefTime.set(lastYear, lastMonth - 2, 1);
        prefTime.set(Calendar.DATE, prefTime.getActualMaximum(Calendar.DATE));

        if (nowTime.compareTo(prefTime) <= 0) {
            return;
        }

        saveTotalOfLastMonth(nowTime);
        saveLastDate(prefYear, prefMonth, nowTime);
        resetTotalFee();
    }

    private void saveTotalOfLastMonth(Calendar nowTime) {
        String monthTotalName = nowTime.get(Calendar.MONTH) + "monthTotal";
        SharedPreferences prefTotal = getSharedPreferences(monthTotalName, Context.MODE_PRIVATE);
        prefTotal.edit().putInt(monthTotalName, getTotalFee()).apply();
    }

    private void saveLastDate(SharedPreferences prefYear, SharedPreferences prefMonth, Calendar nowTime) {
        prefYear.edit().putInt("lastShowYear", nowTime.get(Calendar.YEAR)).apply();
        prefMonth.edit().putInt("lastShowMonth", nowTime.get(Calendar.MONTH) + 1).apply();
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
