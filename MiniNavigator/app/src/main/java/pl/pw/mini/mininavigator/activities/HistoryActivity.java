package pl.pw.mini.mininavigator.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pl.pw.mini.mininavigator.GisServiceClient;
import pl.pw.mini.mininavigator.R;
import pl.pw.mini.mininavigator.data.LocationDate;
import pl.pw.mini.mininavigator.utilities.Callback;
import pl.pw.mini.mininavigator.views.wifiData.DoubleTextAdapter;

public class HistoryActivity extends Activity{

    private String mId;

    private DoubleTextAdapter mAdapter;

    private List<LocationDate> mHistory;
    private List<Pair<String,String>> mDataToDisplay = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle extras = getIntent().getExtras();
        mId = extras.getString("id", "-");

        ListView historyListView = (ListView)findViewById(R.id.historyListView);

        requestHistory();
        requestMap();

        mAdapter = new DoubleTextAdapter(this, mDataToDisplay);
        historyListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        requestMap();
    }

    private void requestHistory() {
        GisServiceClient.get().getHistory(mId, new Callback<List<LocationDate>>() {
            @Override
            public void callback(List<LocationDate> param) {
                for(LocationDate ld : param){
                    Pair<String,String> pair = new Pair<>(ld.Loc.toString(), ld.Dat.toString());
                    mDataToDisplay.add(pair);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void requestMap() {
        final ImageView historyImageView = (ImageView) findViewById(R.id.historyMapImageView);

        GisServiceClient.get().getHistoryMap(mId, new Callback<Bitmap>() {
            @Override
            public void callback(Bitmap param) {
                historyImageView.setImageBitmap(param);
            }
        });
    }
}
