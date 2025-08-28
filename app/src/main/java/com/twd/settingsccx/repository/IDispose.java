package com.twd.settingsccx.repository;

import io.reactivex.disposables.Disposable;

public abstract interface IDispose {
    public abstract void addSubscribe(Disposable paramDisposable);
}
