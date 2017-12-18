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
//view��ȣ������̵Ŀ�ȣ���Ϊ�������Ҷ�Ҫ�ճ�����иߣ���Ϊ����Ҫ�ڱ߽��ϣ��ᳬ��ʵ�ʵ����̵Ĵ�С����иߣ�������Ҳ�������̷�Χ
	    private int mPanelWidth ;       
	    private float mLineHeight;      //���̵��м��
	    private int MAX_LINE=10;      //����������
	    private Paint mPaint = new Paint();      //����һ�����ʶ���
	    private Bitmap mWhitePiece;     //�����ͼƬ
	    private Bitmap mBlackPiece;     //�����ͼƬ
	    //��������ռ�о�ı����ĳ���������Ϊ�иߵ�3/4�������������������ӵĴ�С���Ը��ݶ�����и߶��仯
	    private final float RATIO_PIECE_OF_LINE_HEIGHT = 3 * 1.0f / 4;

	    //����������
	    private boolean mIsWhite = true;
	   //���µİ����x��y������꼯��
	    private ArrayList<Point> mWhitePieceArray = new ArrayList<Point>();
	    //���µĺ���x��y������꼯��
	    private ArrayList<Point> mBlackPieceArray = new ArrayList<Point>();

	    //����һ���ж���Ϸ�Ƿ�����Ĳ���ֵ
	    private boolean mIsGameOver;

	    private final int INIT_WIN = -1;            //��Ϸ��ʼʱ��״̬
	    public static final int WHITE_WIN = 0;      //����Ӯ
	    public static final int BLACK_WIN = 1;      //����Ӯ
	    public static final int NO_WIN = 2;         //����
		private int MAX_COUNT_IN_LINE = 5;

	    private int mGameWinResult = INIT_WIN;      //��ʼ����Ϸ���
	    private OnGameStatusChangeListener listener;   //��Ϸ״̬������

	    //������Ϸ״̬�����������ﴫ�����Ƕ������Ϸ״̬�����Ľӿ���
	    public void setOnGameStatusChangeListener(OnGameStatusChangeListener listener) 
	    {
	       this.listener=listener;
	    }
	  //���¿�ʼ��Ϸ
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
//------------------------------------��һ�������濪��---------------------------------------------------------------
	 //1.0������ʼ����Ϸ��������
    private void init() {
        mPaint.setColor(0x88000000);     //���û�����ɫ
        mPaint.setAntiAlias(true);       //�����
        mPaint.setDither(true);        //������
        mPaint.setStyle(Style.FILL);    //�������ͣ������ǻ���
        //���жϰ׺�����ͼƬ�Ƿ�Ϊ�գ��������³�ʼ��
        if (mWhitePiece == null) 
        {
        	//��ʼ������ͼƬ
            mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        }
        if (mBlackPiece == null) {
            mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
        }
    }
    
	//1.1�����������̵Ĵ�С
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

	        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

	        int width = Math.min(widthSize, heightSize);
	        //���Ƕ����ScrollView��ʱ��������ֵ�����
	        if (widthMode == MeasureSpec.UNSPECIFIED) {
	            width = heightSize;    //�ÿ���ɸ߶Ⱦ���
	        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
	            width = widthSize;    
	        }
	        setMeasuredDimension(width, width);    //�����Σ����Կ��һ��
	        
	}
	
	//1.2������ʼ���ߴ磬�����ȷ���ı�ʱ��ص�
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	// TODO Auto-generated method stub
	super.onSizeChanged(w, h, oldw, oldh);
	 mPanelWidth = w;
     mLineHeight = mPanelWidth * 1.0f / MAX_LINE;    //�иߵ������̵Ŀ�ȳ�����������
     
//     ���ص����ӵĿ��Ϊ�и�*�������涨���һ������3/4
     int pieceWidth = (int) (mLineHeight * RATIO_PIECE_OF_LINE_HEIGHT);
     //���������1����ΪͼƬ��2����Ϊ��3����Ϊ�ߣ�4����Ĭ��false
     mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
//     ������һ��
     mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);

	}
	//1.3�������ƽ���
	@Override
	protected void onDraw(Canvas canvas) 
	{
	// TODO Auto-generated method stub
	super.onDraw(canvas);
	  drawBoard(canvas);    //��������
      drawPiece(canvas);    //��������
     checkGameOver();      //�����Ϸ�Ƿ����
	}
	//---------1.4����ʵ�ֻ��ƽ����е���������------------------------------------------
	
	
            /*��һ������*/
 //��������
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0;i < MAX_LINE; i ++) 
        {
//   �ٶ�һ���и�Ϊ1������и߼�Ϊ0.5����Ϊ�������Ҿ���View���а���иߣ����Ժ��߿�ʼ��λ�ü�Ϊ����иߣ�0.5������ʼ
            int startX = (int) (lineHeight / 2);   //���߿�ʼ��λ�ã�
            
//    ������λ��Ҳ�Ǿ���View�İ���иߣ��������̿�ȼ�ȥ����и߼�Ϊ������λ��
            int endX = (int) (w - lineHeight / 2);  //���߽�����λ��
            
//     ͬ�����߿�ʼ��λ��ҲΪ����иߣ�Ȼ��ӵ�һ�����߿�ʼ��ÿ����һ���и߻�һ������
            int y = (int) ((0.5 + i) * lineHeight);
//       1��������x�Ὺʼλ�ã�2��������y�Ὺʼ��λ�ã�3��������x�����λ�ã�4��������y�������λ�ã�5����Ϊ����
//   ���д������˼��y����Ϊ��ȷ������ÿ�����ߵ���ʼλ�ã�Ҳ����ÿ��һ��y��λ�û�һ������
            canvas.drawLine(startX, y, endX, y, mPaint);    //������
//   �뻮����һ����ֻ�����Ǵ�y�Ὺʼ����λ�û�һ�¾�OK��
            canvas.drawLine(y, startX, y, endX, mPaint);    //������
        }

    }
    
            /*�ڶ�������*/
    
 //��������
    private void drawPiece(Canvas canvas) {
        for (int i = 0,n = mWhitePieceArray.size();i < n;i++) {
            Point whitePoint = mWhitePieceArray.get(i);    //�Ӽ����л�ȡ������Ϣ
//      ��������ͼ����1ΪͼƬ��2Ϊ�����꣬3������
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
           /*����������*/
    
    //�����Ϸ�Ƿ����
    private void checkGameOver() 
	{
		// TODO Auto-generated method stub
		
		  boolean whiteWin = checkFiveInLine(mWhitePieceArray);
	        boolean blackWin = checkFiveInLine(mBlackPieceArray);
	        boolean noWin = checkNoWin(whiteWin,blackWin);
	        //�����Ϸ����,��ȡ��Ϸ���mGameWinResult
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
	            //�ص���Ϸ״̬�ӿ�
	            if (listener != null) 
	            {
	            	//���÷��������������Ϸ���ֵ
	                listener.onGameOver(mGameWinResult);
	            }
	        }
	}

   //--------------- ------------------------------------------------------
    
   
//-----------------------------�ڶ��������û�������������--------------------------------------------------------------- 
    //�����¼�
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) //��Ϸ��������false
        {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) //�����µ�ʱ��
        {
        	//��ȡ�û����������
            int x = (int) event.getX();
            int y = (int) event.getY();
          //�������û��ĵ�����꣬�����Լ���������꣬���ܽ�������ͬһ���ط�����ˣ����������ظ�������
            Point p = getValidPoint(x, y);   
            //�жϵ�ǰ�ط��Ƿ��Ѿ���������
            if (mWhitePieceArray.contains(p) || mBlackPieceArray.contains(p)) 
            {
                return false;
            }

            if (mIsWhite)   //����ǰ��������£��������������list������
            {
                mWhitePieceArray.add(p);
            } 
            else        //����ͰѺ����ӵ�����������list������
            {
                mBlackPieceArray.add(p);
            }
            //ˢ�½���
            invalidate();                                                                                               
            mIsWhite = !mIsWhite;    //ȡ�����������´δ�������ɺ����ӣ�����
            return true;
        }
        return true;
    }
    //���ݴ������ȡ����ĸ���λ��
    private Point getValidPoint(int x, int y) {
        return new Point((int)(x / mLineHeight),(int)(y / mLineHeight));
    }
//--------------------------------------------------------------------------------------
    
//-------------------------------��������������Ĺ����߼�---------------------------------------------------
 //3.0��������Ƿ���������
    private boolean checkFiveInLine(List<Point> points) 
    {
     //ѭ���жϴ��������㼯�ϵ�ÿһ����ĺ�������б����б���Ƿ������������
        for (Point point : points)    
        {
            int x = point.x;
            int y = point.y;

            boolean checkHorizontal = checkHorizontalFiveInLine(x,y,points);
            boolean checkVertical = checkVerticalFiveInLine(x,y,points);
            boolean checkLeftDiagonal = checkLeftDiagonalFiveInLine(x,y,points);
            boolean checkRightDiagonal = checkRightDiagonalFiveInLine(x,y,points);
            
            //�������������ֻҪһ�������������������
            if (checkHorizontal || checkVertical || checkLeftDiagonal || checkRightDiagonal) 
            {
                return true;
            }
        }

        //�κ�һ�������û�У��򷵻�false
        return false;
    }
    
  //3.1��������������û����ͬ���ӵ���������
    private boolean checkHorizontalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1;i < MAX_COUNT_IN_LINE;i++) 
        {
        	//�ж�����Ƿ�����ͬ�ģ����һ�����϶��ԣ������ж������������û����ͬһ�е�������꣩
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
    
    //3.2���������������û����ͬ���ӵ���������
    private boolean checkVerticalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;   //����һ������������ͬ���ӵ�����
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
    
  //3.3���������б��������û����ͬ���ӵ���������
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
    
   //3.4�����������б��������û����ͬ���ӵ���������
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

    //3.5��������Ƿ����
    private boolean checkNoWin(boolean whiteWin, boolean blackWin) {
        if (whiteWin || blackWin) {
            return false;
        }
        int maxPieces = MAX_LINE * MAX_LINE;
        //�������ͺ���������������̸�����,˵������
        if (mWhitePieceArray.size() + mBlackPieceArray.size() == maxPieces) {
            return true;
        }
        return false;
    }
//-------------------------------------------------------------------------------------------------------
  /*  *//**
     * ��View������ʱ��Ҫ������Ϸ����
     *//*
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //������Ϸ����
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhitePieceArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackPieceArray);
        return bundle;
    }

    //�ָ���Ϸ����
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
