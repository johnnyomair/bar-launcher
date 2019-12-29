package com.jo.barlauncher.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.jo.barlauncher.R;
import com.jo.barlauncher.Settings;
import com.jo.barlauncher.util.NotificationHelper;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();

        addPreferencesFromResource(R.xml.settings);

        PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Preference hintPreference = findPreference(Settings.HINT);
            preferenceScreen.removePreference(hintPreference);
        }

        final ListPreference priorityPreference = (ListPreference) findPreference(Settings.PRIORITY);
        priorityPreference.setSummary(priorityPreference.getEntry());
        priorityPreference.setOnPreferenceChangeListener(new Preference
                .OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                priorityPreference.setSummary(priorityPreference.getEntries()[priorityPreference.findIndexOfValue((String) newValue)]);

                return true;
            }
        });

        final ListPreference iconSizePreference = (ListPreference) findPreference(Settings.ICON_SIZE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            preferenceScreen.removePreference(iconSizePreference);
        } else {
            iconSizePreference.setSummary(iconSizePreference.getEntry());
            iconSizePreference.setOnPreferenceChangeListener(new Preference
                    .OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    iconSizePreference.setSummary(iconSizePreference.getEntries()[iconSizePreference.findIndexOfValue((String) newValue)]);

                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        NotificationHelper.getInstance(mContext).toggleNotification(true);
    }
}