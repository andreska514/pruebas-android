package com.andres.sun4all;

import java.io.IOException;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;
public class Main extends Activity {

	private int contador = 0;
	
	//imagen
	Imagen imagen;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//imagen
		ImageView img = (ImageView) findViewById(R.id.ImgFoto);
		img.setOnTouchListener(handlerMover);
		imagen = new Imagen(img);
		
		TextView txtCont = (TextView) findViewById(R.id.txtCont);
		txtCont.setText(String.valueOf(contador));
		
		final Button btnAdd =(Button)findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Intent i = new Intent(Main.this, Imagen.class);
			}
			
		});
	}
	
	View.OnTouchListener handlerMover = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			return imagen.touch(v, event);
			
		}//fin onTouch
	};//fin ontouchListener
	

	

	
}
