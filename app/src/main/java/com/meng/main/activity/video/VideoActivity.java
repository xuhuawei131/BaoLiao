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

package com.meng.main.activity.video;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.main.R;
import com.meng.main.constants.FileConstants;
import com.meng.main.utils.CommUtils;
@SuppressLint("NewApi")
public class VideoActivity extends Activity implements 	SurfaceHolder.Callback,OnClickListener,Runnable{
	private MediaRecorder mediarecorder;// 录制视频的类
	private SurfaceView surfaceview;// 显示视频的控件
	private  Camera camera;
	/**实现这个接口的Callback接口**/
	private SurfaceHolder surfaceHolder;
	/**是否是后置摄像头**/
	private boolean isCameraBack=true;
	/**是否正在录制true录制中 false未录制*/
	private boolean isRecord=false;
	/**0代表前置摄像头，1代表后置摄像头**/
	private int cameraPosition = 1;
	private CameraHelper helper;
	private ImageButton image_camera;
	private ImageButton image_exchange;
	private TextView text_count;
	private File videoFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		helper=new CameraHelper(this);
		setContentView(R.layout.activity_video_recoder);
		findViewById();

	}
	private void findViewById(){
		image_exchange=(ImageButton)findViewById(R.id.image_exchange);
		image_camera=(ImageButton)findViewById(R.id.image_camera);
		text_count=(TextView)findViewById(R.id.text_count);
		surfaceview = (SurfaceView) findViewById(R.id.surfaceView);

		View btn_next=findViewById(R.id.btn_next);
		View btn_cancle=findViewById(R.id.btn_cancel);

		btn_next.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);

		image_camera.setOnClickListener(this);
		image_exchange.setOnClickListener(this);

		SurfaceHolder holder = surfaceview.getHolder();// 取得holder
		holder.addCallback(this); // holder加入回调接口
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//setType必须设置，要不出错.

	}
	@Override
	public void run() {
		int count=0;
		while(isRecord){
			try{
				handler.sendEmptyMessage(count);
				Thread.sleep(1000);
				count++;
			}catch(Exception e){

			}
		}
		handler.sendEmptyMessage(-1);

	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.image_exchange:
				exchangeCameraFace();
				break;
			case R.id.image_camera:
				startOrStopRecod();
				break;
			case R.id.btn_next:
				if(videoFile!=null&&videoFile.exists()){
					Intent intent=new Intent(this,PlayVideoActivity.class);
					intent.putExtra("filePath", videoFile.getAbsolutePath());
					startActivity(intent);
					finish();
				}else{
					Toast.makeText(this, "请录制视频", Toast.LENGTH_LONG).show();
				}

				break;
			case R.id.btn_cancel:
				finish();
				break;
		}

	}

	private void startOrStopRecod(){
		if(isRecord){//如果视频正在录制 那么就停止录制
			isRecord=false;
			image_camera.setImageResource(R.drawable.start);
			if (mediarecorder != null) {
				// 停止录制
				mediarecorder.stop();
				// 释放资源
				mediarecorder.release();
				mediarecorder = null;
			}
			if(camera!=null){
				camera.release();
			}
		}else{//如果视频没有录制 那么就开始录制
			isRecord=true;
			image_camera.setImageResource(R.drawable.stop);
			mediarecorder = new MediaRecorder();
			if(camera!=null){
				camera.release();
				camera=null;
			}
			if(cameraPosition==1){
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头
				camera=helper.resetCameraParam(camera);
				mediarecorder.setOrientationHint(90);//视频旋转90度
			}else{
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开摄像头
				Camera.Parameters parameters = camera.getParameters();
				camera.setDisplayOrientation(90);
				camera.setParameters(parameters);
				mediarecorder.setOrientationHint(270);//视频旋转90度
			}
			camera.unlock();
			mediarecorder.setCamera(camera);

			// 设置录制视频源为Camera(相机)
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
			mediarecorder
					.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// 设置录制的视频编码h263 h264
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			mediarecorder.setVideoSize(640, 480);//176, 144
			mediarecorder.setMaxDuration(1800000);
			//176X144,320X240,640X480,1280×720,1929X1080
			// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
			//				mediarecorder.setVideoFrameRate(15);
			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// 设置视频文件输出的路径
			videoFile=new File(FileConstants.getVideoDir(),System.currentTimeMillis()+".mp4");
			mediarecorder.setOutputFile(videoFile.getAbsolutePath());
			try {
				// 准备录制
				mediarecorder.prepare();
				mediarecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		new Thread(this).start();
	}
	/**
	 *
	 * 功能描述：
	 * 切换前后摄像头
	 * @author WAH-WAY(xuwahwhy@163.com)
	 * <p>创建日期 ：2015年10月22日 下午4:31:33</p>
	 *
	 *
	 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
	 */
	@SuppressLint("NewApi")
	private void exchangeCameraFace() {
		isCameraBack = !isCameraBack;
		int cameraCount = 0;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
			if (cameraPosition == 1) {
				// 现在是后置，变更为前置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					// CAMERA_FACING_BACK后置
					camera.stopPreview();// 停掉原来摄像头的预览
					camera.release();// 释放资源
					camera = null;// 取消原来摄像头
					camera = Camera.open(i);// 打开当前选中的摄像头
					try {
						helper.resetCameraParam(camera);
						camera.setPreviewDisplay(surfaceHolder);// 通过surfaceview显示取景画面
						camera.startPreview();// 开始预览
						cameraPosition = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			} else { // 现在是前置， 变更为后置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
					// CAMERA_FACING_BACK后置
					camera.stopPreview();// 停掉原来摄像头的预览
					camera.release();// 释放资源
					camera = null;// 取消原来摄像头
					camera = Camera.open(i);// 打开当前选中的摄像头
					try {
						helper.resetCameraParam(camera);
						camera.setPreviewDisplay(surfaceHolder);// 通过surfaceview显示取景画面
						camera.startPreview();// 开始预览
						cameraPosition = 1;
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	//------------------------------surface view的回调函数-----------------------------------
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		surfaceHolder = holder;
	}
	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
		try {
			if(isCameraBack){
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头
			}else{
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开摄像头
			}
			helper.resetCameraParam(camera);
			camera.setPreviewDisplay(surfaceHolder);//通过surfaceview显示取景画面
			camera.startPreview();//开始预览
		}catch(Exception e){
		}
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(camera!=null){
			camera.release();
		}
		surfaceview = null;
		surfaceHolder = null;
		if (surfaceHolder != null) {
			surfaceHolder=null;
		}
		if (mediarecorder != null) {
			mediarecorder=null;
		}
	}


	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int count=msg.what;
			if(count==-1){
				long size=CommUtils.getFileSize(videoFile);
				String values=CommUtils.formetFileSize(size);
			}else{
				text_count.setText(CommUtils.getTimeFormat(count));
			}
		}
	};
}
