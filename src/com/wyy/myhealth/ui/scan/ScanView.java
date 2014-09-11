package com.wyy.myhealth.ui.scan;

import com.wyy.myhealth.R;
import com.wyy.myhealth.imag.utils.UpBmp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ScanView extends View {

	private final Paint paint;
	private final int maskColor;
	private final int resultColor;
	private final int frameColor;
	private final int resultPointColor;
	private static final long ANIMATION_DELAY = 60L;
	private static final int OPAQUE = 0xFF;
	public static int saoWidth = 0;
	public static int saoHeigth = 0;
	/**
	 * �м们���ߵ����λ��
	 */
	private int slideTop = 0;

	/**
	 * �м们���ߵ���׶�λ��
	 */
	private int slideBottom = 0;

	/**
	 * �ƶ�����
	 */
	private static final int SPEEN_DISTANCE = 5;

	private Bitmap drawBitmap;
	// �����߸߶�
	private static final int LINE_HEIGHT = 100;

	private Rect dst;

	private boolean isScroll = false;
	/**
	 * �ֻ�����Ļ�ܶ�
	 */
	private static float density;
	/**
	 * �����С
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * �������ɨ�������ľ���
	 */
	private static final int TEXT_PADDING_TOP = 30;

	public boolean isScroll() {
		return isScroll;
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}

	public ScanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		Resources resources = getResources();
		drawBitmap = UpBmp.readBitMap(this, R.drawable.progras_sao);
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);
		frameColor = resources.getColor(R.color.viewfinder_frame);
		resultPointColor = resources.getColor(R.color.possible_result_points);
		density = context.getResources().getDisplayMetrics().density;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		if (saoHeigth != 0) {
			final int top = (height - saoHeigth) / 2;
			final int left = (width - saoWidth) / 2;
			final int right = left + saoWidth;
			final int bottom = top + saoHeigth;

			if (slideTop == 0) {
				slideBottom = bottom - LINE_HEIGHT;
				slideTop = top;

			}

			paint.setColor(maskColor);
			canvas.drawRect(0, 0, width, top, paint);
			canvas.drawRect(0, top, left, height, paint);
			canvas.drawRect(right, top, width, height, paint);
			canvas.drawRect(left, bottom, right, height, paint);

			paint.setTextAlign(Align.CENTER);
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x40);
			canvas.drawText(getResources().getString(R.string.use_scan_notice),
					width / 2, bottom + TEXT_PADDING_TOP * density, paint);

			slideTop += SPEEN_DISTANCE;
			if (slideTop >= slideBottom) {
				slideTop = top;
			}
			if (drawBitmap != null && isScroll) {
				dst = new Rect(left, slideTop, right, slideTop + LINE_HEIGHT);
				// canvas.drawBitmap(drawBitmap, left, slideTop, paint);
				canvas.drawBitmap(drawBitmap, null, dst, paint);
			}

			postInvalidateDelayed(ANIMATION_DELAY, left, top, right, bottom);
		}

		// Log.i(VIEW_LOG_TAG, "ˢ��:"+saoHeigth);

	}

}
