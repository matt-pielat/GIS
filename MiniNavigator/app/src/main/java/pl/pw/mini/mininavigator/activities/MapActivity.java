package pl.pw.mini.mininavigator.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.pw.mini.mininavigator.GisServiceClient;
import pl.pw.mini.mininavigator.OnWifiDataReceptionListener;
import pl.pw.mini.mininavigator.R;
import pl.pw.mini.mininavigator.WifiDataProvider;
import pl.pw.mini.mininavigator.data.LocationDate;
import pl.pw.mini.mininavigator.data.WirelessSignal;
import pl.pw.mini.mininavigator.utilities.Callback;
import pl.pw.mini.mininavigator.views.menu.MenuEntry;
import pl.pw.mini.mininavigator.views.menu.MenuEntryAdapter;

public class MapActivity extends Activity
        implements OnWifiDataReceptionListener, AdapterView.OnItemClickListener {

    private String mId;
    private String mIdToTrack;

    private TextView mCoordsTextView;
    private TextView mFloorTextView;
    private ImageView mMapImageView;

    private List<WirelessSignal> mWifiData = new ArrayList<>();
    private LocationDate mLastLocation;

    private Handler mFriendUpdateHandler = new Handler();
    private int mFriendUpdateInterval = 5000;

    private Callback<LocationDate> mLocationUpdate;
    private Callback<Bitmap> mMapUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        mId = extras.getString("id", "-");

        TextView nicknameTextView = (TextView) findViewById(R.id.nicknameTextView);
        nicknameTextView.setText("id: " + mId);

        mCoordsTextView = (TextView) findViewById(R.id.coordsTextView);
        mFloorTextView = (TextView) findViewById(R.id.floorTextView);
        mMapImageView = (ImageView) findViewById(R.id.mapImageView);

        createDrawerMenu();

        startTracking(mId);
    }

    private void createDrawerMenu() {
        ListView leftMenuListView = (ListView) findViewById(R.id.leftMenuListView);
        MenuEntryAdapter adapter = new MenuEntryAdapter(this);

        adapter.AddEntry(R.string.drawerMenu_trackMe, R.drawable.ic_action_person)
                .AddEntry(R.string.drawerMenu_trackSomeone, R.drawable.ic_action_friends)
                .AddEntry(R.string.drawerMenu_history, R.drawable.ic_action_time)
                .AddEntry(R.string.drawerMenu_wifiData, R.drawable.ic_action_network_wifi);

        leftMenuListView.setAdapter(adapter);
        leftMenuListView.setOnItemClickListener(MapActivity.this);
    }

    @Override
    public void onWifiDataReception(List<WirelessSignal> data) {
        mWifiData = data;
        requestLocation();
    }

    private Runnable mFriendUpdateTask = new Runnable() {
        @Override
        public void run() {
            requestLocation();
            mFriendUpdateHandler.postDelayed(mFriendUpdateTask, mFriendUpdateInterval);
        }
    };

    private void requestLocation() {
        final Callback<LocationDate> callback = new Callback<LocationDate>() {
            @Override
            public void callback(LocationDate response) {
                mLastLocation = response;

                requestMap();

                String coords = String.format("[%.2f, %.2f]", response.Loc.X, response.Loc.Y);

                mCoordsTextView.setText(coords);
                mFloorTextView.setText("floor: " + response.Loc.F);
            }
        };

        if (mIdToTrack == mId){ GisServiceClient.get().getMyLocation(mId, mWifiData, callback); }
        else { GisServiceClient.get().getLocation(mId, callback); }
    }

    private void requestMap() {
        GisServiceClient.get().getLocationMap(mLastLocation.Loc, new Callback<Bitmap>() {
            @Override
            public void callback(Bitmap response) {
                mMapImageView.setImageBitmap(response);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuEntry entry = (MenuEntry)parent.getItemAtPosition(position);

        switch(entry.stringId) {
            case R.string.drawerMenu_trackMe:
                startTracking(mId);
                break;

            case R.string.drawerMenu_trackSomeone:
                final EditText input = new EditText(this);

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.drawerMenu_trackSomeone))
                        .setMessage("Nickname:")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startTracking(input.getText().toString());
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.string.drawerMenu_history:
                final Intent openHistoryActivity = new Intent(this, HistoryActivity.class);
                openHistoryActivity.putExtra("id", mId);
                startActivity(openHistoryActivity);
                break;

            case R.string.drawerMenu_wifiData:
                if (mWifiData.size() == 0){
                    Toast toast = Toast.makeText(this, "No data just yet.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    Intent openWifiDataActivity = new Intent(this, WifiDataActivity.class);

                    int[] levels = new int[mWifiData.size()];
                    String[] macs = new String[mWifiData.size()];

                    int i = 0;
                    for (WirelessSignal ws : mWifiData) {
                        levels[i] = ws.Value;
                        macs[i++] = ws.Id;
                    }

                    openWifiDataActivity.putExtra("levels", levels); // TODO make sure the order is the same
                    openWifiDataActivity.putExtra("macs", macs); // TODO ^^^
                    startActivity(openWifiDataActivity);
                }
                break;
        }
    }

    private void startTracking(String id){
        if (id == mIdToTrack) return;
        mIdToTrack = id;

        TextView trackingTextView = (TextView) findViewById(R.id.trackingTextView);

        if (id == mId) {
            WifiDataProvider.get().setWifiDataReceptionListener(this);
            WifiDataProvider.get().startScanning();
            mFriendUpdateHandler.removeCallbacks(mFriendUpdateTask);

            trackingTextView.setText("my position:");
        }
        else {
            WifiDataProvider.get().setWifiDataReceptionListener(null);
            mFriendUpdateTask.run();

            trackingTextView.setText(id + "'s position:");
        }

        mMapImageView.setImageDrawable(null);
        mCoordsTextView.setText("-");
        mFloorTextView.setText("-");
    }
}
