package com.meng.main.activity.video;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class CameraHelper {
	private Activity activity;

	public CameraHelper(Activity activity){
		this.activity=activity;
	}

	public Camera resetCameraParam(Camera camera) {
		// 设置camera预览的角度，因为默认图片是倾斜90度的
		camera.setDisplayOrientation(90);

		Size pictureSize = null;
		Size previewSize = null;
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewFrameRate(5);
		// 设置旋转代码
		parameters.setRotation(90);
		// parameters.setPictureFormat(PixelFormat.JPEG);

//		List<Size> supportedPictureSizes = SupportedSizesReflect
//				.getSupportedPictureSizes(parameters);
//		List<Size> supportedPreviewSizes = SupportedSizesReflect
//				.getSupportedPreviewSizes(parameters);
//		if (supportedPictureSizes != null && supportedPreviewSizes != null
//				&& supportedPictureSizes.size() > 0
//				&& supportedPreviewSizes.size() > 0) {
//			// 2.x
//			pictureSize = supportedPictureSizes.get(0);
//			int maxSize = 1280;
//			if (maxSize > 0) {
//				for (Size size : supportedPictureSizes) {
//					if (maxSize >= Math.max(size.width, size.height)) {
//						pictureSize = size;
//						break;
//					}
//				}
//			}
//			WindowManager windowManager = (WindowManager) activity
//					.getSystemService(Context.WINDOW_SERVICE);
//			Display display = windowManager.getDefaultDisplay();
//			DisplayMetrics displayMetrics = new DisplayMetrics();
//			display.getMetrics(displayMetrics);
//
//			previewSize = getOptimalPreviewSize(supportedPreviewSizes,
//					display.getWidth(), display.getHeight());
//
//			parameters.setPictureSize(pictureSize.width, pictureSize.height);
//			parameters.setPreviewSize(previewSize.width, previewSize.height);
//
//		}
//		camera.setParameters(parameters);
		return camera;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

}
