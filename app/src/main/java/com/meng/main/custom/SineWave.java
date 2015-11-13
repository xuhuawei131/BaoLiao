package com.meng.main.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class SineWave extends View {

	private static final int MAX_INT_TIME = 40;
	private static final int MIN_INT_TIME = 10;

	private static final int MIN_AMPLIFIER = 100;

	private Paint mPaint = null;

	public int amplifier = MIN_AMPLIFIER;//
	public float frequency = 5.0f; //

	private float phase = 45.0f; //
	private int height = 0;
	private int width = 0;
	private float angle = 0;

	private boolean isRunable = false;

	private static final int mMINVolume = 10;
	private static final int mMAXVolume = 150;

	private static final int SUM = 6;
	private int change = 0;

	public SineWave(Context context) {
		super(context);
		init();
	}


	public SineWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
		isRunable = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isRunable = false;
	}

	public SineWave(Context context, int amplifier, float frequency, float phase) {
		super(context);
		this.frequency = frequency;
		this.amplifier = amplifier;
		this.phase = phase;
		mPaint = new Paint();
	}

	public float GetAmplifier() {
		return amplifier;
	}

	public float GetFrequency() {
		return frequency;
	}

	public float GetPhase() {
		return phase;
	}

	public void setCurrentVoluem(int mRecord_Volume) {
		if (mRecord_Volume > 550) {//����������
			change = 0;
			while (change != SUM) {
				if (amplifier != (MIN_AMPLIFIER + SUM * 12)) {
					amplifier += 12;
					frequency -= 0.5f;
				}
				try {
					Thread.sleep(MIN_INT_TIME);
				} catch (Exception e) {
				}
				angle += 10;
				if (angle == 360) {
					angle = 0;
				}
				postInvalidate();
				change++;
			}
		} else {
			change = 0;
			while (change != SUM) {
				if (amplifier > MIN_AMPLIFIER) {
					amplifier -= 12;
					frequency += 0.5f;
				}
				try {
					Thread.sleep(MAX_INT_TIME);
				} catch (Exception e) {
				}
				angle += 2;
				if (angle == 360) {
					angle = 0;
				}
				postInvalidate();
				change++;
			}
		}
	}


	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isRunable) {
			canvas.drawColor(Color.WHITE);
			height = this.getHeight();
			width = this.getWidth();

			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.GREEN);

			// amplifier = (amplifier * 2 > height) ? (height / 2) : amplifier;
			mPaint.setAlpha(200);
			mPaint.setStrokeWidth(5);
			for (int i = 0; i < width - 1; i++) {
				float startX = i;
				float startY = getY(i);
				float destX = (float) (i + 1);
				float dextY = getY(i + 1);
				canvas.drawLine(startX, startY, destX, dextY, mPaint);
			}

		}
	}

	private float getY(int x) {
		float y = (height / 2)
				- amplifier
				* (float) (Math.sin(angle * 2 * (float) Math.PI / 360.0f + 2
						* Math.PI * frequency * x / width));
		return y;
	}

	public void setChange(float tfrequency, float tamplifier) {
		float distancef = (tfrequency - frequency) / 4;
		float distancea = (tamplifier - amplifier) / 4;
		for (int i = 0; i < 4; i++) {
			frequency += distancef;
			amplifier += distancea;
			angle += 2;
			if (angle == 360) {
				angle = 0;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			postInvalidate();
		}

	}
}
