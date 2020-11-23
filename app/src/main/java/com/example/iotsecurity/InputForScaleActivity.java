package com.example.iotsecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class InputForScaleActivity extends AppCompatActivity {
    EditText ageEdit, heightEdit;
    RadioGroup genderGroup;
    TextView weightTV;
    Button inputComplete, syncScale;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_for_scale);

        ageEdit = findViewById(R.id.age_value);
        heightEdit = findViewById(R.id.height_value);
        genderGroup = findViewById(R.id.gender_group);
        weightTV = findViewById(R.id.weight_value);

        Intent intent = getIntent();
        product = (Product)getIntent().getExtras().get("product");
        try {
            JSONObject productData = new JSONObject(product.data);

            weightTV.setText(productData.get("weight").toString());
            ageEdit.setText(productData.get("age").toString());
            heightEdit.setText(productData.get("height").toString());
            int genderIndex;
            if(productData.get("gender").equals("Male"))
                genderIndex = 0;
            else
                genderIndex = 1;
            ((RadioButton)genderGroup.getChildAt(genderIndex)).setChecked(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        inputComplete = findViewById(R.id.button_complete);
        syncScale = findViewById(R.id.button_sync);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        /**
         * TEST VER
         * 임의 체중 입력
         */
        syncScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightTV.setText("77.3");
            }
        });

        // 하드 코딩 : 체중계는 무조건 하나

        inputComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /**
                     * weight 연동 추가 예정
                     */
                    double weight = Double.parseDouble(weightTV.getText().toString());

                    double age = Double.parseDouble(ageEdit.getText().toString());
                    double height = Double.parseDouble(heightEdit.getText().toString());

                    RadioButton tmpButton = findViewById(genderGroup.getCheckedRadioButtonId());
                    String gender = tmpButton.getText().toString();

                    JSONObject dataForDB = new JSONObject();

                    dataForDB.put("age", age);
                    dataForDB.put("height", height);
                    dataForDB.put("weight", weight);
                    dataForDB.put("gender", gender);

                    product.data = dataForDB.toString();

                    /**
                     * 연결될 때(제품 제어 및 데이터 조회)마다 횟수 증가
                     * database 연결 후 period +1 해서 다시 저장
                     */
                    product.period += 1;
                    mDatabase.child("3").setValue(product);
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                finish();
            }
        });

    }

}