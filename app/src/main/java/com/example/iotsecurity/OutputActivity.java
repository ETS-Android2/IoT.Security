package com.example.iotsecurity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class OutputActivity extends AppCompatActivity {
    TextView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        outputView = findViewById(R.id.output_textview);
        Intent intent = getIntent();
        String temp = (String) intent.getExtras().get("output");
        temp.replaceAll(",", ",\n");
        outputView.setText(temp);
    }
}