package com.example.iotsecurity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InputForScaleActivity extends AppCompatActivity {
    EditText ageEdit, heightEdit;
    RadioGroup genderGroup;
    TextView weightTV;
    Button inputComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_for_scale);

        Intent intent = getIntent();

        ageEdit = findViewById(R.id.age_value);
        heightEdit = findViewById(R.id.height_value);
        genderGroup = findViewById(R.id.gender_group);
        weightTV = findViewById(R.id.weight_value);

        weightTV.setText(intent.getExtras().get("weight").toString());
        ageEdit.setText(intent.getExtras().get("age").toString());
        heightEdit.setText(intent.getExtras().get("height").toString());
        int genderIndex;
        if(intent.getExtras().get("gender").equals("Male"))
            genderIndex = 0;
        else
            genderIndex = 1;
        ((RadioButton)genderGroup.getChildAt(genderIndex)).setChecked(true);

        /**
         * weight 연동 추가 예정
         */
        final double weight = 50.0;

        final double age = Double.parseDouble(ageEdit.getText().toString());
        final double height = Double.parseDouble(heightEdit.getText().toString());

        RadioButton tmpButton = findViewById(genderGroup.getCheckedRadioButtonId());
        final String gender = tmpButton.getText().toString();



        inputComplete = findViewById(R.id.button_complete);
        inputComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("age", age);
                intent.putExtra("height", height);
                intent.putExtra("gender", gender);
                intent.putExtra("weight", weight);
                setResult(0 ,intent);
                finish();
            }
        });

    }

}