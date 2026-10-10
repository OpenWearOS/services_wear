// OpenWearOS: placeholder so SystemServer can start it.
package org.openwearos.server;

import android.content.Context;

import com.android.server.SystemService;

public class WearDisplayService extends SystemService {
    public WearDisplayService(Context context) {
        super(context);
    }

    @Override
    public void onStart() {}
}
