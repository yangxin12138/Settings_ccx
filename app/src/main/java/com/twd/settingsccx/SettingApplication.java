package com.twd.settingsccx;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.twd.settingsccx.commonlibrary.Utils;

public class SettingApplication
        extends MultiDexApplication {
    private static final String TAG = "SettingApplication";
    private ActivityLifecycleCallbacks activityLifecycleCallbacks;
    private long enterBackgroundTime;
    private int settingThemeResId = R.style.Theme_TVSettings2_White;//2131821027;

    private ActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return new ActivityLifecycleCallbacks() {
            private boolean isChangingConfiguration = false;
            private int mForegroundCount = 0;

            @Override
            public void onActivityCreated(Activity activity, Bundle paramAnonymousBundle) {
                Log.d("SettingApplication", "onActivityCreated  " + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d("SettingApplication", "onActivityDestroyed  " + activity.getLocalClassName());
                if ((activity instanceof MainActivity)) {
                    //       KkDataUtils.onExit();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d("SettingApplication", "onActivityPaused  " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d("SettingApplication", "onActivityResumed  " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle paramAnonymousBundle) {
                Log.d("SettingApplication", "onActivitySaveInstanceState  " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d("SettingApplication", "onActivityStarted  " + activity.getLocalClassName());
                mForegroundCount += 1;
                if ((mForegroundCount == 1) && (!isChangingConfiguration)) {
                    Log.d("SettingApplication", "onActivityStarted 进入前台 " + activity.getLocalClassName());
                    if ((enterBackgroundTime > 0L)/* && (KkDataUtils.isInitSdkSuccess())*/ && (System.currentTimeMillis() - enterBackgroundTime > 30000L)) {
                        sentAppStartEvent();
                    }
                }
                isChangingConfiguration = false;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d("SettingApplication", "onActivityStopped  " + activity.getLocalClassName());

                mForegroundCount -= 1;
                if ((mForegroundCount == 0) && (!activity.isChangingConfigurations())) {
                    Log.d("SettingApplication", "onActivityStarted 进入后台 " + activity.getLocalClassName() + "  config=" + isChangingConfiguration +
                            "  newConfig=" + activity.isChangingConfigurations() + " isFinish=" + activity.isFinishing() + " isDestroyed=" + activity.isDestroyed());
                    //        SettingApplication.access$002(SettingApplication.this, System.currentTimeMillis());
                }
                isChangingConfiguration = activity.isChangingConfigurations();
            }
        };
    }

    public void changeTheme(int paramInt) {
        Log.d("SettingApplication", "changeTheme()  切换主题了");
        settingThemeResId = paramInt;
        setTheme(paramInt);
    }

    public int getThemeId() {
        return settingThemeResId;
    }

    public void initLogSwitch(Context paramContext) {
 /*   boolean bool;
    try
    {
      bool = KkUtils.isDebugEnable(paramContext);
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
      bool = false;
    }
    //SettingConfig.IS_DEBUG = bool;
    paramContext = HLog.INSTANCE;
    int i;
    if (SettingConfig.IS_DEBUG) {
      i = -1;
    } else {
      i = 1;
    }
    paramContext.setLogLevel(i);

  */
    }

    public void onCreate() {
        super.onCreate();
        Log.d("SettingApplication", "onCreate()  启动了");
        initLogSwitch(getApplicationContext());
        //changeTheme(KkUtils.getSysThemeTypeResId());
        changeTheme(R.style.Theme_TVSettings2_White);
        //changeTheme(R.style.Theme_TVSettings2_IceCreamBlue);
        //changeTheme(R.style.Theme_TVSettings2_StarrySkyBlue);
        Utils.init(this);
    /*KkDataUtils.init(this, new InitCallBack()
    {
      public void onInitSDK(boolean paramAnonymousBoolean, String paramAnonymousString)
      {
        if (paramAnonymousBoolean) {
          sentAppStartEvent();
        }
      }
    });*/
        if (activityLifecycleCallbacks == null) {
            ActivityLifecycleCallbacks localActivityLifecycleCallbacks = getActivityLifecycleCallbacks();
            activityLifecycleCallbacks = localActivityLifecycleCallbacks;
            registerActivityLifecycleCallbacks(localActivityLifecycleCallbacks);
        }
        // new SettingConfig();
    }

    public void sentAppStartEvent() {
        enterBackgroundTime = 0L;
        //KkDataUtils.sent("systemsetting10_app_start", DataMapUtils.getInstance("time", TimeUtils.getNowString()).putData("sec", null).putData("open_id", null).getDataMap());
    }
}
