package com.twd.settingsccx.repository;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.twd.settingsccx.R;


public class BaseActivity extends AppCompatActivity {
    private final static String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        Application localApplication = getApplication();
        //if ((localApplication instanceof SettingApplication)) {
        //    setTheme(((SettingApplication)localApplication).getThemeId());
        //}
        super.onCreate(savedInstanceState);
    }

    protected void initTitle(String str) {
        TextView titleView = (TextView) findViewById(R.id.titleTV);
        if (titleView == null) {
            return;
        }
        titleView.setText(str);
    }
}