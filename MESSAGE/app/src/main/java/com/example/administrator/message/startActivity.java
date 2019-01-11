package com.example.administrator.message;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class startActivity extends Activity {
    private Button ib_Saolei;
    private Button ib_Record;
    private Button ib_Rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        ib_Saolei = (Button) findViewById(R.id.btnSaolie);
        ib_Saolei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(startActivity.this, MainActivity.class));
                finish();
            }
        });

              ib_Record = (Button) findViewById(R.id.btnRecord);
        ib_Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(startActivity.this,"功能未开发",Toast.LENGTH_SHORT).show();
            }
        });
        ib_Rank = (Button) findViewById(R.id.btnbtnRank);
        ib_Rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(startActivity.this, record.class));
                finish();
            }
        });
   }
    }
