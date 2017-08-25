package pl.pw.mini.mininavigator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.LinkedList;
import java.util.List;

import pl.pw.mini.mininavigator.data.WirelessSignal;

public class WifiDataProvider {

    private static WifiDataProvider sInstance;

    public static WifiDataProvider get() {
        if (sInstance == null) sInstance = getSync();
        return sInstance;
    }

    private static synchronized WifiDataProvider getSync() {
        if (sInstance == null) sInstance = new WifiDataProvider();
        return sInstance;
    }

    private WifiManager mWifiManager;
    private List<ScanResult> mScanResults;
    private boolean mIsScanning = false;

    private OnWifiDataReceptionListener mListener;

    private WifiDataProvider() {
        mWifiManager = (WifiManager) App.get().getSystemService(Context.WIFI_SERVICE);

        App.get().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mScanResults = mWifiManager.getScanResults();

                List<WirelessSignal> results = new LinkedList<WirelessSignal>();
                for (ScanResult sr : mScanResults) {
                    results.add(new WirelessSignal(sr.BSSID, sr.level));
                }

                if (mListener != null) {
                    mListener.onWifiDataReception(results);
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public void setWifiEnabled(boolean enable) {
        mWifiManager.setWifiEnabled(enable);
    }

    public void startScanning() throws IllegalStateException {
        if (mIsScanning) return;

        if (!isWifiEnabled()) {
            throw new IllegalStateException("Wifi is not enabled.");
        }
        mWifiManager.startScan();
        mIsScanning = true;
    }

    public void setWifiDataReceptionListener(OnWifiDataReceptionListener l) {
        mListener = l;
    }

}
