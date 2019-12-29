package com.jo.barlauncher.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.jo.barlauncher.MainActivity;
import com.jo.barlauncher.R;
import com.jo.barlauncher.Settings;
import com.jo.barlauncher.model.App;
import com.jo.barlauncher.model.DataManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationHelper {
    private static final int NOTIFICATION_ID = 6120;

    private static NotificationHelper sInstance;

    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final SharedPreferences mSharedPreferences;
    private final DataManager mDataManager;

    private NotificationHelper(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mDataManager = new DataManager(context);
    }

    public static NotificationHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NotificationHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    public void toggleNotification(boolean cancellingRequired) {
        if (mSharedPreferences.getBoolean(Settings.BAR_LAUNCHER_ENABLED, false)) {
            if (cancellingRequired) {
                mNotificationManager.cancel(NOTIFICATION_ID);
            }

            mNotificationManager.notify(NOTIFICATION_ID, createNotification());
        } else {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setOngoing(true);
        builder.setSmallIcon(R.drawable.ic_logo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            //noinspection deprecation
            builder.setColor(mContext.getResources().getColor(R.color.colorAccent));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setColor(mContext.getResources().getColor(R.color.colorAccent, mContext.getTheme()));
        }

        // noinspection ResourceType
        builder.setPriority(Integer.parseInt(mSharedPreferences.getString(Settings.PRIORITY, Settings.DEFAULT_PRIORITY)));

        List<Long> rows = mDataManager.loadRowList();

        boolean allRowsEmpty = true;

        for (Long row : rows) {
            if (!TextUtils.isEmpty(mSharedPreferences.getString(String.valueOf(row), ""))) {
                allRowsEmpty = false;
            }
        }

        if (allRowsEmpty) {
            builder.setContentTitle(mContext.getString(R.string.title_notification_empty));
            builder.setContentText(mContext.getString(R.string.text_notification_empty));

            Intent intent = new Intent(mContext, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

            builder.setContentIntent(pendingIntent);
            builder.setWhen(0);

            return builder.build();
        } else {
            int padding = Integer.valueOf(mSharedPreferences.getString(Settings.ICON_SIZE, Settings.DEFAULT_ICON_SIZE));
            int paddingInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, mContext.getResources().getDisplayMetrics());

            RemoteViews notificationRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.layout_notification);
            notificationRemoteViews.removeAllViews(R.id.layout_notification);

            for (Long row : rows) {
                List<App> apps = mDataManager.loadAppList(row);

                if (!apps.isEmpty()) {
                    RemoteViews notificationRowRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.layout_notification_row);

                    for (App app : apps) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setComponent(app.componentName);

                        RemoteViews buttonAppRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.button_app);
                        buttonAppRemoteViews.setOnClickPendingIntent(R.id.button_app, PendingIntent.getActivity(mContext, 0, intent, 0));
                        buttonAppRemoteViews.setImageViewBitmap(R.id.button_app, app.icon);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            buttonAppRemoteViews.setViewPadding(R.id.button_app, paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);
                        }

                        notificationRowRemoteViews.addView(R.id.layout_notification_row, buttonAppRemoteViews);
                    }

                    notificationRemoteViews.addView(R.id.layout_notification, notificationRowRemoteViews);
                }
            }

            Notification notification = builder.build();
            notification.contentView = notificationRemoteViews;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification.bigContentView = notificationRemoteViews;
            }

            return notification;
        }
    }
}
