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
import com.google.gson.Gson;
import net_untils.Net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    private Afterclick afterclick;
    private long baseTimer;
    private String username;
    private int flag_number=10;
    private boolean pause_time=false;
    private Timer time_R;
    private int pause_time_record=0;
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
                    SendResultToNet();
                    Result_Dialog(true);

                    break;
                case EXPLOSTION:
                    time_record = time.getText().toString();
                    //SendResultToNet();
                    Result_Dialog(false);
                    break;
            }
        }
    };
    private CountTime timerTask;

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
        timerTask=new CountTime();
        time_R.scheduleAtFixedRate(timerTask, 0, 1000L);
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

                //时间暂停时，不能点击
                if(pause_time)return false;

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

                        if (lattice.lattice[point_x][point_y] > -10 ) {
                            if (flag_number > 0) {
                                Log.i(TAG, "点击了未查旗子的空格");
                                lattice.lattice[point_x][point_y] = lattice.lattice[point_x][point_y] - 120;
                                Integer[] p = {point_x, point_y};
                                if (!afterclick.isCancelled()) {
                                    afterclick = new Afterclick(handler);
                                    afterclick.execute(p);
                                }
                                flag_number = flag_number - 1;
                                Log.i(TAG, "插旗子的格子：" + lattice.lattice[point_x][point_y] + "; is?" + lattice.booleans_lattice[point_x][point_y]);
                            }
                        }
                        //如果已经插上了旗子，再点击旗子则取消
                        else {
                            Log.i(TAG, "点击了已经查旗子的空格");
                            lattice.lattice[point_x][point_y] = lattice.lattice[point_x][point_y] + 120;
                            flag_number=flag_number+1;
                        }
                    }

                }


                boom.setText(" "+flag_number);
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

        //暂停按钮点击事件
        View.OnTouchListener pause_touch=new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    pause.setBackgroundColor(getResources().getColor(R.color.white));

                    if(!pause_time){
                        pause_time_record=timerTask.getCurrentTime();
                        time_R.cancel();
                        pause_time=true;
                    }else{
                        baseTimer = SystemClock.elapsedRealtime();
                        setTime();
                        pause_time=false;
                    }

                }
                else if(event.getAction()==MotionEvent.ACTION_UP){
                    pause.setBackgroundColor(getResources().getColor(R.color.gray));
                }

                if(pause_time)pause.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.start));
                else pause.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.pause));

                return false;
            }
        };
        pause.setOnTouchListener(pause_touch);




    }

    private void Reply() {
        lattice.init();
        new serivce().execute();
        time.setText("00:00");
        pause_time_record=0;
        this.baseTimer = SystemClock.elapsedRealtime();
        setTime();
         flag_number=10;
         boom.setText(flag_number+"");

        dispaly.invalidate();

    }

    private void JudgingAndSovle() {
        int mm = Integer.valueOf(time_record.substring(0, time_record.indexOf(":")));
        int ss = Integer.valueOf(time_record.substring(time_record.indexOf(":")+1));
        SharedPreferences read = getSharedPreferences("lattice", MODE_PRIVATE);
        String value = read.getString("fraction", "99:99");

            int mp = Integer.valueOf(value.substring(0, value.indexOf(":")));
            int sp = Integer.valueOf(value.substring(value.indexOf(":")+1));
            Log.i(TAG,"当前值："+time_record+", 记录值："+value);
            if (mm * 60 + ss < mp * 60 + sp) {
                Log.i(TAG,"记录成功的值");
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
        final TextView shorter=view.findViewById(R.id.shortertime);
        Button determine=view.findViewById(R.id.result_determine);
        Button re=view.findViewById(R.id.result_return);

        dialog.setContentView(view);

        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int)0.94*dispaly.getHeight());
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

        //显示最短时间
        SharedPreferences read = getSharedPreferences("lattice", MODE_PRIVATE);
        String value = read.getString("fraction", "99:99");
        if(!value.equals("99:99")){
            shorter.setText("最短时间："+value);
        }else{
            shorter.setText("无记录");
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
        dialog.setCanceledOnTouchOutside(false);
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

                final Rank rank=new Rank();
                rank.setName(username);
                rank.setTime(0);
                Net net=null;

                try {
                    net=new Net("http://106.14.12.46:8080/Message/servlet/GetRank",rank,1) {
                        @Override

                        protected void onPostExecute(String result) {
                            String frist=null;
                            Rank response_rank= null;
                            try {
                                response_rank = new Gson().fromJson(result, Rank.class);
                                if(response_rank==null) Log.i(TAG,"respone为空");

                                 frist= URLDecoder.decode(response_rank.getL().get(0).getName());
                                 if(frist.length()<10){
                                     for(int i=0;i<(10-frist.length());i++)
                                     {
                                         frist=frist+"  ";
                                         frist=" "+frist;
                                     }

                                 }

                            } catch (Exception e) {
                                frist="服务器出错";
                                e.printStackTrace();
                            }


                            grade.setText(frist);

                        }
                    };
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                net.execute();



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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this,startActivity.class));
        Reply();
        finish();
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


            boolean count = false;
            //结束监测1
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    if (lattice.lattice[i][j] == 100) count = true;
                }
//            //结束监测2
//            for (int i = 0; i < 9; i++)
//                for (int j = 0; j < 9; j++) {
//                    if (lattice.booleans_lattice[i][j] ==false && lattice.lattice[i][j] == 100
//                            ||lattice.booleans_lattice[i][j] ==false && lattice.lattice[i][j] <-10
//                    );
//                        else  count = true;
//
//                }

            if (!count) {
                Bundle b = new Bundle();
                b.putInt("msg", SUCESS);
                Message msg = new Message();
                msg.setData(b);
                handler.sendMessage(msg);
                return null;
            }



            //点击的时旗子，直接返回
            if (lattice.lattice[integers[0]][integers[1]] <-10)return null;

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
    class CountTime extends TimerTask{
        int time;
        public void run() {

            time = (int) ((SystemClock.elapsedRealtime() - MainActivity.this.baseTimer) / 1000)+pause_time_record;
            String mm = new DecimalFormat("00").format(time % 3600 / 60);
            String ss = new DecimalFormat("00").format(time % 60);
            String timeFormat = new String(mm + ":" + ss);
            Message msg = new Message();
            msg.obj = timeFormat;
            handler.sendMessage(msg);
        }

        protected int getCurrentTime(){
            return time;
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

