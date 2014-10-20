package com.test;
//pequeña prueba con activity_segunda
import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.*;
import android.graphics.*;

public class Panel extends SurfaceView implements SurfaceHolder.Callback{
	CanvasThread canvasthread;

	//Zoom & pan touch event
    int y_old=0,y_new=0;int zoomMode=0;
    float pinch_dist_old=0,pinch_dist_new=0;
    int zoomControllerScale=1;//new and old pinch distance to determine Zoom scale
       // These matrices will be used to move and zoom image
       Matrix matrix = new Matrix();
       Matrix savedMatrix = new Matrix();

       // Remember some things for zooming
       PointF start = new PointF();
       PointF mid = new PointF();
       float oldDist = 1f;

       // We can be in one of these 3 states
       static final int NONE = 0;
       static final int PAN = 1;
       static final int ZOOM = 2;
       int mode = NONE;

    private static final String TAG = "DebugTag";
    
	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		getHolder().addCallback(this);
		canvasthread = new CanvasThread(getHolder(),this);
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		canvasthread.setRunning(true);
		canvasthread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		canvasthread.setRunning(false);
		while(retry){
			try{
				canvasthread.join();
				retry = false;
			}catch(InterruptedException e){
				//try again and again
			}
		}
	}
	@Override
    public boolean onTouchEvent(MotionEvent event){
    		PanZoomWithTouch(event);
    	
            invalidate();//necessary to repaint the canvas
            return true;
	}
	@Override
	public void onDraw(Canvas canvas){
		/*Paint paint = new Paint();
		Bitmap kangoo = BitmapFactory.decodeResource(getResources(), 
				R.drawable.canguro);
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(kangoo, 10, 19, null);*/
		Paint paint = new Paint();
		Bitmap sol = BitmapFactory.decodeResource(getResources(), 
				R.drawable.sol);
		canvas.drawBitmap(sol, matrix, paint);
		
	    

	}

	//añado codigo ++++++++++++++++++++++++++++++++++++++++++++++++++++
	/** PanZoomWithTouch function
    /*      Listen to touch actions and perform Zoom or Pan respectively
    /* */
   void PanZoomWithTouch(MotionEvent event){
           switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN://when first finger down, get first point
            savedMatrix.set(matrix);
            start.set(event.getX(), event.getY());
            Log.d(TAG, "mode=PAN");
            mode = PAN;
            break;
         case MotionEvent.ACTION_POINTER_DOWN://when 2nd finger down, get second point
            oldDist = spacing(event); 
            Log.d(TAG, "oldDist=" + oldDist);
            if (oldDist > 10f) {
               savedMatrix.set(matrix);
               midPoint(mid, event); //then get the mide point as centre for zoom
               mode = ZOOM;
               Log.d(TAG, "mode=ZOOM");
            }
            break;
         case MotionEvent.ACTION_UP:
         case MotionEvent.ACTION_POINTER_UP:       //when both fingers are released, do nothing
            mode = NONE;
            Log.d(TAG, "mode=NONE");
            break;
         case MotionEvent.ACTION_MOVE:     //when fingers are dragged, transform matrix for panning
            if (mode == PAN) {
               // ...
               matrix.set(savedMatrix);
               matrix.postTranslate(event.getX() - start.x,
                     event.getY() - start.y);
               Log.d(TAG,"Mapping rect");
                           //start.set(event.getX(), event.getY());
            }
            else if (mode == ZOOM) { //if pinch_zoom, calculate distance ratio for zoom
               float newDist = spacing(event);
               Log.d(TAG, "newDist=" + newDist);
               if (newDist > 10f) {
                  matrix.set(savedMatrix);
                  float scale = newDist / oldDist;
                  matrix.postScale(scale, scale, mid.x, mid.y);
               }
            }
            break;
           }
   }

          /** Determine the space between the first two fingers */
      private float spacing(MotionEvent event) {
         // ...
         float x = event.getX(0) - event.getX(1);
         float y = event.getY(0) - event.getY(1);
         return FloatMath.sqrt(x * x + y * y);
      }

      /** Calculate the mid point of the first two fingers */
      private void midPoint(PointF point, MotionEvent event) {
         // ...
         float x = event.getX(0) + event.getX(1);
         float y = event.getY(0) + event.getY(1);
         point.set(x / 2, y / 2);
      }
	//fin añadir codigo++++++++++++++++++++++++++++++++++++++++++++++++

}
class CanvasThread extends Thread{
	
	private SurfaceHolder _surfaceHolder;
	private Panel _panel;
	private boolean _run = false;
	
	public CanvasThread(SurfaceHolder surfaceHolder, Panel panel){
		_surfaceHolder = surfaceHolder;
		_panel = panel;
	}
	public void setRunning(boolean run){
		_run = run;
	}
	@Override
	public void run(){
		Canvas c;
		while(_run){
			c = null;
			try{
				c = _surfaceHolder.lockCanvas(null);
				synchronized(_surfaceHolder){
					//_panel.onDraw(c);
					_panel.onDraw(c);
				}
			}finally{
				if(c!=null){
					_surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}	
}
//cosas del onDraw, pruebas
/*paint.setColor(Color.RED);
canvas.drawCircle(20, 50, 25, paint);

paint.setAntiAlias(true);
paint.setColor(Color.BLUE);
canvas.drawCircle(60, 50, 25, paint);

paint.setStyle(Paint.Style.FILL_AND_STROKE);
paint.setStrokeWidth(2);
paint.setColor(Color.GREEN);
Path path = new Path();
path.moveTo(4, -10);
path.lineTo(20, 0);
path.lineTo(-9, 0);
path.close();
path.offset(60, 40);
canvas.drawPath(path, paint);
path.offset(90, 100);
canvas.drawPath(path, paint);
path.offset(80, 150);
canvas.drawPath(path, paint);

paint.setStyle(Paint.Style.FILL);
paint.setAntiAlias(true);
paint.setTextSize(20);
canvas.drawText("Hello Android! Fill...", 50, 230,paint);

int x = 75;
int y = 185;
paint.setColor(Color.GRAY);
paint.setTextSize(25);
String rotatedtext = "Rotated helloandroid :)";

Rect rect = new Rect();
paint.getTextBounds(rotatedtext, 0, rotatedtext.length(), rect);
canvas.translate(x, y);
paint.setStyle(Paint.Style.FILL);
 
canvas.drawText("Rotated helloandroid :)", 0, 0, paint);
paint.setStyle(Paint.Style.STROKE);
canvas.drawRect(rect, paint);
 
canvas.translate(-x, -y);
 
                       
paint.setColor(Color.RED);
canvas.rotate(-45, x + rect.exactCenterX(),y + rect.exactCenterY());
paint.setStyle(Paint.Style.FILL);
canvas.drawText(rotatedtext, x, y, paint);

DashPathEffect dashPath = new DashPathEffect(new float[]{10,40}, 1);
paint.setPathEffect(dashPath);
paint.setStrokeWidth(8);
canvas.drawLine(0, 60 , 320, 300, paint);*/
