package pl.pw.mini.mininavigator.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.pw.mini.mininavigator.R;
import pl.pw.mini.mininavigator.views.wifiData.DoubleTextAdapter;

public class WifiDataActivity extends Activity {

    private DoubleTextAdapter mAdapter;
    private List<Pair<String,String>> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_data);

        Bundle extras = getIntent().getExtras();
        int[] levels = extras.getIntArray("levels");
        String[] macs = extras.getStringArray("macs");


        for (int i = 0; i < levels.length; i++) {
            Pair<String, String> pair = new Pair<>(
                    "BSSID: " + macs[i],
                    "RSSI: " + levels[i] + " dBm");
            mData.add(pair);
        }

        mAdapter = new DoubleTextAdapter(this, mData);
        ListView listView = (ListView) findViewById(R.id.wifiDataListView);
        listView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
