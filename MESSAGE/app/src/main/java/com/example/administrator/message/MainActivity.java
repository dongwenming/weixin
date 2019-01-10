package com.example.administrator.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends Activity {
    private static final int SUCESS = 99;
    private static final int EXPLOSTION = 98;
    private static final int NORMAL = 96;
    private static final int REFRESH = 95;
    private TextView boom, time, grade;
    private ImageButton over, flag;
    private MyView dispaly;
    private ImageView pause;
    private String TAG = "监测";
    private boolean state = false;//false为翻开，true为插旗
    private int boomnumber=10;
    private Afterclick afterclick;
    private long baseTimer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.obj!=null){
                time.setText((String)msg.obj);
            }

            switch (msg.getData().getInt("msg")) {
                case NORMAL:
                    dispaly.invalidate();
                    break;
                case SUCESS:
                    showNormalDialog("挑战成功");
                    break;
                case EXPLOSTION:
                    showNormalDialog("挑战失败");
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
    }

    private void setTime(){
    new Timer("开机计时器").scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            int time = (int)((SystemClock.elapsedRealtime() - MainActivity.this.baseTimer) / 1000);
            String mm = new DecimalFormat("00").format(time % 3600 / 60);
            String ss = new DecimalFormat("00").format(time % 60);
            String timeFormat = new String( mm + ":" + ss);
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
                point_x = (int) event.getX() / 73;
                point_y = (int) (event.getY() - 110) / 73;

                if (!lattice.is_init()) lattice.init();

                //点击事件判断
                //如果已经翻开，则不能再响应点击
                Log.i(TAG,"点击的格子："+lattice.lattice[point_x][point_y]+"; is?"+lattice.booleans_lattice[point_x][point_y]);
                if(!lattice.booleans_lattice[point_x][point_y]){
                    //点击翻开按钮时翻开
                    if(!state){
                        lattice.booleans_lattice[point_x][point_y]=true;
                        Integer[] p = {point_x, point_y};
                        if(!afterclick.isCancelled()){
                            afterclick=new Afterclick(handler);
                            afterclick.execute(p);
                        }
                    }
                        //点击旗子按钮时查旗子
                    else {

                        if (lattice.lattice[point_x][point_y] > -50)
                        {
                            Log.i(TAG,"点击了未查旗子的空格");
                            lattice.lattice[point_x][point_y] = lattice.lattice[point_x][point_y] - 160;
                            Log.i(TAG,"插旗子的格子："+lattice.lattice[point_x][point_y]+"; is?"+lattice.booleans_lattice[point_x][point_y]);
                        }
                            //如果已经插上了旗子，再点击旗子则取消
                        else {
                            Log.i(TAG,"点击了已经查旗子的空格");
                            lattice.lattice[point_x][point_y] = lattice.lattice[point_x][point_y] + 160;
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

    private void showNormalDialog(String s){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        //normalDialog.setIcon(R.drawable.icon_dialog);

        normalDialog.setTitle(s);
        normalDialog.setMessage(s);
        normalDialog.setPositiveButton("再来一次",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Reply();
                    }
                });
        normalDialog.setNegativeButton("返回主页",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void Reply(){
        lattice.init();
        new serivce().execute();
        dispaly.invalidate();
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
            if(lattice.lattice[integers[0]][integers[1]]==100) {
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
            point currentpoint=new point(x,y);
            point rear;
            point front;
            boolean[][] is_set = new boolean[9][9];

            for(int i=0;i<9;i++)
                for(int j=0;j<9;j++)
                    is_set[i][j]=false;

            Queue<point> queue=new ArrayBlockingQueue<point>(70);
            queue.add(currentpoint);
            is_set[x][y]=true;
            while (!queue.isEmpty()){
                front=queue.poll();
                Log.i(TAG,"point:x="+front.x+", y="+front.y);
                if(lattice.lattice[front.x][front.y]==0 ) {
                    if (front.x>0 &&front.y>0 && !is_set[front.x - 1][front.y - 1]) {
                        currentpoint=new point(front.x-1,(front.y-1));
                        is_set[front.x-1][front.y-1]=true;
                        queue.add(currentpoint);
                    }
                    if (front.x>0 &&!is_set[front.x - 1][front.y]) {
                        currentpoint=new point(front.x-1,front.y);
                        is_set[front.x-1][front.y]=true;
                        queue.add(currentpoint);
                    }
                    if (front.x>0 &&front.y<8 &&!is_set[front.x - 1][front.y +1]) {
                        currentpoint=new point(front.x-1,front.y+1);
                        is_set[front.x-1][front.y+1]=true;
                        queue.add(currentpoint);
                    }
                    if (front.y>0 && !is_set[front.x ][front.y - 1]) {
                        currentpoint=new point(front.x,front.y-1);
                        is_set[front.x][front.y-1]=true;
                        queue.add(currentpoint);
                    }
                    if (front.y<8&&!is_set[front.x ][front.y + 1]) {
                        currentpoint=new point(front.x,front.y+1);
                        is_set[front.x][front.y+1]=true;
                        queue.add(currentpoint);
                    }
                    if (front.x<8 &&front.y>0 &&!is_set[front.x +1][front.y - 1]) {
                        currentpoint=new point(front.x+1,front.y-1);
                        is_set[front.x+1][front.y-1]=true;
                        queue.add(currentpoint);
                    }
                    if (front.x<8 && !is_set[front.x +1][front.y ]) {
                        currentpoint=new point(front.x+1,front.y);
                        is_set[front.x+1][front.y]=true;
                        queue.add(currentpoint);
                    }
                    if (front.x<8 &&front.y<8 && !is_set[front.x +1][front.y+1 ]) {
                        currentpoint=new point(front.x+1,front.y+1);
                        is_set[front.x+1][front.y+1]=true;
                        queue.add(currentpoint);
                    }
                }
                lattice.booleans_lattice[front.x][front.y]=true;
            }
        }



    }
    class point{
        int x;
        int y;
        point(){
            x=0;
            y=0;
        }
        point(int x,int y){
            this.x=x;
            this.y=y;
        }
    }


}

