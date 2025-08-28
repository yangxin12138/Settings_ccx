package com.twd.settingsccx.repository.keystone;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class Lcd {
    private static final String TAG = "lcd_info";
    protected static Context mContext;
    protected static float mStepX_onepoint = 0.0f;
    protected static float mStepY_onepoint = 0.0f;
    protected static float mStepX_twopoint = 0.0f;
    protected static float mStepY_twopoint = 0.0f;
    protected static int lcdWidth;
    protected static int lcdHeight;
    protected static int lcdValidWidth_top ;
    protected static int lcdValidWidth_bottom ;
    protected static int lcdValidHeight_left;
    protected static int lcdValidHeight_right;

    public final int MODE_ONEPOINT = 1;
    public final int MODE_TWOPOINT = 0;
//    protected static SharedPreferences prefs;
    protected static int mode =0;
    public  Lcd(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm =  context.getResources().getDisplayMetrics();
        lcdWidth = dm.widthPixels;
        lcdHeight = dm.heightPixels;

        if(lcdWidth==1024 && lcdHeight == 600){
            mStepX_onepoint = 2.56f;
            mStepY_onepoint = 1.5f;
            mStepX_twopoint = 5.12f;
            mStepY_twopoint = 3.0f;
        }else if(lcdWidth==1136 && lcdHeight == 640){
            mStepX_onepoint = 1.6f;//2.56f;
            mStepY_onepoint = 2.84f;//1.5f;
            mStepX_twopoint = 3.2f;//5.12f;
            mStepY_twopoint = 5.68f;//3.0f;
        }else if(lcdWidth==1280 && lcdHeight == 720){
            mStepX_onepoint = 3.2f;
            mStepY_onepoint = 1.8f;
            mStepX_twopoint = 6.4f;
            mStepY_twopoint = 3.6f;
        }else if(lcdWidth==1920 && lcdHeight == 1080){
            mStepX_onepoint = 4.8f;
            mStepY_onepoint = 2.7f;
            mStepX_twopoint = 9.6f;
            mStepY_twopoint = 5.4f;
        }else{
            mStepX_onepoint = 2.56f;
            mStepY_onepoint = 1.5f;
            mStepX_twopoint = 5.12f;
            mStepY_twopoint = 3.0f;
        }
//        prefs = context.getSharedPreferences("keystone_mode", Context.MODE_PRIVATE);
//        mode = prefs.getInt("mode",MODE_ONEPOINT);
        Log.d(TAG, "SizeActivity mode: "+mode+", lcdWidth:"+lcdWidth+",lcdHeight:"+lcdHeight);

    }
    public float getStepX(){
        if(mode ==MODE_TWOPOINT){
            return mStepX_twopoint;
        }else if(mode == MODE_ONEPOINT){
            return mStepX_onepoint;
        }
        return mStepX_onepoint;
    }
    public float getStepY(){
        if(mode == MODE_TWOPOINT){
            return mStepY_twopoint;
        }else if(mode == MODE_ONEPOINT){
            return mStepY_onepoint;
        }
        return mStepY_onepoint;
    }
    public int getLcdWidth(){
        return lcdWidth;
    }
    public int getLcdHeight(){
        return lcdHeight;
    }
}