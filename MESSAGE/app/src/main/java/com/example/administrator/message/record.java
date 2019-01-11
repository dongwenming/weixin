package com.example.administrator.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import cn.itcast.Domain.Rank;
import com.google.gson.Gson;
import net_untils.Net;

import java.io.UnsupportedEncodingException;

public class record extends Activity {
    TextView record1;
    TextView name1;
    TextView record2;
    TextView name2;
    TextView record3;
    TextView name3;
    TextView record4;
    TextView name4;
    TextView record5;
    TextView name5;
    TextView record6;
    TextView name6;
    TextView record7;
    TextView name7;
    TextView record8;
    TextView name8;
    TextView record9;
    TextView name9;
    TextView record10;
    TextView name10;
   // Button back;
    Rank rank;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
       // back=findViewById(R.id.title_Back);
        record1=findViewById(R.id.Timecost1);
        name1=findViewById(R.id.Name1);
        record2=findViewById(R.id.Timecost2);
        name2=findViewById(R.id.Name2);
        record3=findViewById(R.id.Timecost3);
        name3=findViewById(R.id.Name3);
        record4=findViewById(R.id.Timecost4);
        name4=findViewById(R.id.Name4);
        record5=findViewById(R.id.Timecost5);
        name5=findViewById(R.id.Name5);
        record6=findViewById(R.id.Timecost6);
        name6=findViewById(R.id.Name6);
        record7=findViewById(R.id.Timecost7);
        name7=findViewById(R.id.Name7);
        record8=findViewById(R.id.Timecost8);
        name8=findViewById(R.id.Name8);
        record9=findViewById(R.id.Timecost9);
        name9=findViewById(R.id.Name9);
        record10=findViewById(R.id.Timecost10);
        name10=findViewById(R.id.Name10);

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(record.this,startActivity.class));
//                finish();
//            }
//        });

        Net net=null;

        try {
            net=new Net("http://106.14.12.46:8080/Message/servlet/GetRank",rank,1) {
                @Override

                protected void onPostExecute(String result) {

                    try {
                        rank = new Gson().fromJson(result, Rank.class);
                        if(rank==null) Log.i("jiance","respone为空");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("record监测",rank.toString());
                    try{
                        record1.setText(CovertToTime(rank.getL().get(0).getTime()));
                        name1.setText(rank.getL().get(0).getName());
                        record2.setText(CovertToTime(rank.getL().get(1).getTime()));
                        name2.setText(rank.getL().get(1).getName());
                        record3.setText(CovertToTime(rank.getL().get(2).getTime()));
                        name3.setText(rank.getL().get(2).getName());
                        record4.setText(CovertToTime(rank.getL().get(3).getTime()));
                        name4.setText(rank.getL().get(3).getName());
                        record5.setText(CovertToTime(rank.getL().get(4).getTime()));
                        name5.setText(rank.getL().get(4).getName());
                        record6.setText(CovertToTime(rank.getL().get(5).getTime()));
                        name6.setText(rank.getL().get(5).getName());
                        record7.setText(CovertToTime(rank.getL().get(6).getTime()));
                        name7.setText(rank.getL().get(6).getName());
                        record8.setText(CovertToTime(rank.getL().get(7).getTime()));
                        name8.setText(rank.getL().get(7).getName());
                        record9.setText(CovertToTime(rank.getL().get(8).getTime()));
                        name9.setText(rank.getL().get(8).getName());
                        record10.setText(CovertToTime(rank.getL().get(9).getTime()));
                        name10.setText(rank.getL().get(9).getName());

                    } catch (Exception e) {
                        record1.setText("Server error");
                        name1.setText("Server error");
                        record2.setText("Server error");
                        name2.setText("Server error");
                        record3.setText("Server error");
                        name3.setText("Server error");
                        record4.setText("Server error");
                        name4.setText("Server error");
                        record5.setText("Server error");
                        name5.setText("Server error");
                        record6.setText("Server error");
                        name6.setText("Server error");
                        record7.setText("Server error");
                        name7.setText("Server error");
                        record8.setText("Server error");
                        name8.setText("Server error");
                        record9.setText("Server error");
                        name9.setText("Server error");
                        record10.setText("Server error");
                        name10.setText("Server error");
                    }




                }
            };
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        net.execute();


    }


    private String CovertToTime(int time){
        String  mm=String.valueOf(time/60);
        if(mm.length()==1)mm="0"+mm;
        String ss=String.valueOf(time%60);
        if(ss.length()==1)ss="0"+ss;
        return mm+":"+ss;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(record.this, startActivity.class));
        finish();
    }
}
