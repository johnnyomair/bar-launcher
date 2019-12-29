package com.jo.barlauncher.legacy;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;


import com.jo.barlauncher.model.App;
import com.jo.barlauncher.model.DataManager;
import com.jo.barlauncher.ui.RowListFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by JohnnyO on 23/08/16.
 */
public class DatabaseMigration {
    private static final String[] APP_COLUMNS = {SQLiteHelper.APP_ACTIVITY_NAME,
            SQLiteHelper.APP_PACKAGE_NAME};

    private static final String[] ROW_COLUMNS = {SQLiteHelper.ROW_ID};

    public static void migrate(Context context) {
        SQLiteHelper helper = new SQLiteHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();

        int version = database.getVersion();

        if (version != 3) {
            Log.e("DatabaseMigration", "Incompatible database version");
            return;
        }

        DataManager dataManager = new DataManager(context);

        List<Long> rows = getRowList(database);
        dataManager.saveRowList(rows);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putLong(RowListFragment.KEY_CURRENT_ROW_ID, Collections.max(rows) + 1).apply();

        for (Long row : rows) {
            List<App> apps = getAppList(database, row);
            dataManager.saveAppList(row, apps);
        }

        database.close();
    }

    private static List<Long> getRowList(SQLiteDatabase database) {
        // I assume that the average user has 4 rows.
        List<Long> rows = new ArrayList<>(4);

        Cursor cursor = database.query(SQLiteHelper.TABLE_ROWS, ROW_COLUMNS, null, null, null, null, SQLiteHelper.ROW_POSITION);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            rows.add(cursor.getLong(0));
            cursor.moveToNext();
        }

        cursor.close();

        return rows;
    }

    private static List<App> getAppList(SQLiteDatabase database, long rowId) {
        String selection = SQLiteHelper.APP_ROW_ID + " = " + rowId;

        Cursor cursor = database.query(SQLiteHelper.TABLE_APPS, APP_COLUMNS, selection, null, null, null, SQLiteHelper.APP_POSITION);
        cursor.moveToFirst();

        // There should be about 10 apps per list.
        List<App> apps = new ArrayList<>(10);

        while (!cursor.isAfterLast()) {
            String activityName = cursor.getString(0);
            String packageName = cursor.getString(1);

            ComponentName componentName = new ComponentName(packageName, activityName);
            apps.add(new App(null, componentName, null));

            cursor.moveToNext();
        }

        cursor.close();

        return apps;
    }
}