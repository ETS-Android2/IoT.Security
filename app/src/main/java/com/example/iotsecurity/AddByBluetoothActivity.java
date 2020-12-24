package com.example.iotsecurity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

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

//        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mBroadcastReceiver1, filter1);
//
//        btAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 임시 코드
                 * 추후 연결 과정에서 장치에 대해 확인이 되어야됨.
                 */
                isBluetoothText.setText("Connected At Bluetooth");

            }
        });

        /**
         * 임시 데이터 추가
         */
        searchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product scale = null;
                try {
                    scale = makeTempScale();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String agreementMsg = "현재 시간 (" + scale.cycle + ")부로 Xaomi의 Mi scale2는 사용자의 " +
                        "나이, 성별, 키, 몸무게 개인 정보를 수집 또는 측정하며 현재 장치 이외에  클라우드에 데이터를 " +
                        "저장 또는 전송하지 않습니다. 장치 사용에 동의하십니까?";
                AlertDialog.Builder builder = new AlertDialog.Builder(AddByBluetoothActivity.this);
                    builder.setTitle("사용자 동의");
                    builder.setMessage(agreementMsg);
                final Product finalScale = scale;
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(AddByBluetoothActivity.this, SearchActivity2.class);
                            intent.putExtra("product", finalScale);
                            intent.putExtra("productNum", 3);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplication(), "제품 등록을 위해서는 동의가 필요합니다.", Toast.LENGTH_SHORT);
                            finish();
                        }
                    });
                    builder.show();
            }
        });
    }

    private boolean checkBluetooth() {
        boolean result = false;

        return result;
    }
    private Product makeTempScale() throws JSONException {
        Product temp = new Product();

        temp.name = "Mi Scale 2";
        temp.provider = "샤오미"; // 데이터 셋에 "샤오미"로 되어 있음, Not "Xiomi"
        temp.category = "체중계";
        temp.modelId = "MI-20201028001";
        temp.piId = "tempID";
        temp.connection = "bluetooth";
        temp.resourceType = "oic.r.scale.weight, oic.r.scale.height, oic.r.scale.age, oic.r.scale.bmi";

        temp.deviceType = "";
        temp.resourceType = "";
        temp.serviceType = "";
        temp.always = 0;
        temp.infoType = "";
        temp.score = -1;

        JSONObject scaleData = new JSONObject();
        scaleData.put("weight", "0");
        scaleData.put("height", "0");
        scaleData.put("gender", "Male");
        scaleData.put("age", "0");

        temp.data = scaleData.toString();

        /**
         * DB에는 연결된 시각을 저장
         * 불러오거나 넣을때 마다 period +1
         * cycle은 연결해제(삭제) 할때까지 유지
         * detail fragment 에서 데이터 출력할 때에는 연결 기간으로 (현재시각 - 연결시각)
         */
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm", java.util.Locale.getDefault());
        temp.cycle = dateFormat.format(date);
        temp.period = 1;

        return temp;
    }

}