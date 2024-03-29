package com.andres.sun4all;

import java.util.ArrayList;
import java.util.List;

import android.util.FloatMath;
import android.util.Log;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.view.MotionEvent;
import android.view.View;

public class Imagen {
	
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 1.7f;
    static final float MIN_ZOOM = 0.9f;
    int mode = NONE;
    float oldDist = 1f;
    static ImageView imageView;
	static Matrix matrix = new Matrix();
	static Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	int lastTouchX;
	int lastTouchY;
	//hasta aqui funciona
	
	//nueva lista de coordenadas con objetos
	ArrayList <Marking> listaPtos; 
	
	//
	Context c;
	Canvas canvas;
	
	
//CONSTRUCTOR --------------------------------------------------------------------------
	
	Imagen(ImageView imView, Context c)
	{	// 0 4 zoom actual x y de la imagen(tamaño si lo multiplicas por width/height)
		// 2 5 posiciones x y del matrix (muy raro)
		//codigo temporal(de momento coge una imagen ya guardada)
		imView.setImageResource(R.drawable.sol);
		imView.setCropToPadding(true);
		imageView = imView;
		this.c = c;
	}
	void guardaCoordenadas(int x, int y){
		Marking m = new Marking(x,y);
		listaPtos.add(m);
	}
//comprueba distancia antes de borrar
	void borraCoordenadas(View v, MotionEvent event){
		//borra de la lista la coordenada si el touch 
		//esta a menos de 50 pixels de alguna coordenada
		for (int i=listaPtos.size()-1; i>=0; i--){
			if (Math.sqrt(
			Math.pow((event.getX()-listaPtos.get(i).x), 2)
			+Math.pow((event.getY()-listaPtos.get(i).y), 2)) < 50){
				listaPtos.remove(i);
			}
		}
	}
//vacio->debe enviar el arraylist<marking> al servidor
	static void enviaCoordenadas(){
		
	}
	//modo add sunspot activado
	public boolean pinta (View v, MotionEvent event){
		switch(event.getAction()){
        // When user touches the screen
        case MotionEvent.ACTION_DOWN:
        	calculaCoordenadasImagen(event);
        	guardaCoordenadas(lastTouchX,lastTouchY);
          //hasta aqui funciona
		}
		
		return true;
	}
	//modo move image activado
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
		        mode = PULSADO;
		        break;
    //pulsar 2
		    case MotionEvent.ACTION_POINTER_DOWN:
		        oldDist = espacio(event);
		        if (oldDist > 10f) {
		            savedMatrix.set(matrix);
		            puntoMedio(mid, event);
		            mode = ZOOM;
		        }
		        break;
	//soltar
		    case MotionEvent.ACTION_UP:
		    case MotionEvent.ACTION_POINTER_UP:
		        mode = NONE;
		        break;
	//mover dedos
		    case MotionEvent.ACTION_MOVE:
		        if (mode == PULSADO) {
		            matrix.set(savedMatrix);
		            matrix.postTranslate(event.getX() - start.x, 
		            		event.getY() - start.y);
		            compruebaZoom();
		        } 
		        else if (mode == ZOOM) {
		            float newDist = espacio(event);
	                matrix.set(savedMatrix);
	                float scale = newDist / oldDist;
	                matrix.postScale(scale, scale, mid.x, mid.y);  
	                compruebaZoom();
		        }
		        compruebaZoom();
		        break;
	    }//fin switch
	    imageView.setImageMatrix(matrix);
	    return true;
    }//fin touch
    //comprueba el zoom para concretar los límites
    //limita el max y min zoom y envia los bordes de la pantalla a limitaBordes()
   //metodo a mejorar
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
        
      //Segunda parte: segun el zoom envia unos valores u otros
		//a limitaBordes(float, float)
        valores = new float[9];
    	matrix.getValues(valores);
    	if (valores[0]<=MIN_ZOOM){
        	valores[0]=MIN_ZOOM;
        	valores[4]=MIN_ZOOM;
        	valores[2]=0;
        	valores[5]=0;
        	matrix.setValues(valores);
        	//imageView.setImageMatrix(matrix);
        }else if(valores[0]>0.9 && valores[0]<=1){
        	limitCorners(-65, -40);
        }else if(valores[0]>1 && valores[0]<=1.1){
        	limitCorners(-166, -123);
        }else if(valores[0]>1.1 && valores[0]<=1.2){
        	limitCorners(-246, -201);
        }else if(valores[0]>1.2 && valores[0]<=1.3){
        	limitCorners(-316, -280);
        }else if(valores[0]>1.3 && valores[0]<=1.4){
        	limitCorners(-409, -359);
        }else if(valores[0]>1.4 && valores[0]<=1.5){
        	limitCorners(-484, -435);
        }else if(valores[0]>1.5 && valores[0]<=1.6){
        	limitCorners(-563, -521);
        }else {// (valores[0]>1.6f)
        	limitCorners(-664, -600);
        }
    }
    //metodo a mejorar
 
	void limitCorners(float valorX, float valorY){
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
	@SuppressLint("FloatMath")
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
	/** Calcula las coordenadas de la pantalla*/
	void calculaCoordPantalla(MotionEvent e){
		// Getting X coordinate
        float mX = e.getX();
        // Getting Y Coordinate
        float mY = e.getY();
        
        Main.txtCont.setText("X :" + mX + " , " + "Y :" + mY);
	}
	/** Calcula las coordenadas absolutas de la imagen*/
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
	void log(String s){
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
//clase que guarda un objeto con coordenadas
	class Marking{
		int x;
		int y;
		Marking(int x, int y){
			this.x = x;
			this.y = y;
		}
		Marking(){}
		int getX(){
			return x;
		}
		int getY(){
			return y;
		}
		void setX(int x){
			this.x = x;
		}
		void setY(int y){
			this.y = y;
		}
		
	}
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//no funciona
/*private void limitDrag(Matrix m) {
String TAG = "touch";
float[] values = new float[9];
m.getValues(values);
float transX = values[Matrix.MTRANS_X];
float transY = values[Matrix.MTRANS_Y];
float scaleX = values[Matrix.MSCALE_X];
float scaleY = values[Matrix.MSCALE_Y];
//ImageView iv = (ImageView)findViewById(R.id.image);
Rect bounds = imageView.getDrawable().getBounds();
Main main= null;
int viewWidth = c.getResources().getDisplayMetrics().widthPixels;
int viewHeight = c.getResources().getDisplayMetrics().widthPixels;
Log.i("vw-vh",viewWidth+"-"+viewHeight);

int width = bounds.right - bounds.left;
int height = bounds.bottom - bounds.top;
int offsetX = 20;
int offsetY = 80;
float minX = (-width + 20) * scaleX;
float minY = (-height + 20) * scaleY;
float maxX = minX+viewWidth+offsetX;
float maxY = minY+viewHeight-offsetY;
Log.d(TAG, "minX:"+minX);
Log.d(TAG, "maxX:"+maxX);
Log.d(TAG, "minY:"+minY);
Log.d(TAG, "maxY:"+maxY);
if(transX > (maxX)) {
	//transX = viewWidth - 20;
	Log.d(TAG, "transX >");
	transX = maxX;
} else if(transX < minX) {
	Log.d(TAG, "transX <");
	transX = minX;
}
if(transY > (maxY)) {
	// transY = viewHeight - 80;
	Log.d(TAG, "transY >");
	transY = maxY;
} else if(transY < minY) {
	transY = minY;
	Log.d(TAG, "transY <");
}
values[Matrix.MTRANS_X] = transX;
values[Matrix.MTRANS_Y] = transY;
m.setValues(values);
}*/
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
/*
 * if (valores[0]<=0.9f){
	valores[0]=0.9f;
	valores[4]=0.9f;
	valores[2]=0;
	valores[5]=0;
 */
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
/*public void compruebaValores(){
//log("compruebaValores");
valores = new float[9];
matrix.getValues(valores);


if (valores[0]<=MIN_ZOOM){
	valores[0]=MIN_ZOOM;
	valores[4]=MIN_ZOOM;
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
}*/
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
//borrado de compruebaValores
/*}else if(valores[0]>1.6 && valores[0]<=1.7){
limitaBordes(-644, -600);*/


/*private void limitDrag(Matrix m, ImageView view) {

int _y_up=20;

float[] values = new float[9];
m.getValues(values);
float transX = values[Matrix.MTRANS_X];
float transY = values[Matrix.MTRANS_Y];
float scaleX = values[Matrix.MSCALE_X];
float scaleY = values[Matrix.MSCALE_Y];

Rect bounds = view.getDrawable().getBounds();
int viewWidth = Main.viewWidth;
int viewHeight = Main.viewHeight;

if(viewHeight<=480){
    _y_up=0;
}
if(viewHeight>480&&viewHeight<980)
{
    _y_up=140;
}
int width = bounds.right - bounds.left;
int height = bounds.bottom - bounds.top;
int __width=width;
int __height=height;
width = viewWidth / 2;
height = viewHeight / 2;
//height = 200 ;
float minX = (-width) ;//* scaleX;
float minY = (-height) ;//* scaleY;

if ((transX) > (viewWidth)) {
    transX = viewWidth;
} 
else if (transX < minX) {
    transX = minX;
}
if ((-transX) > (viewWidth)) {
         // _x_right
    transX = -(viewWidth);
} 
else if (-transX < minX) {
    transX = -(minX+30);
}
if ((transY) > (viewHeight)) {
//  _y_up
    transY =( viewHeight);
} 
else if (transY < minY) {
    transY = (minY+_y_up);
}
if ((-transY) > (viewHeight)) {
//  _y_down
    transY = -(viewHeight);
} 
else if (-transY < minY) {

    transY = -(minY+170);
}

values[Matrix.MTRANS_X] = transX;
values[Matrix.MTRANS_Y] = transY;
m.setValues(values);
}*/
//* * * * * * * * * * * * * * * * * * * * * * * * * * * * *

