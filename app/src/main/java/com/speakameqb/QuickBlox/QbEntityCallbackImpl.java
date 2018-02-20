package com.speakameqb.QuickBlox;

import android.os.Bundle;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

/**
 * Created by Peter on 16-Oct-17.
 */

public class QbEntityCallbackImpl<T> implements QBEntityCallback<T> {

    public QbEntityCallbackImpl() {
    }

    @Override
    public void onSuccess(T result, Bundle bundle) {

    }

    @Override
    public void onError(QBResponseException e) {

    }
}
