package com.lyl.bluetoothutils.application;

import android.app.Application;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ApplicationUtils {
    static Application sApplication;

    public static Application getApp() {
        if (sApplication != null) return sApplication;
        if (sApplication == null) {
            sApplication = getApplicationByReflect();
        }
        if (sApplication == null) throw new NullPointerException("Application reflect NUll");
        return sApplication;
    }

    public static void initApp(Application application) {
        sApplication = application;
    }

    private static Application getApplicationByReflect() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object thread = getActivityThread();
            Object app = activityThreadClass.getMethod("getApplication").invoke(thread);
            if (app == null) {
                return null;
            }
            return (Application) app;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getActivityThreadInActivityThreadStaticField() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            return sCurrentActivityThreadField.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getActivityThread() {
        Object activityThread = getActivityThreadInActivityThreadStaticField();
        if (activityThread != null) return activityThread;
        activityThread = getActivityThreadInActivityThreadStaticMethod();
        if (activityThread != null) return activityThread;
        return getActivityThreadInLoadedApkField();
    }

    private static Object getActivityThreadInLoadedApkField() {
        try {
            Field mLoadedApkField = Application.class.getDeclaredField("mLoadedApk");
            mLoadedApkField.setAccessible(true);
            Object mLoadedApk = mLoadedApkField.get(getApplicationByReflect());
            Field mActivityThreadField = mLoadedApk.getClass().getDeclaredField("mActivityThread");
            mActivityThreadField.setAccessible(true);
            return mActivityThreadField.get(mLoadedApk);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object getActivityThreadInActivityThreadStaticMethod() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            return activityThreadClass.getMethod("currentActivityThread").invoke(null);
        } catch (Exception e) {
            return null;
        }
    }
}
