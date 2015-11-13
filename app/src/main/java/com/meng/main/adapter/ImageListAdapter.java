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

package com.meng.main.adapter;

import java.util.List;

import com.meng.main.R;
import com.meng.main.bean.BitmapSize;
import com.meng.main.bean.ImageItem;
import com.meng.main.image.MyImageManager;
import com.meng.main.utils.CommUtils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageListAdapter extends BaseAdapter {
	private Context mContext;
	private List<ImageItem> mDataList;
	private OnClickListener listener;
	public ImageListAdapter(Context context, OnClickListener listener,
			List<ImageItem> dataList){
		this.mContext = context;
		this.mDataList = dataList;
		this.listener = listener;
	}
	
	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView=View.inflate(mContext, R.layout.item_image_list, null);
		}
		ImageView imageVie=(ImageView)convertView.findViewById(R.id.image);
		imageVie.setTag(position);
		imageVie.setOnClickListener(listener);
		ImageItem item=mDataList.get(position);
		
		MyImageManager.getInstance().loadLocalImage(mContext, imageVie,
				item.sourcePath, new BitmapSize(CommUtils.dip2px(90),CommUtils.dip2px(90)), R.drawable.ic_launcher,
				R.drawable.ic_launcher);
		return convertView;

	}

}
