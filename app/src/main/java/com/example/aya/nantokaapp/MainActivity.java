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
    public static final String TIME_ZONE = "Asia/Tokyo";
    public static final String KEY_TOTAL_FEE = "fee";
    public static final String KEY_LAST_SHOW = "lastShow";
    public static final String KEY_LAST_SHOW_YEAR = "lastShowYear";
    public static final String KEY_LAST_SHOW_MONTH = "lastShowMonth";
    public static final String MONTH_TOTAL = "monthTotal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);

        totalOfLastMonth();
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

        if (price != 0) {
            saveTotalFee(updatedFee);
        }
    }

    private void saveTotalFee(int totalFee) {
        // TODO: これMONTH_TOTALんとこにいれてもいーような気もする
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
        SharedPreferences prefDate = getSharedPreferences(KEY_LAST_SHOW, Context.MODE_PRIVATE);

        int lastShowYear = prefDate.getInt(KEY_LAST_SHOW_YEAR, 0);
        int lastShowMonth = prefDate.getInt(KEY_LAST_SHOW_MONTH, 0);

        Calendar nowDate = getNowDate();

        if (lastShowYear == 0 && lastShowMonth == 0) {
            saveLastDate(nowDate);
            return;
        }

        if (nowDate.compareTo(getPrefLastDate(lastShowYear, lastShowMonth)) <= 0) {
            return;
        }

        saveTotalOfLastMonth(nowDate);
        saveLastDate(nowDate);
        resetTotalFee();
    }

    private Calendar getNowDate() {
        // あれ、Date and Time APIつかえないの…？？（しょぼーん
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        Calendar nowDate = Calendar.getInstance(timeZone);
        nowDate.set(Calendar.MONTH, nowDate.get(Calendar.MONTH) + 1);
        return nowDate;
    }

    private void saveLastDate(Calendar nowDate) {
        SharedPreferences prefDate = getSharedPreferences(KEY_LAST_SHOW, Context.MODE_PRIVATE);
        prefDate.edit().putInt(KEY_LAST_SHOW_YEAR, nowDate.get(Calendar.YEAR)).apply();
        prefDate.edit().putInt(KEY_LAST_SHOW_MONTH, nowDate.get(Calendar.MONTH)).apply();
    }

    private Calendar getPrefLastDate(int lastShowYear, int lastShowMonth) {
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE);
        Calendar date = Calendar.getInstance(timeZone);
        date.set(lastShowYear, lastShowMonth, 1);
        date.set(Calendar.DATE, date.getActualMaximum(Calendar.DATE));
        return date;
    }

    private void saveTotalOfLastMonth(Calendar nowTime) {
        // TODO: 2ヶ月あいたときのこと考えてなかった
        SharedPreferences prefTotal = getSharedPreferences(MONTH_TOTAL, Context.MODE_PRIVATE);
        prefTotal.edit().putInt(String.valueOf(nowTime.get(Calendar.MONTH) - 1), getTotalFee()).apply();
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

            case R.id.action_timer:
                startActivity(new Intent(MainActivity.this, TimerActivity.class));
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
