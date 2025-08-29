package com.twd.settingsccx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.twd.settingsccx.databinding.ActivityProjectionBinding;
import com.twd.settingsccx.model.SettingItem;
import com.twd.settingsccx.utils.AutoFocusUtils;
import com.twd.settingsccx.utils.SystemPropertiesUtils;
import com.twd.settingsccx.utils.Utils;
import com.twd.settingsccx.viewmodel.KeystoneViewModel;
import com.twd.settingsccx.viewmodel.ProjectorViewModel;

import java.security.Key;

public class ProjectionActivity extends AppCompatActivity {
    private ActivityProjectionBinding binding;
    private ProjectorViewModel viewModel;
    private KeystoneViewModel keystoneVM;
    private final String TAG = ProjectionActivity.class.getName();
    private static final String PATH_CONTROL_MIPI = "persist.sys.projection";
    private static final int MIN_ZOOM = 50;
    private static final int MAX_ZOOM = 100;
    private static final int STEP = 5;

    AutoFocusUtils autoFocusUtils ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化数据绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_projection);

        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(ProjectorViewModel.class);
        keystoneVM = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())
        ).get(KeystoneViewModel.class);
        // 绑定ViewModel到布局
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        binding.projectionModeInclude.itemRoot.setOnFocusChangeListener((v,hashCode) ->{
            binding.projectionModeInclude.tvContent.setSelected(hashCode);
        });
        viewModel.initData(getApplication());
        viewModel.initZoomFromDisk(getApplication());
        viewModel.setActionListener(new ProjectorViewModel.OnSettingActionListener() {
            @Override
            public boolean onLeftRight(int keyCode, SettingItem item, KeyEvent event) {

                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }

                /* 左右键：只处理投影方式 / 自动校正 / 缩放 */
                if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT &&
                    keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) return false;

                if (item == viewModel.getProjectionModeItem()) {
                    cycleProjectionMode(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT);
                    return true;
                }
                if (item == viewModel.getAutoCorrectItem()) {
                    toggleAutoCorrect();
                    return true;
                }
                if (item == viewModel.getZoomItem()) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        keystoneVM.zoomOut();   // -5%
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        keystoneVM.zoomIn();    // +5%
                    }
                    adjustZoom(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT);
                    return true;
                }
                return false;
            }

            @Override
            public void onClick(SettingItem item) {
                if (item == viewModel.getKeystoneCorrectionItem()) {
                    openFourPointKeystone();
                } else if (item == viewModel.getResetItem()) {
                    resetAll();
                }
            }
        });
        autoFocusUtils = new AutoFocusUtils();
        String autoCorrect = autoFocusUtils.getVerticalCorrectStatus();
        if (autoCorrect.equals("1")){
            binding.keystoneInclude.itemRoot.setVisibility(View.INVISIBLE);
            binding.zoomInclude.itemRoot.setVisibility(View.INVISIBLE);
            binding.resetInclude.itemRoot.setVisibility(View.INVISIBLE);
        }else {
            binding.keystoneInclude.itemRoot.setVisibility(View.VISIBLE);
            binding.zoomInclude.itemRoot.setVisibility(View.VISIBLE);
            binding.resetInclude.itemRoot.setVisibility(View.VISIBLE);
        }
    }
    /*
    * 循环切换投影模式*/
    private void cycleProjectionMode(boolean next) {
        String front = getString(R.string.projection_front);
        String rear = getString(R.string.projection_rear);
        String front_ceiling = getString(R.string.projection_front_ceiling);
        String rear_ceiling = getString(R.string.projection_rear_ceiling);
        String[] modes = {front,rear,front_ceiling,rear_ceiling};
        String cur = viewModel.getProjectionModeItem().getContent();
        Log.d(TAG, "cycleProjectionMode: 当前的投影标题是 ："+ cur);
        int idx = 0;
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(cur)){
                idx = i;
                break;
            }
        }
        idx = next ? (idx + 1) % modes.length : (idx - 1 + modes.length) % modes.length;
        Log.d(TAG, "cycleProjectionMode: 处理后的idx是 ： " +idx);
        viewModel.switchProjectionMode(modes[idx]);
        SystemPropertiesUtils.setProperty(PATH_CONTROL_MIPI,String.valueOf(idx));
    }

    private void toggleAutoCorrect() {
        String cur = viewModel.getAutoCorrectItem().getContent();
        boolean cur_autoCorrect = getString(R.string.projection_auto_disable).equals(cur);
        viewModel.getAutoCorrectItem().setContent(getString(R.string.projection_auto_disable).equals(cur) ?
                getString(R.string.projection_auto_enable) : getString(R.string.projection_auto_disable));
        autoFocusUtils.setVerticalCorrectEnable(cur_autoCorrect); //如果显示的是关闭，就传参true
        //TODO:调用切换自动梯形矫正

        if(cur_autoCorrect){
            keystoneVM.restoreKeystone(); // 重置坐标
            keystoneVM.savePoint(4); // 保存所有点的坐标
            keystoneVM.applyZoom(100); // 应用默认缩放
            viewModel.getZoomItem().setContent(100 + "%");

            binding.keystoneInclude.itemRoot.setVisibility(View.INVISIBLE);
            binding.zoomInclude.itemRoot.setVisibility(View.INVISIBLE);
            binding.resetInclude.itemRoot.setVisibility(View.INVISIBLE);
        }else {
            binding.keystoneInclude.itemRoot.setVisibility(View.VISIBLE);
            binding.zoomInclude.itemRoot.setVisibility(View.VISIBLE);
            binding.resetInclude.itemRoot.setVisibility(View.VISIBLE);
        }
    }

    private void adjustZoom(boolean increase) {
        int current = Integer.parseInt(viewModel.getZoomItem().getContent().replace("%", ""));
        current = increase
                ? Math.min(current + STEP, MAX_ZOOM)
                : Math.max(current - STEP, MIN_ZOOM);
        viewModel.getZoomItem().setContent(current + "%");
    }

    private void openFourPointKeystone() {
        // TODO: 启动梯形校正界面
        Intent intent = new Intent(this, SinglePointActivity.class);
        startActivity(intent);
    }

    private void resetAll() {
        //TODO:调用全部设置为一个默认值
        viewModel.switchProjectionMode("正装背投");
        viewModel.getAutoCorrectItem().setContent("关闭");
        viewModel.getZoomItem().setContent("100%");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: -----------返回执行初始化");
        viewModel.initZoomFromDisk(getApplication());
    }
}