package com.twd.settingsccx.repository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class BaseBindingVmFragment<D extends ViewDataBinding, VM extends BaseViewModel>
        extends BaseFragment {
    private static final String TAG = "BaseBindingVmFragment";
    protected D binding;
    protected VM viewModel;
    protected ActivityResultLauncher<Intent> launcher;

    private void createViewModel() {
        Log.d(TAG, " createViewModel  ......");
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if ((type instanceof ParameterizedType)) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                modelClass = BaseViewModel.class;
            }
            Log.d(TAG, " createViewModel  ......" + modelClass);
            viewModel = (VM) createViewModel(this, (Class) modelClass);
        }
        getLifecycle().addObserver((LifecycleObserver) viewModel);
    }

    public <T extends AndroidViewModel> T createViewModel(Fragment paramFragment, Class<T> paramClass) {
        //return (T) new ViewModelProvider(this).get(paramClass);
        return (T) new ViewModelProvider(this, AndroidViewModelFactory.getInstance(requireActivity().getApplication())).get(paramClass);
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        Log.d(TAG, " onCreate  ......");
        super.onCreate(paramBundle);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
            @Override
            public void onActivityResult(Object result) {
                onLaunchActivityResult((ActivityResult) result);
            }
        });
        createViewModel();
        Log.d(TAG, " onCreate  ......end");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView  ......");
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        Log.d(TAG, " onCreateView   ......end");
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver((LifecycleObserver) viewModel);
        launcher.unregister();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, " onDestroyView  ......");
        if (binding != null) {
            binding.unbind();
            binding = null;
        }
        super.onDestroyView();
    }

    protected abstract int getLayoutId();

    public void onLaunchActivityResult(ActivityResult paramActivityResult) {
    }
}
