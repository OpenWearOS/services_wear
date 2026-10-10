package org.openwearos.server;

import android.content.Context;
import android.util.Slog;

import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.policy.WearModeServiceInternal;

/**
 * Wear mode manager. Pass-through for now: publishes WearModeServiceInternal
 * and
 * intercepts no button. The state engine (modes/ModeEngine) is wired in a later
 * stage.
 */
public class ModeManagerService extends SystemService {
	private static final String TAG = "WearModes";

	public ModeManagerService(Context context) {
		super(context);
	}

	@Override
	public void onStart() {
		LocalServices.addService(WearModeServiceInternal.class, new WearModeServiceInternal() {
			@Override
			public boolean onButtonDown(int keyCode) {
				return false;
			}

			@Override
			public boolean onButtonUp(int keyCode) {
				return false;
			}

			@Override
			public void onKeyguardOccludedChanged(boolean occluded) {
			}

			@Override
			public boolean shouldInterceptButton(int keyCode) {
				return false;
			}

			@Override
			public boolean shouldInterceptButtonPressBehavior(int keyCode) {
				return false;
			}
		});
		Slog.i(TAG, "started");
	}

	@Override
	public void onBootPhase(int phase) {
		if (phase == PHASE_BOOT_COMPLETED) { // OpenWearOS
			WearConfig config = new WearConfig(getContext());
			Slog.i(TAG, "config from " + config.source() + ", ambient off in theater mode: "
					+ config.bool("config_disableAmbientInTheaterMode"));
		}
	}
}
