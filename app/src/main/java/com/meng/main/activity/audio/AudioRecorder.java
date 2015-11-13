package com.meng.main.activity.audio;

import java.io.File;
import java.io.IOException;

import com.meng.main.MyApplication;
import com.meng.main.constants.FileConstants;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


public class AudioRecorder implements Runnable,OnErrorListener{
	private static final int  RECODER_STATUS_COUNT=0;

	private static final int RECODER_STAUTS_SHORT=1;
	private static final int RECODER_STAUTS_END=2;
	private static final int RECODER_STAUTS_CANCEL=3;
	private static final int RECODER_STAUTS_RECODER_FAIL=4;

	private File audioFile;//录音的文件
	private int RECORD_MAX_TIME=61;//毫秒 最长录取时间1分钟 多出来的一秒是不计入的
	//单声道
	public static final int AUDIO_CHANNEL_SINGLE = 1;
	//多声道/双声道
	public static final int AUDIO_CHANNEL_MORE = 2;
	private MediaRecorder recorder;
	private OnRecoderAudioListener listener;
	private static AudioRecorder self;
	/**0表示停止  1表示正在运行  2表示取消 3表示已经达到最大的时间限制**/
	public int status=0;
	public int count=0;//
	private Context context;
	private AudioRecorder() {
		context=MyApplication.context;
	}

	public static AudioRecorder getInstance() {
		if (self == null) {
			self = new AudioRecorder();
		}
		return self;
	}


	public void startRecord(OnRecoderAudioListener listener) {
		try{
			record(listener);
		}catch(Exception e){
			if(listener!=null){
				listener.onRecoderAudioError(2, "录音初始化失败");
			}
		}
	}


	private void record(OnRecoderAudioListener newlistener)
			throws IllegalStateException, IOException {
		releaseRecoder();
		muteAudioFocus(true);

		listener=newlistener;
		audioFile = new File(FileConstants.getAudioDir(),System.currentTimeMillis()+".amr");

		if (audioFile.exists()) {
			audioFile.delete();
		}
		audioFile.createNewFile();
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioChannels(AUDIO_CHANNEL_SINGLE);
		recorder.setAudioEncodingBitRate(3000);
		recorder.setMaxDuration(RECORD_MAX_TIME*1000);
		recorder.setOutputFile(audioFile.getAbsolutePath());
		recorder.setOnErrorListener(this);
		recorder.prepare();
		recorder.start();
		status=1;
		count=0;
//		new Thread(this).start();
	}
	/**
	 *
	 * 功能描述：
	 * 是否关闭其他的音频
	 * @author WAH-WAY(xuwahwhy@163.com)
	 * <p>创建日期 ：2015年10月20日 下午2:16:13</p>
	 *
	 * @param bMute
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public static boolean muteAudioFocus(boolean bMute) {
		Context context=MyApplication.context;
		boolean bool = false;
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (bMute) {
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		} else {
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		return bool;
	}



	public void stopRecord() {
		status= 0;
	}
	/**
	 *
	 * 功能描述：
	 * 取消录音
	 * @author WAH-WAY(xuwahwhy@163.com)
	 * <p>创建日期 ：2015年10月20日 下午2:15:51</p>
	 *
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public void cancleRecod() {
		status= 2;
		handler.sendEmptyMessage(RECODER_STAUTS_CANCEL);
	}

	public boolean isRecoding(){
		return status==1;
	}

	public interface OnRecoderAudioListener{
		/**
		 *
		 * 功能描述：
		 *
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月19日 下午3:27:29</p>
		 *
		 * @param file
		 * @param duringTime
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		void OnRecoderSoundComplete(File file, int duringTime);
		void onCountChange(int count);
		void onRecoderAudioError(int code, String msg);
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		status=2;
		handler.sendEmptyMessage(RECODER_STAUTS_RECODER_FAIL);
	}
	@Override
	public void run() {
		while(status==1){
			if(count<RECORD_MAX_TIME){
				count++;
				handler.sendEmptyMessage(RECODER_STATUS_COUNT);
			}else{
				status=3;
				handler.sendEmptyMessage(RECODER_STAUTS_END);
				break;
			}
			try{
				Thread.sleep(1000);
			}catch(Exception e){

			}
		}
		switch(status){
			case 0://正常的停止
				if(count<3){
					handler.sendEmptyMessage(RECODER_STAUTS_SHORT);
				}else{
					handler.sendEmptyMessage(RECODER_STAUTS_END);
				}
				break;
			case 2://取消录音
				handler.sendEmptyMessage(RECODER_STAUTS_CANCEL);
				break;
			case 3://最大值
				handler.sendEmptyMessage(RECODER_STAUTS_END);
				break;
		}


	}
	private void releaseRecoder(){
		if (recorder != null) {
			recorder.setOnErrorListener(null);
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}
	/**
	 *
	 * 功能描述：
	 * 获取当前音量值
	 * @author 许华维(WAH-WAY)
	 * <p>创建日期 ：2015-10-27 下午4:10:51</p>
	 *
	 * @return
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	public int getCurrentVolume(){
		if(recorder!=null){
			return recorder.getMaxAmplitude();
		}else{
			return 0;
		}

	}

	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int what=msg.what;
			if(listener==null){
				Toast.makeText(context, "没有设置回调", Toast.LENGTH_SHORT).show();
				return;
			}
			switch(what){
				case RECODER_STATUS_COUNT:
					listener.onCountChange(count);
					break;
				case RECODER_STAUTS_END:
					releaseRecoder();
					muteAudioFocus(false);
					listener.OnRecoderSoundComplete(audioFile, count);
					audioFile=null;
					count=0;
					break;
				case RECODER_STAUTS_RECODER_FAIL:
					count = 0;
					releaseRecoder();
					muteAudioFocus(false);
					if(audioFile!=null&&audioFile.exists()){
						audioFile.delete();
					}
					listener.onRecoderAudioError(3, "录音出现异常");
				case RECODER_STAUTS_SHORT:
					count = 0;
					releaseRecoder();
					muteAudioFocus(false);
					if(audioFile!=null&&audioFile.exists()){
						audioFile.delete();
					}
					listener.onRecoderAudioError(3, "录取时间太短");
				case RECODER_STAUTS_CANCEL:
					count = 0;
					releaseRecoder();
					muteAudioFocus(false);
					if(audioFile!=null&&audioFile.exists()){
						audioFile.delete();
					}
					break;
			}
		}
	};


}
