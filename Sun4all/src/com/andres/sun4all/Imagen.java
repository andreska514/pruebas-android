package com.andres.sun4all;

//https://github.com/sephiroth74/ImageViewZoom
import java.io.IOException;


import android.util.FloatMath;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class Imagen {

	//Matrix para el zoom
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	Matrix primerMatrix ;
	
	float[]valoresIniciales = new float[9];
	

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;
	
	int mode = NONE;
	
	Imagen(ImageView imgView)//Constructor
	{
		//imgView = (ImageView) findViewById(R.id.ImgFoto);
		// 2 5 0 4
		//imgView.setScaleType(ScaleType.CENTER);
		imgView.setImageResource(R.drawable.sol);
		primerMatrix = new Matrix();
		primerMatrix.getValues(valoresIniciales);

		logMatrix(primerMatrix, imgView);
		//img.setOnTouchListener(handlerMover);
	}
	
	private void logMatrix(Matrix matrix, ImageView imageView){
		float[] values = new float[9];
		matrix.getValues(values);
		
		Log.i("valores",""+values[0]+"-"+values[1]+"-"
		+values[2]+"-"+values[3]+"-"+values[4]+"-"
		+values[5]+"-"+values[6]+"-"
		+values[7]+"-"+values[8]);
		
		float globalX = values[2];
        float globalY = values[5];
        float width = values[0]* imageView.getWidth();
        float height = values[4] * imageView.getHeight();

        Log.i("","x(2):" + globalX 
        		+ " y(5):" + globalY + " width(0):" + width 
        		+ " height(4):"+ height);
	}

	/** Determina el espacio entre los 2 primeros dedos*/
	private float spacing(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}
	/** Calcula el punto medio entre los 2 dedos*/
	private void midPoint(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
    public boolean touch(View v, MotionEvent event)
    {
    	ImageView view =(ImageView) v;
	    
	    // Handle touch events here...
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	
	    //pulsar 1	    
	    case MotionEvent.ACTION_DOWN:
	        savedMatrix.set(matrix);
	        start.set(event.getX(), event.getY());
	        Log.d("accion", "mode=PULSADO");
	        mode = PULSADO;
	        break;
	    //pulsar 2
	    case MotionEvent.ACTION_POINTER_DOWN:
	        oldDist = spacing(event);
	        Log.d("accion", "oldDist=" + oldDist);
	        if (oldDist > 10f) {
	            savedMatrix.set(matrix);
	            midPoint(mid, event);
	            mode = ZOOM;
	            Log.d("accion", "mode=ZOOM");
	        }
	        break;
//soltar
	    case MotionEvent.ACTION_UP:
	    case MotionEvent.ACTION_POINTER_UP:
	    	Log.i("accion","ACTION_UP"+mode);
	        mode = NONE;
	        Log.d("accion", "mode=NONE");
	        break;
//mover
	    case MotionEvent.ACTION_MOVE:
	    	//Log.i("accion","ACTION_MOVE");
	    	
	    	//Comprobacion valores minimos
	    	float[]valores = new float[9];
	    	matrix.getValues(valores);
	    	
            if (valores[0]<valoresIniciales[0]){
            	Log.d("mode zoom ","zoom if");
            	
            	matrix.setValues(valoresIniciales);
            	break;
            }
            //***********************************
		            
	        if (mode == PULSADO) {
	            Log.i("mode","drag");
	            matrix.set(savedMatrix);
	            matrix.postTranslate(event.getX() - start.x, event.getY()
	                    - start.y);
	        } 
	        else if (mode == ZOOM) {
	        	//Log.i("mode","zoom");
	            float newDist = spacing(event);
                matrix.set(savedMatrix);
                float scale = newDist / oldDist;
                matrix.postScale(scale, scale, mid.x, mid.y);  
	                //scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
	                
	        }
	        break;
	    }//fin switch

	    view.setImageMatrix(matrix);
	    logMatrix(matrix, view);
	    return true;
    }//fin touch
    
    //******************************************************************************
    //**************** Metodos por comprobar / Metodos no usados *******************
    //******************************************************************************
    
	private float getXValueFromMatrix(Matrix matrix) {

        float[] values = new float[9];
           matrix.getValues(values);
           float globalX = values[2];

           return globalX;
    }
	private float getYValueFromMatrix(Matrix matrix) {

        float[] values = new float[9];
           matrix.getValues(values);
           float globalY = values[5];

           return globalY;
    }
	private float getWidthFromMatrix(Matrix matrix, ImageView imageview) {
        float[] values = new float[9];
           matrix.getValues(values);

           float width = values[0]* imageview.getWidth();

           return width;
    }
    private float getHeightFromMatrix(Matrix matrix, ImageView imageview) {

        float[] values = new float[9];
           matrix.getValues(values);

           float height = values[4] * imageview.getHeight();

           return height;
    }

}
