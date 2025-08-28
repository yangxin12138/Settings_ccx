package com.twd.settingsccx.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.DefaultLifecycleObserver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel<T extends BaseModel>
        extends AndroidViewModel implements IDispose, DefaultLifecycleObserver {
    private CompositeDisposable mCompositeDisposable = null;
    protected T model;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        createModel();
    }

    private void createModel() {
        if (model == null) {
            try {
                Class modelClass;
                Type type = getClass().getGenericSuperclass();
                if (!(type instanceof ParameterizedType)) {
                    modelClass = BaseModel.class;
                }else {
                    modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                }
                model = (T) (modelClass).newInstance();
            } catch (InstantiationException instantiationException) {
                instantiationException.printStackTrace();
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    @Override
    public void addSubscribe(Disposable paramDisposable) {
        if (paramDisposable == null) {
            return;
        }
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(paramDisposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (model != null) {
            model.onCleared();
        }
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }
}
