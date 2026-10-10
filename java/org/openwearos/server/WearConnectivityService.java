// OpenWearOS: placeholder so SystemServer can start it.
package org.openwearos.server;

import android.content.Context;

import com.android.server.SystemService;

public class WearConnectivityService extends SystemService {
    public WearConnectivityService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {}
}
