/*
 * Copyright 2013 Johannes Obermair
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jo.barlauncher.legacy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "apps.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_APPS = "apps";
    public static final String TABLE_ROWS = "rows";

    public static final String ROW_ID = "id";
    public static final String ROW_NAME = "name";
    public static final String ROW_POSITION = "position";

    public static final String APP_ID = "id";
    public static final String APP_NAME = "name";
    public static final String APP_ACTIVITY_NAME = "activity_name";
    public static final String APP_PACKAGE_NAME = "package_name";
    public static final String APP_POSITION = "position";
    public static final String APP_ROW_ID = "row_id";

    private static final String DATABASE_CREATE_TABLE_ROWS = "create table " + TABLE_ROWS +
            "(" + ROW_ID + " integer primary key autoincrement, " + ROW_NAME + " text, " +
            ROW_POSITION + " integer );";

    private static final String DATABASE_CREATE_TABLE_APPS = "create table " + TABLE_APPS +
            "(" + APP_ID + " integer primary key autoincrement, " + APP_NAME + " text, " +
            APP_ACTIVITY_NAME + " text, " + APP_PACKAGE_NAME + " text, " +
            APP_POSITION + " integer, " + APP_ROW_ID + " integer default 1);";

    private static final String DATABASE_UPDATE_TABLE_APPS = "alter table " + TABLE_APPS +
            " add " + APP_ROW_ID + " integer default 1;";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_ROWS);
        database.execSQL(DATABASE_CREATE_TABLE_APPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion == 2 && newVersion == 3) {
            database.execSQL(DATABASE_CREATE_TABLE_ROWS);
            database.execSQL(DATABASE_UPDATE_TABLE_APPS);
        } else {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_ROWS);

            onCreate(database);
        }
    }
}