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
* ImageActivity.java V1.0 2015-10-28 ����4:34:20
*
* Copyright JIAYUAN Co. ,Ltd. All rights reserved.
*
* Modification history(By WAH-WAY):
*
* Description:
*/

package com.meng.main.activity.image;


import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.meng.main.R;
import com.meng.main.adapter.ImageGridAdapter;
import com.meng.main.adapter.ImageGridAdapter.OnGridItemClickListener;
import com.meng.main.adapter.ImageListAdapter;
import com.meng.main.bean.ImageBucket;
import com.meng.main.bean.ImageItem;
import com.meng.main.constants.FileConstants;
import com.meng.main.custom.HorizontalListView;
/**
 * 
		* ����������
		* ���ѡ��
		* @author ��ά(WAH-WAY)
		*
		* <p>�޸���ʷ��(�޸��ˣ��޸�ʱ�䣬�޸�ԭ��/����)</p>
 */
public class ImageActivity extends Activity implements Runnable,OnItemClickListener,OnClickListener,OnGridItemClickListener{
private ImageFetcher mImageFetcher;
private ArrayList<ImageBucket> arrayList;
private ImageGridAdapter gridAdapter;
private ImageListAdapter listAdapter;
private ArrayList<ImageItem> gridList;
private GridView gridView;
private HorizontalListView horizionGridView;
private File cameraFile;
private Uri uri;
private Button btn_sure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		init();
		setContentView(R.layout.activity_image);
		findViewByIds();
		requestService();
		
	}
	private void init(){
		gridList=new ArrayList<ImageItem>();
		gridAdapter = new ImageGridAdapter(ImageActivity.this, ImageActivity.this, gridList);
		listAdapter=new ImageListAdapter(ImageActivity.this, ImageActivity.this, ImageCacheManager.getInstance().getAllSelectList());
		gridAdapter.setOnGridItemClickListener(ImageActivity.this);
	}
	
	private void findViewByIds(){
		btn_sure=(Button)findViewById(R.id.btn_sure);
		View btn_cancle=findViewById(R.id.btn_cancel);
		gridView=(GridView)findViewById(R.id.gridview);
		horizionGridView=(HorizontalListView)findViewById(R.id.horizionGridView);
		gridView.setAdapter(gridAdapter);
		horizionGridView.setAdapter(listAdapter);
		
		horizionGridView.setOnItemClickListener(this);
		btn_sure.setOnClickListener(this);
		btn_cancle.setOnClickListener(this);
	}
	
	private void  requestService(){
		new Thread(this).start();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hanler.removeMessages(1);
	}

	@Override
	public void run() {
		mImageFetcher = ImageFetcher.getInstance(this);
		arrayList = mImageFetcher.getImagesBucketList(true);
		ImageItem cameraItem = new ImageItem();
		cameraItem.isCamera = true;
		gridList.add(cameraItem);
		if(arrayList!=null){
			for(ImageBucket item:arrayList){
				gridList.addAll(item.imageList);
			}
		}
		hanler.sendEmptyMessage(1);
		
	}
	private Handler hanler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			gridAdapter.notifyDataSetChanged();
		}
	};
	@Override
	public void onGridItemClick(int position) {
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.layout_camera:
			int size=ImageCacheManager.getInstance().getAllSelectList().size();
			if(size<ImageCacheManager.SELECT_ITEM_MAX){
				startCamera();
			}else{
				Toast.makeText(this, "最多能选择"
						+ ImageCacheManager.SELECT_ITEM_MAX + "张图片", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.selected_tag:
			int position = (Integer) v.getTag();
			setItemSelect(position);
			break;
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_sure:
			
			break;
		}
	}
	private void startCamera(){
		cameraFile = new File(FileConstants.getImageDir(),System.currentTimeMillis()+".png");
		uri=Uri.fromFile(cameraFile);
		Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
		Intent intent_camera =getPackageManager().getLaunchIntentForPackage("com.android.camera");
		if (intent_camera != null) {
			intentCamera.setPackage("com.android.camera");
		}
		intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intentCamera, 100);
	}
	@Override
	public void onActivityResult(int request, int result, Intent data) {
		switch(request){
		case 100:
			if(cameraFile!=null&&cameraFile.exists()){
				ImageItem item=mImageFetcher.getImageItemByUrl(uri);
				ImageCacheManager.getInstance().addImageItem(item);
				listAdapter.notifyDataSetChanged();
			}
			break;
		}
	}
	
	private void setItemSelect(int position) {
		int size = ImageCacheManager.getInstance().getAllSelectList().size();
		ImageItem item = gridList.get(position);
		if (item.isSelected) {
			item.isSelected = false;
			ImageCacheManager.getInstance().removeImageItem(item);
			gridAdapter.notifyDataSetChanged();
			listAdapter.notifyDataSetChanged();
		} else {
			if (size >= ImageCacheManager.SELECT_ITEM_MAX){
				Toast.makeText(this, "最多能选择"
						+ ImageCacheManager.SELECT_ITEM_MAX + "张图片", Toast.LENGTH_SHORT).show();
				return;
			} else {
				item.isSelected = true;
				ImageCacheManager.getInstance().addImageItem(item);
				gridAdapter.notifyDataSetChanged();
				listAdapter.notifyDataSetChanged();
			}
		}
		size = ImageCacheManager.getInstance().getAllSelectList().size();
		btn_sure.setText(size + "/" + ImageCacheManager.SELECT_ITEM_MAX);
		if (size > 0) {
			btn_sure.setEnabled(true);
		} else {
			btn_sure.setEnabled(false);
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
}
