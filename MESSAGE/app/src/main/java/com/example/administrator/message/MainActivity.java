package com.example.administrator.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cn.itcast.Domain.Rank;
import net_untils.Net;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends Activity {
    private static final int SUCESS = 99;
    private static final int EXPLOSTION = 98;
    private static final int NORMAL = 96;
    private static final int REFRESH = 95;
    private String time_record = "00:00";
    private TextView boom, time, grade;
    private ImageButton over, flag;
    private MyView dispaly;
    private ImageView pause;
    private String TAG = "监测";
    private boolean state = false;//false为翻开，true为插旗
    private int boomnumber = 10;
    private Afterclick afterclick;
    private long baseTimer;
    private String username;
    Timer time_R;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null) {
                time.setText((String) msg.obj);
            }

            switch (msg.getData().getInt("msg")) {
                case NORMAL:
                    dispaly.invalidate();
                    break;
                case SUCESS:
                    time_record = time.getText().toString();
                    JudgingAndSovle();

                    Result_Dialog(true);

                    break;
                case EXPLOSTION:
                    time_record = time.getText().toString();
                    SendResultToNet();
                    Result_Dialog(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        afterclick = new Afterclick(handler);
        init();
        setlistener();
        new serivce().execute();
        setTime();
        user_nameDialog();

    }

    private void setTime() {
        time_R = new Timer("开机计时器");
        time_R.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int time = (int) ((SystemClock.elapsedRealtime() - MainActivity.this.baseTimer) / 1000);
                String mm = new DecimalFormat("00").format(time % 3600 / 60);
                String ss = new DecimalFormat("00").format(time % 60);
                String timeFormat = new String(mm + ":" + ss);
                Message msg = new Message();
                msg.obj = timeFormat;
                handler.sendMessage(msg);
            }

        }, 0, 1000L);
    }

    private void init() {
        boom = findViewById(R.id.boomnumber);
        time = findViewById(R.id.time);
        grade = findViewById(R.id.grade);
        pause = findViewById(R.id.pause);
        over = findViewById(R.id.over);
        flag = findViewById(R.id.flag);
        dispaly = findViewById(R.id.dispaly);
        over.setBackgroundColor(getResources().getColor(R.color.gray));
        flag.setBackgroundColor(getResources().getColor(R.color.white));
        this.baseTimer = SystemClock.elapsedRealtime();

    }

    private void setlistener() {

        //地图点击事件
        View.OnTouchListener touchListener = new View.OnTouchListener() {
            int point_x;
            int point_y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Log.i(TAG, "x=" + event.getX() + "y=" + event.getY());
                point_x = (int) event.getX() / dispaly.get_interval();
                point_y = (int) (event.getY() - dispaly.get_Blank_distance()) / dispaly.get_interval();

                if (point_x < 0 || point_x > 8 || point_y < 0 || point_y > 8) return false;

                if (!lattice.is_init()) lattice.init();

                //点击事件判断
                //如果已经翻开，则不能再响应点击
                Log.i(TAG, "点击的格子：" + lattice.lattice[point_x][point_y] + "; is?" + lattice.booleans_lattice[point_x][point_y]);
                if (!lattice.booleans_lattice[point_x][point_y]) {
                    //点击翻开按钮时翻开
                    if (!state) {
                        lattice.booleans_lattice[point_x][point_y] = true;
                        Integer[] p = {point_x, point_y};
                        if (!afterclick.isCancelled()) {
                            afterclick = new Afterclick(handler);
                            afterclick.execute(p);
                        }
                    }
                    //点击旗子按钮时查旗子
                    else {

                        if (lattice.lattice[point_x][point_y] > -10) {
                            Log.i(TAG, "点击了未查旗子的空格");
                            lattice.lattice[point_x][point_y] = lattice.lattice[point_x][point_y] - 120;
                            Log.i(TAG, "插旗子的格子：" + lattice.lattice[point_x][point_y] + "; is?" + lattice.booleans_lattice[point_x][point_y]);
                        }
                        //如果已经插上了旗子，再点击旗子则取消
                        else {
                            Log.i(TAG, "点击了已经查旗子的空格");
                            lattice.lattice[point_x][point_y] = lattice.lattice[point_x][point_y] + 120;
                        }
                    }

                }


                dispaly.invalidate();

                return false;
            }
        };
        dispaly.setOnTouchListener(touchListener);

        //插旗按钮事件
        View.OnClickListener flag_l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = true;
                flag.setBackgroundColor(getResources().getColor(R.color.gray));
                over.setBackgroundColor(getResources().getColor(R.color.white));
            }
        };
        flag.setOnClickListener(flag_l);

        //翻开按钮事件
        View.OnClickListener over_l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = false;
                over.setBackgroundColor(getResources().getColor(R.color.gray));
                flag.setBackgroundColor(getResources().getColor(R.color.white));

            }
        };
        over.setOnClickListener(over_l);


    }

    private void Reply() {
        lattice.init();
        new serivce().execute();
        time.setText("00:00");
        this.baseTimer = SystemClock.elapsedRealtime();
        setTime();

        dispaly.invalidate();

    }

    private void JudgingAndSovle() {
        int mm = Integer.valueOf(time_record.substring(0, time_record.indexOf(":")));
        int ss = Integer.valueOf(time_record.substring(time_record.indexOf(":")+1));
        SharedPreferences read = getSharedPreferences("lattice", MODE_WORLD_READABLE);
        String value = read.getString("fraction", "99:00");

            int mp = Integer.valueOf(value.substring(0, value.indexOf(":")));
            int sp = Integer.valueOf(value.substring(value.indexOf(":")+1));
            if (mm * 60 + ss < mp * 60 + sp) {
                SharedPreferences.Editor editor = getSharedPreferences("lattice",  MODE_PRIVATE).edit();
                editor.putString("fraction", time_record);
                editor.commit();
            }
        }



    private void Result_Dialog(boolean result ){
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.result_dialog, null);
        final ImageView image=view.findViewById(R.id.result_image);
        final TextView dispaly_time=view.findViewById(R.id.dispaly_time);
        Button determine=view.findViewById(R.id.result_determine);
        Button re=view.findViewById(R.id.result_return);
        dialog.setContentView(view);

        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight(dispaly.getHeight());
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = dispaly.getWidth();
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        if(result){
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.sucess));
            dispaly_time.setText("耗时："+time_record);
        }
        else{
            image.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.fairue));
            dispaly_time.setText("耗时："+time_record);
        }

        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reply();
                time_R.cancel();
                dialog.dismiss();
            }
        });
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reply();
                time_R.cancel();
                startActivity(new Intent(MainActivity.this, startActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    private void user_nameDialog(){
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.username_dialog, null);
        final EditText edit=view.findViewById(R.id.username_edit);
        Button confirm=view.findViewById(R.id.userde_termine);
        Button re=view.findViewById(R.id.user_return);
        dialog.setContentView(view);

        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小

        view.setMinimumHeight(300);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 600;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username =edit.getText().toString();

                dialog.dismiss();
            }
        });
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,startActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void SendResultToNet(){
        Rank rank=new Rank();
        rank.setName(username);
        rank.setTime(timeConvert(time_record));
        Net net=null;

        try {
            net=new Net("http://106.14.12.46:8080/Message/servlet/GetRank",rank,1) {
                @Override
                protected void onPostExecute(String result) {

                }
            };
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        net.execute();


    }




    class Afterclick extends AsyncTask<Integer, String, String> {
        Handler handler;

        Afterclick(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            //点击事件之后响应处理

            //炸弹判定
            if (lattice.lattice[integers[0]][integers[1]] == 100) {
                Bundle b = new Bundle();
                b.putInt("msg", EXPLOSTION);
                Message msg = new Message();
                msg.setData(b);
                handler.sendMessage(msg);
            }

            //结束监测
            boolean count = false;
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    if (lattice.lattice[i][j] == 100) count = true;
                }
            if (!count) {
                Bundle b = new Bundle();
                b.putInt("msg", SUCESS);
                Message msg = new Message();
                msg.setData(b);
                handler.sendMessage(msg);
                return null;
            }

            //点击到空白
            search(integers[0], integers[1]);
            Bundle b = new Bundle();
            b.putInt("msg", NORMAL);
            Message msg = new Message();
            msg.setData(b);
            handler.sendMessage(msg);
            return null;
        }

        //空白格展开
        private void search(int x, int y) {
            point currentpoint = new point(x, y);
            point rear;
            point front;
            boolean[][] is_set = new boolean[9][9];

            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++)
                    is_set[i][j] = false;

            Queue<point> queue = new ArrayBlockingQueue<point>(70);
            queue.add(currentpoint);
            is_set[x][y] = true;
            while (!queue.isEmpty()) {
                front = queue.poll();
                Log.i(TAG, "point:x=" + front.x + ", y=" + front.y);
                if (lattice.lattice[front.x][front.y] == 0) {
                    if (front.x > 0 && front.y > 0 && !is_set[front.x - 1][front.y - 1]) {
                        currentpoint = new point(front.x - 1, (front.y - 1));
                        is_set[front.x - 1][front.y - 1] = true;
                        queue.add(currentpoint);
                    }
                    if (front.x > 0 && !is_set[front.x - 1][front.y]) {
                        currentpoint = new point(front.x - 1, front.y);
                        is_set[front.x - 1][front.y] = true;
                        queue.add(currentpoint);
                    }
                    if (front.x > 0 && front.y < 8 && !is_set[front.x - 1][front.y + 1]) {
                        currentpoint = new point(front.x - 1, front.y + 1);
                        is_set[front.x - 1][front.y + 1] = true;
                        queue.add(currentpoint);
                    }
                    if (front.y > 0 && !is_set[front.x][front.y - 1]) {
                        currentpoint = new point(front.x, front.y - 1);
                        is_set[front.x][front.y - 1] = true;
                        queue.add(currentpoint);
                    }
                    if (front.y < 8 && !is_set[front.x][front.y + 1]) {
                        currentpoint = new point(front.x, front.y + 1);
                        is_set[front.x][front.y + 1] = true;
                        queue.add(currentpoint);
                    }
                    if (front.x < 8 && front.y > 0 && !is_set[front.x + 1][front.y - 1]) {
                        currentpoint = new point(front.x + 1, front.y - 1);
                        is_set[front.x + 1][front.y - 1] = true;
                        queue.add(currentpoint);
                    }
                    if (front.x < 8 && !is_set[front.x + 1][front.y]) {
                        currentpoint = new point(front.x + 1, front.y);
                        is_set[front.x + 1][front.y] = true;
                        queue.add(currentpoint);
                    }
                    if (front.x < 8 && front.y < 8 && !is_set[front.x + 1][front.y + 1]) {
                        currentpoint = new point(front.x + 1, front.y + 1);
                        is_set[front.x + 1][front.y + 1] = true;
                        queue.add(currentpoint);
                    }
                }
                lattice.booleans_lattice[front.x][front.y] = true;
            }
        }


    }

    class point {
        int x;
        int y;

        point() {
            x = 0;
            y = 0;
        }

        point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static int timeConvert(String time){
        byte[] minute = time.getBytes();
        int mm = Integer.valueOf(time.substring(0, time.indexOf(":")));
        int ss = Integer.valueOf(time.substring(time.indexOf(":")+1));


        return mm*60+ss;

    }



}

