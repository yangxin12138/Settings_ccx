package com.twd.settingsccx;

import android.os.Bundle;

import com.twd.settingsccx.fragment.SinglePointFragment;
import com.twd.settingsccx.repository.BaseActivity;
import com.twd.settingsccx.utils.UiUtils;

/**
 * @Author:Yangxin
 * @Description:
 * @time: Create in 上午10:40 26/8/2025
 */
public class SinglePointActivity extends BaseActivity {
    public static int mode = 0;
    protected void onCreate(Bundle paramBundle){
        super.onCreate(paramBundle);
        UiUtils.replaceFragment(getSupportFragmentManager(),16908290, SinglePointFragment.newInstance());
    }
}
