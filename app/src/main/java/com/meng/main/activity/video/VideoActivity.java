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
* VideoActivity.java V1.0 2015-10-26 ����5:27:05
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
	private MediaRecorder mediarecorder;// ¼����Ƶ����
	private SurfaceView surfaceview;// ��ʾ��Ƶ�Ŀؼ�
	private  Camera camera;
	/**ʵ������ӿڵ�Callback�ӿ�**/
	private SurfaceHolder surfaceHolder;
	/**�Ƿ��Ǻ�������ͷ**/
	private boolean isCameraBack=true;
	/**�Ƿ�����¼��true¼���� falseδ¼��*/
	private boolean isRecord=false;
	/**0����ǰ������ͷ��1�����������ͷ**/
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
		
		SurfaceHolder holder = surfaceview.getHolder();// ȡ��holder
		holder.addCallback(this); // holder����ص��ӿ�
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//setType�������ã�Ҫ������.
		
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
				Toast.makeText(this, "��¼����Ƶ", Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	
	}
	
	private void startOrStopRecod(){
		if(isRecord){//�����Ƶ����¼�� ��ô��ֹͣ¼��
			isRecord=false;
			image_camera.setImageResource(R.drawable.start);
			if (mediarecorder != null) {
				// ֹͣ¼��
				mediarecorder.stop();
				// �ͷ���Դ
				mediarecorder.release();
				mediarecorder = null;
			}
			if(camera!=null){
				camera.release();
			}
		}else{//�����Ƶû��¼�� ��ô�Ϳ�ʼ¼��
			isRecord=true;
			image_camera.setImageResource(R.drawable.stop);
			mediarecorder = new MediaRecorder();
			if(camera!=null){
				camera.release();
				camera=null;
			}
			if(cameraPosition==1){
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//������ͷ
		         camera=helper.resetCameraParam(camera);
		         mediarecorder.setOrientationHint(90);//��Ƶ��ת90��
			}else{
				 camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//������ͷ
		         Camera.Parameters parameters = camera.getParameters();
		         camera.setDisplayOrientation(90);
		         camera.setParameters(parameters); 
				 mediarecorder.setOrientationHint(270);//��Ƶ��ת90��
			}
		    camera.unlock();
		    mediarecorder.setCamera(camera);
			
			// ����¼����ƵԴΪCamera(���)
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// ����¼����ɺ���Ƶ�ķ�װ��ʽTHREE_GPPΪ3gp.MPEG_4Ϊmp4
			mediarecorder
					.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// ����¼�Ƶ���Ƶ����h263 h264
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// ������Ƶ¼�Ƶķֱ��ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�
			mediarecorder.setVideoSize(640, 480);//176, 144
			mediarecorder.setMaxDuration(1800000);
			//176X144,320X240,640X480,1280��720,1929X1080
			// ����¼�Ƶ���Ƶ֡�ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�
	//				mediarecorder.setVideoFrameRate(15);
			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// ������Ƶ�ļ������·��
			videoFile=new File(FileConstants.getVideoDir(),System.currentTimeMillis()+".mp4");
			mediarecorder.setOutputFile(videoFile.getAbsolutePath());
			try {
				// ׼��¼��
				mediarecorder.prepare();
				mediarecorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		new Thread(this).start();;
	}
	/**
	 * 
			* ����������
			* �л�ǰ������ͷ
			* @author WAH-WAY(xuwahwhy@163.com)
			* <p>�������� ��2015��10��22�� ����4:31:33</p>
			*
			*
			* <p>�޸���ʷ ��(�޸��ˣ��޸�ʱ�䣬�޸�ԭ��/����)</p>
	 */
	@SuppressLint("NewApi")
	private void exchangeCameraFace() {
		if (isCameraBack) {
			isCameraBack = false;
		} else {
			isCameraBack = true;
		}
		int cameraCount = 0;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = Camera.getNumberOfCameras();// �õ�����ͷ�ĸ���
		for (int i = 0; i < cameraCount; i++) {
			Camera.getCameraInfo(i, cameraInfo);// �õ�ÿһ������ͷ����Ϣ
			if (cameraPosition == 1) {
				// �����Ǻ��ã����Ϊǰ��
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// ��������ͷ�ķ�λ��CAMERA_FACING_FRONTǰ��
																					// CAMERA_FACING_BACK����
					camera.stopPreview();// ͣ��ԭ������ͷ��Ԥ��
					camera.release();// �ͷ���Դ
					camera = null;// ȡ��ԭ������ͷ
					camera = Camera.open(i);// �򿪵�ǰѡ�е�����ͷ
					try {
						helper.resetCameraParam(camera);
						camera.setPreviewDisplay(surfaceHolder);// ͨ��surfaceview��ʾȡ������
						camera.startPreview();// ��ʼԤ��
						cameraPosition = 0;
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			} else { // ������ǰ�ã� ���Ϊ����
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// ��������ͷ�ķ�λ��CAMERA_FACING_FRONTǰ��
																				// CAMERA_FACING_BACK����
					camera.stopPreview();// ͣ��ԭ������ͷ��Ԥ��
					camera.release();// �ͷ���Դ
					camera = null;// ȡ��ԭ������ͷ
					camera = Camera.open(i);// �򿪵�ǰѡ�е�����ͷ
					try {
						helper.resetCameraParam(camera);
						camera.setPreviewDisplay(surfaceHolder);// ͨ��surfaceview��ʾȡ������
						camera.startPreview();// ��ʼԤ��
						cameraPosition = 1;
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
	//------------------------------surface view�Ļص�����-----------------------------------		
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
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//������ͷ
			}else{
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//������ͷ
			}
	    	helper.resetCameraParam(camera);
	        camera.setPreviewDisplay(surfaceHolder);//ͨ��surfaceview��ʾȡ������
	        camera.startPreview();//��ʼԤ��
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
