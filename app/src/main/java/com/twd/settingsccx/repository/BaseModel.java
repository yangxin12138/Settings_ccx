package com.twd.settingsccx.repository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseModel implements IModel, IDispose {
    private CompositeDisposable mCompositeDisposable = null;

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
    public void onCleared() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }


}
