package com.twd.settingsccx.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.twd.settingsccx.SettingApplication;

public class UiUtils {
    public static void addFragment(FragmentManager fragmentManager, int paramInt, Fragment paramFragment1, Fragment paramFragment2) {
        fragmentManager.beginTransaction().add(paramInt, paramFragment1).hide(paramFragment2).addToBackStack(null).commitAllowingStateLoss();
    }

    public static void addFragment(FragmentManager fragmentManager, int paramInt, Fragment paramFragment1, Fragment paramFragment2, String paramString) {
        fragmentManager.beginTransaction().add(paramInt, paramFragment1, paramString).hide(paramFragment2).addToBackStack(null).commitAllowingStateLoss();
    }

    public static String getPackageVersionName(Context mContext, String paramString) {
        try {
            return mContext.getPackageManager().getPackageInfo(paramString, 0).versionName;
        } catch (Exception paramContext) {
            paramContext.printStackTrace();
        }
        return null;
    }

    public static int getScreenHeight(Resources paramResources) {
        if (paramResources == null) {
            return 0;
        }
        return paramResources.getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Resources paramResources) {
        if (paramResources == null) {
            return 0;
        }
        return paramResources.getDisplayMetrics().widthPixels;
    }

    public static int getThemeResId(Resources.Theme paramTheme, int paramInt) {
        TypedValue localTypedValue = new TypedValue();
        paramTheme.resolveAttribute(paramInt, localTypedValue, true);
        return localTypedValue.resourceId;
    }

    public static boolean isIdle(Context paramContext) {
        boolean bool = true;
        if (paramContext == null) {
            return true;
        }
        PowerManager pm = (PowerManager) paramContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!pm.isDeviceIdleMode()) {
                if (!pm.isInteractive()) {
                    return true;
                }
                bool = false;
            }
            return bool;
        } else if (Build.VERSION.SDK_INT >= 20) {
            return pm.isInteractive() ^ true;
        }
        return pm.isScreenOn() ^ true;
        //return bool;
    }

    public static void popBackStack(FragmentManager fragmentManager, String paramString) {
        fragmentManager.popBackStack(paramString, 1);
    }

    public static void replaceFragment(FragmentManager fragmentManager, int paramInt, Fragment paramFragment) {
        fragmentManager.beginTransaction().replace(paramInt, paramFragment).commitAllowingStateLoss();
    }

    public static void replaceFragment(FragmentManager fragmentManager, int paramInt, Fragment paramFragment, String paramString) {
        fragmentManager.beginTransaction().replace(paramInt, paramFragment, paramString).commitAllowingStateLoss();
    }

    public static void replaceFragmentHadBackStack(FragmentManager fragmentManager, int paramInt, Fragment paramFragment, String paramString) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(paramInt, paramFragment);
        transaction.addToBackStack(paramString);
        transaction.commitAllowingStateLoss();
    }

    public static void setOnClickListener(View paramView, final View.OnClickListener onClickListener) {
        if (paramView != null) {
            if (onClickListener == null) {
                return;
            }
            paramView.setOnClickListener(onClickListener);
            paramView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                    if (keycode == keyEvent.KEYCODE_DPAD_RIGHT) {
                        int action = keyEvent.getAction();
                        if (action != KeyEvent.ACTION_DOWN) {
                            if (action != KeyEvent.ACTION_UP) {
                                return false;
                            }
                            //UiUtils.this.setPressed(false);
                            onClickListener.onClick(view);
                            return true;
                        }
                        //UiUtils.this.setPressed(true);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
