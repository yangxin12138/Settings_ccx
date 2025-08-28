package com.twd.settingsccx.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午11:21 23/8/2025
 */
public class Utils {
    private static final String TAG = Utils.class.getName();
    private static final String PATH_CONTROL_MIPI = "persist.sys.projection";
    public static int getProjectionMode(){
        int ret = 0;
        ret = Integer.parseInt(SystemPropertiesUtils.getProperty(PATH_CONTROL_MIPI,"5"));
        return ret;
    }


}
