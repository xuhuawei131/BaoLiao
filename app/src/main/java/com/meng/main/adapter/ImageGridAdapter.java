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
import com.squareup.picasso.Callback;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageGridAdapter extends BaseAdapter implements OnClickListener{
	private Context mContext;
	private List<ImageItem> mDataList;
	private OnGridItemClickListener itemListener;
	private OnClickListener listener;
	public ImageGridAdapter(Context context, OnClickListener listener,
			List<ImageItem> dataList){
		this.mContext = context;
		this.mDataList = dataList;
		this.listener = listener;
	}
	
	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}
	public void setOnGridItemClickListener(OnGridItemClickListener itemListener){
		this.itemListener=itemListener;
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (mDataList.get(position).isCamera) {
			return 0;
		} else {
			return 1;
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		if (type == 0) {
			ViewHolder0 mHolder;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.item_image_list_0, null);
				mHolder = new ViewHolder0();
				mHolder.layout_Camera = convertView
						.findViewById(R.id.layout_camera);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder0) convertView.getTag();
			}
			mHolder.layout_Camera.setOnClickListener(listener);
		} else {
			final ViewHolder1 mHolder;
			if (convertView == null) {
				convertView = View.inflate(mContext,
						R.layout.item_image_list_1, null);
				mHolder = new ViewHolder1();
				mHolder.imageIv = (ImageView) convertView
						.findViewById(R.id.image);
				mHolder.selected_tag = (ImageView) convertView
						.findViewById(R.id.selected_tag);
				mHolder.toggleButton = (ImageView) convertView
						.findViewById(R.id.toggle_button);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder1) convertView.getTag();
			}
			mHolder.selected_tag.setTag(position);
			mHolder.selected_tag.setOnClickListener(listener);
			mHolder.imageIv.setTag(position);
			mHolder.imageIv.setOnClickListener(this);
			ImageItem item = mDataList.get(position);

			if (item.isSelected) {
				mHolder.selected_tag
						.setImageResource(R.drawable.photoselect_pic_sel);
				mHolder.toggleButton.setSelected(true);
			} else {
				mHolder.selected_tag
						.setImageResource(R.drawable.photoselect_pic_unsel);
				mHolder.toggleButton.setSelected(false);
			}

			int width=CommUtils.getWindowWH()[0]/3;
			MyImageManager.getInstance().loadLocalImage(mContext, mHolder.imageIv,
					item.sourcePath, new BitmapSize(width,width), R.drawable.ic_launcher,
					R.drawable.ic_launcher);
		}
		return convertView;
	}
	private class ViewHolder1 {
		 ImageView imageIv;
		 ImageView selected_tag;
		 ImageView toggleButton;
	}
	private class ViewHolder0 {
		 View layout_Camera;
	}
	
	public interface OnGridItemClickListener{
		public void onGridItemClick(int position);
	}
	
	@Override
	public void onClick(View v) {
		int position=(Integer)v.getTag();
		itemListener.onGridItemClick(position);
	}
}
