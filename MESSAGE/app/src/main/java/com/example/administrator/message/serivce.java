package com.example.administrator.message;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/*生成地雷格异步任务*/
public class serivce extends AsyncTask<Handler, String, String> {
    List<poin> list = new ArrayList<>();
    boolean isexist=false;

    @Override
    protected String doInBackground(Handler... handlers) {
        if(!lattice.is_init()) lattice.init();
        init();
        init_number();

        print();
        list.clear();


        return null;
    }

       public void init() {
        Random random = new Random();
        poin p;

        while(list.size()<10){
            isexist=false;
            p=new poin(random.nextInt(8),random.nextInt(8));

            for(int i=0;i<list.size();i++){
                if(p.x==list.get(i).x && p.y==list.get(i).y)isexist=true;
            }

            if(!isexist)list.add(p);

        }


       }
//初始化地图
       public void init_number() {

        //初始化炸弹
        for (poin p : list) {
            Log.i("炸弹监测",list.size()+"");

            //设置炸弹
            lattice.lattice[p.x][p.y]=100;

            //设置炸弹边上的空格中的数值
            for (int m = -1; m < 2; m++) {
                for (int j = -1; j < 2; j++) {

                    if (  (p.x + m) >= 0 && (p.x + m) < 9 &&
                            (p.y + j) >= 0 && (p.y + j) < 9) {

                        Log.i("监测","x="+(p.x + m)+", y="+(p.y + j)+", p.x="+p.x+", p.y="+p.y);
                                lattice.lattice[p.x + m][p.y + j] =
                                lattice.lattice[p.x + m][p.y + j] + 1;
                    }

                }
            }


            for (int i=0;i<9;i++)
                for (int j=0;j<9;j++)
                {
                    if (lattice.lattice[i][j]>100) lattice.lattice[i][j]=100;
                }
        }


        }

        private void print(){

        for(int i=0;i<9;i++)
            Log.i("初始化监测,第"+i+"行", lattice.lattice[i][0]+" "+
                    lattice.lattice[i][1]+" "+
                    lattice.lattice[i][2]+" "+ lattice.lattice[i][0]+" "+
                    lattice.lattice[i][3]+" "+ lattice.lattice[i][4]+" "+
                            lattice.lattice[i][5]+" "+
                            lattice.lattice[i][6]+" "+ lattice.lattice[i][7]+" "+
                    lattice.lattice[i][8]+" "

            );
        }







    }

class poin {
    protected int x;
    protected int y;
    poin(int x,int y){
        this.x=x;
        this.y=y;
    }
}


