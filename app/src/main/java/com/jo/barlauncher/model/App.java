package com.jo.barlauncher.model;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class App implements Parcelable{
    public final String label;

    public final ComponentName componentName;

    public final Bitmap icon;

    public App(String label, ComponentName componentName, Bitmap icon) {
        this.label = label;
        this.componentName = componentName;
        this.icon = icon;
    }

    protected App(Parcel in) {
        label = in.readString();
        componentName = in.readParcelable(ComponentName.class.getClassLoader());
        icon = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<App> CREATOR = new Creator<App>() {
        @Override
        public App createFromParcel(Parcel in) {
            return new App(in);
        }

        @Override
        public App[] newArray(int size) {
            return new App[size];
        }
    };

    @Override
    public String toString() {
        return componentName.flattenToShortString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(label);
        out.writeParcelable(componentName, flags);
        out.writeParcelable(icon, flags);
    }
}
