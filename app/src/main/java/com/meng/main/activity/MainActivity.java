package com.meng.main.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.meng.main.R;
import com.meng.main.R.id;
import com.meng.main.R.layout;
import com.meng.main.activity.audio.AudioActivity;
import com.meng.main.activity.image.ImageActivity;
import com.meng.main.activity.video.VideoActivity;

public class MainActivity extends Activity implements OnClickListener{
	private ImageView image_voice;
	private View image_video;
	private View layout_image;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		View image_back=findViewById(R.id.image_back);
		View image_task=findViewById(R.id.image_task);
		
		image_voice = (ImageView) findViewById(R.id.image_voice);
		image_video=findViewById(R.id.image_video);
		layout_image=findViewById(R.id.layout_image);
		
		image_voice.setOnClickListener(this);
		image_video.setOnClickListener(this);
		layout_image.setOnClickListener(this);
		image_back.setOnClickListener(this);
		image_task.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.image_voice:
			startActivityForResult(new Intent(this,AudioActivity.class), 100);
			break;
		case R.id.image_video:
			startActivityForResult(new Intent(this,VideoActivity.class), 200);
			break;
		case R.id.layout_image:
			startActivityForResult(new Intent(this,ImageActivity.class), 300);
			
			break;
		case R.id.image_back:
			finish();
			break;
		case R.id.image_task:
			
			break;
		}
	}
}
