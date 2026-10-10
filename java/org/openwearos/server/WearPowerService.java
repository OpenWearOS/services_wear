package org.openwearos.server;

import android.content.Context;
import android.util.Slog;

import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.policy.WearPowerServiceInputInterceptorInternal;

/**
 * Wear power policy. Pass-through for now: it only publishes the input interceptor that
 * WearWindowManagerPolicy calls. Ambient config, power tracking and the touch/rotary
 * mediators come in later stages.
 */
public class WearPowerService extends SystemService {
    private static final String TAG = "WearPower";

    public WearPowerService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {
        LocalServices.addService(WearPowerServiceInputInterceptorInternal.class, new Interceptor());
        Slog.i(TAG, "started");
    }

    private static final class Interceptor extends WearPowerServiceInputInterceptorInternal {
        @Override
        public boolean interceptInteractiveMotion(int deviceId, int source, long eventTimeNanos) {
            return false;
        }

        @Override
        public boolean interceptWakeAttempt(int deviceId, int source, long eventTimeNanos,
                boolean sleepingOrGoingToSleep) {
            return false;
        }

        @Override
        public void onKeyEvent(int keyCode, boolean down) {}

        @Override
        public void onWakeFromMotion(int deviceId, int source, long eventTimeNanos,
                int displayStateBeforeWake) {}
    }
}
