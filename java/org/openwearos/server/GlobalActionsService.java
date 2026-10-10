// OpenWearOS: placeholder so SystemServer can start it.
package org.openwearos.server;

import android.content.Context;

import com.android.server.SystemService;

public class GlobalActionsService extends SystemService {
    public GlobalActionsService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {}
}
