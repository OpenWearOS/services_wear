package org.openwearos.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Slog;

import java.util.Map;

/**
 * Wear config from the resource package org.openwearos.resources, looked up by name. If the package is missing
 * or lacks a name, the built-in default is used, so boot never depends on it.
 */
public final class WearConfig {
    public static final String RES_PACKAGE = "org.openwearos.resources";
    private static final String TAG = "WearConfig";

    private static final Map<String, Object> DEFAULTS = Map.ofEntries(
            Map.entry("config_automaticBatterySaverModeEntryPercentage", 10),
            Map.entry("config_automaticBatterySaverModeExitPercentage", 90),
            Map.entry("config_automaticBatterySaverModeSupported", false),
            Map.entry("config_defaultAlarmVibrationIntensity", -1),
            Map.entry("config_defaultNotificationVibrationIntensity", -1),
            Map.entry("config_defaultRingVibrationIntensity", -1),
            Map.entry("config_defaultRotaryEncoderWakeSetting", 1),
            Map.entry("config_disableAODWhilePlugged", false),
            Map.entry("config_disableAmbientInSchoolMode", true),
            Map.entry("config_disableAmbientInTheaterMode", true),
            Map.entry("config_disableImageMaskBurnInProtection", false),
            Map.entry("config_disableSystemBatterySaverForCustomization", false),
            Map.entry("config_disableTiltToWakeInSchoolMode", true),
            Map.entry("config_disableTouchWhenOffBody", false),
            Map.entry("config_displayOffloadApiPreference", 2),
            Map.entry("config_displayOffload_disableTtbWhenAodOff", true),
            Map.entry("config_displayOffload_disableTtbWhenLocked", true),
            Map.entry("config_enableQuickDozeInBatterySaverMode", false),
            Map.entry("config_enableQuickDozeInBedtimeMode", false),
            Map.entry("config_enableQuickDozeInSchoolMode", true),
            Map.entry("config_enableQuickDozeWhenOffBody", true),
            Map.entry("config_enableRemoteAnimationsForGlobalTransitions", true),
            Map.entry("config_enableWearBatterySaver", false),
            Map.entry("config_enableWearSchoolMode", false),
            Map.entry("config_enableWearTouchLockMode", true),
            Map.entry("config_longPressEnabledPowerMenu", true),
            Map.entry("config_onDisplayOffloadUpdateDozeIntervalMs", 0),
            Map.entry("config_rotaryEncoderGuardAfterWakeMillis", 2000),
            Map.entry("config_rotaryEncoderWakeEnabled", false),
            Map.entry("config_rotaryEncoderWakeEnabledDuringBedtimeMode", false),
            Map.entry("config_showEmergencyCallPowerMenu", true),
            Map.entry("config_sidekickBrightenAlpha", 80),
            Map.entry("config_sidekickDimmingAlpha", 80),
            Map.entry("config_systemTracePowerMenu", false),
            Map.entry("config_twmAlwaysOnScreen", false),
            Map.entry("config_twmAutoBrightness", false),
            Map.entry("config_twmBrightenOnWristTilt", true),
            Map.entry("config_twmBrightnessFixedBright", 102),
            Map.entry("config_twmBrightnessFixedDim", 1),
            Map.entry("config_twmDurationOfBrightState", 10000),
            Map.entry("config_useFixedTwmBrightness", false),
            Map.entry("config_userAbsentInteractivityTimeout", 2400000),
            Map.entry("config_wearEsimDevice", false),
            Map.entry("config_writeTouchLockStateToSysProp", false));

    private final Context mContext;
    private volatile Resources mRes;
    private boolean mWarned;

    public WearConfig(Context context) {
        mContext = context;
    }

    public boolean bool(String name) {
        Resources r = res();
        if (r != null) {
            int id = r.getIdentifier(name, "bool", RES_PACKAGE);
            if (id != 0) return r.getBoolean(id);
        }
        return (Boolean) fallback(name);
    }

    public int integer(String name) {
        Resources r = res();
        if (r != null) {
            int id = r.getIdentifier(name, "integer", RES_PACKAGE);
            if (id != 0) return r.getInteger(id);
        }
        return (Integer) fallback(name);
    }

    /** "package" or "built-in defaults", for logs. */
    public String source() {
        return res() != null ? "package " + RES_PACKAGE : "built-in defaults";
    }

    private static Object fallback(String name) {
        Object v = DEFAULTS.get(name);
        if (v == null) throw new IllegalArgumentException("unknown Wear config " + name);
        return v;
    }

    private Resources res() {
        Resources r = mRes;
        if (r != null) return r;
        try {
            r = mContext.getPackageManager().getResourcesForApplication(RES_PACKAGE);
            mRes = r;
        } catch (PackageManager.NameNotFoundException e) {
            if (!mWarned) {
                mWarned = true;
                Slog.w(TAG, RES_PACKAGE + " not installed yet; using built-in defaults");
            }
        }
        return r;
    }
}
