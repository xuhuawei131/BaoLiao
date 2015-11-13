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
*
* Copyright JIAYUAN Co. ,Ltd. All rights reserved.
*
* Modification history(By WAH-WAY):
*
* Description:
*/

package com.meng.main.activity.audio;


import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.main.R;
import com.meng.main.activity.audio.AudioRecorder.OnRecoderAudioListener;
import com.meng.main.custom.SineWave;
import com.meng.main.utils.CommUtils;

public class AudioActivity extends Activity implements OnRecoderAudioListener,OnClickListener,Runnable {
	private TextView text_count;
	private ImageButton image_audio;
	private AudioRecorder audioRecoder;
	private boolean isRunning=true;
	
	private int change=0;
	private SineWave sineWave;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_audio);
		text_count=(TextView)findViewById(R.id.text_count);
		image_audio=(ImageButton)findViewById(R.id.image_audio);
		sineWave=(SineWave)findViewById(R.id.sineWave);
		
		image_audio.setOnClickListener(this);
		audioRecoder=AudioRecorder.getInstance();
		image_audio.setImageResource(R.drawable.start);
		new Thread(this).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRunning=false;
	}

	@Override
	public void run() {
		while(isRunning){
			int mRecord_Volume=audioRecoder.getCurrentVolume();
			sineWave.setCurrentVoluem(mRecord_Volume);
//			try{
//				Thread.sleep(40);
//			}catch(Exception e){
//				
//			}
				
//			handler.sendEmptyMessage(mRecord_Volume);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.image_audio:
			if(audioRecoder.isRecoding()){
				image_audio.setImageResource(R.drawable.stop);
				audioRecoder.stopRecord();
			}else{
				image_audio.setImageResource(R.drawable.start);
				audioRecoder.startRecord(this);
			}
			break;
		}
	}

	@Override
	public void OnRecoderSoundComplete(File file, int duringTime) {
		
	}
	@Override
	public void onCountChange(int count) {
		text_count.setText(CommUtils.getTimeFormat(count));
	}
	@Override
	public void onRecoderAudioError(int code, String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			sineWave.setCurrentVoluem(msg.what);
		}
	};
}
