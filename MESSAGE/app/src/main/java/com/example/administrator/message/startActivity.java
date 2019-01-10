package com.example.administrator.message;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

//              ib_Record = (Button) findViewById(R.id.btnRecord);
//        ib_Record.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,Record.class));
//                finish();
//            }
//        });
//        ib_Rank = (Button) findViewById(R.id.btnRank);
//        ib_Rank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this,Record.class));
//                finish();
//            }
//        });
//   }
    }
}