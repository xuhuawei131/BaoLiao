/**

                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         ���汣��       ����BUG
 * PlayVideoActivity.java V1.0 2015-10-30 ����3:44:37
 *
 * Copyright JIAYUAN Co. ,Ltd. All rights reserved.
 *
 * Modification history(By WAH-WAY):
 *
 * Description:
 */

package com.meng.main.activity.video;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.main.R;
import com.meng.main.constants.FileConstants;
import com.meng.main.utils.CommUtils;

public class PlayVideoActivity extends Activity implements OnClickListener,
SurfaceHolder.Callback{
	private SurfaceView surfaceView;// ��ʾ��Ƶ�Ŀؼ�
	private MediaPlayer mediaPlayer;
	private File filename;
	private TextView text_size;
	private ImageButton image_play;
	private ImageView image_cover;
	private SurfaceHolder surfaceHolder;
	private boolean isVo=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		initData();
		setContentView(R.layout.activity_video_player);
		findViewById();
		requestSerice();
	}

	private void initData() {
		String filePath = this.getIntent().getStringExtra("filePath");
		filename=new File(filePath);
	}

	private void findViewById() {
		image_cover=(ImageView)findViewById(R.id.image_cover);
		image_play = (ImageButton) findViewById(R.id.image_play);
		text_size = (TextView) findViewById(R.id.text_size);

		surfaceView = (SurfaceView)findViewById(R.id.video_surfaceView);
		// surfaceView.getHolder().setFixedSize(176, 144);//���÷ֱ���
		surfaceHolder=surfaceView.getHolder();
//		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// ����surfaceview��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ
		surfaceHolder.addCallback(this);// ��surface�����״̬���м���

		image_play.setOnClickListener(this);
//		Uri uri=Uri.fromFile(filename);
		Bitmap bitmap=CommUtils.getVideoThumbnail(filename.getAbsolutePath(),500,500);
		image_cover.setImageBitmap(bitmap);
	}

	private void requestSerice() {
		long size=CommUtils.getFileSize(filename);
		text_size.setText(CommUtils.formetFileSize(size));
	}

	// ----------------------------------------
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) { 
			mediaPlayer.stop(); 
			}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_play:
			image_cover.setVisibility(View.GONE);
			if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
				 mediaPlayer.stop();
			} else {
				playVideo();
			}
			break;
		}
	}
private void playVideo(){
	mediaPlayer= new MediaPlayer();
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//����������������
    mediaPlayer.setDisplay(surfaceView.getHolder());//����videoӰƬ��surfaceviewholder����
    try {
		mediaPlayer.setDataSource(filename.getAbsolutePath());
		mediaPlayer.prepare();//����
		mediaPlayer.start();//����
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
