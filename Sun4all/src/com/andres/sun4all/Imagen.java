package com.andres.sun4all;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MotionEvent;
import android.view.View;

public class Imagen extends ImageView {
	
	static final int NONE = 0;
	static final int PULSADO = 1;
	static final int ZOOM = 2;
	static final float MAX_ZOOM = 2f;
    static float MIN_ZOOM = 0.9f;
    int mode = NONE;
    float oldDist = 1f;
    
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float[]valores;
	
	int lastTouchX;
	int lastTouchY;
	
	float inicioX;
	float inicioY;
	float finalX;
	float finalY;
	
	int absInicioX;
	int absInicioY;
	int absFinalX;
	int absFinalY;
	
	int viewWidth, viewHeight;
	
	boolean inverted = false;
	boolean pinta = false;
	boolean borra = false;
	boolean square = false;
	
	boolean point=true;
	/** array of absolute coordinates*/
	ArrayList <Marking> listaPtos = new ArrayList<Marking>(); 
	/** array of relative coordinates*/
	ArrayList <Mark> listaMarcas = new ArrayList<Mark>();
	
	ArrayList <RelativeSquare> listaRelativeSquare = new ArrayList<RelativeSquare>();
	ArrayList <AbsoluteSquare> listaAbsoluteSquare = new ArrayList<AbsoluteSquare>();
	
	MotionEvent lastEvent;
	Context context;
	Bitmap inicial;
	Bitmap bitmap;
	Bitmap positivo;
	Bitmap negativo;
	Bitmap cruz = BitmapFactory.decodeResource(getResources(), R.drawable.cruz);
	Paint pincelSquare = new Paint();
	
	ProgressDialog pDialog;
	JSONObject finalJson;
	
	/** Method used within Constructors*/
	public void init(){
		inicial = BitmapFactory.decodeResource(getResources(), R.drawable.sol);
		setOnTouchListener(clickImagen);
		resetSquareCoordinates();
		pincelSquare.setColor(Color.BLUE);
		pincelSquare.setStyle(Style.STROKE);
		pincelSquare.setStrokeWidth(3);
	}
	/** Constructor 1*/
	public Imagen(Context c, AttributeSet attr) {
		super(c, attr);
		context=c;
		init();
	}
	/** Constructor 2*/
	public Imagen(Context c) {
		super(c);
		context=c;
		init();
	}
	/** Draw the imageView*/
	@Override
	public void onDraw(Canvas c){
		String s = getResources().getString(R.string.sunspot);
		if(bitmap!=null){
			limitCorners();
			c.drawBitmap(bitmap, matrix, new Paint());
		}
		Main.txtCont.setText(s+listaMarcas.size());
		if(pinta){
			if(!square)
				this.setOnTouchListener(clickPinta);
			else
				this.setOnTouchListener(clickCuadrado);
		}
		else{
			this.setOnTouchListener(clickImagen);
		}
		if(listaPtos.size()>0){
			for(Mark mark:listaMarcas){	
				float laX = mark.x-(cruz.getWidth()/2);
				float laY = mark.y-(cruz.getHeight()/2);
				c.drawBitmap(cruz, laX, laY, new Paint());
			}
		}
		if(listaRelativeSquare.size()>0){
			for (RelativeSquare sq:listaRelativeSquare){
				float laX = sq.iniX;
				float laY = sq.iniY;
				float lado = sq.finX-sq.iniX;
				c.drawRect(laX, laY, laX+lado, laY+lado, pincelSquare);
			}
		}
	}
	/** Save relative and absolute coordinates in listaMarcas/listaPtos*/
	void saveCoordinates(float x, float y){
		//relative coordinates
		Mark mark = new Mark(x,y);
		listaMarcas.add(mark);
		Main.txtCont.setText(x+"-"+y);
		
		//absolute coordinates
		Marking marking = new Marking(lastTouchX,lastTouchY);
		listaPtos.add(marking);
	}
	/** Delete coordinates near touch event(less than 50px)*/
	void delCoordinates(View v, MotionEvent event){
		float evX = event.getX();
		float evY = event.getY();
		float lado;
		for (int i=0; i<listaPtos.size(); i++){
			Mark m = listaMarcas.get(i);
			float marX = m.x;
			float marY = m.y;
			if(Math.abs(evX-marX)+Math.abs(evY-marY)<50){
				listaMarcas.remove(i);
				listaPtos.remove(i);
			}
		}
		for(int i=0;i<listaRelativeSquare.size(); i++){
			RelativeSquare r1 = listaRelativeSquare.get(i);
			lado = r1.finX - r1.iniX;
			float difIniX = Math.abs(evX-r1.iniX);
			float difIniY = Math.abs(evY-r1.iniY);
			float difFinX = Math.abs(evX-r1.finX);
			float difFinY = Math.abs(evY-r1.finY);
			if((difIniX)<25){
				if (evY>=r1.iniY && evY<=r1.iniY+lado){
					listaRelativeSquare.remove(i);
					listaAbsoluteSquare.remove(i);
				}
			}
			else if((difIniY)<25){
				if (evX>=r1.iniX && evX<=r1.iniX+lado){
					listaRelativeSquare.remove(i);
					listaAbsoluteSquare.remove(i);
				}
			}
			else if((difFinX)<25){
				if (evY<=r1.finY && evY>=r1.finY-lado){
					listaRelativeSquare.remove(i);
					listaAbsoluteSquare.remove(i);
				}
			}
			else if((difFinY)<25){
				if (evX<=r1.finX && evX>=r1.finX-lado){
					listaRelativeSquare.remove(i);
					listaAbsoluteSquare.remove(i);
				}
			}
			else{
				
			}
			
		}
	}
	/** Enable move mode when touch the imageView*/
	View.OnTouchListener clickImagen = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			lastEvent = event;
		    switch (event.getAction() & MotionEvent.ACTION_MASK) {
			    case MotionEvent.ACTION_DOWN:
			        savedMatrix.set(matrix);
			        start.set(event.getX(), event.getY());
			        mode = PULSADO;
			        break;
			    case MotionEvent.ACTION_POINTER_DOWN:
			        oldDist = espacio(event);
			        if (oldDist > 10f) {
			            savedMatrix.set(matrix);
			            puntoMedio(mid, event);
			            mode = ZOOM;
			        }
			        break;
			    case MotionEvent.ACTION_UP:
			    case MotionEvent.ACTION_POINTER_UP:
			        mode = NONE;
			        break;
			    case MotionEvent.ACTION_MOVE:
			        if (mode == PULSADO) {
			            matrix.set(savedMatrix);
			            float matX = event.getX()-start.x;
			            float matY = event.getY()-start.y;
			            matrix.postTranslate(matX, matY);
			        } 
			        else if (mode == ZOOM) {
			        	
			            float newDist = espacio(event);
		                matrix.set(savedMatrix);
		                float scale = newDist / oldDist;
		                matrix.postScale(scale, scale, mid.x, mid.y);  
			        }
			        checkZoom();
			        limitCorners();
			        break;
		    }//end switch
		    mueveCoordenadas();
		    moveSquares();
		    invalidate();
		    return true;
		}//end OnTouch
	};//end touchListener
	/** Enable paint mode when touch the imageview*/
	View.OnTouchListener clickPinta = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				calculaCoordenadasImagen(event);
				if(!borra){ 
					saveCoordinates(event.getX(),event.getY());
					Main.txtCont.setText("getX"+event.getX() +" getX"+event.getY());
				}
				else{
					delCoordinates(v, event);
				}
			}
			invalidate();
			return true;
		}//end onTouch
	};//end onTouchListener
	/** enable paint Squares touching the screen*/
	View.OnTouchListener clickCuadrado = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction() & MotionEvent.ACTION_MASK){
				case MotionEvent.ACTION_DOWN:
					if(inicioX==-1){
						inicioX= event.getX();
						inicioY= event.getY();
						calculaCoordenadasImagen(event);
						absInicioX = lastTouchX;
						absInicioY = lastTouchY;
					}
					break;
				case MotionEvent.ACTION_UP:
					finalX = event.getX();
					finalY = event.getY();
					calculaCoordenadasImagen(event);
					absFinalX = lastTouchX;
					absFinalY = lastTouchY;
					saveSquare(inicioX,inicioY,finalX,finalY);
					break;
			}
			invalidate();
			return true;
		}//end OnTouch
	};//end OnTouchListener
	void resetSquareCoordinates(){
		inicioX=-1;
		inicioY=-1;
		finalX=-1;
		finalY=-1;
	}
	void saveSquare(float iniX, float iniY, float finX, float finY){
		//relative
		RelativeSquare relSquare = new RelativeSquare(iniX, iniY, finX, finY);
		listaRelativeSquare.add(relSquare);
		//absolute
		AbsoluteSquare absSquare = new AbsoluteSquare(absInicioX, absInicioY, absFinalX, absFinalY);
		listaAbsoluteSquare.add(absSquare);
		//reiniciamos
		resetSquareCoordinates();
	}
	/** Set the new zoom of matrix*/
	void setZoom(float zoom){
    	float[] values = new float[9];
        matrix.getValues(values);
    	values[Matrix.MSCALE_X] = zoom;
        values[Matrix.MSCALE_Y] = zoom; 
        matrix.setValues(values);
        limitCorners();
    }
	/**Chech the zoom for narrow limits, it starts limitCorners ()*/
    public void checkZoom(){
    	float[] values = new float[9];
        matrix.getValues(values);
        float scaleX = values[Matrix.MSCALE_X];
        if(scaleX > MAX_ZOOM) {
        	setZoom(MAX_ZOOM);
        } 
        else if(scaleX < MIN_ZOOM) {
        	setZoom(MIN_ZOOM);
        }
    }
    /** Limit the corners, it holds the image inside the imageView*/
    private void limitCorners() {
    	viewWidth = this.getWidth();
		viewHeight = this.getHeight();
		float []m = new float[9];
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];
		float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
		float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());
		if (fixTransX != 0 || fixTransY != 0) {
			matrix.postTranslate(fixTransX, fixTransY);
		}
		matrix.getValues(m);
		if (getImageWidth() < viewWidth) {
			m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
		}
		if (getImageHeight() < viewHeight) {
			m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
		}
		matrix.setValues(m);
	}
	/** Used within limitCorners*/
	private float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;
		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}
		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}
	/** Get the width of image (bitmapWidth*Zoom)*/
	float getImageWidth(){
		float []m = new float[9];
		matrix.getValues(m);
		return m[Matrix.MSCALE_X]*bitmap.getWidth();
	}
	/** Get the Height of image (bitmapHeight*Zoom)*/
	float getImageHeight(){
		float []m = new float[9];
		matrix.getValues(m);
		return m[Matrix.MSCALE_X]*bitmap.getHeight();
	}/** return the space between the 2 fingers*/
	@SuppressLint("FloatMath")
	private float espacio(MotionEvent event) {
	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return FloatMath.sqrt(x * x + y * y);
	}
	/** return the midpoint between the 2 fingers*/
	private void puntoMedio(PointF point, MotionEvent event) {
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
	/** Calculate the absolute coordinates of the image*/
	void calculaCoordenadasImagen(MotionEvent e){
		float []m = new float[9];
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X] * -1;
		float transY = m[Matrix.MTRANS_Y] * -1;
		float scaleX = m[Matrix.MSCALE_X];
		float scaleY = m[Matrix.MSCALE_Y];
		lastTouchX = Math.abs((int) ((e.getX() + transX) / scaleX));
		lastTouchY = Math.abs((int) ((e.getY() + transY) / scaleY));
	}
	/** Move the relative coordinates when the image was moved*/
	void mueveCoordenadas(){
		for (int i=0; i<listaPtos.size();i++){
			Marking pto = listaPtos.get(i);
			Mark mark = listaMarcas.get(i);
			float[]coor = new float[2];
			coor[0]=pto.x;
			coor[1]=pto.y;
			matrix.mapPoints(coor);
			mark.setX(coor[0]);
			mark.setY(coor[1]);
			listaMarcas.set(i, mark);
		}
	}
	void moveSquares(){
		for(int i=0; i<listaAbsoluteSquare.size(); i++){
			AbsoluteSquare absSq = listaAbsoluteSquare.get(i);
			RelativeSquare relSq = listaRelativeSquare.get(i);
			
			/** ini coords*/
			float[]coorIni = new float[2];
			coorIni[0]=absSq.iniX;
			coorIni[1]=absSq.iniY;

			matrix.mapPoints(coorIni);
			relSq.iniX = coorIni[0];
			relSq.iniY = coorIni[1];

			/** finals coords*/
			float[]coorFin = new float[2];
			coorFin[0]=absSq.finX;
			coorFin[1]=absSq.finY;
			matrix.mapPoints(coorFin);
			relSq.finX = coorFin[0];
			relSq.finY = coorFin[1];
			listaRelativeSquare.set(i, relSq);
		}
	}
	/** Change the bitmap(take the source)*/
	void changeBitmap(Bitmap b){
		bitmap = b;
		invalidate();
	}
	/** Execute new LoadImage with the String passed */
	void preparaDescarga(String [] s){
		new LoadImage().execute(s);
	}
	/** Invert the image(black-white)*/
	void invertBitmap(){
		if(inverted){
			bitmap=positivo;
			inverted=false;
		}else{
			bitmap=negativo;
			inverted=true;
		}
		invalidate();
	}
	void invertBitmap(boolean b){
		if(b){
			bitmap=positivo;
		}else{
			bitmap=negativo;
		}
		invalidate();
	}
	/** Download de 2 images passed*/
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			bitmap=null;
			positivo=null;
			negativo=null;
			pDialog = new ProgressDialog((Main)context);
			pDialog.setMessage(getResources().getString(R.string.progressBar));
			pDialog.show();
		}
		protected Bitmap doInBackground(String... args) {
			try {
				positivo = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
				negativo = BitmapFactory.decodeStream((InputStream)new URL(args[1]).getContent());
				setZoom(MIN_ZOOM);
				postInvalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return positivo;
		}
		protected void onPostExecute(Bitmap image) {
			if(image != null){
				bitmap = image;
				invalidate();
				pDialog.dismiss();
				Toast.makeText(context, getResources().getString(R.string.downloadOK), Toast.LENGTH_SHORT).show();
			}
			else{
				pDialog.dismiss();
				Toast.makeText(context, getResources().getString(R.string.downloadError), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	void prepareJson(){
		
		
		if(listaPtos.size()>0 || listaAbsoluteSquare.size()>0){
			JSONArray coorJson= new JSONArray();
			
			for(int x=0; x<listaPtos.size();x++){
				Marking ptos = listaPtos.get(x);
				JSONObject tempJson = new JSONObject();
				try{
					tempJson.put("x", ptos.getX());
					tempJson.put("y", ptos.getY());
					tempJson.put("width", 0);
				}catch(JSONException jEx){
					jEx.printStackTrace();
				}
				coorJson.put(tempJson);
			}
			
			for(int x=0; x< listaAbsoluteSquare.size();x++){
				AbsoluteSquare as = listaAbsoluteSquare.get(x);
				JSONObject tempJson = new JSONObject();
				try{
					tempJson.put("x", as.iniX);
					tempJson.put("y", as.iniY);
					tempJson.put("width", as.finX-as.iniX);
				}catch(JSONException jEx){
					jEx.printStackTrace();
				}
				coorJson.put(tempJson);
			}
			
			finalJson = new JSONObject();
			try{
				finalJson.put("description",Main.cadUrl);
				finalJson.put("points", coorJson);
			}catch(JSONException jEx){
				jEx.printStackTrace();
			}
		}
		else{
			finalJson = new JSONObject();
			try{
				finalJson.put("description",Main.cadUrl);
			}catch(JSONException jEx){
				jEx.printStackTrace();
			}
		}
		Log.d("finalJson",""+finalJson);
	}
	
	/** absolute coordinates*/
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
	/** relative coordinates*/
	class Mark{
		float x;
		float y;
		Mark(float x, float y){
			this.x = x;
			this.y = y;
		}
		Mark(){}
		float getX(){
			return x;
		}
		float getY(){
			return y;
		}
		void setX(float x){
			this.x = x;
		}
		void setY(float y){
			this.y = y;
		}
	}
	/** Coordinates from absolute square*/
	class AbsoluteSquare{
		int iniX,iniY,finX,finY;
		AbsoluteSquare(int inX, int inY, int fiX, int fiY){
			iniX=inX;
			iniY=inY;
			finX=fiX;
			finY=fiY;
		}
	
	}
	/** Coordinates from relative square*/
	class RelativeSquare{
		float iniX,iniY,finX,finY;
		RelativeSquare(float inX, float inY, float fiX, float fiY){
			iniX=inX;
			iniY=inY;
			finX=fiX;
			finY=fiY;
		}
	}
	
}