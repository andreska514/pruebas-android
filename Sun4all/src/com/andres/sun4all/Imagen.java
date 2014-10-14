package com.andres.sun4all;

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

public class Imagen{
	
//CONSTANTES
	// zoom
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 1.7f;
    static final float MIN_ZOOM = 0.9f;
		
//VARIABLES y OBJETOS
    // zoom
    int mode = NONE;
    float oldDist = 1f;
    
//OBJETOS
    static ImageView imageView;
	// zoom
	static Matrix matrix = new Matrix();
	static Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	//hasta aqui funciona
	int lastTouchX;
	int lastTouchY;
	
	
	Imagen(ImageView imView)//Constructor
	{	// 0 4 zoom actual x y de la imagen(tamaño si lo multiplicas por width/height)
		// 2 5 posiciones x y del matrix (muy raro)
		//codigo temporal(de momento coge una imagen ya guardada)
		imView.setImageResource(R.drawable.sol);
		imView.setCropToPadding(true);
		imageView = imView;
		
	}
	//metodo que cogera una imagen aleatoria del servidor
	void cogeImagenAleatoria()
	{
		
	}
	public boolean pinta (View v, MotionEvent event){
		calculaCoordenadasImagen(event);
		
		switch(event.getAction()){
        // When user touches the screen
        case MotionEvent.ACTION_DOWN:
            Main.txtCont.setText("X :"+lastTouchX+" , "+"Y :"+lastTouchY);
            
		}
		return true;
	}
    public boolean touch(View v, MotionEvent event)
    {
    	imageView =(ImageView) v;
		imageView.setScaleType(ScaleType.MATRIX);
		
		//codigo probando
		
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
    //pulsar 1	    
		    case MotionEvent.ACTION_DOWN:
		        savedMatrix.set(matrix);
		        start.set(event.getX(), event.getY());
		        //Log.d("accion", "mode=PULSADO");
		        mode = PULSADO;
		        imageView.setImageMatrix(matrix);
		        //compruebaValores();
		        break;
    //pulsar 2
		    case MotionEvent.ACTION_POINTER_DOWN:
		        oldDist = espacio(event);
		        //Log.d("accion", "oldDist=" + oldDist);
		        if (oldDist > 10f) {
		            savedMatrix.set(matrix);
		            puntoMedio(mid, event);
		            mode = ZOOM;
		        }
		        imageView.setImageMatrix(matrix);
		        break;
	//soltar
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		        mode = NONE;
		        //Log.d("accion", "mode=NONE");
		        imageView.setImageMatrix(matrix);
		        break;
	//mover dedos
		    case MotionEvent.ACTION_MOVE:
		        if (mode == PULSADO) {
		        	//log("ACTION_MOVE -> pulsado");
		            matrix.set(savedMatrix);
		            matrix.postTranslate(event.getX() - start.x, 
		            		event.getY() - start.y);
		            compruebaZoom();
		        } 
		        else if (mode == ZOOM) {
		        	//log("ACTION_MOVE -> zoom");
		            float newDist = espacio(event);
	                matrix.set(savedMatrix);
	                float scale = newDist / oldDist;
	                matrix.postScale(scale, scale, mid.x, mid.y);  
	                compruebaZoom();
		        }
		        imageView.setImageMatrix(matrix);
		        compruebaZoom();
		        break;
	    }//fin switch
	    return true;
    }//fin touch
    //comprueba el zoom para concretar los límites
       
    //limita el max y min zoom y ejecuta compruebaValores()
    public void compruebaZoom(){
    	float[] values = new float[9];
        matrix.getValues(values);
        
        //compruebo el zoom
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        if(scaleX > MAX_ZOOM) {
    	scaleX = MAX_ZOOM;
        } else if(scaleX < MIN_ZOOM) {
    	scaleX = MIN_ZOOM;
        }

        if(scaleY > MAX_ZOOM) {
    	scaleY = MAX_ZOOM;
        } else if(scaleY < MIN_ZOOM) {
    	scaleY = MIN_ZOOM;
        }

        values[Matrix.MSCALE_X] = scaleX;
        values[Matrix.MSCALE_Y] = scaleY; 
        matrix.setValues(values);
        compruebaValores();
    }
    //comprueba el zoom actual y envia los bordes de la pantalla a limitaBordes()
	public void compruebaValores(){
    	//log("compruebaValores");
    	valores = new float[9];
    	matrix.getValues(valores);
    	
        if (valores[0]<=0.9f){
        	valores[0]=0.9f;
        	valores[4]=0.9f;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        	imageView.setImageMatrix(matrix);
        }else if(valores[0]>0.9 && valores[0]<=1){
        	limitaBordes(-65, -40);
        }else if(valores[0]>1 && valores[0]<=1.1){
        	limitaBordes(-166, -123);
        }else if(valores[0]>1.1 && valores[0]<=1.2){
        	limitaBordes(-246, -201);
        }else if(valores[0]>1.2 && valores[0]<=1.3){
        	limitaBordes(-316, -280);
        }else if(valores[0]>1.3 && valores[0]<=1.4){
        	limitaBordes(-409, -359);
        }else if(valores[0]>1.4 && valores[0]<=1.5){
        	limitaBordes(-484, -435);
        }else if(valores[0]>1.5 && valores[0]<=1.6){
        	limitaBordes(-563, -521);
        }else if(valores[0]>1.6 && valores[0]<=1.7){
        	limitaBordes(-644, -600);
        }else {// (valores[0]>1.7f){
        	//valores[0]=1.7f;
        	//valores[4]=1.7f;
        	limitaBordes(-664, -600);
        }
    }
    //establece los limites del matrix segun lo recibido de compruebaValores()
	void limitaBordes(float valorX, float valorY){
    	//log("determinando");
    	//if(x>0)
    	if(valores[2]>0){
    		valores[2]=0;
    		matrix.setValues(valores);
    	}//if(y>0)
    	if(valores[5]>0){
    		valores[5]=0;
    		matrix.setValues(valores);
    	}//if(x<valorX)
    	if(valores[2]< valorX){
    		valores[2]= valorX;
    		matrix.setValues(valores);
    	}//if(y<valorY)
    	if(valores[5]< valorY){
    		valores[5]= valorY;
    		matrix.setValues(valores);
    	}
    	imageView.setImageMatrix(matrix);
    }
    
	/** Determina el espacio entre los 2 primeros dedos*/
	private float espacio(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}
	/** Calcula el punto medio entre los 2 dedos*/
	private void puntoMedio(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
	//calcula las coordenadas de la pantalla
	void calculaCoordPantalla(MotionEvent e){
		// Getting X coordinate
        float mX = e.getX();
        // Getting Y Coordinate
        float mY = e.getY();
        
        Main.txtCont.setText("X :" + mX + " , " + "Y :" + mY);
	}
	//calcula las coordenadas absolutas de la imagen
	void calculaCoordenadasImagen(MotionEvent e){
		float []m = new float[9];
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X] * -1;
		float transY = m[Matrix.MTRANS_Y] * -1;
		float scaleX = m[Matrix.MSCALE_X];
		float scaleY = m[Matrix.MSCALE_Y];
		lastTouchX = (int) ((e.getX() + transX) / scaleX);
		lastTouchY = (int) ((e.getY() + transY) / scaleY);
		lastTouchX = Math.abs(lastTouchX);
		lastTouchY = Math.abs(lastTouchY);
	}
	//logs rapidos, de quita y pon
	private void log(String s){
		Log.i("",s);
	}
    
    static void logMatrix(Matrix matrix, ImageView imageView){
		float[] values = new float[9];
		Main.contador++;
		matrix.getValues(values);
		Log.i("  ",Main.contador+"-----------veces---------------- ");
		Log.i("valores",""+values[0]+"/"+values[1]+"/"
		+values[2]+"/"+values[3]+"/"+values[4]+"/"
		+values[5]+"/"+values[6]+"/"
		+values[7]+"/"+values[8]);
		
        float width = values[0]* imageView.getWidth();
        float height = values[4] * imageView.getHeight();

        Log.i("globalX[2]",""+values[2]);
        Log.i("globalY[5]",""+values[5]);
        Log.i("width[0]",""+width+"("+values[0]+" x "+imageView.getWidth()+")");
        Log.i("height[4]",""+height+"("+values[4]+" x "+imageView.getHeight()+")");
	}

}
