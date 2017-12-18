package com.example.wuzilianzhu;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.Window;
public class MainActivity extends Activity {

	   private WuziqiPanel mGamePanel;
	    private Builder alertBuilder;
	    private AlertDialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

        //��Ϸ����ʱ�����Ի���
        alertBuilder = new Builder(MainActivity.this);
        alertBuilder.setPositiveButton("����һ��", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGamePanel.restartGame();
            }
        });
        alertBuilder.setNegativeButton("�˳���Ϸ", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle("�˾ֽ���");

        mGamePanel = (WuziqiPanel) findViewById(R.id.id_wuziqi);
//        �ӿڻص�
        mGamePanel.setOnGameStatusChangeListener(new OnGameStatusChangeListener() {
            @Override
            public void onGameOver(int gameWinResult) {
                switch (gameWinResult) {
                    case WuziqiPanel.WHITE_WIN:
                        alertBuilder.setMessage("����ʤ��!");
                        break;
                    case WuziqiPanel.BLACK_WIN:
                        alertBuilder.setMessage("����ʤ��!");
                        break;
                    case WuziqiPanel.NO_WIN:
                        alertBuilder.setMessage("����!");
                        break;
                }
                alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });
    }
}
