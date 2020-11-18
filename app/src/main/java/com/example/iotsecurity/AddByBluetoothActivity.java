package com.example.iotsecurity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddByBluetoothActivity extends AppCompatActivity {
    Button checkBluetooth, searchProduct;
    TextView isBluetoothText, pushButtonTxt;
    ImageView pushButtonImg;

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(getApplicationContext(), "Bluetooth is off!", Toast.LENGTH_SHORT);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(getApplicationContext(), "Bluetooth is on!", Toast.LENGTH_SHORT);
                        break;
                } // switch
            } // if
        } // receive
    };

    BluetoothAdapter btAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_by_bluetooth);

        pushButtonImg = findViewById(R.id.push_button_img);
        pushButtonTxt = findViewById(R.id.push_button_txt);
        isBluetoothText = findViewById(R.id.is_bluetooth);
        checkBluetooth = findViewById(R.id.check_bluetooth);
        searchProduct = findViewById(R.id.search_product);

        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter1);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btAdapter == null)
                    Toast.makeText(getApplicationContext(), "NOT Capable Bluetooth", Toast.LENGTH_SHORT);
                else if(btAdapter.isEnabled()) {
                    selectBluetoothDevice();
                }
                else {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 10);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 10:
                if(requestCode == RESULT_OK);

        }
    }

    private void selectBluetoothDevice() {

    }

    private boolean checkBluetooth(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkCapabilities isWifi = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if(isWifi != null) {
            if(isWifi.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                return true;
        }
        return false;
    }

}