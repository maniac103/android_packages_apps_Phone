package com.android.phone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class PhoneSettings {
    private PhoneSettings() {
    }

    public static boolean vibrateOnOutgoingCall(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_VIBRATE_OUTGOING, true);
    }
    public static boolean vibrateEvery45Seconds(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_VIBRATE_45, false);
    }
    public static boolean vibrateOnHangup(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_VIBRATE_HANGUP, true);
    }
    public static boolean vibrateOnCallWaiting(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_VIBRATE_CALL_WAITING, false);
    }

    public static boolean keepScreenAwake(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_SCREEN_AWAKE, false);
    }
    public static boolean alwaysUseProximitySensor(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_ALWAYS_PROXIMITY, false);
    }
    public static boolean showCallLogAfterCall(Context context) {
        return !getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_RETURN_HOME, true);
    }
    public static boolean ledNotificationEnabled(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_LED_NOTIFY, true);
    }
    public static boolean showOrganization(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_SHOW_ORGAN, false);
    }
    public static boolean useLeftHandedLayout(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_LEFT_HAND, false);
    }
    public static boolean forceTouchUi(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_FORCE_TOUCH, false);
    }
    public static boolean allowInCallRotation(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.ROTATE_INCALL_SCREEN, false);
    }
    public static boolean handleCallInBackground(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BG_INCALL_SCREEN, false);
    }
    public static boolean hideHoldButton(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_HIDE_HOLD_BUTTON, false);
    }

    public static boolean blacklistEnabled(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_ENABLE_BLACKLIST, false);
    }
    public static boolean blacklistRegexEnabled(Context context) {
        return getPrefs(context).getBoolean(CallFeaturesSetting.BUTTON_BLACK_REGEX, false);
    }

    public static int trackballAnswerDelay(Context context) {
        return parseTrackballValue(getPrefs(context).getString(CallFeaturesSetting.BUTTON_TRACKBALL_ANSWER, null));
    }
    public static int trackballHangupDelay(Context context) {
        return parseTrackballValue(getPrefs(context).getString(CallFeaturesSetting.BUTTON_TRACKBALL_HANGUP, null));
    }

    public static String getVoiceQualityParameter(Context context) {
        String param = context.getResources().getString(R.string.voice_quality_param);
        if (TextUtils.isEmpty(param)) {
            return null;
        }

        String value = getVoiceQualityValue(context);
        if (value == null) {
            return null;
        }

        return param + "=" + value;
    }

    public static String getVoiceQualityValue(Context context) {
        String value = getPrefs(context).getString(CallFeaturesSetting.BUTTON_VOICE_QUALITY_KEY, null);
        if (value != null) {
            return value;
        }

        /* use first value of entry list */
        String[] values = context.getResources().getStringArray(R.array.voice_quality_values);
        if (values.length > 0) {
            return values[0];
        }

        return null;
    }

    private static int parseTrackballValue(String value) {
        if (TextUtils.equals(value, "dt")) {
            return 0;
        } else if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
            }
        }
        return -1;
    }

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
