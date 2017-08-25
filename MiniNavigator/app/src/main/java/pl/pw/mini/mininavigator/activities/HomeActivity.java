package pl.pw.mini.mininavigator.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

import pl.pw.mini.mininavigator.R;
import pl.pw.mini.mininavigator.WifiDataProvider;


public class HomeActivity extends Activity implements View.OnClickListener {

    private EditText mNicknameEditText;

    private Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        mNicknameEditText.setText("noname" + mRandom.nextInt(1000));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openMapActivityButton:
                if (!WifiDataProvider.get().isWifiEnabled()) {
                    showWifiEnableDialog();
                } else {
                    Intent openMapActivity = new Intent(this, MapActivity.class);
                    openMapActivity.putExtra("id", mNicknameEditText.getText().toString());
                    startActivity(openMapActivity);
                }
                break;

            case R.id.openSettingsActivityButton:
                Intent openSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(openSettingsActivity);
                break;
        }
    }

    private void showWifiEnableDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("WiFi turned off")
                .setMessage("Enable it?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiDataProvider.get().setWifiEnabled(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
