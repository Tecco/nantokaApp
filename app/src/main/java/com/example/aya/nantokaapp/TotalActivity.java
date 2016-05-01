package com.example.aya.nantokaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by aya on 2016/03/31.
 */
public class TotalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setMenuReturnButton();

        displayLastMonth();
        displayLastMonthTotal();
    }

    private void setMenuReturnButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayLastMonth() {
        TextView text = (TextView) findViewById(R.id.last_month);
        text.setText("先月（" + getLastMonth() + "月）、");
    }

    private void displayLastMonthTotal() {
        TextView text = (TextView) findViewById(R.id.last_month_fee_text);
        text.setText(getPrefLastMonthTotal(getLastMonth()) + getString(R.string.yen));
    }

    public int getLastMonth() {
        TimeZone timeZone = TimeZone.getTimeZone(MainActivity.TIME_ZONE);
        Calendar nowTime = Calendar.getInstance(timeZone);
        return nowTime.get(Calendar.MONTH);
    }

    private int getPrefLastMonthTotal(int lastMonth) {
        SharedPreferences pref = getSharedPreferences(MainActivity.MONTH_TOTAL, Context.MODE_PRIVATE);
        return pref.getInt(String.valueOf(lastMonth), 0);
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
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}