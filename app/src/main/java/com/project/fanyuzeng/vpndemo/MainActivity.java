package com.project.fanyuzeng.vpndemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static final int VPN_SERVICE_LAUNCH_CODE = 10001;
    private Button mStartVpnBtn;
    private boolean mIsVpnStart;
    private BroadcastReceiver mVpnRunningReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mVpnRunningReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mIsVpnStart = intent.getBooleanExtra(VpnServiceTest.VPN_RUNNING_EXTRA_NAME, false);

            }
        };
        IntentFilter filter = new IntentFilter(VpnServiceTest.VPN_RUNNING_FILTER);
        registerReceiver(mVpnRunningReceiver, filter);


        mStartVpnBtn = (Button) findViewById(R.id.id_btn_start_vpn);

        mStartVpnBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = VpnService.prepare(getApplicationContext());
                if (intent != null) {
                    startActivityForResult(intent, VPN_SERVICE_LAUNCH_CODE);
                } else {
                    onActivityResult(VPN_SERVICE_LAUNCH_CODE, RESULT_OK, null);
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == VPN_SERVICE_LAUNCH_CODE) {

            Intent intent = new Intent(this, VpnServiceTest.class);
            startService(intent);
            changeButtonStatus(true);
        }
    }

    private void changeButtonStatus(boolean isVpnStart) {
        if (isVpnStart) {
            mStartVpnBtn.setEnabled(false);
            mStartVpnBtn.setText(R.string.close_vpn_in_notification);
        } else {
            mStartVpnBtn.setEnabled(true);
            mStartVpnBtn.setText(R.string.start_vpn);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        changeButtonStatus(mIsVpnStart && VpnServiceTest.isRunning());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mVpnRunningReceiver);
    }
}
