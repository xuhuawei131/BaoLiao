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
* MyImageManager.java V1.0 2015-10-28 ����3:51:53
*
* Copyright JIAYUAN Co. ,Ltd. All rights reserved.
*
* Modification history(By WAH-WAY):
*
* Description:
*/

package com.meng.main.image;

import java.io.File;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.meng.main.MyApplication;
import com.meng.main.bean.BitmapSize;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class MyImageManager {

private static MyImageManager instance;
	
	private MyImageManager(){
		
	}
	public static MyImageManager getInstance(){
		if(instance==null){
			instance=new MyImageManager();
		}
		return instance;
	}

	public void loadLocalImage(Context context, ImageView view, String path,
			BitmapSize size, int loadingDrawable, int errorDrawable){
		RequestCreator mPicasso=Picasso.with(MyApplication.context).load(new File(path));
		if(loadingDrawable!=0){
			mPicasso.placeholder(loadingDrawable);
		}
		if(errorDrawable!=0){
			 mPicasso.error(errorDrawable);
		}
		if (size == null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			File file = new File(path);
			if (file.exists()) {
				BitmapFactory.decodeFile(path, options);
				int width = options.outWidth;
				int height = options.outHeight;
				float sacle=100.0f/width;
				mPicasso.resize(100,(int)(height*sacle));
			} else {
				BitmapFactory.decodeResource(context.getResources(),
						errorDrawable, options);
				int width = options.outWidth;
				int height = options.outHeight;
				mPicasso.resize(width, height);
			}
		} else {
			mPicasso.resize(size.width, size.height);
		}
        mPicasso.centerCrop();
        mPicasso.into(view,null);
	}
	
}
