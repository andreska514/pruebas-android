package com.andres.sun4all;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.FloatMath;
import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class Main extends Activity {
	//zoom***********************************************
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();  
	
	// 4 estados
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	static final int DRAW =3;
	int mode = NONE;
	// datos para zoom
		PointF start = new PointF();
		PointF mid = new PointF();
		float oldDist = 1f;
	
	// Limit zoomable/pannable image
		private float[] matrixValues = new float[9];
		private float maxZoom;
		private float minZoom;
		private float height;
		private float width;
		private RectF viewRect;
		
		
	//**************************************************

	private ImageView img;
	private int contador = 0;
	//private Bitmap loadedImage;
	//private String address = "http://www.losporque.com/wp-content/uploads/2008/09/el_origen_de_las_manchas_solares.jpg";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		img = (ImageView) findViewById(R.id.ImgFoto);
		img.setImageResource(R.drawable.sol);
		TextView txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		//downloadFile(address);
	}
	void downloadFile(String address){
		URL imageUrl = null;
		try{
			imageUrl = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			Log.i("downloadFile", "2 -> "+address);
			conn.connect();
			Log.i("downloadFile", "3");
			//loadedImage = BitmapFactory.decodeStream(conn.getInputStream());
			Log.i("downloadFile", "4");
			//imageView.setImageBitmap(loadedImage);
			Log.i("downloadFile", "5");
			
		}catch(Exception e){
			Log.i("downloadFile", "estoy en catch");
			Toast.makeText(getApplicationContext(), "Error al cargar la imagen"+e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){  init();   }
	}
	private void init() {
		maxZoom = 4;
		minZoom = 0.25f;
		height = img.getDrawable().getIntrinsicHeight()+20;
		width = img.getDrawable().getIntrinsicWidth()+20;
		viewRect = new RectF(0, 0, img.getWidth()+20, img.getHeight()+20);
	}
/////////************touch events for image Moving, panning and zooming   ***********///
public boolean onTouch(View v, MotionEvent event) {

	// Dump touch event to log
	//dumpEvent(event);
	// Handle touch events here...
	switch (event.getAction() & MotionEvent.ACTION_MASK) {
	case MotionEvent.ACTION_DOWN:
		savedMatrix.set(matrix);
		start.set(event.getX(), event.getY());
		Log.d("TAG", "mode=DRAG");
		mode = DRAG;
		break;
	case MotionEvent.ACTION_POINTER_DOWN:
		oldDist = spacing(event);
		Log.d("TAG", "oldDist=" + oldDist);
		if (oldDist > 10f) {
			savedMatrix.set(matrix);
			midPoint(mid, event);
			mode = ZOOM;
			Log.d("TAG", "mode=ZOOM");
		}
		break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_POINTER_UP:
		mode = NONE;
		Log.d("TAG", "mode=NONE");
		break;
	case MotionEvent.ACTION_MOVE:
		if (mode == DRAW){ onTouchEvent(event);}
		if (mode == DRAG) {
			///code for draging..        
		} 
		else if (mode == ZOOM) {
			float newDist = spacing(event);
			Log.d("TAG", "newDist=" + newDist);
			if (newDist > 10f) {
				matrix.set(savedMatrix);
				float scale = newDist / oldDist;
				matrix.getValues(matrixValues);
				float currentScale = matrixValues[Matrix.MSCALE_X];
				// limit zoom
				if (scale * currentScale > maxZoom) {
					scale = maxZoom / currentScale; 
				}else if(scale * currentScale < minZoom){
					scale = minZoom / currentScale; 
				}
				matrix.postScale(scale, scale, mid.x, mid.y);
			}
		}
		break;
	}
	img.setImageMatrix(matrix);
	return true; // indicate event was handled
}

//*******************Determine the space between the first two fingers
private float spacing(MotionEvent event) {
	float x = event.getX(0) - event.getX(1);
	float y = event.getY(0) - event.getY(1);
	return FloatMath.sqrt(x * x + y * y);
}

//************* Calculate the mid point of the first two fingers 
private void midPoint(PointF point, MotionEvent event) {
	float x = event.getX(0) + event.getX(1);
	float y = event.getY(0) + event.getY(1);
	point.set(x / 2, y / 2);
}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
}