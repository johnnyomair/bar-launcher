package com.jo.barlauncher.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.HashMap;

public class IconCacheHelper {
    private static IconCacheHelper sInstance;

    private final HashMap<ActivityInfo, Bitmap> mCache;

    private final PackageManager mPackageManager;

    private final Resources.Theme mTheme;

    private IconCacheHelper(Context context) {
        mCache = new HashMap<>();
        mPackageManager = context.getPackageManager();
        mTheme = context.getApplicationContext().getTheme();
    }

    public static IconCacheHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new IconCacheHelper(context.getApplicationContext());
        }

        return sInstance;
    }

    public Bitmap loadIcon(ActivityInfo activityInfo) throws PackageManager.NameNotFoundException {
        Bitmap bitmap = mCache.get(activityInfo);

        if (bitmap == null) {
            try {
                int iconId = activityInfo.getIconResource();
                Resources resources = mPackageManager.getResourcesForApplication(activityInfo.applicationInfo);
                bitmap = getBitmapForIconId(resources, iconId);

                mCache.put(activityInfo, bitmap);
            } catch (Resources.NotFoundException e) {
                throw new PackageManager.NameNotFoundException();
            }

        }

        return bitmap;
    }

    private Bitmap getBitmapForIconId(Resources resources, int iconId) throws Resources.NotFoundException {
        Drawable drawable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            drawable = resources.getDrawable(iconId, mTheme);
        } else {
            //noinspection deprecation
            drawable = resources.getDrawable(iconId);
        }

        return drawableToBitmap(drawable);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
