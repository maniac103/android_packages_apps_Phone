/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.phone;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.android.internal.telephony.Phone;

public class GsmUmtsCallOptions extends PreferenceActivity {
    private static final String LOG_TAG = "GsmUmtsCallOptions";
    private final boolean DBG = (PhoneApp.DBG_LEVEL >= 2);

    public static final String SHOW_SSN_KEY = "button_show_ssn_key";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.gsm_umts_call_options);

        if (PhoneApp.getPhone().getPhoneType() != Phone.PHONE_TYPE_GSM) {
            //disable the entire screen
            getPreferenceScreen().setEnabled(false);
        }
    }
}
