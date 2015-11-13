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
*
* Modification history(By WAH-WAY):
*
* Description:
*/

		package com.meng.main.activity.image;

import java.util.ArrayList;

import com.meng.main.bean.ImageItem;

public class ImageCacheManager {
	public static  int SELECT_ITEM_MAX=9;
	private static ImageCacheManager instance;
	private ArrayList<ImageItem> selectList;
	private ImageCacheManager() {
		selectList=new ArrayList<ImageItem>();
	}

	public static ImageCacheManager getInstance() {
		if (instance == null) {
			instance = new ImageCacheManager();
		}
		return instance;
	}
	

	public  ArrayList<ImageItem> getAllSelectList(){
		return selectList;
	}
	

	public  void addImageItem(ImageItem bean){
		if(selectList!=null&&!selectList.contains(bean)){
			selectList.add(bean);
		}
	}

	public  void clearImageItem() {
		selectList.clear();
	}

	public  void removeImageItem(ImageItem bean){
		if(selectList.contains(bean)){
			selectList.remove(bean);
		}
	}
	
	
}
