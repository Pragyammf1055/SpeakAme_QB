package com.speakameqb.Xmpp;

import android.os.Binder;

import java.lang.ref.WeakReference;

/**
 * Created by MAX on 21-Sep-16.
 */
public class LocalBinder<S> extends Binder {
    private final WeakReference<S> mService;

    public LocalBinder(final S service) {
        mService = new WeakReference<S>(service);
    }

    public S getService() {
        return mService.get();
    }
}
