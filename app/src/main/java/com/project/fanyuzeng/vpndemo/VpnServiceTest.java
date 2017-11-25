package com.project.fanyuzeng.vpndemo;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.project.fanyuzeng.vpndemo.utils.CloseableUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: fanyuzeng on 2017/11/15 16:04
 * @desc:
 */
public class VpnServiceTest extends VpnService {
    private static final String TAG = "VpnServiceTest";
    public static final String VPN_RUNNING_FILTER = "com.project.fanyuzeng.vpndemo.VpnServiceTest";
    public static final String VPN_RUNNING_EXTRA_NAME = "isRunning";
    /**
     * 10.0.0.2表示模拟器或者手机本机地址
     */
    public static final String VPN_ADDRESS = "10.0.0.2";
    /**
     * 路由地址0.0.0.0表示拦截所有从设备上发送出去的IP数据报
     */
    public static final String VPN_ROUTE = "0.0.0.0";
    private static boolean isRunning = false;
    private ParcelFileDescriptor mInterface;
    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, ">> onCreate >> ");
        isRunning = true;
        Intent intent = new Intent(VPN_RUNNING_FILTER);
        intent.putExtra(VPN_RUNNING_EXTRA_NAME, isRunning);
        sendBroadcast(intent);
        setupVpn();
        mExecutorService = Executors.newFixedThreadPool(5);

        mExecutorService.execute(new VpnRunnable(mInterface.getFileDescriptor()));

    }

    /**
     * 配置VPN
     */
    private void setupVpn() {
        if (mInterface == null) {
            mInterface = new Builder()
                    .addAddress(VPN_ADDRESS, 32)
                    .addRoute(VPN_ROUTE, 0)
                    .setSession("VpnDemo")
                    .establish();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        CloseableUtil.closeResource(mInterface);
    }

    public static boolean isRunning() {
        return isRunning;
    }
}
