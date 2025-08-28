package com.twd.settingsccx;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private final String TAG = MainActivity.class.getName();

    private LinearLayout LL_wifi;
    private LinearLayout LL_bluetooth;
    private LinearLayout LL_projection;
    private LinearLayout LL_about;
    private LinearLayout LL_date;
    private LinearLayout LL_language;
    private LinearLayout LL_reset;
    private LinearLayout LL_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        LinearLayout[] views = {
                LL_wifi = findViewById(R.id.LL_wifi),
                LL_bluetooth = findViewById(R.id.LL_bluetooth),
                LL_projection = findViewById(R.id.LL_projection),
                LL_about = findViewById(R.id.LL_about),
                LL_date = findViewById(R.id.LL_date),
                LL_language = findViewById(R.id.LL_language),
                LL_reset = findViewById(R.id.LL_reset),
                LL_settings = findViewById(R.id.LL_settings)
        };
        for (LinearLayout view : views) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setClickable(true);
            view.setOnClickListener(this::onClick);
            view.setOnFocusChangeListener(this::onFocusChange);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if(v.getId() == R.id.LL_wifi){
            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        } else if (v.getId() == R.id.LL_bluetooth) {
            intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
        } else if (v.getId() == R.id.LL_projection) {
            intent = new Intent(this, ProjectionActivity.class);
        } else if (v.getId() == R.id.LL_about) {
            intent = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
        } else if (v.getId() == R.id.LL_date) {
            intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        } else if (v.getId() == R.id.LL_language) {
            intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        } else if (v.getId() == R.id.LL_reset) {
            intent = new Intent(Settings.ACTION_PRIVACY_SETTINGS);
        } else if (v.getId() == R.id.LL_settings) {
            intent = new Intent(Settings.ACTION_SETTINGS);
        }
        if (intent != null){
            startActivity(intent);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            v.setForeground(getResources().getDrawable(R.drawable.border_white));
            v.animate().scaleX(1.1f).scaleY(1.1f).translationZ(1f).setDuration(100);
        }else {
            v.setForeground(null);
            v.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(100);
        }
    }
}