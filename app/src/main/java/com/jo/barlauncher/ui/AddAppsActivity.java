package com.jo.barlauncher.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.jo.barlauncher.R;
import com.jo.barlauncher.model.App;
import com.jo.barlauncher.util.IconCacheHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddAppsActivity extends AppCompatActivity {
    private List<App> apps;
    private AppsAdapter appsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_add_apps);

        apps = new ArrayList<>(100);
        appsAdapter = new AppsAdapter(apps, false, true);

        RecyclerView recyclerView = (RecyclerView) findViewById(android.R.id.list);
        recyclerView.setAdapter(appsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        new LoadInstalledAppsTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return false;
        }
    }

    private class LoadInstalledAppsTask extends AsyncTask<Void, Void, List<App>> {

        @Override
        protected List<App> doInBackground(Void... params) {
            ArrayList<App> apps = new ArrayList<>();

            IconCacheHelper helper = IconCacheHelper.getInstance(AddAppsActivity.this);

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            PackageManager packageManager = getPackageManager();
            ActivityInfo activityInfo;
            ComponentName componentName;
            String label;
            Bitmap icon;

            for (ResolveInfo resolveInfo : packageManager.queryIntentActivities(intent, 0)) {
                activityInfo = resolveInfo.activityInfo;
                componentName = new ComponentName(activityInfo.packageName, activityInfo.name);

                try {
                    label = activityInfo.loadLabel(packageManager).toString();
                    icon = helper.loadIcon(activityInfo);

                    apps.add(new App(label, componentName, icon));
                } catch (PackageManager.NameNotFoundException e) {
                    // Couldn't load icon, skip app
                }
            }

            Collections.sort(apps, new Comparator<App>() {
                @Override
                public int compare(App a, App b) {
                    return a.label.compareToIgnoreCase(b.label);
                }
            });

            return apps;
        }

        @Override
        protected void onPostExecute(List<App> result) {
            apps.clear();
            apps.addAll(result);
            appsAdapter.notifyDataSetChanged();
        }
    }

    private static class SelectableApp extends App implements Parcelable {
        private boolean selected;

        public SelectableApp(String label, ComponentName componentName, Bitmap icon) {
            super(label, componentName, icon);
        }

        protected SelectableApp(Parcel in) {
            super(in);
            selected = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeByte((byte) (selected ? 1 : 0));
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<SelectableApp> CREATOR = new Creator<SelectableApp>() {
            @Override
            public SelectableApp createFromParcel(Parcel in) {
                return new SelectableApp(in);
            }

            @Override
            public SelectableApp[] newArray(int size) {
                return new SelectableApp[size];
            }
        };

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}