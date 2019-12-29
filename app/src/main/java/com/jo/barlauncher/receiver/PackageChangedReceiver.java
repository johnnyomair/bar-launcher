package com.jo.barlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jo.barlauncher.util.NotificationHelper;

public class PackageChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getData().getSchemeSpecificPart();

        if (packageName == null || packageName.length() == 0) {
            return;
        }

        NotificationHelper.getInstance(context).toggleNotification(false);
    }
}
