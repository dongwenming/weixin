package com.example.administrator.message;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyView extends View {
    int lineColor;
    int squareColor;
    int choosecolor;
    int chooseImageSrc;
    String text;
    Paint mypaint;
    Rect rect;
    String TAG="监测";
    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.myview);
        if (ta != null) {
            squareColor = ta.getColor(R.styleable.myview_squareColor, 0);
            choosecolor = ta.getColor(R.styleable.myview_chooseColor, 0);
            lineColor = ta.getColor(R.styleable.myview_lineColor, 0);
            chooseImageSrc = ta.getResourceId(R.styleable.myview_chooseImageSrc,0);
            text = ta.getString(R.styleable.myview_text);
            ta.recycle();
        }
        mypaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect=new Rect();


    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //Log.e(TAG, "onMeasure--widthMode-->" + widthMode);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:

                break;
            case MeasureSpec.AT_MOST:

                break;
            case MeasureSpec.UNSPECIFIED:

                break;
        }
//        Log.e(TAG, "onMeasure--widthSize-->" + widthSize);
//        Log.e(TAG, "onMeasure--heightMode-->" + heightMode);
//        Log.e(TAG, "onMeasure--heightSize-->" + heightSize);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
       // Log.e(TAG, "onLayout");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //划线
        drawline(canvas);
        //画地图
        drawlattice(canvas);
    }

    private void drawline(Canvas canvas){

        lineColor=getResources().getColor(R.color.black);
        mypaint.setColor(lineColor );

        //canvas.drawRect(0,0,getWidth(),getHeight(),mypaint);
        float x,y;
        y=x=getWidth()/9;
        int he=(getHeight()-getWidth())/2;

        for(int i=0;i<10;i++){
            mypaint.setColor(getResources().getColor(R.color.red));
            canvas.drawLine(i*x,he,i*x,getWidth()+he,mypaint);
            //Log.i(TAG,"X:"+i*x);
        }

        for(int i=0;i<10;i++){
            mypaint.setColor(getResources().getColor(R.color.red));
            canvas.drawLine(0,he+i*y,getWidth(),he+i*y,mypaint);
           // Log.i(TAG,"Y:"+(he+i*y));

        }
    }

    private void drawlattice(Canvas canvas){
        if(!lattice.is_init())lattice.init();

        Bitmap flag=BitmapFactory.decodeResource(getResources(),R.drawable.flag);
        Bitmap boom=BitmapFactory.decodeResource(getResources(),R.drawable.boom);
        Rect flag_rect=new Rect(0,0,flag.getWidth(),flag.getHeight());
        Rect boom_rect=new Rect(0,0,boom.getWidth(),boom.getHeight());
        Rect imag=new Rect();;

        int point=getWidth()/9;
        int he=(getHeight()-getWidth())/2;
        int right;
        int bottom;
        if(!lattice.is_init())lattice.init();
        Log.i(TAG,"point:"+point);
        Log.i(TAG,"间隔："+he);

        for(int i=0;i<9;i++)
            for(int j=0;j<9;j++){
                imag.set(i*point,he+j*point,i*point+3+(point-5),he+j*point+3+(point-5));

                if(lattice.booleans_lattice[i][j] && lattice.lattice[i][j]>-50){

                    if(lattice.lattice[i][j]==100)
                        //画炸弹
                        canvas.drawBitmap(boom,boom_rect,imag,mypaint);
                    else if(lattice.lattice[i][j]==0){
                        //画翻开后的空白格
                        mypaint.setColor(getResources().getColor(R.color.white));
                        canvas.drawRect(imag,mypaint);
                    }else{
                            mypaint.setTextSize(30);
                            mypaint.setColor(getResources().getColor(R.color.black));
                            canvas.drawText(lattice.lattice[i][j] + "", i * point + 30, he + j * point + 60, mypaint);

                    }
                }


                else if(lattice.lattice[i][j]<-10)
                    //画红旗
                    canvas.drawBitmap(flag,flag_rect,imag,mypaint);
                else{

                    mypaint.setColor(getResources().getColor(R.color.gray));
                    right=i*point+3+(point-5);
                    bottom=j*point+he+(point-5)+3;

                    //画灰格
                    canvas.drawRect(i*point+3,he+j*point+3,right,bottom,mypaint);

                }
            }



    }
    public  int get_interval(){
        return getWidth()/9;
    }

    public int get_Blank_distance(){
        return (getHeight()-getWidth())/2;
    }





}
