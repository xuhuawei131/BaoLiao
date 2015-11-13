package com.meng.main.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextPaint;

import com.meng.main.MyApplication;
import com.meng.main.custom.MediaMetadataRetriever;


public class CommUtils{

		/**
		 *
		 * 功能描述：
		 * 发送广播
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月6日 下午10:35:47</p>
		 *
		 * @param context
		 * @param intent
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static void sendBroadCastReceiver(Context context,Intent intent){
			context.sendBroadcast(intent);
		}

		/**
		 *
		 * 功能描述：
		 * 验证手机是否正确
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月12日 下午9:18:13</p>
		 *
		 * @param phone
		 * @return
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static boolean isPhoneFormat(String phone) {
			String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
			Pattern p = Pattern.compile(regExp);
			Matcher m = p.matcher(phone);
			boolean isCorrect = m.find();// boolean
			return isCorrect;
		}
		/**
		 *
		 * 功能描述：
		 * 验证邮箱是否正确
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月12日 下午9:18:03</p>
		 *
		 * @param email
		 * @return
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static boolean isEmailFormat(String email) {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher("email");
			boolean isMatched = matcher.matches();
			return isMatched;
		}
		/***
		 *
		 * 功能描述：
		 * 删除文件夹下的所有文件以及文件夹
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月12日 下午9:17:40</p>
		 *
		 * @param pathDir
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static void deletePathFiles(String pathDir) {
			File PHOTO_DIR = new File(pathDir);
			if (!PHOTO_DIR.exists()) {
				return;
			}
			File[] files = PHOTO_DIR.listFiles();
			if (files.length > 0) {
				for (File f : files) {
					if (f.isDirectory()) {
						deletePathFiles(f.getAbsolutePath());
					} else {
						f.delete();
					}
				}
			}
		}
		/**
		 *
		 * 功能描述：
		 * 小数点取值
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月12日 下午9:22:44</p>
		 *
		 * @param num
		 * @return
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static String  getPoint(float num){
			DecimalFormat   df=new   java.text.DecimalFormat("#.##");
			return df.format(num);
		}


		public static boolean isJsonArray(String src) {
			boolean re = true;
			try {
				new JSONArray(src);
			} catch (JSONException e) {
				re = false;
			} finally {
				return re;
			}
		}


		public static boolean isJsonObject(String src) {
			boolean re = true;
			try {
				new JSONObject(src);
			} catch (JSONException e) {
				re = false;
			} finally {
				return re;
			}
		}
		/**
		 * 自动分割文本
		 *
		 * @param content
		 *            需要分割的文本
		 * @param p
		 *            画笔，用来根据字体测量文本的宽度
		 * @param width
		 *            指定的宽度
		 * @return 一个指定行宽多行字符串
		 */
		public static String autoSplit(String content, TextPaint p, float width) {
			int length = content.length();
			float textWidth = 0;
			textWidth = p.measureText(content);
			if (textWidth <= width) {
				return content;
			}
			StringBuffer sb = new StringBuffer();
			int start = 0, end = 1;
			String tempStr = "";
			while (start < length) {
				if (p.measureText(content, start, end) > width) { // 文本宽度超出控件宽度时
					tempStr = content.subSequence(start, end) + "\n";
					sb.append(tempStr);
					start = end;
				}
				if (end == length) { // 不足一行的文本
					tempStr = content.subSequence(start, end) + "\n";
					sb.append(tempStr);
					break;
				}
				end += 1;
			}
			return sb.toString();
		}

		/**
		 * 判断当前是否有SD卡
		 *
		 * @return
		 */
		public static boolean isSDCardExists() {
			return android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}
		/**
		 *
		 * 功能描述：
		 * 将秒转化为 00：00：00格式字符串
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月22日 下午5:15:37</p>
		 *
		 * @param count
		 * @return
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static String getTimeFormat(int count){
			StringBuilder sb=new StringBuilder();
			int hour=count/3600;
			if(hour>9){
				sb.append(hour);
			}else{
				sb.append("0");
				sb.append(hour);
			}
			sb.append(":");
			int minu=count/60;
			if(minu>9){
				sb.append(minu);
			}else{
				sb.append("0");
				sb.append(minu);
			}
			sb.append(":");

			int sec=count%60;
			if(sec>9){
				sb.append(sec);
			}else{
				sb.append("0");
				sb.append(sec);
			}
			return sb.toString();
		}
		/**
		 *
		 * 功能描述：
		 * 文件大小单位
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月22日 下午10:12:41</p>
		 *
		 * @param fileS
		 * @return
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static String formetFileSize(long fileS) {// 转换文件大小
			DecimalFormat df = new DecimalFormat("#.00");
			String fileSizeString = "";
			if (fileS < 1024) {
				fileSizeString = df.format((double) fileS) + "B";
			} else if (fileS < 1048576) {
				fileSizeString = df.format((double) fileS / 1024) + "K";
			} else if (fileS < 1073741824) {
				fileSizeString = df.format((double) fileS / 1048576) + "M";
			} else {
				fileSizeString = df.format((double) fileS / 1073741824) + "G";
			}
			return fileSizeString;
		}
		/**
		 *
		 * 功能描述：
		 * 获取文件的大小
		 * @author WAH-WAY(xuwahwhy@163.com)
		 * <p>创建日期 ：2015年10月22日 下午10:25:57</p>
		 *
		 * @param file
		 * @return
		 *
		 * <p>修改历史 ：(修改人，修改时间，修改原因/内容)</p>
		 */
		public static long getFileSize(File file) {
			long s = 0;
			if (file != null && file.exists()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					s = fis.available();
				} catch (FileNotFoundException e) {

				} catch (IOException e) {

				}
			}
			return s;
		}

		/**
		 * 获取屏幕的宽高 [0] w [1] h
		 * */
		public static int[] getWindowWH() {
			int[] wh = new int[2];
			wh[0] = MyApplication.context.getResources().getDisplayMetrics().widthPixels;
			wh[1] = MyApplication.context.getResources().getDisplayMetrics().heightPixels;
			return wh;
		}
		/**
		 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
		 */
		public static int dip2px(float dpValue) {
			final float scale = MyApplication.context.getResources()
					.getDisplayMetrics().density;
			return (int) (dpValue * scale + 0.5f);
		}

		/**
		 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
		 */
		public static int px2dipfloat(float pxValue) {
			final float scale = MyApplication.context.getResources()
					.getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}

//	public static Bitmap createVideoThumbnail(String filePath) {
//        Bitmap bitmap = null;
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        try {
//            retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
//            retriever.setDataSource(filePath);
//            bitmap = retriever.captureFrame();
//        } catch(IllegalArgumentException ex) {
//            // Assume this is a corrupt video file
//        } catch (RuntimeException ex) {
//            // Assume this is a corrupt video file.
//        } finally {
//            try {
//                retriever.release();
//            } catch (RuntimeException ex) {
//                // Ignore failures while cleaning up.
//            }
//        }
//        return bitmap;
//    }

		public static Bitmap getVideoThumbnail(String videoPath, int width, int height) {
			Bitmap bitmap = null;
			// 获取视频的缩略图
			bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
			return bitmap;
		}


}
