package pl.pw.mini.mininavigator;

import java.util.List;

import pl.pw.mini.mininavigator.data.WirelessSignal;

public interface OnWifiDataReceptionListener {
    void onWifiDataReception(List<WirelessSignal> data);
}
