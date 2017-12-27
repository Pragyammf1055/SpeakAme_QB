package com.speakame.QuickBlox;

import android.os.Bundle;

import com.quickblox.core.QBEntityCallback;

/**
 * Created by Peter on 13-Oct-17.
 */

public class QbEntityCallbackWrapper<T> extends QbEntityCallbackTwoTypeWrapper<T, T> {
    public QbEntityCallbackWrapper(QBEntityCallback<T> callback) {
        super(callback);
    }

    @Override
    public void onSuccess(T t, Bundle bundle) {
        onSuccessInMainThread(t, bundle);
    }
}
