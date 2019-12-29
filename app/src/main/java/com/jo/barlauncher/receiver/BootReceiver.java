package com.jo.barlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jo.barlauncher.util.NotificationHelper;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper.getInstance(context).toggleNotification(false);
    }
}
