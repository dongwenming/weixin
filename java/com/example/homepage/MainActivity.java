package com.example.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private Button ib_Saolei;
    private Button ib_Record;
    private Button ib_Rank;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ib_Saolei = (Button) findViewById(R.id.btnSaolie);
        ib_Saolei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Record.class));
                finish();
            }
        });
//        ib_Record = (ImageButton) findViewById(R.id.btnRecord);
//        ib_Record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,Record.class));
//                finish();
//            }
//        });
//        ib_Rank = (ImageButton) findViewById(R.id.btnRank);
//        ib_Rank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,Record.class));
//                finish();
//            }
//        });
   }
}
