package com.geetion.buglytest;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

import java.util.Locale;

public class MyApplication extends Application {
    private final String TAG = MyApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        setStrictMode();
        // 设置是否开启热更新能力，默认为true
        Beta.enableHotfix = true;
        // 设置是否自动下载补丁
        Beta.canAutoDownloadPatch = true;
        // 设置是否提示用户重启
        Beta.canNotifyUserRestart = true;
        // 设置是否自动合成补丁
        Beta.canAutoPatch = true;
        /**
         *  全量升级状态回调
         */
        Beta.upgradeStateListener = new UpgradeStateListener() {
            @Override
            public void onUpgradeFailed(boolean b) {
            }

            @Override
            public void onUpgradeSuccess(boolean b) {
            }

            @Override
            public void onUpgradeNoVersion(boolean b) {
                Log.d(TAG, "最新版本");
            }

            @Override
            public void onUpgrading(boolean b) {
                Log.d(TAG, "onUpgrading");
            }

            @Override
            public void onDownloadCompleted(boolean b) {

            }
        };

        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {
                Log.d(TAG, "patchFileUrl");
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Log.d(TAG, String.format(Locale.getDefault(),
                        "%s %d%%",
                        Beta.strNotificationDownloading,
                        (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)));
            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {
                Log.d(TAG, patchFilePath);
            }

            @Override
            public void onDownloadFailure(String msg) {
                Log.e(TAG, msg);
            }

            @Override
            public void onApplySuccess(String msg) {
                Log.d(TAG, msg);

            }

            @Override
            public void onApplyFailure(String msg) {
                Log.e(TAG, msg);
            }

            @Override
            public void onPatchRollback() {
                Log.d(TAG, "onPatchRollback");
            }
        };

        long start = System.currentTimeMillis();
        Bugly.setUserId(this, "falue");
        Bugly.setUserTag(this, 123456);
        Bugly.putUserData(this, "key1", "123");
        Bugly.setAppChannel(this, "bugly");
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(this, "7f51acd725", true);
        long end = System.currentTimeMillis();
        Log.e("init time--->", end - start + "ms");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        Beta.installTinker();
    }

    @TargetApi(9)
    protected void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
    }
}