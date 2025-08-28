package com.twd.settingsccx.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.twd.settingsccx.R;
import com.twd.settingsccx.model.SettingItem;
import com.twd.settingsccx.utils.AutoFocusUtils;
import com.twd.settingsccx.utils.Utils;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 下午4:24 21/8/2025
 */
public class ProjectorViewModel extends ViewModel {
    // 投影方式设置项
    private SettingItem projectionModeItem;
    // 自动校正设置项
    private SettingItem autoCorrectItem;
    // 四点梯形校正设置项
    private SettingItem keystoneCorrectionItem;
    // 缩放设置项
    private SettingItem zoomItem;
    // 重置设置项
    private SettingItem resetItem;

    // Getter方法（供布局绑定）
    public SettingItem getProjectionModeItem() {
        return projectionModeItem;
    }

    public SettingItem getAutoCorrectItem() {
        return autoCorrectItem;
    }

    public SettingItem getKeystoneCorrectionItem() {
        return keystoneCorrectionItem;
    }

    public SettingItem getZoomItem() {
        return zoomItem;
    }

    public SettingItem getResetItem() {
        return resetItem;
    }
    private static final int MIN_ZOOM = 50;
    private static final int MAX_ZOOM = 100;
    private static final int STEP = 5;
    // 可添加修改设置的方法（例如切换投影方式）
    public void switchProjectionMode(String newMode) {
        Log.d("yangxin", "switchProjectionMode: 设置投影方式为 "+newMode);
        projectionModeItem.setContent(newMode);
    }

    public ProjectorViewModel() {}
    AutoFocusUtils autoFocusUtils ;
    public void initData(Application paramApplication){
        int postion = Utils.getProjectionMode();
        if (postion == 0){ //正装正投
            projectionModeItem = new SettingItem(R.drawable.projection_mode,paramApplication.getString(R.string.projection_mode),paramApplication.getString(R.string.projection_front), View.VISIBLE,View.VISIBLE);
        } else if (postion == 1) { //正装背投
            projectionModeItem = new SettingItem(R.drawable.projection_mode,paramApplication.getString(R.string.projection_mode),paramApplication.getString(R.string.projection_rear), View.VISIBLE,View.VISIBLE);
        }else if (postion == 2) { //背装正投
            projectionModeItem = new SettingItem(R.drawable.projection_mode,paramApplication.getString(R.string.projection_mode),paramApplication.getString(R.string.projection_front_ceiling), View.VISIBLE,View.VISIBLE);
        }else if (postion == 3) { //背装背投
            projectionModeItem = new SettingItem(R.drawable.projection_mode,paramApplication.getString(R.string.projection_mode),paramApplication.getString(R.string.projection_rear_ceiling), View.VISIBLE,View.VISIBLE);
        }
        autoFocusUtils = new AutoFocusUtils();
        String autoCorrect = autoFocusUtils.getTrapezoidCorrectStatus();
        autoCorrectItem = new SettingItem(R.drawable.projection_auto, paramApplication.getString(R.string.projection_auto)
                ,paramApplication.getString(autoCorrect.equals("1")?R.string.projection_auto_enable:R.string.projection_auto_disable),View.VISIBLE,View.VISIBLE);

        keystoneCorrectionItem = new SettingItem(R.drawable.projection_four, paramApplication.getString(R.string.projection_four), "",View.INVISIBLE,View.INVISIBLE);
        zoomItem = new SettingItem(R.drawable.projection_zoom, paramApplication.getString(R.string.projection_zoom), "100%",View.VISIBLE,View.VISIBLE);
        resetItem = new SettingItem(R.drawable.projection_reset, paramApplication.getString(R.string.projection_reset), "",View.INVISIBLE,View.INVISIBLE);
    }

    public interface OnSettingActionListener {
        /** 左右键事件：返回 true 表示消费掉该事件 */
        boolean onLeftRight(int keyCode, SettingItem item, KeyEvent even);
        /** 点击事件 */
        void onClick(SettingItem item);
    }
    private OnSettingActionListener actionListener;

    public void setActionListener(OnSettingActionListener l) {
        this.actionListener = l;
    }
    /* =============== 供布局绑定的公开方法 =============== */
    public boolean onKeyLeftRight(int keyCode, SettingItem item, KeyEvent event) {
        return actionListener != null && actionListener.onLeftRight(keyCode, item,event);
    }

    public void onItemClick(SettingItem item) {
        if (actionListener != null) actionListener.onClick(item);
    }

    /**
     * @param increase true=加5%，false=减5%
     */
    public void adjustZoom(boolean increase) {
        int current = Integer.parseInt(zoomItem.getContent().replace("%", ""));
        current = increase
                ? Math.min(current + STEP, MAX_ZOOM)
                : Math.max(current - STEP, MIN_ZOOM);
        zoomItem.setContent(current + "%");
    }
}
