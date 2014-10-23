package com.andres.sun4all;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import com.andres.multitouch.MoveGestureDetector;
import com.andres.multitouch.MoveGestureDetector.SimpleOnMoveGestureListener;
import com.andres.multitouch.RotateGestureDetector;
import com.andres.multitouch.RotateGestureDetector.SimpleOnRotateGestureListener;
import android.view.View;

class ViewPort extends View {
	List<Layer> layers = new LinkedList<Layer>();
	int[] ids = {R.drawable.sol, R.drawable.canguro, R.drawable.ic_launcher};

	public ViewPort(Context context) {
		super(context);
		Log.i("aqu","constructor ViewPort");
		Resources res = getResources();
		for (int i = 0; i < ids.length; i++) {
			Layer l = new Layer(context, this, BitmapFactory.decodeResource(res, ids[i]));
			layers.add(l);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Layer l : layers) {
			l.draw(canvas);
		}
	}

	private Layer target;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			target = null;
			for (int i = layers.size() - 1; i >= 0; i--) {
				Layer l = layers.get(i);
				if (l.contains(event)) {
					target = l;
					layers.remove(l);
					layers.add(l);
					invalidate();
					break;
				}
			}
		}
		if (target == null) {
			return false;
		}
		return target.onTouchEvent(event);
	}
}

class Layer {
	Matrix matrix = new Matrix();
	Matrix inverse = new Matrix();
	RectF bounds;
	View parent;
	Bitmap bitmap;
	MoveGestureDetector mgd;
	ScaleGestureDetector sgd;
	RotateGestureDetector rgd;

	public Layer(Context ctx, View p, Bitmap b) {
		parent = p;
		bitmap = b;
		bounds = new RectF(0, 0, b.getWidth(), b.getHeight());
		mgd = new MoveGestureDetector(ctx, mgl);
		sgd = new ScaleGestureDetector(ctx, sgl);
		rgd = new RotateGestureDetector(ctx, rgl);
		matrix.postTranslate(50 + (float) Math.random() * 50, 50 + (float) Math.random() * 50);
	}

	public boolean contains(MotionEvent event) {
		matrix.invert(inverse);
		float[] pts = {event.getX(), event.getY()};
		inverse.mapPoints(pts);
		if (!bounds.contains(pts[0], pts[1])) {
			return false;
		}
		return Color.alpha(bitmap.getPixel((int) pts[0], (int) pts[1])) != 0;
	}

	public boolean onTouchEvent(MotionEvent event) {
		mgd.onTouchEvent(event);
		sgd.onTouchEvent(event);
		rgd.onTouchEvent(event);
		return true;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, matrix, null);
	}

	SimpleOnMoveGestureListener mgl = new SimpleOnMoveGestureListener() {
		@Override
		public boolean onMove(MoveGestureDetector detector) {
			PointF delta = detector.getFocusDelta();
			matrix.postTranslate(delta.x, delta.y);
			parent.invalidate();
			return true;
		}
	};

	SimpleOnScaleGestureListener sgl = new SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scale = detector.getScaleFactor();
			matrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());
			parent.invalidate();
			return true;
		}
	};

	SimpleOnRotateGestureListener rgl = new SimpleOnRotateGestureListener() {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			matrix.postRotate(-detector.getRotationDegreesDelta(), detector.getFocusX(), detector.getFocusY());
			parent.invalidate();
			return true;
		};
	};
}
//oro!
//http://stackoverflow.com/a/21657145/4116091
//https://github.com/pskink/android-gesture-detectors