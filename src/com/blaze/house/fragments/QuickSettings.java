/*
 * Copyright (C) 2016-2023 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blaze.house.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.blaze.ThemeUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.List;

@SearchIndexable
public class QuickSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    public static final String TAG = "QuickSettings";

    private static final String KEY_QS_PANEL_STYLE  = "qs_panel_style";

    private ListPreference mQsPanelStyle;

    private static ThemeUtils mThemeUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThemeUtils = new ThemeUtils(getActivity());

        final Context mContext = getActivity().getApplicationContext();
        final ContentResolver resolver = mContext.getContentResolver();

        String qsPanelStyle = Integer.toString(Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE , 0, UserHandle.USER_CURRENT));

        mQsPanelStyle = (ListPreference) findPreference(KEY_QS_PANEL_STYLE);
        int index = mQsPanelStyle.findIndexOfValue(qsPanelStyle);
        mQsPanelStyle.setValue(qsPanelStyle);
        mQsPanelStyle.setSummary(mQsPanelStyle.getEntries()[index]);
        mQsPanelStyle.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mQsPanelStyle) {
            int value = Integer.parseInt((String) newValue);
            int index = mQsPanelStyle.findIndexOfValue((String) newValue);
            mQsPanelStyle.setValue((String) newValue);
            mQsPanelStyle.setSummary(mQsPanelStyle.getEntries()[index]);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_PANEL_STYLE, value, UserHandle.USER_CURRENT);
            updateQsPanelStyle(getActivity());
            return true;
        }
        return false;
    }

    public static void reset(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();

        Settings.System.putIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE, 0, UserHandle.USER_CURRENT);
                
        updateQsPanelStyle(mContext);
    }

    private static void updateQsPanelStyle(Context context) {
        ContentResolver resolver = context.getContentResolver();

        int qsPanelStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE, 0, UserHandle.USER_CURRENT);

        String qsPanelStyleCategory = "android.theme.customization.qs_panel";
        String overlayThemeTarget  = "com.android.systemui";
        String overlayThemePackage  = "com.android.systemui";

        switch (qsPanelStyle) {
            case 1:
              overlayThemePackage = "com.android.system.qs.outline";
              break;
            case 2:
            case 3:
              overlayThemePackage = "com.android.system.qs.twotoneaccent";
              break;
            case 4:
              overlayThemePackage = "com.android.system.qs.shaded";
              break;
            case 5:
              overlayThemePackage = "com.android.system.qs.cyberpunk";
              break;
            case 6:
              overlayThemePackage = "com.android.system.qs.neumorph";
              break;
            case 7:
              overlayThemePackage = "com.android.system.qs.reflected";
              break;
            case 8:
              overlayThemePackage = "com.android.system.qs.surround";
              break;
            case 9:
              overlayThemePackage = "com.android.system.qs.thin";
              break;
            default:
              break;
        }

        if (mThemeUtils == null) {
            mThemeUtils = new ThemeUtils(context);
        }

        // reset all overlays before applying
        mThemeUtils.setOverlayEnabled(qsPanelStyleCategory, overlayThemeTarget, overlayThemeTarget);

        if (qsPanelStyle > 0) {
            mThemeUtils.setOverlayEnabled(qsPanelStyleCategory, overlayThemePackage, overlayThemeTarget);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.BLAZE_HOUSE;
    }

    /**
     * For search
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.qs_panel_settings);
}