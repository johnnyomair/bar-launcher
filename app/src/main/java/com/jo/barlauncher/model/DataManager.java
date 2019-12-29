package com.jo.barlauncher.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;


import com.jo.barlauncher.util.IconCacheHelper;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String KEY_ROW_LIST = "row_list";

    private static final String DELIMITER = ",";

    private final Context context;

    private final PackageManager packageManager;

    private final SharedPreferences preferences;

    public DataManager(Context context) {
        this.context = context;
        packageManager = context.getPackageManager();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public List<Long> loadRowList() {
        List<Long> rows = new ArrayList<>(4);

        String rowsString = preferences.getString(KEY_ROW_LIST, "");

        if (!TextUtils.isEmpty(rowsString)) {
            String[] rowsArray = rowsString.split(DELIMITER);

            for (String row : rowsArray) {
                rows.add(Long.valueOf(row));
            }
        }

        return rows;
    }

    public void saveRowList(List<Long> rows) {
        preferences.edit().putString(DataManager.KEY_ROW_LIST, TextUtils.join(DELIMITER, rows)).apply();
    }

    public List<App> loadAppList(long row) {
        List<App> apps = new ArrayList<>(10);
        String appsString = preferences.getString(String.valueOf(row), "");

        if (!TextUtils.isEmpty(appsString)) {
            boolean dataSetDirty = false;
            IconCacheHelper helper = IconCacheHelper.getInstance(context);

            String[] appListArray = appsString.split(DELIMITER);

            for (String appListArrayItem : appListArray) {
                ComponentName componentName = ComponentName.unflattenFromString(appListArrayItem);

                try {
                    ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, 0);
                    String label = activityInfo.loadLabel(packageManager).toString();
                    Bitmap icon = helper.loadIcon(activityInfo);

                    apps.add(new App(label, componentName, icon));
                } catch (PackageManager.NameNotFoundException e) {
                    dataSetDirty = true;
                }
            }

            if (dataSetDirty) {
                saveAppList(row, apps);
            }
        }

        return apps;
    }

    public void saveAppList(long row, List<App> apps) {
        preferences.edit().putString(String.valueOf(row), TextUtils.join(DELIMITER, apps)).apply();
    }
}