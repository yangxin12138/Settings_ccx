package com.twd.settingsccx.utils;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午11:02 19/3/2025
 */
public class AutoFocusUtils {


    public AutoFocusUtils() {
    }

    /**
     * 设置 自动梯形矫正 开启或者关闭
     * 如使用康佳梯形矫正算法，此处无需填写
     * @param enable：true代表需要开启自动梯形矫正   false代表需要关闭自动梯形矫正
     */
    public void setTrapezoidCorrectEnable(boolean enable){
        if (enable) {
            SystemPropertiesUtils.setProperty("persist.sys.trapezoid", "1");
        }else {
            SystemPropertiesUtils.setProperty("persist.sys.trapezoid", "0");
        }
    }

    /**
     * 获取 自动梯形矫正 开启或者关闭 的状态
     * 如使用康佳梯形矫正算法，此处无需填写
     * @return status: true代表当前开启了自动梯形矫正   false代表当前关闭了自动梯形矫正
     */
    public String getTrapezoidCorrectStatus(){
        return SystemPropertiesUtils.getProperty("persist.sys.trapezoid","-1");
    }


}
