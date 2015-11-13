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

package com.meng.main.activity.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.meng.main.bean.ImageBucket;
import com.meng.main.bean.ImageItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;
import android.util.Log;


public class ImageFetcher {
	private static ImageFetcher instance;
	private Context mContext;
	private HashMap<String, ImageBucket> mBucketList = new HashMap<String, ImageBucket>();
	private HashMap<String, String> mThumbnailList = new HashMap<String, String>();


	private ImageFetcher(Context context)
	{
		this.mContext = context;
	}

	public synchronized static ImageFetcher getInstance(Context context) {
		if (instance == null) {
			instance = new ImageFetcher(context);
		}
		return instance;
	}
	
	public void clearData(){
		mBucketList.clear();
		mThumbnailList.clear();
		instance=null;
	}

	boolean hasBuildImagesBucketList = false;


	public ArrayList<ImageBucket> getImagesBucketList(boolean refresh)
	{
		if (refresh || (!refresh && !hasBuildImagesBucketList))
		{
			buildImagesBucketList();
		}
		
		ArrayList<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> itr = mBucketList.entrySet()
				.iterator();
		while (itr.hasNext())
		{
			Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
					.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}


	private void buildImagesBucketList() {
		Cursor cur = null;
		try {
			long startTime = System.currentTimeMillis();
			getThumbnail();
			String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
					Media.DATA, Media.BUCKET_DISPLAY_NAME };
			cur = mContext.getContentResolver().query(
					Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
			if (cur.moveToFirst()) {
				// ��ȡָ���е�����
				int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
				int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
				int bucketDisplayNameIndex = cur
						.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
				int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
				do {
					String _id = cur.getString(photoIDIndex);
					String path = cur.getString(photoPathIndex);
					if(!TextUtils.isEmpty(path)&&path.toLowerCase().endsWith(".gif")){
						continue;
					}
					String bucketName = cur.getString(bucketDisplayNameIndex);
					String bucketId = cur.getString(bucketIdIndex);

					ImageBucket bucket = mBucketList.get(bucketId);
					
					if (bucket == null) {
						bucket = new ImageBucket();
						mBucketList.put(bucketId, bucket);
						bucket.imageList = new ArrayList<ImageItem>();
						bucket.bucketName = bucketName;
						bucket.bucketId = bucketId;
					}
					bucket.count++;
					ImageItem imageItem = new ImageItem();
					imageItem.imageId = _id;
					imageItem.sourcePath = path;
					imageItem.thumbnailPath = mThumbnailList.get(_id);
					bucket.imageList.add(imageItem);
				} while (cur.moveToNext());
			}

			hasBuildImagesBucketList = true;
			long endTime = System.currentTimeMillis();
			Log.d(ImageFetcher.class.getName(), "use time: "
					+ (endTime - startTime) + " ms");
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}
	
	
	public  ImageItem getImageItemByUrl(Uri photoUri){
		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(photoUri, proj,
				null, null, null);
		if (cursor != null) {
			int id_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
			int date_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				int id = cursor.getInt(id_index);
				String path = cursor.getString(date_index);
				ImageItem item = new ImageItem();
				item.imageId = String.valueOf(id);
				item.sourcePath = path;
				return item;
			}
		}
		return null;
	}
	

	@SuppressLint("DefaultLocale")
	private void getThumbnail() {
		Cursor cur = null;
		try {
			String[] projection = { Thumbnails.IMAGE_ID, Thumbnails.DATA };
			cur = mContext.getContentResolver().query(
					Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null,
					null);
			if (cur.moveToFirst()) {
				int image_id;
				String image_path;
				int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
				int dataColumn = cur.getColumnIndex(Thumbnails.DATA);
				do {
					image_id = cur.getInt(image_idColumn);
					image_path = cur.getString(dataColumn);
					if(!TextUtils.isEmpty(image_path)&&image_path.toLowerCase().endsWith(".gif")){
						continue;
					}
					mThumbnailList.put("" + image_id, image_path);
				} while (cur.moveToNext());
			}
			// getThumbnailColumnData(cursor);
		} finally {
			cur.close();
		}
	}


	@SuppressWarnings("unused")
	private void getThumbnailColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			int image_id;
			String image_path;
			int image_idColumn = cur.getColumnIndex(Thumbnails.IMAGE_ID);
			int dataColumn = cur.getColumnIndex(Thumbnails.DATA);

			do {
				image_id = cur.getInt(image_idColumn);
				image_path = cur.getString(dataColumn);

				mThumbnailList.put("" + image_id, image_path);
			} while (cur.moveToNext());
		}
	}


}
