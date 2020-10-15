package com.example.iotsecurity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SearchActivity2 extends AppCompatActivity {
    TextView name, category, provider, data, modelId, piId, productName, connection, display,
            portable, agree, deviceType, serviceType, cycle, period, always, infoType;

    Product product;
    FloatingActionButton findSim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search3);
        product = (Product)getIntent().getExtras().get("product");

        //textView 설정
        name = findViewById(R.id.name_content);
        category = findViewById(R.id.category_content);
        provider = findViewById(R.id.provider_content);
        data = findViewById(R.id.data_content);
        modelId = findViewById(R.id.model_id_content);
        piId = findViewById(R.id.pi_id_content);
        productName = findViewById(R.id.product_name_content);
        connection = findViewById(R.id.connection_content);
        display = findViewById(R.id.display_content);
        portable = findViewById(R.id.portable_content);
        agree = findViewById(R.id.agree_content);
        deviceType = findViewById(R.id.device_type_content);
        serviceType = findViewById(R.id.service_type_content);
        cycle = findViewById(R.id.cycle_content);
        period = findViewById(R.id.period_content);
        always = findViewById(R.id.always_content);
        infoType = findViewById(R.id.info_type_content);

        // 불러온 값을 채우기
        name.setText(product.name);
        provider.setText(product.provider);
        category.setText(product.category);
        modelId.setText(product.modelId);
        piId.setText(product.piId);
        productName.setText(product.productName);
        connection.setText(product.connection);
        display.setText(String.valueOf(product.display));
        portable.setText(String.valueOf(product.portable));
        agree.setText(String.valueOf(product.agree));
        deviceType.setText(product.deviceType);
        serviceType.setText(product.serviceType);
        cycle.setText(product.cycle);
        period.setText(String.valueOf(product.period));
        infoType.setText(product.infoType);
        data.setText("");
        if(product.always == 1)
            always.setText("수집안함");
        else if(product.always == 2)
            always.setText("조건 수집");
        else if(product.always == 3)
            always.setText("상시 수집");
        else
            always.setText("");

        findSim = findViewById(R.id.find_similar);
        findSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
    }
}