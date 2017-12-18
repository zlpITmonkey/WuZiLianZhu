package com.example.wuzilianzhu;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class WuziqiPanel extends View 
{
//view宽度，即棋盘的宽度，因为上下左右都要空出半个行高（因为棋子要在边界上，会超出实际的棋盘的大小半个行高），所以也算做棋盘范围
	    private int mPanelWidth ;       
	    private float mLineHeight;      //棋盘单行间距
	    private int MAX_LINE=10;      //棋盘行列数
	    private Paint mPaint = new Paint();      //定义一个画笔对象
	    private Bitmap mWhitePiece;     //白棋的图片
	    private Bitmap mBlackPiece;     //黑棋的图片
	    //设置棋子占行距的比例的常量（这里为行高的3/4），这样做可以让棋子的大小可以根据定义的行高而变化
	    private final float RATIO_PIECE_OF_LINE_HEIGHT = 3 * 1.0f / 4;

	    //白棋子先下
	    private boolean mIsWhite = true;
	   //已下的白棋的x，y轴的坐标集合
	    private ArrayList<Point> mWhitePieceArray = new ArrayList<Point>();
	    //已下的黑棋x，y轴的坐标集合
	    private ArrayList<Point> mBlackPieceArray = new ArrayList<Point>();

	    //定义一个判断游戏是否结束的布尔值
	    private boolean mIsGameOver;

	    private final int INIT_WIN = -1;            //游戏开始时的状态
	    public static final int WHITE_WIN = 0;      //白棋赢
	    public static final int BLACK_WIN = 1;      //黑棋赢
	    public static final int NO_WIN = 2;         //和棋
		private int MAX_COUNT_IN_LINE = 5;

	    private int mGameWinResult = INIT_WIN;      //初始化游戏结果
	    private OnGameStatusChangeListener listener;   //游戏状态监听器

	    //设置游戏状态监听器，这里传入我们定义的游戏状态监听的接口类
	    public void setOnGameStatusChangeListener(OnGameStatusChangeListener listener) 
	    {
	       this.listener=listener;
	    }
	  //重新开始游戏
	    public void restartGame() {
	        mWhitePieceArray.clear();
	        mBlackPieceArray.clear();
	        mIsGameOver = false;
	        mGameWinResult = INIT_WIN;
	        invalidate();
	    }

	    public WuziqiPanel(Context context) {
	        this(context,null);
	    }

	@SuppressLint("NewApi")
	public WuziqiPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub                                               
		init();
	}
//------------------------------------第一步：界面开发---------------------------------------------------------------
	 //1.0步：初始化游戏界面数据
    private void init() {
        mPaint.setColor(0x88000000);     //设置画笔颜色
        mPaint.setAntiAlias(true);       //抗锯齿
        mPaint.setDither(true);        //防抖动
        mPaint.setStyle(Style.FILL);    //设置类型，这里是画线
        //先判断白黑棋子图片是否为空，是则重新初始化
        if (mWhitePiece == null) 
        {
        	//初始化棋子图片
            mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        }
        if (mBlackPiece == null) {
            mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
        }
    }
    
	//1.1步：设置棋盘的大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

	        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

	        int width = Math.min(widthSize, heightSize);
	        //解决嵌套在ScrollView中时等情况出现的问题
	        if (widthMode == MeasureSpec.UNSPECIFIED) {
	            width = heightSize;    //让宽度由高度决定
	        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
	            width = widthSize;    
	        }
	        setMeasuredDimension(width, width);    //正方形，所以宽高一样
	        
	}
	
	//1.2步：初始化尺寸，当宽高确定改变时会回调
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// TODO Auto-generated method stub
	super.onSizeChanged(w, h, oldw, oldh);
	 mPanelWidth = w;
     mLineHeight = mPanelWidth * 1.0f / MAX_LINE;    //行高等于棋盘的宽度除以总行列数
     
//     加载的棋子的宽度为行高*我们上面定义的一个比例3/4
     int pieceWidth = (int) (mLineHeight * RATIO_PIECE_OF_LINE_HEIGHT);
     //这个方法的1参数为图片，2参数为宽，3参数为高，4参数默认false
     mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
//     黑棋子一样
     mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);

	}
	//1.3步：绘制界面
	@Override
	protected void onDraw(Canvas canvas) 
	{
	// TODO Auto-generated method stub
	super.onDraw(canvas);
	  drawBoard(canvas);    //绘制棋盘
      drawPiece(canvas);    //绘制棋子
     checkGameOver();      //检查游戏是否结束
	}
	//---------1.4步：实现绘制界面中的三个方法------------------------------------------
	
	
            /*第一个方法*/
 //绘制棋盘
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0;i < MAX_LINE; i ++) 
        {
//   假定一个行高为1，半个行高即为0.5，因为上下左右距离View都有半个行高，所以横线开始的位置即为半个行高（0.5）处开始
            int startX = (int) (lineHeight / 2);   //横线开始的位置，
            
//    结束的位置也是距离View的半个行高，所以棋盘宽度减去半个行高即为结束的位置
            int endX = (int) (w - lineHeight / 2);  //横线结束的位置
            
//     同理，竖线开始的位置也为半个行高，然后从第一根竖线开始，每距离一个行高画一条竖线
            int y = (int) ((0.5 + i) * lineHeight);
//       1参数横线x轴开始位置，2参数横线y轴开始的位置，3参数横线x轴结束位置，4参数横线y轴结束的位置，5参数为画笔
//   这行代码的意思：y轴是为了确定纵向每条横线的起始位置，也就是每个一个y的位置画一条横线
            canvas.drawLine(startX, y, endX, y, mPaint);    //画横线
//   与划横线一样，只不过是从y轴开始画，位置换一下就OK了
            canvas.drawLine(y, startX, y, endX, mPaint);    //画竖线
        }

    }
    
            /*第二个方法*/
    
 //绘制棋子
    private void drawPiece(Canvas canvas) {
        for (int i = 0,n = mWhitePieceArray.size();i < n;i++) {
            Point whitePoint = mWhitePieceArray.get(i);    //从集合中获取坐标信息
//      绘制棋子图案，1为图片，2为横坐标，3纵坐标
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 -RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight,
                    (whitePoint.y + (1 -RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight,null);
        }
        for (int i = 0,n = mBlackPieceArray.size();i < n;i++) {
            Point blackPoint = mBlackPieceArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 -RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight,
                    (blackPoint.y + (1 -RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight,null);
        }
    }
           /*第三个方法*/
    
    //检查游戏是否结束
    private void checkGameOver() 
	{
		// TODO Auto-generated method stub
		
		  boolean whiteWin = checkFiveInLine(mWhitePieceArray);
	        boolean blackWin = checkFiveInLine(mBlackPieceArray);
	        boolean noWin = checkNoWin(whiteWin,blackWin);
	        //如果游戏结束,获取游戏结果mGameWinResult
	        if (whiteWin) 
	        {
	            mGameWinResult = WHITE_WIN;
	        } else if (blackWin) {
	            mGameWinResult = BLACK_WIN;
	        } else if(noWin){
	            mGameWinResult = NO_WIN;
	        }
	        if (whiteWin || blackWin || noWin) {
	            mIsGameOver = true;
	            //回调游戏状态接口
	            if (listener != null) 
	            {
	            	//调用方法，传入参数游戏结果值
	                listener.onGameOver(mGameWinResult);
	            }
	        }
	}

   //--------------- ------------------------------------------------------
    
   
//-----------------------------第二步：与用户交互（触屏）--------------------------------------------------------------- 
    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) //游戏结束返回false
        {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) //当按下的时候
        {
        	//获取用户点击的坐标
            int x = (int) event.getX();
            int y = (int) event.getY();
          //不存贮用户的点击坐标，而是自己定义好坐标，就能解决如果在同一个地方点击了，而不产生重复的棋子
            Point p = getValidPoint(x, y);   
            //判断当前地方是否已经有了棋子
            if (mWhitePieceArray.contains(p) || mBlackPieceArray.contains(p)) 
            {
                return false;
            }

            if (mIsWhite)   //如果是白棋子在下，则把坐标放入白棋list集合中
            {
                mWhitePieceArray.add(p);
            } 
            else        //否则就把黑棋子的坐标放入黑棋list集合中
            {
                mBlackPieceArray.add(p);
            }
            //刷新界面
            invalidate();                                                                                               
            mIsWhite = !mIsWhite;    //取反操作，即下次触摸即变成黑棋子，反复
            return true;
        }
        return true;
    }
    //根据触摸点获取最近的格子位置
    private Point getValidPoint(int x, int y) {
        return new Point((int)(x / mLineHeight),(int)(y / mLineHeight));
    }
//--------------------------------------------------------------------------------------
    
//-------------------------------第三步：五子棋的规则逻辑---------------------------------------------------
 //3.0步：检查是否五子连珠
    private boolean checkFiveInLine(List<Point> points) 
    {
     //循环判断传入的坐标点集合的每一个点的横竖，左斜，右斜线是否五个棋子相连
        for (Point point : points)    
        {
            int x = point.x;
            int y = point.y;

            boolean checkHorizontal = checkHorizontalFiveInLine(x,y,points);
            boolean checkVertical = checkVerticalFiveInLine(x,y,points);
            boolean checkLeftDiagonal = checkLeftDiagonalFiveInLine(x,y,points);
            boolean checkRightDiagonal = checkRightDiagonalFiveInLine(x,y,points);
            
            //上面四种情况，只要一种情况成立则五子连珠
            if (checkHorizontal || checkVertical || checkLeftDiagonal || checkRightDiagonal) 
            {
                return true;
            }
        }

        //任何一种情况都没有，则返回false
        return false;
    }
    
  //3.1步：检查横线上有没有相同棋子的五子连珠
    private boolean checkHorizontalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) 
        {
        	//判断左边是否有相同的（相对一个集合而言，即是判断这个集合中有没有在同一列的五个坐标）
            if (points.contains(new Point(x - i, y))) 
            {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }
    
    //3.2步：检查竖线上有没有相同棋子的五子连珠
    private boolean checkVerticalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;   //定义一个用来计算相同棋子的数量
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }
    
  //3.3步检查向左斜的线上有没有相同棋子的五子连珠
    private boolean checkLeftDiagonalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }
    
   //3.4步：检查向右斜的线上有没有相同棋子的五子连珠
    private boolean checkRightDiagonalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //3.5步：检查是否和棋
    private boolean checkNoWin(boolean whiteWin, boolean blackWin) {
        if (whiteWin || blackWin) {
            return false;
        }
        int maxPieces = MAX_LINE * MAX_LINE;
        //如果白棋和黑棋的总数等于棋盘格子数,说明和棋
        if (mWhitePieceArray.size() + mBlackPieceArray.size() == maxPieces) {
            return true;
        }
        return false;
    }
//-------------------------------------------------------------------------------------------------------
  /*  *//**
     * 当View被销毁时需要保存游戏数据
     *//*
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //保存游戏数据
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhitePieceArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackPieceArray);
        return bundle;
    }

    //恢复游戏数据
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhitePieceArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackPieceArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }*/
}
